package com.taketoritei.order.omiyage.service;

import static com.taketoritei.order.jooq.tables.DCart.*;
import static com.taketoritei.order.jooq.tables.DCartLang.*;
import static com.taketoritei.order.jooq.tables.DOrder.*;
import static com.taketoritei.order.jooq.tables.DOrderLang.*;
import static com.taketoritei.order.jooq.tables.MOmiyage.*;
import static com.taketoritei.order.jooq.tables.VdCart.*;
import static com.taketoritei.order.jooq.tables.VdOrder.*;
import static org.jooq.impl.DSL.*;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.UUID;

import org.jooq.Record;
import org.jooq.Record6;
import org.jooq.Record7;
import org.jooq.SelectConditionStep;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taketoritei.order.common.Consts.LangEnum;
import com.taketoritei.order.common.Consts.OmiyageCategoryEnum;
import com.taketoritei.order.common.service.BaseService;
import com.taketoritei.order.jooq.tables.records.DCartLangRecord;
import com.taketoritei.order.jooq.tables.records.DCartRecord;
import com.taketoritei.order.jooq.tables.records.DOrderRecord;
import com.taketoritei.order.jooq.tables.records.VdCartRecord;
import com.taketoritei.order.omiyage.form.AdminOmiyageLangMasterForm;
import com.taketoritei.order.omiyage.form.AdminOmiyageMasterForm;
import com.taketoritei.order.omiyage.form.CartForm;
import com.taketoritei.order.omiyage.form.OrderForm;

@Service
@Transactional(rollbackFor = Exception.class)
public class SouvenirService extends BaseService {

	@Autowired
	MessageSource messages;

	@Autowired
	OmiyageMasterService omiyageMasterService;

	@Autowired
	Environment env;

	/**
	 * カート情報を取得
	 * @param locale
	 * @param token
	 * @param omiyageId
	 * @param price
	 * @param omiyageName
	 * @return
	 */
	public List<DCartRecord> getCartList(Locale locale, String token) {
		SelectConditionStep<DCartRecord> step = jooq
			.selectFrom(D_CART)
			.where(D_CART.TOKEN.eq(token));
		/*
		if (null != omiyageId) {
			step.and(VD_CART.OMIYAGE_ID.eq(omiyageId));
		}
		if (null != price) {
			step.and(VD_CART.PRICE.eq(price));
		}
		if (null != omiyageName) {
			step.and(VD_CART.NAME.eq(omiyageName));
		}
		*/
		return step.fetch();
	}

	/**
	 * カート情報を取得
	 * @param locale
	 * @param token
	 * @param omiyageId
	 * @param price
	 * @param omiyageName
	 * @return
	 */
	public List<DCartLangRecord> getCartLangList(int cartId) {
		return jooq
			.selectFrom(D_CART_LANG)
			.where(D_CART_LANG.CART_ID.eq(cartId))
			.fetch();
	}


	/**
	 * ビューからカート情報を取得
	 * @param locale
	 * @param token
	 * @param omiyageId
	 * @param price
	 * @param omiyageName
	 * @return
	 */
	public List<VdCartRecord> getViewCartList(Locale locale, String token, Integer omiyageId, Integer price, String omiyageName) {
		SelectConditionStep<VdCartRecord> step = jooq
			.selectFrom(VD_CART)
			.where(VD_CART.TOKEN.eq(token));

		if (null != omiyageId) {
			step.and(VD_CART.OMIYAGE_ID.eq(omiyageId));
		}
		if (null != price) {
			step.and(VD_CART.PRICE.eq(price));
		}
		if (null != omiyageName) {
			step.and(VD_CART.NAME.eq(omiyageName));
		}
		return step.fetch();
	}


	/**
	 * お土産大カテゴリーの取得
	 */
	public Map<String, String> getCategoryMap(Locale locale) {

		// TODO カテゴリー決まったら更新
		Map<String, String> categoryMap = new HashMap<String, String>();
		categoryMap.put(OmiyageCategoryEnum.ZAKKA.getCode(), messages.getMessage("omiyage.16", null, locale));
		categoryMap.put(OmiyageCategoryEnum.SHOKUHIN.getCode(), messages.getMessage("omiyage.17", null, locale));
		categoryMap.put(OmiyageCategoryEnum.JUICE.getCode(), messages.getMessage("omiyage.18", null, locale));

		return categoryMap;
	}

