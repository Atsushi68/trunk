package com.taketoritei.order.reserve.bath.service;

import static com.taketoritei.order.jooq.tables.DReserveBath.*;
import static com.taketoritei.order.jooq.tables.MTime.*;
import static com.taketoritei.order.jooq.tables.MUser.*;
import static org.jooq.impl.DSL.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taketoritei.order.common.Consts.BathEnum;
import com.taketoritei.order.common.DateUtil;
import com.taketoritei.order.jooq.tables.records.DReserveBathRecord;
import com.taketoritei.order.jooq.tables.records.MTimeRecord;
import com.taketoritei.order.login.form.User;
import com.taketoritei.order.reserve.bath.form.BathInputForm;
import com.taketoritei.order.reserve.bath.form.BathReservedForm;
import com.taketoritei.order.reserve.bath.form.BathTimeForm;
import com.taketoritei.order.reserve.bath.form.TimeForm;

@Service
@Transactional(rollbackFor = Exception.class)
public class BathService extends BaseBathService {

	@Autowired
	MessageSource messages;

	/**
	 * 予約済みの予約情報を取得する
	 * 最新の1件のみ
	 */
	public BathReservedForm getReservedBath(String roomNo, String reserveDate, Locale locale) {

		// 予約済でも時間が過ぎていたら同じ日に予約できるように修正
		// ---------------------------------

		// 現在時刻のhm(時分)を取得
		String nowHm = DateUtil.getHmForTimeCode();

		// 現在時間のTIME_CDを取得
		MTimeRecord nowTimeRec = jooq.selectFrom(M_TIME).where(M_TIME.TIME_CD.eq(
				jooq.select(max(M_TIME.TIME_CD)).from(M_TIME) //
				.where(M_TIME.HM_FROM.le(nowHm)) //
				.and(M_TIME.HM_TO.gt(nowHm)) //
		)).fetchOne();

		// 予約した時間を15分過ぎていたら予約ができるようにする為に、
		// 上記で取得した現在のTIME_CDのfrom時間と、現在日時を比較して15分過ぎていたら次のTIME_CDにする
		int nowTimeCdInt = Integer.parseInt(nowTimeRec.getTimeCd());
		int nowHmFrom = Integer.parseInt(nowTimeRec.getHmFrom().toString());
		int plus15From = nowHmFrom + 15;
		if (plus15From < Integer.parseInt(nowHm)) {
			nowTimeCdInt = nowTimeCdInt + 1;
		}
		String nowTimeCd = String.format("%02d", nowTimeCdInt);

		// 現在日かどうか
		Result<DReserveBathRecord> bathRecs = null;
 		if (reserveDate.equals(DateUtil.getBussinessDateYmd())) {
 			// 現在日の場合、TIME_CDが未来のデータのみを取得（予約した時間が過ぎていたら予約可能に）
 			bathRecs = jooq.selectFrom(D_RESERVE_BATH) //
				.where(D_RESERVE_BATH.RESERVE_DATE.eq(reserveDate)) //
				.and(D_RESERVE_BATH.ROOM_NO.eq(roomNo)) //
				.and(D_RESERVE_BATH.DEL_FLG.eq(false)) //
				.and(D_RESERVE_BATH.TIME_CD.ge(nowTimeCd)) //
				.orderBy(D_RESERVE_BATH.TIME_CD) //
				.fetch();

		} else {
			// 現在日以外は指定された予約を取得
			bathRecs = jooq.selectFrom(D_RESERVE_BATH) //
				.where(D_RESERVE_BATH.RESERVE_DATE.eq(reserveDate)) //
				.and(D_RESERVE_BATH.ROOM_NO.eq(roomNo)) //
				.and(D_RESERVE_BATH.DEL_FLG.eq(false)) //
				.orderBy(D_RESERVE_BATH.TIME_CD) //
				.fetch();
		}

		// ---------------------------------

		// 未来の予約があれば予約不可
		if (bathRecs.size() != 0) {

			DReserveBathRecord rec = bathRecs.get(0);

			// m_time
			MTimeRecord timeRec = jooq.selectFrom(M_TIME) //
					.where(M_TIME.TIME_CD.eq(rec.getTimeCd()))
					.fetchOne();
			TimeForm time = createTimeForm(timeRec, locale);

			// 現在時間の時間
			String hm = DateUtil.getHmForTimeCode();

			BathReservedForm form = new BathReservedForm();
			form.setTimeCd(time.getTimeCd());
			form.setTimeNm(time.getNext() + time.getDispText());
			form.setBathCd(rec.getBathCd());
			form.setBathNm(getBathNm(rec.getBathCd(), locale));
			if (reserveDate.equals(DateUtil.getBussinessDateYmd())) {
				form.setOverTime(Integer.parseInt(time.getHmTo()) <= Integer.parseInt(hm));
				form.setInTime(Integer.parseInt(time.getHmFrom()) <= Integer.parseInt(hm));
			} else {
				form.setOverTime(false);
			}
			return form;
		} else {
			return null;
		}
	}

