package com.taketoritei.order.omiyage.service;

import static com.taketoritei.order.jooq.tables.DOrder.*;
import static com.taketoritei.order.jooq.tables.DOrderLang.*;
import static org.jooq.impl.DSL.*;

import java.io.File;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.jooq.Record;
import org.jooq.Record6;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taketoritei.order.common.Consts.LangEnum;
import com.taketoritei.order.common.FileUtil;
import com.taketoritei.order.common.JasperGen;
import com.taketoritei.order.common.service.BaseService;
import com.taketoritei.order.jooq.tables.records.DOrderRecord;
import com.taketoritei.order.jooq.tables.records.MRoomRecord;
import com.taketoritei.order.login.service.UserService;
import com.taketoritei.order.omiyage.entity.AdminOmiyageReportCheckList;
import com.taketoritei.order.omiyage.entity.AdminOmiyageReportInvoice;
import com.taketoritei.order.omiyage.entity.AdminOmiyageReportInvoiceMeisai;
import com.taketoritei.order.omiyage.form.AdminOmiyageInputForm;
import com.taketoritei.order.omiyage.form.AdminOmiyageMasterForm;
import com.taketoritei.order.omiyage.form.AdminOmiyageOrderPdfDownloadForm;
import com.taketoritei.order.room.service.RoomService;

import net.sf.jasperreports.engine.JasperPrint;

@Service
@Transactional(rollbackFor = Exception.class)
public class AdminOmiyageService extends BaseService {

	@Autowired
	private OmiyageMasterService omiyageMasterService;

	@Autowired
	private UserService userService;

	@Autowired
	private RoomService roomService;

	@Autowired
	private Environment env;

	@Autowired
	private JasperGen gen;

	/**
	 * ユーザーの注文しているお土産を全件取得
	 * @param omiyageId
	 * @param price
	 * @param token
	 * @param name
	 * @param lang
	 * @return
	 */
	public JSONArray getOmiyageListJson() {

		JSONArray jsonArray = new JSONArray();

		// 部屋一覧を取得
		List<MRoomRecord> roomList = roomService.getRoomList();
		for (MRoomRecord room : roomList) {
			jsonArray.put(this.getOmiyageJsonByRoom(room.getRoomNo()));
		}
		return jsonArray;
	}

	/**
	 * ユーザーの注文しているお土産を取得
	 * @param omiyageId
	 * @param price
	 * @param token
	 * @param name
	 * @param lang
	 * @return
	 */
	public JSONArray getOmiyageJsonByRoom(String roomNo) {

		JSONArray jsonArray = new JSONArray();

		// 客室Noからトークンを取得
		Record roomUser = userService.getUserByRoom(roomNo, false);
		if (null == roomUser) {
			return jsonArray;
		}

		List<Record6<Integer,String,Integer,Integer,String,BigDecimal>> result = jooq
			.select(min(D_ORDER.ORDER_ID).as(D_ORDER.ORDER_ID), D_ORDER.TOKEN, D_ORDER.OMIYAGE_ID, D_ORDER.PRICE, D_ORDER_LANG.NAME, sum(D_ORDER.NUM).as(D_ORDER.NUM))
			.from(D_ORDER.join(D_ORDER_LANG).on(D_ORDER.ORDER_ID.eq(D_ORDER_LANG.ORDER_ID)))
			.where(D_ORDER.DEL_FLG.eq(false))
			.and(D_ORDER.TOKEN.eq(roomUser.get("token").toString()))
			.and(D_ORDER_LANG.LANGUAGE.eq(LangEnum.LANG_JP.getCode()))
			.groupBy(D_ORDER.TOKEN, D_ORDER.OMIYAGE_ID, D_ORDER.PRICE, D_ORDER_LANG.NAME)
			.fetch();

		// jsonに変換
		for (Record6<Integer,String,Integer,Integer,String,BigDecimal> rec : result) {
			JSONObject omiyageJson = new JSONObject();
			omiyageJson.put("order_id", rec.get("order_id"));
			omiyageJson.put("token", rec.get("token"));
			omiyageJson.put("omiyage_id", rec.get("omiyage_id"));
			omiyageJson.put("price", rec.get("price"));
			omiyageJson.put("name", rec.get("name"));
			omiyageJson.put("num", rec.get("num"));
			omiyageJson.put("room_no", roomNo);
			jsonArray.put(omiyageJson);
		}
		return jsonArray;
	}

