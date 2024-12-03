package com.taketoritei.order.reserve.bath.service;

import static com.taketoritei.order.jooq.tables.DReserveBath.*;

import java.util.List;

import org.jooq.Result;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taketoritei.order.common.Consts.BathEnum;
import com.taketoritei.order.common.DateUtil;
import com.taketoritei.order.jooq.tables.records.DReserveBathRecord;
import com.taketoritei.order.reserve.bath.form.AdminBathForm;
import com.taketoritei.order.reserve.bath.form.AdminBathUsageForm;
import com.taketoritei.order.reserve.bath.form.TimeForm;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminBathService extends BaseBathService {

	@Autowired
	MessageSource messages;

	@Autowired
	BathService bathService;

	/**
	 * 管理画面 指定した日時、時間、風呂の予約を取得
	 */
	public List<DReserveBathRecord> searchAdminBath(AdminBathForm form) {

		String rd = form.getReserveDate();
		String dd = rd.replace("-", "");

		return jooq //
				.selectFrom(D_RESERVE_BATH) //
				.where(D_RESERVE_BATH.RESERVE_DATE.eq(dd)) //
				.and(D_RESERVE_BATH.TIME_CD.eq(form.getTimeCd())) //
				.and(D_RESERVE_BATH.BATH_CD.eq(form.getBathCd())) //
				.and(D_RESERVE_BATH.DEL_FLG.eq(false)) //
				.fetch();
	}


	/**
	 * 管理画面 予約キャンセル
	 */
	public int cancelAdminBath(AdminBathUsageForm form) {

		return jooq //
				.update(D_RESERVE_BATH) //
				.set(D_RESERVE_BATH.DEL_FLG, true) //
				.set(D_RESERVE_BATH.LAST_DATE, getLastDate()) //
				.set(D_RESERVE_BATH.LAST_USER, getLastUser()) //
				.where(D_RESERVE_BATH.RESERVE_DATE.eq(form.getReserveDate())) //
				.and(D_RESERVE_BATH.TIME_CD.eq(form.getTimeCd())) //
				.and(D_RESERVE_BATH.BATH_CD.eq(form.getBathCd())) //
				.and(D_RESERVE_BATH.ROOM_NO.eq(form.getRoomNo())) //
				.execute(); //
	}

	/**
	 * 管理画面 予約登録
	 * @throws Exception
	 */
	public int insertAdminBath(AdminBathForm form, String token) {

		// すでに予約されていればキャンセル
		jooq //
			.update(D_RESERVE_BATH) //
			.set(D_RESERVE_BATH.DEL_FLG, true) //
			.set(D_RESERVE_BATH.LAST_DATE, getLastDate()) //
			.set(D_RESERVE_BATH.LAST_USER, getLastUser()) //
			.where(D_RESERVE_BATH.RESERVE_DATE.eq(form.getReserveDate().replace("-", ""))) //
			.and(D_RESERVE_BATH.TIME_CD.eq(form.getTimeCd())) //
			.and(D_RESERVE_BATH.BATH_CD.eq(form.getBathCd())) //
			.and(D_RESERVE_BATH.DEL_FLG.eq(false)) //
			.execute(); //

		int count = bathService.getReservedBathCount(token);

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
						form.getReserveDate().replace("-", ""), //
						form.getTimeCd(), //
						form.getBathCd(), //
						form.getRoomNo(), //
						count,
						token,
						false, //
						getLastDate(), //
						getLastUser()) //
				.execute();
	}

	/**
	 * 管理画面 予約状況の取得API
	 */
	public JSONArray getReserveBathJson(String reserveDate) {

		// 貸切風呂予約を取得
		Result<DReserveBathRecord> bathRecs = jooq.selectFrom(D_RESERVE_BATH) //
				.where(D_RESERVE_BATH.RESERVE_DATE.eq(reserveDate)) //
				.and(D_RESERVE_BATH.DEL_FLG.eq(false)) //
				.orderBy(D_RESERVE_BATH.TIME_CD.asc()) //
				.fetch();

		// 選択可能なm_timeの全件を取得
		List<TimeForm> timeList = getSelectableTimeList();

		// 貸切風呂風呂
		BathEnum[] bathEnum = BathEnum.values();

		// 現在時間の時間
		String hm = DateUtil.getHmForTimeCode();


		String businessDate = DateUtil.getBussinessDateYmd();

		// jsonを生成
		// --------------------------------------------------------------
		JSONArray jsonArray = new JSONArray();
		for (TimeForm time : timeList) {

			JSONObject timeJson = new JSONObject();
			timeJson.put("time_cd", time.getTimeCd());
			timeJson.put("time_nm", time.getNext() + time.getDispText());
			timeJson.put("date_range", (Integer.parseInt(businessDate) - Integer.parseInt(reserveDate)));
			timeJson.put("over_time", Integer.parseInt(time.getHmFrom()) <= Integer.parseInt(hm));
			timeJson.put("bath_type", time.getBathType());

			for (BathEnum bath : bathEnum) {
				String roomNo = "";
				int reserveCount = 0;

				// 予約済みの部屋番号を取得
				for (DReserveBathRecord bathRec : bathRecs) {
					if (bathRec.getReserveDate().equals(reserveDate) //
							&& bathRec.getTimeCd().equals(time.getTimeCd()) //
							&& bathRec.getBathCd().equals(bath.getCode())) {

						roomNo = bathRec.getRoomNo();
						reserveCount = bathRec.getReserveCount() == null ? 0 : bathRec.getReserveCount();
						break;
					}
				}
				timeJson.put("bath_" + bath.getCode() + "_room_no", roomNo);
				timeJson.put("bath_" + bath.getCode() + "_count", reserveCount);
			}
			jsonArray.put(timeJson);
		}

		return jsonArray;
	}

}