	/**
	 * カートの中身を取得
	 * 同じ商品はまとめる
	 * @param locale
	 * @param token
	 * @return
	 */
	public List<CartForm> getCartGroupList(Locale locale, String token, Integer omiyageId, Integer price, String omiyageName) {

		SelectConditionStep<Record7<String,Integer,Integer,String,BigDecimal,String,String>> step = jooq
			.select(
				VD_CART.TOKEN,
				VD_CART.OMIYAGE_ID,
				VD_CART.PRICE,
				VD_CART.NAME,
				sum(VD_CART.NUM).as(VD_CART.NUM),
				VD_CART.CART_TOKEN,
				M_OMIYAGE.IMAGE_EXT
			)
			.from(VD_CART.join(M_OMIYAGE).on(VD_CART.OMIYAGE_ID.eq(M_OMIYAGE.OMIYAGE_ID)))
			.where(VD_CART.LANGUAGE.eq(locale.getLanguage()))
			.and(VD_CART.TOKEN.eq(token));

		if (null != omiyageId) {
			step.and(VD_CART.OMIYAGE_ID.eq(omiyageId));
		}
		if (null != price) {
			step.and(VD_CART.PRICE.eq(price));
		}
		if (null != omiyageName) {
			step.and(VD_CART.NAME.eq(omiyageName));
		}
		List<Record7<String, Integer, Integer, String, BigDecimal, String, String>> result = step.groupBy(
				VD_CART.TOKEN,
				VD_CART.OMIYAGE_ID,
				VD_CART.PRICE,
				VD_CART.NAME,
				VD_CART.CART_TOKEN,
				M_OMIYAGE.IMAGE_EXT
		)
		.fetch();

		// formに格納
		List<CartForm> cartFormList = new ArrayList<CartForm>();
		for (Record ret : result) {
			CartForm cartForm = new CartForm();
			cartForm.setToken(ret.get("token").toString());
			cartForm.setOmiyageId(Integer.parseInt(ret.get("omiyage_id").toString()));
			cartForm.setPrice(Integer.parseInt(ret.get("price").toString()));
			cartForm.setName(ret.get("name").toString());
			cartForm.setNum(Integer.parseInt(ret.get("num").toString()));
			cartForm.setCartToken(ret.get("cart_token") == null ? null : ret.get("cart_token").toString());
			cartForm.setImageExt(ret.get("image_ext").toString());
			cartFormList.add(cartForm);
		}
		return cartFormList;
	}


	/**
	 * 購入済お土産情報を取得
	 * 同じお土産はまとめる
	 * @param locale
	 * @param token
	 * @return
	 */
	public List<OrderForm> getOrderGroupList(Locale locale, String token) {

		// 購入済お土産取得
		List<Record6<String,Integer,Integer,String,BigDecimal,String>> result = jooq
			.select(
				VD_ORDER.TOKEN,
				VD_ORDER.OMIYAGE_ID,
				VD_ORDER.PRICE,
				VD_ORDER.NAME,
				sum(VD_ORDER.NUM).as(VD_ORDER.NUM),
				M_OMIYAGE.IMAGE_EXT
			)
			.from(VD_ORDER.join(M_OMIYAGE).on(VD_ORDER.OMIYAGE_ID.eq(M_OMIYAGE.OMIYAGE_ID)))
			.where(VD_ORDER.DEL_FLG.eq(false))
			.and(VD_ORDER.TOKEN.eq(token))
			.and(VD_ORDER.LANGUAGE.eq(locale.getLanguage()))
			.groupBy(VD_ORDER.TOKEN, VD_ORDER.OMIYAGE_ID, VD_ORDER.PRICE, VD_ORDER.NAME, M_OMIYAGE.IMAGE_EXT)
			.fetch();

		// フォームに格納
		List<OrderForm> orderFormList = new ArrayList<OrderForm>();
		for (Record6<String, Integer, Integer, String, BigDecimal, String> res : result) {
			OrderForm order = new OrderForm();
			order.setToken(token);
			order.setOmiyageId(Integer.parseInt(res.get("omiyage_id").toString()));
			order.setPrice(Integer.parseInt(res.get("price").toString()));
			order.setName(res.get("name").toString());
			order.setNum(Integer.parseInt(res.get("num").toString()));
			order.setImageExt(res.get("image_ext").toString());
			orderFormList.add(order);
		}
		return orderFormList;
	}

	/**
	 * カートに追加する
	 * @param omiyageId
	 * @param num
	 */
	public void addCart(String token, int omiyageId, int price, int num) {

		// カートを更新したらカートキーを新しくセット
		String cartToken = updateCartToken(token);

		// カートテーブルに追加
		DCartRecord result =  jooq //
			.insertInto(
					D_CART, //
					D_CART.TOKEN, //
					D_CART.OMIYAGE_ID, //
					D_CART.PRICE, //
					D_CART.NUM, //
					D_CART.CART_TOKEN, //
					D_CART.DEL_FLG, //
					D_CART.LAST_DATE,
					D_CART.LAST_USER) //
			.values(
					token, //
					omiyageId, //
					price, //
					num, //
					cartToken, //
					false, //
					getLastDate(), //
					getLastUser()) //
			.returning(D_CART.CART_ID)
			.fetchOne();

		// 追加したカートIDを取得
		int cartId = result.getCartId();

		// お土産の全言語取得
		AdminOmiyageMasterForm form = omiyageMasterService.getOmiyageMasterForm(omiyageId);

		// カート言語テーブルに追加
		insertCartLang(cartId, LangEnum.LANG_JP.getCode(), form.getJa());
		insertCartLang(cartId, LangEnum.LANG_EN.getCode(), form.getEn());
		insertCartLang(cartId, LangEnum.LANG_CN.getCode(), form.getZhCn());
		insertCartLang(cartId, LangEnum.LANG_TW.getCode(), form.getZhTw());
		insertCartLang(cartId, LangEnum.LANG_KO.getCode(), form.getKo());

	}
	private void insertCartLang(int cartId, String lang, AdminOmiyageLangMasterForm form) {
		jooq.insertInto(
			D_CART_LANG, //
			D_CART_LANG.CART_ID, //
			D_CART_LANG.LANGUAGE, //
			D_CART_LANG.NAME) //
			.values(
					cartId, //
					lang, //
					form.getName()) //
			.execute();
	}