	/**
	 * ユーザーの注文しているお土産を取得
	 * @param omiyageId
	 * @param price
	 * @param token
	 * @param name
	 * @param lang
	 * @return
	 */
	public List<Record> getOmiyageOrderByKey(String token, int omiyageId, int price, String name, String lang) {

		 return jooq
			.select(D_ORDER.fields())
			.from(D_ORDER.join(D_ORDER_LANG).on(D_ORDER.ORDER_ID.eq(D_ORDER_LANG.ORDER_ID)))
			.where(D_ORDER.DEL_FLG.eq(false))
			.and(D_ORDER.TOKEN.eq(token))
			.and(D_ORDER.OMIYAGE_ID.eq(omiyageId))
			.and(D_ORDER.PRICE.eq(price))
			.and(D_ORDER_LANG.NAME.eq(name))
			.and(D_ORDER_LANG.LANGUAGE.eq(lang))
			.fetch();
	}

	/**
	 * お土産注文の登録
	 * @param form
	 */
	public String registOmiyageOrderByform(AdminOmiyageInputForm form) {

		String error = "";

		String roomNo = form.getRoomNo();
		int omiyageId = Integer.parseInt(form.getOmiyageId());

		// 客室Noからトークンを取得
		Record roomUser = userService.getUserByRoom(roomNo, false);
		if (null == roomUser) {
			error = "部屋のログイン情報が作成されていません。客室用QRコードを発行してください。";
			return error;
		}

		// 商品IDから商品を取得
		AdminOmiyageMasterForm adminOmiyageMasterForm = omiyageMasterService.getOmiyageMasterForm(omiyageId);
		if (null == adminOmiyageMasterForm.getOmiyageId()) {
			error = "入力されたお土産は登録されていません。";
			return error;
		}

		String token = roomUser.get("token").toString();
		int price = Integer.parseInt(adminOmiyageMasterForm.getPrice());
		String nameJa = adminOmiyageMasterForm.getJa().getName();
		String nameEn = adminOmiyageMasterForm.getEn().getName();
		String nameZhCn = adminOmiyageMasterForm.getZhCn().getName();
		String nameZhTw = adminOmiyageMasterForm.getZhTw().getName();
		String nameKo = adminOmiyageMasterForm.getKo().getName();

		// 同じ商品でも新規行を追加
		DOrderRecord rec = jooq
			.insertInto(
				D_ORDER,
				D_ORDER.TOKEN,
				D_ORDER.OMIYAGE_ID,
				D_ORDER.PRICE,
				D_ORDER.NUM,
				D_ORDER.DEL_FLG,
				D_ORDER.LAST_DATE,
				D_ORDER.LAST_USER)
			.values(
				token,
				omiyageId,
				price,
				Integer.parseInt(form.getNum()),
				false,
				getLastDate(),
				getLastUser())
			.returning(D_ORDER.ORDER_ID)
			.fetchOne();

		// 商品マスタの商品名を追加
		int orderId = rec.getOrderId();
		insertOrderLang(orderId, LangEnum.LANG_JP.getCode(), nameJa);
		insertOrderLang(orderId, LangEnum.LANG_EN.getCode(), nameEn);
		insertOrderLang(orderId, LangEnum.LANG_CN.getCode(), nameZhCn);
		insertOrderLang(orderId, LangEnum.LANG_TW.getCode(), nameZhTw);
		insertOrderLang(orderId, LangEnum.LANG_KO.getCode(), nameKo);

		return error;
	}

	private void insertOrderLang(int orderId, String lang, String name) {
		jooq
		.insertInto(
			D_ORDER_LANG,
			D_ORDER_LANG.ORDER_ID,
			D_ORDER_LANG.LANGUAGE,
			D_ORDER_LANG.NAME
		).values(
			orderId,
			lang,
			name
		).execute();
	}


	/**
	 * 客室で注文を削除（管理画面用）
	 * @param orderId
	 */
	public void deleteOmiyageOrderRoom(int orderId) {

		// 注文情報を取得
		Record record = jooq
			.selectFrom(D_ORDER.join(D_ORDER_LANG).on(D_ORDER.ORDER_ID.eq(D_ORDER_LANG.ORDER_ID)))
			.where(D_ORDER.ORDER_ID.eq(orderId))
			.and(D_ORDER_LANG.LANGUAGE.eq(LangEnum.LANG_JP.getCode()))
			.fetchOne();

		// 取得したトークンの客室番号を取得
		String roomNo = userService.getRoomNoByToken(record.get("token").toString());

		// 迷子注文を個数まとめずに取得
		List<Record> orderRecord = this.getOmiyageLostOrder(LangEnum.LANG_JP.getCode(), roomNo, false);

		// 迷子注文を削除
		for (Record rec : orderRecord) {
			jooq.update(D_ORDER)
			.set(D_ORDER.DEL_FLG, true)
			.where(D_ORDER.ORDER_ID.eq(Integer.parseInt(rec.get("order_id").toString())))
			.execute();
		}
	}