	/**
	 * 予約表の一覧を取得する
	 */
	public List<BathTimeForm> getBath(String roomNo, String reserveDate, Locale locale) {

		// 貸切風呂予約を取得
		Result<DReserveBathRecord> bathRecs = jooq.selectFrom(D_RESERVE_BATH) //
				.where(D_RESERVE_BATH.RESERVE_DATE.eq(reserveDate)) //
				.and(D_RESERVE_BATH.DEL_FLG.eq(false)) //
				.orderBy(D_RESERVE_BATH.TIME_CD.asc()) //
				.fetch();

		// 選択可能なm_timeの全件を取得
		List<TimeForm> timeList = getSelectableTimeList(locale);

		// 現在時間の時間
		String hm = DateUtil.getHmForTimeCode();

		// 予約済みの予約情報を取得する
		BathReservedForm reserved = getReservedBath(roomNo, reserveDate, locale);

		List<BathTimeForm> list = new ArrayList<BathTimeForm>();
		for (TimeForm time : timeList) {

			BathTimeForm form = new BathTimeForm();

			form.setTimeCd(time.getTimeCd());
			form.setTimeNm(time.getNext() + time.getDispText());
			form.setBathType(time.getBathType());
			if (reserveDate.equals(DateUtil.getBussinessDateYmd()))
				// 開始時間が過ぎているものは予約不可に
				form.setOverTime(Integer.parseInt(time.getHmFrom()) < Integer.parseInt(hm));
			else
				form.setOverTime(false);

			// 未予約、または予約している場合でも時間が過ぎている場合は予約可能
			if (reserved == null) {
				form.setBath1(getReservedRoomNo(bathRecs, BathEnum.BATH1.getCode(), reserveDate, time.getTimeCd()) == null);
				form.setBath2(getReservedRoomNo(bathRecs, BathEnum.BATH2.getCode(), reserveDate, time.getTimeCd()) == null);
				form.setBath3(getReservedRoomNo(bathRecs, BathEnum.BATH3.getCode(), reserveDate, time.getTimeCd()) == null);
				form.setBath4(getReservedRoomNo(bathRecs, BathEnum.BATH4.getCode(), reserveDate, time.getTimeCd()) == null);
				form.setBath5(getReservedRoomNo(bathRecs, BathEnum.BATH5.getCode(), reserveDate, time.getTimeCd()) == null);
				form.setBath6(getReservedRoomNo(bathRecs, BathEnum.BATH6.getCode(), reserveDate, time.getTimeCd()) == null);
				form.setBath7(getReservedRoomNo(bathRecs, BathEnum.BATH7.getCode(), reserveDate, time.getTimeCd()) == null);
				form.setBath8(getReservedRoomNo(bathRecs, BathEnum.BATH8.getCode(), reserveDate, time.getTimeCd()) == null);
			} else {
				// 予約中であれば予約不可
				form.setBath1(false);
				form.setBath2(false);
				form.setBath3(false);
				form.setBath4(false);
				form.setBath5(false);
				form.setBath6(false);
				form.setBath7(false);
				form.setBath8(false);
			}

			list.add(form);
		}

		return list;
	}

