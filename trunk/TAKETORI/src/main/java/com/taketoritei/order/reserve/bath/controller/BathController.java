package com.taketoritei.order.reserve.bath.controller;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.Errors;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taketoritei.order.common.Consts.BathEnum;
import com.taketoritei.order.common.DateUtil;
import com.taketoritei.order.common.controller.FrontBaseController;
import com.taketoritei.order.config.IllegalException;
import com.taketoritei.order.login.form.User;
import com.taketoritei.order.reserve.bath.form.BathInputForm;
import com.taketoritei.order.reserve.bath.service.BathService;
import com.taketoritei.order.room.service.RoomService;

/**
 * 貸切風呂
 */
@Controller
public class BathController extends FrontBaseController {

	@Autowired
	MessageSource messages;

	@Autowired
	BathService service;

	@Autowired
	RoomService roomService;

	/**
	 * 初期表示
	 * 
	 * @throws ParseException
	 */
	@GetMapping("/{locale}/bath")
	public String bath(@RequestParam(name = "date", required = false) String reserveDate, Locale locale, Model model,
			UsernamePasswordAuthenticationToken token) throws ParseException {

		User user = (User) token.getPrincipal();

		if (StringUtils.isEmpty(reserveDate))
			reserveDate = DateUtil.getBussinessUserDateYmd();

		setModelValue(model, reserveDate, user, locale);
		return "reserve/bath/bath";
	}

	/**
	 * 予約キャンセル
	 * 
	 * @throws ParseException
	 */
	@PostMapping("{locale}/bath/cancel")
	public String bathCancel(Locale locale, Model model, UsernamePasswordAuthenticationToken token, BathInputForm form,
			Errors errors) throws ParseException {

		User user = (User) token.getPrincipal();
		String reserveDate = DateUtil.getBussinessUserDateYmd();

		// キャンセル
		service.cancelBath(user, reserveDate, form);

		// キャンセル後の予約回数を再設定
		service.resetReservedBathCount(user.getToken());

		model.addAttribute("message", messages.getMessage("bath.46", null, locale));

		setModelValue(model, reserveDate, user, locale);

		return "reserve/bath/bath";
	}

	/**
	 * 予約登録
	 * 
	 * @throws ParseException
	 */
	@PostMapping("{locale}/bath/regist")
	public String bathRegist(Locale locale, Model model, UsernamePasswordAuthenticationToken token, BathInputForm form,
			BindingResult result) throws ParseException {

		User user = (User) token.getPrincipal();

		String reserveDate = form.getReserveDate();
		String dateFrom = new SimpleDateFormat("yyyyMMdd").format(user.getFormDt());
		String dateTo = DateUtil.getBussinessDateYmd(user.getToDt());

		// 日付が範囲内かをチェック
		if (Integer.parseInt(reserveDate) < Integer.parseInt(dateFrom) //
				|| Integer.parseInt(reserveDate) > Integer.parseInt(dateTo))
			throw new IllegalException("不正なアクセスです。");

		// チェック
		String errMessage = service.checkBath(user, reserveDate, form, locale);

		if (errMessage == null) {

			// 予約の数分のカウントをセット
			int count = service.getReservedBathCount(user.getToken());

			// 登録
			service.insertBath(user, reserveDate, form, count);

			// 予約が近い順にカウントを再設定
			service.resetReservedBathCount(user.getToken());

			model.addAttribute("message", messages.getMessage("bath.45", null, locale));

			setModelValue(model, reserveDate, user, locale);
			return "redirect:/" + locale.getLanguage() + "/bath?date=" + reserveDate;

		} else {
			// メッセージ
			model.addAttribute("check", errMessage);

			setModelValue(model, reserveDate, user, locale);
			return "reserve/bath/bath";
		}
	}

	/**
	 * modelに画面表示値を設定する
	 * 
	 * @throws ParseException
	 */
	private void setModelValue(Model model, String reserveDate, User user, Locale locale) throws ParseException {
		// 部屋番号
		String roomNo = user.getRoomNo();

		model.addAttribute("roomNo", roomNo);

		String dateFrom = new SimpleDateFormat("yyyyMMdd").format(user.getFormDt());
		String dateTo = DateUtil.getBussinessUserDateYmd(user.getToDt());

		// 日付が範囲内かをチェック
		if (Integer.parseInt(reserveDate) < Integer.parseInt(dateFrom) //
				|| Integer.parseInt(reserveDate) > Integer.parseInt(dateTo))
			throw new IllegalException("不正なアクセスです。");

		// 日付設定
		model.addAttribute("reserveDispDate",
				reserveDate.substring(0, 4) + "/" + reserveDate.substring(4, 6) + "/" + reserveDate.substring(6, 8));

		model.addAttribute("beforeReserveDate", null);

		if (Integer.parseInt(reserveDate) > Integer.parseInt(dateFrom)) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = dateFormat.parse(reserveDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			model.addAttribute("beforeReserveDate", dateFormat.format(calendar.getTime()));
		}

		model.addAttribute("afterReserveDate", null);

		if (Integer.parseInt(reserveDate) < Integer.parseInt(dateTo) - 1) {
			SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = dateFormat.parse(reserveDate);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			model.addAttribute("afterReserveDate", dateFormat.format(calendar.getTime()));
		}

		model.addAttribute("reserveDate", reserveDate);
		model.addAttribute("bathNm1", service.getBathNm(BathEnum.BATH1.getCode(), locale));
		model.addAttribute("bathNm2", service.getBathNm(BathEnum.BATH2.getCode(), locale));
		model.addAttribute("bathNm3", service.getBathNm(BathEnum.BATH3.getCode(), locale));
		model.addAttribute("bathNm4", service.getBathNm(BathEnum.BATH4.getCode(), locale));
		model.addAttribute("bathNm5", service.getBathNm(BathEnum.BATH5.getCode(), locale));
		model.addAttribute("bathNm6", service.getBathNm(BathEnum.BATH6.getCode(), locale));
		model.addAttribute("bathNm7", service.getBathNm(BathEnum.BATH7.getCode(), locale));
		model.addAttribute("bathNm8", service.getBathNm(BathEnum.BATH8.getCode(), locale));
		model.addAttribute("reserve", service.getReservedBath(roomNo, reserveDate, locale));
		model.addAttribute("list", service.getBath(roomNo, reserveDate, locale));
	}

}