	/**
	 * 注文を削除
	 * @param orderId
	 */
	public void deleteOmiyageOrder(int orderId) {

		// 注文情報を取得
		Record record = jooq
			.selectFrom(D_ORDER.join(D_ORDER_LANG).on(D_ORDER.ORDER_ID.eq(D_ORDER_LANG.ORDER_ID)))
			.where(D_ORDER.ORDER_ID.eq(orderId))
			.and(D_ORDER_LANG.LANGUAGE.eq(LangEnum.LANG_JP.getCode()))
			.fetchOne();

		List<Record> orderRecord = this.getOmiyageOrderByKey(
			record.get("token").toString(),
			Integer.parseInt(record.get("omiyage_id").toString()),
			Integer.parseInt(record.get("price").toString()),
			record.get("name").toString(),
			LangEnum.LANG_JP.getCode()
		);

		for (Record rec : orderRecord) {
			jooq.update(D_ORDER)
			.set(D_ORDER.DEL_FLG, true)
			.where(D_ORDER.ORDER_ID.eq(Integer.parseInt(rec.get("order_id").toString())))
			.execute();
		}
	}


	/**
	 * 迷子注文データを取得
	 * 現在のユーザー以外の有効期限が切れていないユーザーデータに紐付くお土産注文を取得
	 * @return
	 */
	public List<Record> getOmiyageLostOrder(String lang, String roomNo, boolean group) {

		// 現在日時を取得
		Timestamp now = new Timestamp(System.currentTimeMillis());

		String select = "";
		if (group) {
			select = "   max(o.order_id) as order_id " +
					"    , o.omiyage_id " +
					"    , o.price " +
					"    , sum(o.num) as num " +
					"    , max(o.last_date) as last_date " +
					"    , usr.room_no " +
					"    , lg.name ";
		} else {
			select = "   o.order_id " +
					"    , o.omiyage_id " +
					"    , o.price " +
					"    , o.num " +
					"    , o.last_date " +
					"    , usr.room_no " +
					"    , lg.name ";
		}

		String sql =
			" select " + select +
			" from " +
			" ( " +
			"    select" +
			"        u.room_no" +
			"        , u.token " +
			"    from" +
			"        m_room r " +
			"        inner join ( " +
			"            select" +
			"                ROW_NUMBER() OVER ( " +
			"                    PARTITION BY" +
			"                        a.room_no " +
			"                    ORDER BY" +
			"                        (a.last_date) DESC" +
			"                ) AS rank" +
			"                , a.* " +
			"            from" +
			"                m_user a" +
			"        ) as u " +
			"            on r.room_no = u.room_no " +
			"            and u.rank <> 1 " +
			"            and u.to_dt >= ? " +
			"    group by" +
			"        u.room_no" +
			"        , u.token" +
			" ) as usr " +
			" inner join d_order o " +
			"    on o.token = usr.token " +
			"    and o.del_flg = false " +
			" inner join d_order_lang lg " +
			"    on o.order_id = lg.order_id " +
			"    and lg.language = ? " +
			" where" +
			"    EXISTS ( " +
			"        select" +
			"            u.room_no" +
			"            , u.token " +
			"        from" +
			"            m_room r " +
			"            inner join ( " +
			"                select" +
			"                    ROW_NUMBER() OVER ( " +
			"                        PARTITION BY" +
			"                            a.room_no " +
			"                        ORDER BY" +
			"                            (a.last_date) DESC" +
			"                    ) AS rank" +
			"                    , a.* " +
			"                from" +
			"                    m_user a" +
			"            ) as u " +
			"                on r.room_no = u.room_no " +
			"                and u.rank = 1 " +
			"                and u.to_dt >= ? " +
			"                and usr.room_no = u.room_no " +
			"                and usr.token <> u.token" +
			"    )";
		// 部屋指定
		if (null != roomNo) {
			sql += " and usr.room_no = ?";
		}
		if (group) {
			sql += " group by usr.room_no, o.omiyage_id, o.price, lg.name";
		}
		sql += " order by room_no, last_date";
		return jooq.resultQuery(sql, now, lang, now, roomNo).fetch();
	}