	private static String getReservedRoomNo(Result<DReserveBathRecord> bathRecs, String bathCd, String reserveDate, String timeCd) {
		String reservedRoomNo = null;
		for (DReserveBathRecord bathRec : bathRecs) {
			if (bathRec.getReserveDate().equals(reserveDate) //
					&& bathRec.getTimeCd().equals(timeCd) //
					&& bathRec.getBathCd().equals(bathCd)) {
				reservedRoomNo = bathRec.getRoomNo();
				break;
			}
		}
		return reservedRoomNo;
	}

	/**
	 * 温泉名称の取得
	 */
	public String getBathNm(String bathCd, Locale locale) {

		if (BathEnum.BATH1.getCode().equals(bathCd))
			return messages.getMessage("bath.41", null, locale);
		if (BathEnum.BATH2.getCode().equals(bathCd))
			return messages.getMessage("bath.42", null, locale);
		if (BathEnum.BATH3.getCode().equals(bathCd))
			return messages.getMessage("bath.43", null, locale);
		if (BathEnum.BATH4.getCode().equals(bathCd))
			return messages.getMessage("bath.44", null, locale);
		if (BathEnum.BATH5.getCode().equals(bathCd))
			return messages.getMessage("bath.51", null, locale);
		if (BathEnum.BATH6.getCode().equals(bathCd))
			return messages.getMessage("bath.52", null, locale);
		if (BathEnum.BATH7.getCode().equals(bathCd))
			return messages.getMessage("bath.53", null, locale);
		if (BathEnum.BATH8.getCode().equals(bathCd))
			return messages.getMessage("bath.54", null, locale);

		return null;
	}


	/**
	 * 利用可能かチェック
	 */
	public String checkBath(User user, String reserveDate, BathInputForm form, Locale locale) {

		// 現在時間が操作可能範囲時間帯かをチェック
		// -------------------------------------------------
		if (reserveDate.equals(DateUtil.getBussinessDateYmd())) {
			String hm = DateUtil.getHmForTimeCode();
			List<MTimeRecord> timeRec = jooq.selectFrom(M_TIME) //
					.where(M_TIME.HM_FROM.le(hm))
					.and(M_TIME.HM_TO.gt(hm))
					.orderBy(M_TIME.TIME_CD)
					.fetch();
			if (timeRec.size() != 0) {
				if (Integer.parseInt(timeRec.get(0).getTimeCd()) > Integer.parseInt(form.getTimeCd())) {
					return messages.getMessage("bath.39", null, locale);
				}
			}
		}

		// すでに利用済みかをチェック
		// -------------------------------------------------
		BathReservedForm reserveForm = getReservedBath(user.getRoomNo(), reserveDate, locale);
		if (reserveForm != null) {
			return messages.getMessage("bath.47", null, locale) + messages.getMessage("bath.48", null, locale);

		}

		// 当該風呂が予約済みかをチェック
		// -------------------------------------------------
		Result<DReserveBathRecord> reserveRecs = jooq.selectFrom(D_RESERVE_BATH) //
				.where(D_RESERVE_BATH.RESERVE_DATE.eq(reserveDate)) //
				.and(D_RESERVE_BATH.TIME_CD.eq(form.getTimeCd())) //
				.and(D_RESERVE_BATH.BATH_CD.eq(form.getBathCd())) //
				.and(D_RESERVE_BATH.DEL_FLG.eq(false))
				.fetch();

		if (reserveRecs.size() != 0) {
			return messages.getMessage("bath.38", null, locale);
		}

		return null;
	}