	/**
	 * ユーザー情報の有効期限からお土産の購入期限を取得
	 * @param toDate
	 * @return
	 */
	public String getToDateTime(Timestamp toDate) {
		String toDateTime = "";

		// お土産購入の購入期限時間
		String toHour = env.getRequiredProperty("omiyage.order-to-hour");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd " + toHour + ":00:00");
		toDateTime = sdf.format(toDate);

		return toDateTime;
	}


	/**
	 * ユーザー情報の有効期限からお土産の購入期限を取得
	 * @param toDate
	 * @return
	 * @throws ParseException
	 */
	public boolean checkToDateTime(Timestamp toDate) throws ParseException {
		String toDateTimeStr = "";

		// お土産購入の購入期限時間
		String toHour = env.getRequiredProperty("omiyage.order-to-hour");

		SimpleDateFormat sdf = new SimpleDateFormat("yyyy/MM/dd " + toHour + ":00:00");
		toDateTimeStr = sdf.format(toDate);

		// 現在日時と比較
		Date toDateTime = sdf.parse(toDateTimeStr);
		// 有効期限より現在日時のほうが前かどうか
		return toDateTime.after(new Date());
	}


	/**
	 * カートトークンを作成しDBに登録
	 * @param token
	 */
	public String updateCartToken(String token) {

		// トークンを作成
		String cartToken = UUID.randomUUID().toString().replace("-", "");

		jooq
		.update(D_CART) //
		.set(D_CART.CART_TOKEN, cartToken) //
		.where(D_CART.TOKEN.eq(token))
		.execute();

		return cartToken;
	}


	/**
	 * カートから削除
	 * @param locale
	 * @param token
	 * @param omiyageId
	 * @param price
	 * @param omiyageName
	 * @return
	 */
	public String deleteCart(Locale locale, String token, int omiyageId, int price, String omiyageName) {

		// カートを更新したらカートキーを新しくセット
		String cartToken = updateCartToken(token);

		// 削除するデータを取得
		List<VdCartRecord> deleteList = getViewCartList(locale, token, omiyageId, price, omiyageName);
		for (VdCartRecord del : deleteList) {
			// D_CART削除
			jooq
			.deleteFrom(D_CART)
			.where(D_CART.CART_ID.eq(del.getCartId()))
			.execute();

			// D_CART_LANG削除
			jooq
			.deleteFrom(D_CART_LANG)
			.where(D_CART_LANG.CART_ID.eq(del.getCartId()))
			.execute();
		}
		return cartToken;
	}


	/**
	 * カートから購入
	 * @param locale
	 * @param token
	 * @param omiyageId
	 * @param price
	 * @param omiyageName
	 * @return
	 */
	public void insertOrder(Locale locale, String token) {

		// カート内データを取得
		List<DCartRecord> cartList = getCartList(locale, token);

		// 追加
		for (DCartRecord cart : cartList) {
			// D_ORDER
			DOrderRecord result = jooq.insertInto( //
				D_ORDER, //
				D_ORDER.TOKEN, //
				D_ORDER.OMIYAGE_ID, //
				D_ORDER.PRICE, //
				D_ORDER.NUM, //
				D_ORDER.DEL_FLG, //
				D_ORDER.LAST_DATE, //
				D_ORDER.LAST_USER
			) //
			.values(
				token, //
				cart.getOmiyageId(), //
				cart.getPrice(), //
				cart.getNum(), //
				false, //
				getLastDate(), //
				getLastUser() //
			) //
			.returning(D_ORDER.ORDER_ID)
			.fetchOne();

			int orderId = result.getOrderId();

			// D_ORDER_LANG
			List<DCartLangRecord> langList = this.getCartLangList(cart.getCartId());
			for (DCartLangRecord lang : langList) {

				jooq.insertInto( //
					D_ORDER_LANG, //
					D_ORDER_LANG.ORDER_ID, //
					D_ORDER_LANG.LANGUAGE, //
					D_ORDER_LANG.NAME //
				) //
				.values(
					orderId, //
					lang.getLanguage(), //
					lang.getName() //
				) //
				.execute();
			}
		}

		// カートからは削除
		for (DCartRecord cart : cartList) {

			// D_CART削除
			jooq
			.deleteFrom(D_CART)
			.where(D_CART.CART_ID.eq(cart.getCartId()))
			.execute();

			// D_CART_LANG削除
			jooq
			.deleteFrom(D_CART_LANG)
			.where(D_CART_LANG.CART_ID.eq(cart.getCartId()))
			.execute();
		}
	}
}