	/**
	 * 迷子注文データをjsonで取得
	 * 現在のユーザー以外の有効期限が切れていないユーザーデータに紐付くお土産注文を取得
	 * @return
	 */
	public JSONArray getOmiyageLostOrderJson(String lang, String roomNo) {

		JSONArray jsonArray = new JSONArray();

		List<Record> lostOrder = this.getOmiyageLostOrder(lang, roomNo, true);
		for (Record rec : lostOrder) {
			JSONObject omiyageJson = new JSONObject();
			omiyageJson.put("order_id", rec.get("order_id"));
			omiyageJson.put("omiyage_id", rec.get("omiyage_id"));
			omiyageJson.put("price", rec.get("price"));
			omiyageJson.put("name", rec.get("name"));
			omiyageJson.put("num", rec.get("num"));
			omiyageJson.put("room_no", rec.get("room_no"));
			jsonArray.put(omiyageJson);
		}
		return jsonArray;
	}

	/**
	 * 管理画面お土産注文PDF作成
	 * @param form
	 * @return
	 * @throws Exception
	 */
	public byte[] createOmiyagePdf(AdminOmiyageOrderPdfDownloadForm form) throws Exception {

		// PDFファイル読み込み用
		byte[] fileContent = null;

		// yamlから取得
		String outputPdfDir = env.getRequiredProperty("omiyage.report.output-pdf-dir");
		String outputPdfFileTmp = "";

		Map<String, Object> params = new HashMap<String, Object>();
		String templateName = "";
		JasperPrint print = null;

		// PDFを作成
		if ("invoice".equals(form.getPdfType())) {

			// テンプレート名
			templateName = "invoice";

			// 納品書
			outputPdfFileTmp = env.getRequiredProperty("omiyage.report.output-invoice-file");

			// パラメーター
			params.put("title", "納品書");
			params.put("date", new SimpleDateFormat("yyyy年MM月dd日").format(new Date()));
			params.put("imgPath", env.getRequiredProperty("omiyage.report.img-dir"));
			params.put("jasperDir", env.getRequiredProperty("omiyage.report.jasper-dir"));

			// フィールドに使うリストを取得
			List<AdminOmiyageReportInvoice> field = new ArrayList<AdminOmiyageReportInvoice>();
			field = getOmiyageReportInvoice(form);

			// PDFを作成
			print = gen.getJasperPrint(templateName, field, params);

		} else if ("check_list".equals(form.getPdfType())) {

			// テンプレート名
			templateName = "checklist";

			// チェックリスト
			outputPdfFileTmp = env.getRequiredProperty("omiyage.report.output-checklist-file");

			// パラメーター
			params.put("title", "お土産チェックリスト");
			params.put("date", new SimpleDateFormat("yyyy年MM月dd日 HH時mm分").format(new Date()) + "発行");

			// フィールドに使うリストを取得
			List<AdminOmiyageReportCheckList> field = new ArrayList<AdminOmiyageReportCheckList>();
			field = getOmiyageReportCheckList(form);

			// PDFを作成
			print = gen.getJasperPrint(templateName, field, params);
		}
		FileUtil.createFolder(outputPdfDir);
		gen.outputPdf(print, outputPdfDir + outputPdfFileTmp);

		// 作成したPDFを読み込み
		if (new File(outputPdfDir + outputPdfFileTmp).exists()) {
			fileContent = FileUtil.convertToByteArray(outputPdfDir + outputPdfFileTmp);
		} else {
			return null;
		}
		return fileContent;
	}


