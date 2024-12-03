package com.taketoritei.order.omiyage.controller;

import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpSession;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.taketoritei.order.common.Pagination;
import com.taketoritei.order.common.controller.FrontBaseController;
import com.taketoritei.order.config.IllegalException;
import com.taketoritei.order.jooq.tables.records.VmOmiyageRecord;
import com.taketoritei.order.login.form.User;
import com.taketoritei.order.omiyage.form.AdminOmiyageMasterSearchForm;
import com.taketoritei.order.omiyage.form.CartForm;
import com.taketoritei.order.omiyage.form.OrderForm;
import com.taketoritei.order.omiyage.service.OmiyageMasterService;
import com.taketoritei.order.omiyage.service.SouvenirService;

@Controller
public class SouvenirController extends FrontBaseController {

	@Autowired
	MessageSource messages;

	@Autowired
	SouvenirService service;

	@Autowired
	OmiyageMasterService omiyageMasterService;

	@Autowired
	HttpSession session;


	/**
	 * お土産一覧
	 * お土産キーワード検索
	 * @param model
	 * @return
	 */
	@GetMapping("/{locale}/sv")
	public String souvenirIndex(Locale locale, Model model, AdminOmiyageMasterSearchForm searchForm,
			@RequestParam(name="category", required = false) String category,
			@RequestParam(name="freeword", required = false) String freeword,
			@RequestParam(name="page", required = false) Integer page) {

		// 検索条件があればセッションにセット
		if (null != category || null != freeword) {
			session.setAttribute("category", category);
			session.setAttribute("freeword", freeword);
		}
		// 現在ページを取得
		if (page == null) {
			if (null == session.getAttribute("page")) {
				page = 1;
			} else {
				page = Integer.parseInt(session.getAttribute("page").toString());
			}
		}
	    session.setAttribute("page", page);

		// 検索条件をセット
	    category = session.getAttribute("category") == null ? "" : session.getAttribute("category").toString();
	    freeword = session.getAttribute("freeword") == null ? "" : session.getAttribute("freeword").toString();

		searchForm.setCategory(category);
		searchForm.setFreeword(freeword);

		// お土産のリストをページャーで取得
		// ①総数取得
	    int totalListCnt = omiyageMasterService.getCountOmiyageMasterList(searchForm, locale.getLanguage());

	    // ②総数と現在ページをセット
	    Pagination pagination = new Pagination(totalListCnt, page);

	    // ③現在ページと総数からlimit、offsetを取得
	    searchForm.setLimit(pagination.getPageSize());
	    searchForm.setOffset(pagination.getStartIndex());

	    // 検索
		List<VmOmiyageRecord> omiyageList = omiyageMasterService.getOmiyageMasterList(searchForm, locale.getLanguage());

		model.addAttribute("omiyageList", omiyageList);
	    model.addAttribute("pagination", pagination);
	    model.addAttribute("searchForm", searchForm);

		// カテゴリー一覧取得
		model.addAttribute("categoryMap", service.getCategoryMap(locale));
		model.addAttribute("selectCategory", category);
		model.addAttribute("inputFreeword", freeword);
		model.addAttribute("pageNow", page);

		return "omiyage/souvenir";
	}


	/**
	 * お土産詳細
	 * @param model
	 * @return
	 */
	@GetMapping("/{locale}/sv/detail/{omiyageId}")
	public String souvenirDetail(Locale locale, @PathVariable("omiyageId") String omiyageId, Model model) {

		// お土産情報を取得
		VmOmiyageRecord omiyage = omiyageMasterService.getOmiyageMaster(omiyageId, locale.getLanguage());
	    model.addAttribute("omiyage", omiyage);
		return "omiyage/souvenir_detail";
	}

	/**
	 * カートに追加
	 * @param model
	 * @return
	 */
	@PostMapping("/{locale}/sv/add/cart")
	public String souvenirAddCart(Locale locale, Model model, UsernamePasswordAuthenticationToken token,
			@RequestParam(name="omiyageId", required = false) String omiyageId,
			@RequestParam(name="num", required = false) String num) {

		// ユーザー情報取得
		User user = (User) token.getPrincipal();

		// お土産IDが無い場合不正なアクセス
		if (StringUtils.isEmpty(omiyageId)) {
			throw new IllegalException(messages.getMessage("error.illegal", null, locale));
		}
		// お土産情報を取得
		VmOmiyageRecord omiyage = omiyageMasterService.getOmiyageMaster(omiyageId, locale.getLanguage());

		// 取得できない場合不正なアクセス
		if (null == omiyage) {
			throw new IllegalException(messages.getMessage("error.illegal", null, locale));
		}

		// バリデーション
		List<String> check = new ArrayList<String>();
		if (StringUtils.isEmpty(num)) {
			check.add(messages.getMessage("error.require", new Object[] { messages.getMessage("cart.3", null, locale) }, locale));
		    model.addAttribute("check", check);
		}

		// エラーがあるか
		if (0 == check.size()) {
			// エラーが無い場合カートに入れる
			service.addCart(user.getToken(), Integer.parseInt(omiyageId), omiyage.getPrice(), Integer.parseInt(num));
		    model.addAttribute("message", messages.getMessage("cart.28", null, locale)); // 追加しました。
		}
	    model.addAttribute("omiyage", omiyage);
		return "omiyage/souvenir_detail";
	}


