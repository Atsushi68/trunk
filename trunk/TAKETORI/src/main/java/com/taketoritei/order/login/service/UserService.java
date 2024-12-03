package com.taketoritei.order.login.service;

import static com.taketoritei.order.jooq.tables.MUser.*;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.taketoritei.order.common.FileUtil;
import com.taketoritei.order.common.JasperGen;
import com.taketoritei.order.common.service.BaseService;
import com.taketoritei.order.common.validation.ValidateUtil;
import com.taketoritei.order.config.IllegalException;
import com.taketoritei.order.jooq.tables.records.MUserRecord;
import com.taketoritei.order.login.entity.ReportLogin;
import com.taketoritei.order.room.form.AdminRoomForm;
import com.taketoritei.order.room.form.AdminRoomListForm;

import net.sf.jasperreports.engine.JasperPrint;

/**
 * ユーザのサービスクラス
 */
@Service
@Transactional(rollbackFor = Exception.class)
public class UserService extends BaseService {

	@Autowired
	private Environment env;

	@Autowired
	private JasperGen gen;

	/**
	 * トークンを作成
	 */
	public String createToken() {
		String token = "";
		// ハイフンは除去
		token = UUID.randomUUID().toString().replace("-", "");

		// 一応同じものが無いかチェック
		MUserRecord chk = jooq.selectFrom(M_USER).where(M_USER.TOKEN.eq(token)).fetchOne();
		if (null != chk) {
			// 再起
			token = createToken();
		}
		return token;
	}

	/**
	 * ログイン時、ユーザー取得
	 */
	public Record getUserByLogin(String roomNo, String token) {

		// 現在日時を取得
		Timestamp now = new Timestamp(System.currentTimeMillis());

		String sql = "select" +
				"    r.room_no" +
				"    , r.floor" +
				"    , u.token" +
				"    , u.from_dt" +
				"    , u.to_dt" +
				"    , u.action_name" +
				"    , u.del_flg" +
				"    , u.last_date" +
				"    , u.last_user " +
				"from" +
				"    m_room r " +
				"    left join ( " +
				"        select" +
				"            ROW_NUMBER() OVER ( " +
				"                PARTITION BY" +
				"                    a.room_no " +
				"                ORDER BY" +
				"                    (a.last_date) DESC" +
				"            ) AS rank" +
				"            , a.* " +
				"        from" +
				"            m_user a" +
				"    ) as u " +
				"        on r.room_no = u.room_no " +
				"        and u.rank = 1 " +
				"where " +
				"    r.room_no = ? and " +
				"    u.token = ? and " +
				"    u.from_dt <= ? and " +
				"    u.to_dt >= ? and " +
				"    u.del_flg = ?";

		return jooq.resultQuery(sql, roomNo, token, now, now, false).fetchOne();
	}

	/**
	 * 部屋の有効なログイン情報を取得
	 * @return
	 */
	public Record getUserByRoom(String roomNo, Boolean delFlg) {

		// 現在日時を取得
		Timestamp now = new Timestamp(System.currentTimeMillis());

		String sql = "select" +
				"    r.room_no" +
				"    , r.floor" +
				"    , u.token" +
				"    , u.from_dt" +
				"    , u.to_dt" +
				"    , u.action_name" +
				"    , u.del_flg" +
				"    , u.last_date" +
				"    , u.last_user " +
				"from" +
				"    m_room r " +
				"    left join ( " +
				"        select" +
				"            ROW_NUMBER() OVER ( " +
				"                PARTITION BY" +
				"                    a.room_no " +
				"                ORDER BY" +
				"                    (a.last_date) DESC" +
				"            ) AS rank" +
				"            , a.* " +
				"        from" +
				"            m_user a" +
				"    ) as u " +
				"        on r.room_no = u.room_no " +
				"        and u.rank = 1 " +
				"where " +
				"    r.room_no = ? and " +
				"    u.to_dt >= ? ";

		if (null != delFlg) {
			sql += " and u.del_flg = ? ";
		}
		return jooq.resultQuery(sql, roomNo, now, delFlg).fetchOne();
	}