	/**
	 * 管理画面お土産注文チェックリストPDFデータ取得
	 * @param form
	 * @return
	 */
	public List<AdminOmiyageReportCheckList> getOmiyageReportCheckList(AdminOmiyageOrderPdfDownloadForm form) {

		List<AdminOmiyageReportCheckList> result = new ArrayList<AdminOmiyageReportCheckList>();

		// 部屋一覧を取得
		List<MRoomRecord> roomList = roomService.getRoomList();
		for (MRoomRecord room : roomList) {

			// 客室Noからトークンを取得
			Record roomUser = userService.getUserByRoom(room.getRoomNo(), false);
			if (null == roomUser) {
				continue;
			}

			// 部屋ごとのお土産注文を取得
			List<Record6<Integer,String,Integer,Integer,String,BigDecimal>> record = jooq
				.select(min(D_ORDER.ORDER_ID).as(D_ORDER.ORDER_ID), D_ORDER.TOKEN, D_ORDER.OMIYAGE_ID, D_ORDER.PRICE, D_ORDER_LANG.NAME, sum(D_ORDER.NUM).as(D_ORDER.NUM))
				.from(D_ORDER.join(D_ORDER_LANG).on(D_ORDER.ORDER_ID.eq(D_ORDER_LANG.ORDER_ID)))
				.where(D_ORDER.DEL_FLG.eq(false))
				.and(D_ORDER.TOKEN.eq(roomUser.get("token").toString()))
				.and(D_ORDER_LANG.LANGUAGE.eq(LangEnum.LANG_JP.getCode()))
				.groupBy(D_ORDER.TOKEN, D_ORDER.OMIYAGE_ID, D_ORDER.PRICE, D_ORDER_LANG.NAME)
				.fetch();

			// 格納する
			for (Record6<Integer,String,Integer,Integer,String,BigDecimal> rec : record) {

				AdminOmiyageReportCheckList omiyage = new AdminOmiyageReportCheckList();
				omiyage.setRoomNo(room.getRoomNo());
				omiyage.setOmiyageName(rec.get("name").toString());
				omiyage.setNum(rec.get("num").toString());
				result.add(omiyage);
			}
		}
		return result;
	}


	/**
	 * 管理画面お土産注文納品書PDFデータ取得
	 * @param form
	 * @return
	 */
	public List<AdminOmiyageReportInvoice> getOmiyageReportInvoice(AdminOmiyageOrderPdfDownloadForm form) {

		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMdd");
		List<AdminOmiyageReportInvoice> invoiceList = new ArrayList<AdminOmiyageReportInvoice>();

		// 部屋一覧を取得
		List<MRoomRecord> roomList = roomService.getRoomList();
		for (MRoomRecord room : roomList) {

			// 客室Noからトークンを取得
			Record roomUser = userService.getUserByRoom(room.getRoomNo(), false);
			if (null == roomUser) {
				continue;
			}

			// 部屋ごとのお土産注文を取得
			List<Record6<Integer,String,Integer,Integer,String,BigDecimal>> record = jooq
				.select(min(D_ORDER.ORDER_ID).as(D_ORDER.ORDER_ID), D_ORDER.TOKEN, D_ORDER.OMIYAGE_ID, D_ORDER.PRICE, D_ORDER_LANG.NAME, sum(D_ORDER.NUM).as(D_ORDER.NUM))
				.from(D_ORDER.join(D_ORDER_LANG).on(D_ORDER.ORDER_ID.eq(D_ORDER_LANG.ORDER_ID)))
				.where(D_ORDER.DEL_FLG.eq(false))
				.and(D_ORDER.TOKEN.eq(roomUser.get("token").toString()))
				.and(D_ORDER_LANG.LANGUAGE.eq(LangEnum.LANG_JP.getCode()))
				.groupBy(D_ORDER.TOKEN, D_ORDER.OMIYAGE_ID, D_ORDER.PRICE, D_ORDER_LANG.NAME)
				.fetch();

			// 部屋ごとに格納する
			List<AdminOmiyageReportInvoiceMeisai> meisaiList = new ArrayList<AdminOmiyageReportInvoiceMeisai>();
			int totalPrice = 0;
			int totalNum = 0;
			for (Record6<Integer,String,Integer,Integer,String,BigDecimal> rec : record) {

				int shokei = Integer.parseInt(rec.get("num").toString()) * Integer.parseInt(rec.get("price").toString());

				// 各部屋の明細のデータを格納
				AdminOmiyageReportInvoiceMeisai meisai = new AdminOmiyageReportInvoiceMeisai();
				meisai.setOmiyageName(rec.get("name").toString());
				meisai.setNum(rec.get("num").toString());
				meisai.setPrice(rec.get("price").toString());
				meisai.setShokeiPrice((NumberFormat.getCurrencyInstance()).format(shokei));
				meisaiList.add(meisai);

				// 部屋ごとの合計金額
				totalPrice += shokei;
				totalNum += Integer.parseInt(rec.get("num").toString());
			}
			AdminOmiyageReportInvoice invoice = new AdminOmiyageReportInvoice();
			invoice.setRoomNo(room.getRoomNo());
			invoice.setList(meisaiList);
			invoice.setInvoiceNo(sdf.format(new Date()) + room.getRoomNo());
			invoice.setTotalPrice((NumberFormat.getCurrencyInstance()).format(totalPrice));
			invoice.setTotalNum(totalNum);
			invoiceList.add(invoice);
		}
		return invoiceList;
	}
}
