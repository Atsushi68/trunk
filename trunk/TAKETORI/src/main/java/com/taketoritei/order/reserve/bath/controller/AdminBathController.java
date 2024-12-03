package com.taketoritei.order.reserve.bath.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.taketoritei.order.common.Consts.BathEnum;
import com.taketoritei.order.common.DateUtil;
import com.taketoritei.order.common.controller.AdminBaseController;
import com.taketoritei.order.common.validation.ValidateUtil;
import com.taketoritei.order.jooq.tables.records.DReserveBathRecord;
import com.taketoritei.order.reserve.bath.form.AdminBathForm;
import com.taketoritei.order.reserve.bath.service.AdminBathService;
import com.taketoritei.order.reserve.bath.service.BathService;
import com.taketoritei.order.room.service.RoomService;

/**
 * 貸切風呂予約
 */
@Controller
public class AdminBathController extends AdminBaseController {

	@Autowired
	MessageSource messages;

	@Autowired
	AdminBathService service;

	@Autowired
	RoomService roomService;

	@Autowired
	BathService bathService;

	/**
	 * 管理画面 初期表示
	 */
	@GetMapping("/admin/bath")
	public String adminBath(Model model) {

		AdminBathForm form = new AdminBathForm();
		form.setReserveDate(DateUtil.getBussinessDate().replace("/", "-"));

		setAdminModel(model, form);
		return "reserve/bath/admin_bath";
	}

	/**
	 * 管理画面 予約登録
	 */
	@PostMapping("/admin/bath/regist")
	public String adminBathRegist(Model model, Principal principal, AdminBathForm form) {

		// チェック
		List<String> check = new ArrayList<String>();
		if (StringUtils.isEmpty(form.getReserveDate()))
			check.add(messages.getMessage("error.require", new Object[] { "営業日" }, Locale.getDefault()));
		else if (!ValidateUtil.isDate(form.getReserveDate()))
			check.add(messages.getMessage("error.isDate", new Object[] {"営業日", "yyyy-MM-dd"}, Locale.getDefault()));
		if (StringUtils.isEmpty(form.getTimeCd()))
			check.add(messages.getMessage("error.require", new Object[] { "時間帯" }, Locale.getDefault()));
		if (StringUtils.isEmpty(form.getBathCd()))
			check.add(messages.getMessage("error.require", new Object[] { "貸切風呂" }, Locale.getDefault()));
		if (StringUtils.isEmpty(form.getRoomNo()))
			check.add(messages.getMessage("error.require", new Object[] { "客室" }, Locale.getDefault()));

		// 部屋のユーザー情報を取得
		Record user = roomService.getUserByRoomNo(form.getRoomNo());
		if (user == null) {
			check.add(messages.getMessage("error.admin.noRoomUser", null, Locale.getDefault()));
		}

		if (check.size() == 0) {

			// 既に予約がある場合はエラー
			List<DReserveBathRecord> res = service.searchAdminBath(form);
			if (res.size() == 0) {

				// 更新
				service.insertAdminBath(form, user.get("token").toString());

				// 予約後の予約回数を再設定
				bathService.resetReservedBathCount(user.get("token").toString());

				// メッセージ
				model.addAttribute("message", "貸切風呂を予約しました");

				setAdminModel(model, form);
				return "reserve/bath/admin_bath";

			} else {

				// エラーメッセージ
				check.add(messages.getMessage("error.isReserve", null, Locale.getDefault()));
				model.addAttribute("check", check);

				setAdminModel(model, form);
				return "reserve/bath/admin_bath";

			}

		} else {
			// エラーメッセージ
			model.addAttribute("check", check);

			setAdminModel(model, form);
			return "reserve/bath/admin_bath";
		}
	}

	/**
	 * 管理画面で使用する配列をmodelに設定する
	 */
	private void setAdminModel(Model model, AdminBathForm form) {
		model.addAttribute("form", form);
		model.addAttribute("bathEnum", BathEnum.values());
		model.addAttribute("roomList", roomService.getRoomList());
		model.addAttribute("timeList", service.getSelectableTimeList());
	}

	/**
	 * 管理画面 予約状況の取得API
	 */
	@GetMapping("/admin/bath/reserve/")
	@ResponseBody
	public String searchApi() {

		return searchApi(DateUtil.getBussinessDateYmd());
	}

	/**
	 * 管理画面 予約状況の取得API
	 */
	@GetMapping("/admin/bath/reserve/{reserveDate}")
	@ResponseBody
	public String searchApi(@PathVariable("reserveDate") String reserveDate) {

		return service.getReserveBathJson(reserveDate).toString();
	}

}