	/**
	 * トークンから客室番号を取得
	 * @return
	 */
	public String getRoomNoByToken(String token) {

		String roomNo = "";

		MUserRecord rec = jooq.selectFrom(M_USER).where(M_USER.TOKEN.eq(token)).limit(1).fetchOne();
		if (null != rec) {
			roomNo = rec.getRoomNo();
		}
		return roomNo;
	}


	/**
	 * 部屋のログイン情報が有効かチェック
	 * @param token
	 * @return
	 */
	public boolean checkUserLogin(String token) {

		boolean result = false;


		return result;
	}

	/**
	 * 登録前のバリデーション
	 * @param adminRoomForm
	 * @return
	 */
	public List<String> validationForm(AdminRoomForm adminRoomForm) {

		List<String> check = new ArrayList<String>();

		// 客室番号
		String roomNo = adminRoomForm.getRoomNo();

		// チェックされたものを保存
		adminRoomForm.setCheck(roomNo);

		// 日付（文字列）
		String fromDt = adminRoomForm.getFromDtStr();
		String toDt = adminRoomForm.getToDtStr();

		// 入力チェック
		if (StringUtils.isEmpty(fromDt)) {
			// 空かチェック
			check.add("客室" + roomNo + "の有効期限fromを入力してください。");
		} else if (ValidateUtil.isDate(fromDt) == false) {
			// 有効な日付形式かチェック
			check.add("客室" + roomNo + "の有効期限fromが不正な日付です。");
		}
		if (StringUtils.isEmpty(toDt)) {
			// 空かチェック
			check.add("客室" + roomNo + "の有効期限toを入力してください。");
		} else if (ValidateUtil.isDate(toDt) == false) {
			// 有効な日付形式かチェック
			check.add("客室" + roomNo + "の有効期限toが不正な日付です。");
		}

		if (check.size() == 0) {
			if (ValidateUtil.isDateRange(fromDt, toDt, "yyyy-MM-dd") == false) {
				check.add("客室" + roomNo + "の日付範囲が正しくありません。");
			}
		}

		return check;
	}

