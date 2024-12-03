package com.taketoritei.order.login.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taketoritei.order.common.Consts.BathEnum;
import com.taketoritei.order.common.DateUtil;
import com.taketoritei.order.common.controller.AdminBaseController;
import com.taketoritei.order.reserve.bath.form.AdminBathYoyakuForm;
import com.taketoritei.order.reserve.bath.service.AdminBathService;

/**
 * 管理者ログイン
 */
@Controller
public class AdminLoginController extends AdminBaseController {

	@Autowired
    PasswordEncoder passwordEncoder;

	@Autowired
	AdminBathService service;

	/**
	 * 決まったURL以外は403エラーにするのでコンテキストのみなら403エラーにする
	 * @param token
	 * @param model
	 * @param principal
	 * @return
	 */
	@RequestMapping("/")
	public String index(@RequestParam("token") String token, Model model) {
		throw new AccessDeniedException("403 returned");
	}

	@RequestMapping("/admin")
	public String adminIndex(Model model) {

		BathEnum[] bathEnum = BathEnum.values();

		List<AdminBathYoyakuForm> adminBathYoyakuFormList = new ArrayList<AdminBathYoyakuForm>();

		// ロジックで取得
		JSONArray jsonArray = service.getReserveBathJson(DateUtil.getBussinessDateYmd());

		// jsonからフォームに変換
		for (int i=0; i<jsonArray.length(); i++) {
			AdminBathYoyakuForm adminBathYoyakuForm = new AdminBathYoyakuForm();
			JSONObject obj = jsonArray.getJSONObject(i);

			adminBathYoyakuForm.setTimeCd(obj.getString("time_cd"));
			adminBathYoyakuForm.setTimeNm(obj.getString("time_nm"));
			adminBathYoyakuForm.setBathType(obj.getString("bath_type"));
			adminBathYoyakuForm.setDateRange(obj.getInt("date_range"));
			adminBathYoyakuForm.setOverTime(obj.getBoolean("over_time"));

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

		return "login/admin_top";
	}

	@RequestMapping("/admin/login")
	public String login(HttpServletRequest request, Model model) {
		return "login/admin_login";
	}

	/**
	 * ログイン成功時に表示するトップ画面
	 * @param model
	 * @param principal
	 * @return
	 */
	@RequestMapping("/admin/top")
	public String admin(HttpServletRequest request, Model model) {

        // 権限がviewerなら朝食・夕食画面に遷移
        if (request.isUserInRole("ROLE_VIEWER")) {

		    // セッションをセット
		    request.getSession(false).setAttribute("isViewer", true);

            // 朝食・夕食画面に遷移
            return "redirect:/admin/dining";
        }

        // 管理者
        // 他は管理画面TOP
   		// セッションをセット
		request.getSession(false).setAttribute("isAdmin", true);

		BathEnum[] bathEnum = BathEnum.values();

		List<AdminBathYoyakuForm> adminBathYoyakuFormList = new ArrayList<AdminBathYoyakuForm>();

		// ロジックで取得
		JSONArray jsonArray = service.getReserveBathJson(DateUtil.getBussinessDateYmd());

		// jsonからフォームに変換
		for (int i=0; i<jsonArray.length(); i++) {
			AdminBathYoyakuForm adminBathYoyakuForm = new AdminBathYoyakuForm();
			JSONObject obj = jsonArray.getJSONObject(i);

			adminBathYoyakuForm.setTimeCd(obj.getString("time_cd"));
			adminBathYoyakuForm.setTimeNm(obj.getString("time_nm"));
			adminBathYoyakuForm.setBathType(obj.getString("bath_type"));
			adminBathYoyakuForm.setDateRange(obj.getInt("date_range"));
			adminBathYoyakuForm.setOverTime(obj.getBoolean("over_time"));
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
		return "login/admin_top";
	}
}