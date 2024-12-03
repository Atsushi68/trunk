package com.taketoritei.order.login.controller;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.RequestMapping;

import com.taketoritei.order.common.controller.FrontBaseController;
import com.taketoritei.order.login.service.UserService;

/**
 * ユーザー　ログイン
 */
@Controller
public class LoginController extends FrontBaseController {

	@Autowired
	MessageSource messages;

	@Autowired
	private UserService userService;

	/**
	 * セッションが切れた場合にアクセスされるメソッド
	 * @param roomId
	 * @param token
	 * @param model
	 * @param request
	 * @return
	 */
	@RequestMapping("/session/error")
	public String sessionError(@CookieValue(value="roomNo", required=false) String roomNo, @CookieValue(value="token", required=false) String token, Model model, HttpServletRequest request) {

		// 一旦現在のセッションを削除
		HttpSession session = request.getSession(true);
		String lang = "ja";
		if (null != session.getAttribute("lang")) {
			lang = session.getAttribute("lang").toString();
		}
		Locale locale = new Locale(lang);
		session.invalidate();

		String error = messages.getMessage("error.login", null, locale);
		error += messages.getMessage("error.relogin", null, locale);

		// セッションの代わりにcookieからログイン情報を取得
		if (null == roomNo || null == token) {

			// セッションが無いかつcookieも無い場合はエラー、QRコード読み取りから行う
			model.addAttribute("errorMsg", error);
			return "error";
		}

		// cookieからログイン情報を取得できた場合は有効かチェック
		Record rec = userService.getUserByLogin(roomNo, token);
		if (null == rec) {
			// 有効でない場合はエラー
			model.addAttribute("errorMsg", error);
			return "error";
		}

		// 有効な場合は認証
		return "redirect:/user/login?room=" + roomNo + "&token=" + token;
	}
}