	/**
	 * ユーザー情報を新規登録
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public List<ReportLogin> registUserByForm(AdminRoomListForm formList) throws Exception {

		// yamlから取得
		String loginUrl = env.getRequiredProperty("room.login-url");
		String qrCodeDir = env.getRequiredProperty("room.qr-code-dir");
		int loginToHour = Integer.parseInt(env.getRequiredProperty("room.login-to-hour"));
		String outputNewDir = env.getRequiredProperty("room.report.output-new-dir");
		String outputLoginFile = env.getRequiredProperty("room.report.output-login-file");

		// レポート作成用
		List<ReportLogin> reportLoginList = new ArrayList<ReportLogin>();

		// 現在日時を取得
		Timestamp now = new Timestamp(System.currentTimeMillis());

		// 時間加算用
		Calendar calendar = Calendar.getInstance();

		// 1行ずつ新規登録
		//int count = 1;
		for (AdminRoomForm adminRoomForm : formList.getAdminRoomListForm()) {

			// チェックボックスにチェックがあるもののみ対象
			if (!StringUtils.isEmpty(adminRoomForm.getCheck())) {

				String roomNo = adminRoomForm.getRoomNo();
				String fromDtStr = adminRoomForm.getFromDtStr();
				String toDtStr = adminRoomForm.getToDtStr();
				String token = this.createToken();

				// 時間は入力された日付の〇時にセット
				SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
				calendar.setTimeInMillis(sdf.parse(fromDtStr).getTime());
				calendar.add(Calendar.HOUR_OF_DAY, loginToHour);
				Timestamp fromDt = new Timestamp(calendar.getTimeInMillis());
				calendar.setTimeInMillis(sdf.parse(toDtStr).getTime());
				calendar.add(Calendar.HOUR_OF_DAY, loginToHour);
				Timestamp toDt = new Timestamp(calendar.getTimeInMillis());

				// 登録
				jooq
					.insertInto(M_USER, M_USER.ROOM_NO, M_USER.TOKEN, M_USER.FROM_DT, M_USER.TO_DT,
						M_USER.ACTION_NAME, M_USER.DEL_FLG, M_USER.LAST_DATE, M_USER.LAST_USER)
					.values(roomNo, token, fromDt, toDt, "新規", false, now, getLastUser())
					.execute();

				// QAコードを作成
				FileUtil.createFolder(qrCodeDir);
				String filePath = qrCodeDir + roomNo + ".png";
				createQrCode(loginUrl + "?room=" + roomNo + "&token=" + token, filePath);

				// PDFに出力するフィールド用に格納する
				SimpleDateFormat pdfFormat = new SimpleDateFormat("yyyy年MM月dd日朝HH時まで");
				// 偶数か奇数か
				// if (count % 2 == 0) {
					//reportLoginList.get(reportLoginList.size() - 1).setRoomNo2(roomNo);
					//reportLoginList.get(reportLoginList.size() - 1).setToDtStr2(pdfFormat.format(toDt));

				// } else {
					ReportLogin reportLogin = new ReportLogin();
					reportLogin.setRoomNo1(roomNo);
					reportLogin.setToDtStr1(pdfFormat.format(toDt));
					reportLoginList.add(reportLogin);
				// }
				//count++;
			}
		}

		// PDFをダウンロード
		// パラメーター
		Map<String, Object> params = new HashMap<String, Object>();
		params.put("title", "ログイン情報発行");
		params.put("date", new SimpleDateFormat("yyyy年MM月dd日").format(new Date()) + "発行");
		params.put("imgPath", qrCodeDir);

		// フィールド
		JasperPrint print = gen.getJasperPrint("login", reportLoginList, params);

		// PDFをレスポンスへ設定
		FileUtil.createFolder(outputNewDir);
		gen.outputPdf(print, outputNewDir + outputLoginFile);

		return reportLoginList;
	}

	/**
	 * ユーザー情報を新規登録
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public String editUserByForm(AdminRoomListForm formList, String kbn, String roomNo) throws Exception {

		String message = "";
		Record record;

		// 区分によって処理を分ける
		if ("使用不可".equals(kbn)) {

			// 新規発行されている（有効なデータが存在する）か一応チェック
			record = this.getUserByRoom(roomNo, false);
			if (null == record) {
				// 通常無い
				throw new IllegalException("不正なアクセスです。");
			}
			// 区分によって登録
			this.editUser(record, kbn, true); // 使用不可に更新
			message = "客室" + roomNo + "を使用不可に更新しました。";

		} else if ("解除".equals(kbn)) {

			// 使用不可になっているか一応チェック
			record = this.getUserByRoom(roomNo, true);
			if (null == record) {
				// 通常無い
				throw new IllegalException("不正なアクセスです。");
			}
			// 区分によって登録
			this.editUser(record, kbn, false); // 使用不可に更新
			message = "客室" + roomNo + "を使用不可を解除しました。";

		} else if ("再発行".equals(kbn)) {

			// 新規発行されている（有効なデータが存在する）か一応チェック
			record = this.getUserByRoom(roomNo, false);
			if (null == record) {
				// 通常無い
				throw new IllegalException("不正なアクセスです。");
			}

			// 登録とQRコード作成とPDFを作成
			// yamlから取得
			String loginUrl = env.getRequiredProperty("room.login-url");
			String qrCodeDir = env.getRequiredProperty("room.qr-code-dir");
			String outputReDir = env.getRequiredProperty("room.report.output-re-dir");
			String outputLoginFile = env.getRequiredProperty("room.report.output-login-file");
			int loginToHour = Integer.parseInt(env.getRequiredProperty("room.login-to-hour"));

			// レポート作成用
			List<ReportLogin> reportLoginList = new ArrayList<ReportLogin>();

			// 現在日時を取得
			Timestamp now = new Timestamp(System.currentTimeMillis());

			// 時間加算用
			Calendar calendar = Calendar.getInstance();

			// 1行ずつ新規登録
			//int count = 1;
			for (AdminRoomForm adminRoomForm : formList.getAdminRoomListForm()) {

				// チェックボックスにチェックがあるもののみ対象
				if (!StringUtils.isEmpty(adminRoomForm.getCheck())) {

					roomNo = adminRoomForm.getRoomNo();
					String fromDtStr = adminRoomForm.getFromDtStr();
					String toDtStr = adminRoomForm.getToDtStr();
					String token = record.get("token").toString(); // 再発行時のトークンは変更しない

					// 時間は入力された日付の朝10時にセット
					SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
					calendar.setTimeInMillis(sdf.parse(fromDtStr).getTime());
					calendar.add(Calendar.HOUR_OF_DAY, loginToHour);
					Timestamp fromDt = new Timestamp(calendar.getTimeInMillis());
					calendar.setTimeInMillis(sdf.parse(toDtStr).getTime());
					calendar.add(Calendar.HOUR_OF_DAY, loginToHour);
					Timestamp toDt = new Timestamp(calendar.getTimeInMillis());

					// 登録
					jooq
						.insertInto(M_USER, M_USER.ROOM_NO, M_USER.TOKEN, M_USER.FROM_DT, M_USER.TO_DT,
							M_USER.ACTION_NAME, M_USER.DEL_FLG, M_USER.LAST_DATE, M_USER.LAST_USER)
						.values(roomNo, token, fromDt, toDt, kbn, false, now, getLastUser())
						.execute();

					// QAコードを作成
					FileUtil.createFolder(qrCodeDir);
					String filePath = qrCodeDir + roomNo + ".png";
					createQrCode(loginUrl + "?room=" + roomNo + "&token=" + token, filePath);

					// PDFに出力するフィールド用に格納する
					SimpleDateFormat pdfFormat = new SimpleDateFormat("yyyy年MM月dd日朝HH時まで");
					// 偶数か奇数か
					// if (count % 2 == 0) {
						// reportLoginList.get(reportLoginList.size() - 1).setRoomNo2(roomNo);
						// reportLoginList.get(reportLoginList.size() - 1).setToDtStr2(pdfFormat.format(toDt));

					// } else {
						ReportLogin reportLogin = new ReportLogin();
						reportLogin.setRoomNo1(roomNo);
						reportLogin.setToDtStr1(pdfFormat.format(toDt));
						reportLoginList.add(reportLogin);
					// }
					//count++;
				}
			}
			// パラメーター
			Map<String, Object> params = new HashMap<String, Object>();
			params.put("title", "ログイン情報発行");
			params.put("date", new SimpleDateFormat("yyyy年MM月dd日").format(new Date()) + "発行");
			params.put("imgPath", qrCodeDir);

			// フィールド
			JasperPrint print = gen.getJasperPrint("login", reportLoginList, params);

			// PDFを作成・保存
			FileUtil.createFolder(outputReDir);
			gen.outputPdf(print, outputReDir + outputLoginFile);

			message = "QRコードを再発行しました。ダウンロードボタンからダウンロードしてください。";
		}
		return message;
	}

	/**
	 * QRコードの作成
	 * @throws Exception
	 */
	private void createQrCode(String text, String filePath) throws Exception {

		// yamlから取得
		int loginQrCodeW = Integer.parseInt(env.getRequiredProperty("room.login-qr-code-w"));
		int loginQrCodeH = Integer.parseInt(env.getRequiredProperty("room.login-qr-code-h"));

		QRCodeWriter qrCodeWriter = new QRCodeWriter();
		BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, loginQrCodeW, loginQrCodeH);
		Path path = Paths.get(filePath);
		MatrixToImageWriter.writeToPath(bitMatrix, "PNG", path);
	}

	/**
	 * 更新する
	 * 更新レコードを追加
	 */
	public void editUser(Record record, String actionName, Boolean delFlg) {

		jooq
		.insertInto(M_USER, M_USER.ROOM_NO, M_USER.TOKEN, M_USER.FROM_DT, M_USER.TO_DT,
			M_USER.ACTION_NAME, M_USER.DEL_FLG, M_USER.LAST_DATE, M_USER.LAST_USER)
		.values(record.get("room_no").toString(), record.get("token").toString(), (Timestamp)record.get("from_dt"), (Timestamp)record.get("to_dt"), actionName, delFlg, getLastDate(), getLastUser())
		.execute();
	}
}