	/**
	 * カート
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@GetMapping("/{locale}/sv/cart")
	public String souvenirCartIndex(Locale locale, Model model, UsernamePasswordAuthenticationToken auth) throws ParseException {

		// ユーザー情報取得
		User user = (User) auth.getPrincipal();
		String token = user.getToken();

		// カートの情報を取得
		List<CartForm> cartFormList = service.getCartGroupList(locale, token, null, null, null);

		// カート画面開いた場合はカートのトークンをセッションにセット
		if (0 == cartFormList.size()) {
			session.setAttribute("cartToken", null);
		} else {
			session.setAttribute("cartToken", cartFormList.get(0).getCartToken());
		}

		// 合計金額を取得
		int totalCartPrice = 0;
		for (CartForm cartForm : cartFormList) {
			totalCartPrice += cartForm.getNum() * cartForm.getPrice();
		}

		// 確定済を取得
		List<OrderForm> orderFormList = service.getOrderGroupList(locale, token);
		// 合計金額を取得
		int totalOrderPrice = 0;
		for (OrderForm orderForm : orderFormList) {
			totalOrderPrice += orderForm.getNum() * orderForm.getPrice();
		}

		// 購入期限が過ぎていないかチェック
		Boolean checkToDate = service.checkToDateTime(user.getToDt());

		model.addAttribute("cartFormList", cartFormList);
		model.addAttribute("totalCartPrice", totalCartPrice);
		model.addAttribute("toDateTime", service.getToDateTime(user.getToDt()));
		model.addAttribute("orderFormList", orderFormList);
		model.addAttribute("totalOrderPrice", totalOrderPrice);
		model.addAttribute("checkToDate", checkToDate);

		System.out.println(checkToDate);

		return "omiyage/souvenir_cart";
	}


	/**
	 * カートのお土産削除
	 * @param model
	 * @return
	 */
	@PostMapping("/{locale}/sv/cart/delete")
	public String souvenirCartDelete(Locale locale, Model model, UsernamePasswordAuthenticationToken token,
			@RequestParam(name="omiyage_id") int omiyageId,
			@RequestParam(name="price") int price,
			@RequestParam(name="omiyage_name") String omiyageName,
			RedirectAttributes redirectAttributes) {

		// ユーザー情報取得
		User user = (User) token.getPrincipal();

		// 取得できない場合エラー
		List<String> check = new ArrayList<String>();
		List<CartForm> checkCart = service.getCartGroupList(locale, user.getToken(), omiyageId, price, omiyageName);
		if (0 == checkCart.size()) {
			// 通常ない
			throw new IllegalException(messages.getMessage("error.illegal", null, locale));
		}

		// カート更新されている場合はエラー
		if (0 == checkCart.size()) {
			check.add(messages.getMessage("cart.31", null, locale));
			model.addAttribute("check", check);
		}

		// 削除
		if (0 == check.size()) {
			String cartToken = service.deleteCart(locale, user.getToken(), omiyageId, price, omiyageName);
			session.setAttribute("cartToken", cartToken);
			redirectAttributes.addFlashAttribute("message", messages.getMessage("cart.30", null, locale));
		}
		return "redirect:/" + locale.getLanguage() + "/sv/cart";
	}


	/**
	 * お土産購入確定
	 * @param model
	 * @return
	 * @throws ParseException
	 */
	@PostMapping("/{locale}/sv/cart/order")
	public String souvenirAddOrder(Locale locale, Model model, UsernamePasswordAuthenticationToken token, RedirectAttributes redirectAttributes) throws ParseException {

		List<String> check = new ArrayList<String>();
		String message = "";

		// ユーザー情報取得
		User user = (User) token.getPrincipal();

		// 取得できない場合エラー
		List<CartForm> checkCart = service.getCartGroupList(locale, user.getToken(), null, null, null);
		if (0 == checkCart.size()) {
			// 通常ない
			throw new IllegalException(messages.getMessage("error.illegal", null, locale));
		}
		// 購入期限を過ぎていたらエラー
		if (!service.checkToDateTime(user.getToDt())) {
			throw new IllegalException(messages.getMessage("omiyage.14", null, locale));
		}
		// カート更新されている場合はエラー
		if (!session.getAttribute("cartToken").equals(checkCart.get(0).getCartToken())) {
			check.add(messages.getMessage("cart.31", null, locale));
			redirectAttributes.addFlashAttribute("check", check);
		}

		// エラーがあるか
		if (0 == check.size()) {
			service.insertOrder(locale, user.getToken());
			session.setAttribute("cartToken", null);
			message = messages.getMessage("cart.33", null, locale);
		}

		// リダイレクト
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/" + locale.getLanguage() + "/sv/cart";
	}
}
