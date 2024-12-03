package com.taketoritei.order.reserve.bath.controller;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.taketoritei.order.common.Consts.BathEnum;
import com.taketoritei.order.common.DateUtil;
import com.taketoritei.order.common.controller.AdminBaseController;
import com.taketoritei.order.common.validation.ValidateUtil;
import com.taketoritei.order.reserve.bath.form.AdminBathUsageForm;
import com.taketoritei.order.reserve.bath.form.AdminBathYoyakuForm;
import com.taketoritei.order.reserve.bath.service.AdminBathService;
import com.taketoritei.order.reserve.bath.service.BathService;
import com.taketoritei.order.room.service.RoomService;

/**
 * 貸切風呂利用状況
 */
@Controller
public class AdminBathUsageController extends AdminBaseController {

	@Autowired
	MessageSource messages;

	@Autowired
	AdminBathService service;

	@Autowired
	RoomService roomService;

	@Autowired
	BathService bathService;


	private void setUsageForm(Model model, AdminBathUsageForm form) {

		// 取得
		form.setDisplayDate(form.getReserveInputDate().replace("-", "/"));
		List<AdminBathYoyakuForm> adminBathYoyakuFormList = new ArrayList<AdminBathYoyakuForm>();
		BathEnum[] bathEnum = BathEnum.values();
		JSONArray jsonArray = service.getReserveBathJson(form.getReserveDate());

		// jsonからフォームに変換
		for (int i=0; i<jsonArray.length(); i++) {
			AdminBathYoyakuForm adminBathYoyakuForm = new AdminBathYoyakuForm();
			JSONObject obj = jsonArray.getJSONObject(i);

			adminBathYoyakuForm.setTimeCd(obj.getString("time_cd"));
			adminBathYoyakuForm.setTimeNm(obj.getString("time_nm"));
			adminBathYoyakuForm.setDateRange(obj.getInt("date_range"));
			adminBathYoyakuForm.setOverTime(obj.getBoolean("over_time"));
			adminBathYoyakuForm.setBathType(obj.getString("bath_type"));
			for (BathEnum bath : bathEnum) {
				if ("1".equals(bath.getCode())) {
					adminBathYoyakuForm.setBath1RoomNo(obj.getString("bath_" + bath.getCode() + "_room_no"));
					adminBathYoyakuForm.setBath1Count(obj.getInt("bath_" + bath.getCode() + "_count"));
				} else if ("2".equals(bath.getCode())) {
					adminBathYoyakuForm.setBath2RoomNo(obj.getString("bath_" + bath.getCode() + "_room_no"));
					adminBathYoyakuForm.setBath2Count(obj.getInt("bath_" + bath.getCode() + "_count"));
				} else if ("3".equals(bath.getCode())) {
					adminBathYoyakuForm.setBath3RoomNo(obj.getString("bath_" + bath.getCode() + "_room_no"));
					adminBathYoyakuForm.setBath3Count(obj.getInt("bath_" + bath.getCode() + "_count"));
				} else if ("4".equals(bath.getCode())) {
					adminBathYoyakuForm.setBath4RoomNo(obj.getString("bath_" + bath.getCode() + "_room_no"));
					adminBathYoyakuForm.setBath4Count(obj.getInt("bath_" + bath.getCode() + "_count"));
				} else if ("5".equals(bath.getCode())) {
					adminBathYoyakuForm.setBath5RoomNo(obj.getString("bath_" + bath.getCode() + "_room_no"));
					adminBathYoyakuForm.setBath5Count(obj.getInt("bath_" + bath.getCode() + "_count"));
				} else if ("6".equals(bath.getCode())) {
					adminBathYoyakuForm.setBath6RoomNo(obj.getString("bath_" + bath.getCode() + "_room_no"));
					adminBathYoyakuForm.setBath6Count(obj.getInt("bath_" + bath.getCode() + "_count"));
				} else if ("7".equals(bath.getCode())) {
					adminBathYoyakuForm.setBath7RoomNo(obj.getString("bath_" + bath.getCode() + "_room_no"));
					adminBathYoyakuForm.setBath7Count(obj.getInt("bath_" + bath.getCode() + "_count"));
				} else if ("8".equals(bath.getCode())) {
					adminBathYoyakuForm.setBath8RoomNo(obj.getString("bath_" + bath.getCode() + "_room_no"));
					adminBathYoyakuForm.setBath8Count(obj.getInt("bath_" + bath.getCode() + "_count"));
				}
			}
			adminBathYoyakuFormList.add(adminBathYoyakuForm);
		}
		model.addAttribute("adminBathYoyakuFormList", adminBathYoyakuFormList);
	}

	/**
	 * 管理画面 初期表示
	 */
	@GetMapping("/admin/bath/usage")
	public String adminBathUsage(Model model) {

		AdminBathUsageForm form = new AdminBathUsageForm();
		form.setReserveInputDate(DateUtil.getBussinessDate().replace("/", "-"));
		form.setReserveDate(DateUtil.getBussinessDateYmd());

		setUsageForm(model, form);

		model.addAttribute("form", form);
		return "reserve/bath/admin_bath_usage";
	}

	/**
	 * 管理画面 初期表示
	 */
	@PostMapping("/admin/bath/usage")
	public String adminBathUsage(AdminBathUsageForm form, Model model) {

		List<String> check = new ArrayList<String>();
		if (StringUtils.isEmpty(form.getReserveInputDate()))
			check.add(messages.getMessage("error.require", new Object[] { "営業日" }, Locale.getDefault()));
		else if (!ValidateUtil.isDate(form.getReserveInputDate()))
			check.add(messages.getMessage("error.isDate", new Object[] {"営業日", "yyyy-MM-dd"}, Locale.getDefault()));

		if (check.size() == 0) {
			form.setReserveDate(form.getReserveInputDate().replace("/", "").replace("-", ""));
		} else {
			// エラーメッセージ
			model.addAttribute("check", check);
		}

		setUsageForm(model, form);

		model.addAttribute("form", form);
		return "reserve/bath/admin_bath_usage";
	}

	/**
	 * 管理画面 予約キャンセル
	 */
	@PostMapping("/admin/bath/usage/cancel")
	public String adminBathUsage(Model model, Principal principal, AdminBathUsageForm form) {

		// 更新
		service.cancelAdminBath(form);

		// 部屋のユーザー情報を取得
		Record user = roomService.getUserByRoomNo(form.getRoomNo());

		// キャンセル後の予約回数を再設定
		bathService.resetReservedBathCount(user.get("token").toString());

		// メッセージ
		model.addAttribute("message", "貸切風呂予約をキャンセルしました");

		// formを初期状態にするため、初期表示を呼び出す
		String reserveDate = form.getReserveDate();
		String reserveInputDate = form.getReserveDate().substring(0, 4) //
				+ "-" + form.getReserveDate().substring(4, 6) //
				+ "-" + form.getReserveDate().substring(6, 8);
		form = new AdminBathUsageForm();
		form.setReserveDate(reserveDate);
		form.setReserveInputDate(reserveInputDate);
		model.addAttribute("form", form);

		setUsageForm(model, form);

		return "reserve/bath/admin_bath_usage";
	}
}