	/**
	 * 予約キャンセル
	 */
	public int cancelBath(User user, String reserveDate, BathInputForm form) {

		return jooq //
				.update(D_RESERVE_BATH) //
				.set(D_RESERVE_BATH.DEL_FLG, true) //
				.set(D_RESERVE_BATH.LAST_DATE, getLastDate()) //
				.set(D_RESERVE_BATH.LAST_USER, getLastUser()) //
				.where(D_RESERVE_BATH.RESERVE_DATE.eq(form.getReserveDate())) //
				.and(D_RESERVE_BATH.TIME_CD.eq(form.getTimeCd())) //
				.and(D_RESERVE_BATH.BATH_CD.eq(form.getBathCd())) //
				.and(D_RESERVE_BATH.ROOM_NO.eq(user.getRoomNo())) //
				.execute(); //
	}

	/**
	 * 予約登録
	 */
	public int insertBath(User user, String reserveDate, BathInputForm form, int count) {

		// 予約登録
		return jooq //
				.insertInto(
						D_RESERVE_BATH, //
						D_RESERVE_BATH.RESERVE_DATE, //
						D_RESERVE_BATH.TIME_CD, //
						D_RESERVE_BATH.BATH_CD, //
						D_RESERVE_BATH.ROOM_NO, //
						D_RESERVE_BATH.RESERVE_COUNT,
						D_RESERVE_BATH.TOKEN,
						D_RESERVE_BATH.DEL_FLG, //
						D_RESERVE_BATH.LAST_DATE, //
						D_RESERVE_BATH.LAST_USER) //
				.values(
						reserveDate, //
						form.getTimeCd(), //
						form.getBathCd(), //
						user.getRoomNo(), //
						count, //
						user.getToken(), //
						false, //
						getLastDate(), //
						getLastUser()) //
				.execute();
	}


	/**
	 * カウントを予約日が少ないほうから再設定
	 * @param token
	 * @return
	 */
	public void resetReservedBathCount(String token) {
		List<DReserveBathRecord> userReservedList = jooq.selectFrom(D_RESERVE_BATH)
			.where(D_RESERVE_BATH.DEL_FLG.eq(false))
			.and(D_RESERVE_BATH.TOKEN.eq(token))
			.orderBy(D_RESERVE_BATH.RESERVE_DATE.asc(), D_RESERVE_BATH.TIME_CD.asc()).fetch();

		// ユーザーの予約をすべて取得
		int count = 1;
		for (DReserveBathRecord userReserved : userReservedList) {
			userReserved.setReserveCount(count);
			updateReserveBathCount(userReserved);
			count++;
		}
	}

	/**
	 * 同じユーザーで何回目の登録かを取得
	 * @param token
	 * @return
	 */
	public int getReservedBathCount(String token) {

		Result<Record> ret = jooq.selectFrom(D_RESERVE_BATH.join(M_USER).on(D_RESERVE_BATH.ROOM_NO.eq(M_USER.ROOM_NO).and(D_RESERVE_BATH.TOKEN.eq(M_USER.TOKEN))))
				.where(D_RESERVE_BATH.DEL_FLG.eq(false))
				.and(M_USER.DEL_FLG.eq(false))
				.and(M_USER.TOKEN.eq(token))
				.fetch();

		if (ret == null || ret.size() == 0) {
			return 1;
		}
		return ret.size() + 1;
	}


	public int updateReserveBathCount(DReserveBathRecord reserveBathRecord) {
		return jooq.update(D_RESERVE_BATH)
		.set(D_RESERVE_BATH.RESERVE_COUNT, reserveBathRecord.getReserveCount())
		.where(D_RESERVE_BATH.ROOM_NO.eq(reserveBathRecord.getRoomNo())
		.and(D_RESERVE_BATH.TOKEN.eq(reserveBathRecord.getToken()))
		.and(D_RESERVE_BATH.RESERVE_DATE.eq(reserveBathRecord.getReserveDate()))
		.and(D_RESERVE_BATH.TIME_CD.eq(reserveBathRecord.getTimeCd()))
		.and(D_RESERVE_BATH.BATH_CD.eq(reserveBathRecord.getBathCd())))
		.execute();
	}

}
