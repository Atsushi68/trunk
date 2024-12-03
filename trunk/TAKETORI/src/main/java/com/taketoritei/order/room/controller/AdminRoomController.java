package com.taketoritei.order.room.controller;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.jooq.Record;
import org.jooq.Result;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.google.zxing.WriterException;
import com.taketoritei.order.common.FileUtil;
import com.taketoritei.order.common.controller.AdminBaseController;
import com.taketoritei.order.login.service.UserService;
import com.taketoritei.order.room.form.AdminRoomForm;
import com.taketoritei.order.room.form.AdminRoomListForm;
import com.taketoritei.order.room.service.RoomService;

import net.sf.jasperreports.engine.JRException;


/**
 * 客室管理
 */
@Controller
public class AdminRoomController extends AdminBaseController {

	@Autowired
	private RoomService roomService;

	@Autowired
	private UserService userService;

	@Autowired
	private Environment env;


	private void setAttributeModel(Model model) {

		model.addAttribute("nowDt", new Timestamp(System.currentTimeMillis()));

		// yamlから設定値を取得
		String outputNewDir = env.getRequiredProperty("room.report.output-new-dir");
		String outputLoginFile = env.getRequiredProperty("room.report.output-login-file");
		model.addAttribute("downloadFile", new File(outputNewDir + outputLoginFile).exists());
	}

	/**
	 * 客室　QRコード新規発行の初期表示
	 */
	@GetMapping("/admin/room/qrcode/new")
	public String adminRoomQrcodeNew(Model model) throws IOException, WriterException {

		Result<Record> roomUserList = roomService.getRoomUserList();

		// フォームにセット
		AdminRoomListForm formList = new AdminRoomListForm(roomUserList);
		model.addAttribute("formList", formList);

		setAttributeModel(model);
		return "room/admin_room_qrcode_new";
	}

	/**
	 * 客室　QRコード編集の初期表示
	 */
	@GetMapping("/admin/room/qrcode/edit")
	public String adminRoom(Model model) throws IOException, WriterException {

		Result<Record> roomUserList = roomService.getRoomUserList();

		// フォームにセット
		AdminRoomListForm formList = new AdminRoomListForm(roomUserList);
		model.addAttribute("formList", formList);

		setAttributeModel(model);
		return "room/admin_room_qrcode_edit";
	}

	/**
	 * 客室ログイン情報の新規発行
	 */
	@PostMapping("/admin/room/regist")
	public String adminRoomRegist(
			HttpServletResponse response,
			Model model,
			@ModelAttribute("formList") @Validated AdminRoomListForm formList, BindingResult result,
			@RequestParam(name = "chk_rooms", required = false) List<String> chkRoomList,
			RedirectAttributes redirectAttributes) throws JRException, Exception {

		// エラーメッセージ
		List<String> errorList = new ArrayList<String>();

		// 入力チェック
		// -------------------------------
		if (null == chkRoomList) {
			errorList.add("１つもチェックされていません。発行する客室にチェックを入れてください。");

		} else {

			// 客室番号を基準にループ
			for (AdminRoomForm adminRoomForm : formList.getAdminRoomListForm()) {
				// チェックボックスにチェックがあるもののみ対象
				if (chkRoomList.contains(adminRoomForm.getRoomNo())) {
					// バリデーション
					List<String> errors = userService.validationForm(adminRoomForm);
					if (errors.size() != 0) {
						errorList.addAll(errors);
					}
				}
			}
		}
		// -------------------------------

		// エラーがある場合処理しない
		if (0 != errorList.size()) {
			model.addAttribute("formList", formList);
			model.addAttribute("check", errorList);

			setAttributeModel(model);
			return "room/admin_room_qrcode_new";
		}

		// 登録
		userService.registUserByForm(formList);

		// リダイレクト
		redirectAttributes.addFlashAttribute("message", "QRコードを発行しました。ダウンロードボタンからダウンロードしてください。");
		return "redirect:/admin/room/qrcode/new";
	}

	/**
	 * 客室ログイン情報の再発行
	 */
	@PostMapping("/admin/room/edit")
	public String adminRoomEdit(
			@RequestParam(name = "kbn", required = true) String kbn,
			@RequestParam(name = "roomNo", required = true) String roomNo,
			HttpServletResponse response,
			Model model,
			@ModelAttribute("formList") @Validated AdminRoomListForm formList, BindingResult result,
			RedirectAttributes redirectAttributes) throws JRException, Exception {

		// エラーメッセージ
		List<String> errorList = new ArrayList<String>();

		// 再発行の場合のみ入力チェック
		if ("再発行".equals(kbn)) {
			// 客室番号を基準にループ
			for (AdminRoomForm adminRoomForm : formList.getAdminRoomListForm()) {
				// ボタンが押された客室が対象
				if (roomNo.equals(adminRoomForm.getRoomNo())) {
					// 入力チェック
					errorList.addAll(userService.validationForm(adminRoomForm));
				}
			}
		}

		// エラーがある場合処理しない
		if (0 != errorList.size()) {
			model.addAttribute("formList", formList);
			model.addAttribute("check", errorList);

			setAttributeModel(model);
			return "room/admin_room_qrcode_edit";
		}

		// エラーが無い場合は登録
		String message = userService.editUserByForm(formList, kbn, roomNo);

		// リダイレクト
		redirectAttributes.addFlashAttribute("message", message);
		return "redirect:/admin/room/qrcode/edit";
	}


	/**
	 * 新規発行のPDFダウンロード
	 */
	@GetMapping("/admin/room/qrcode/download/{kbn}")
	public String adminRoomQrcodeDownload(@PathVariable("kbn") String kbn, HttpServletResponse response, RedirectAttributes redirectAttributes) throws Exception {

		// yamlから取得
		String outputNewDir = env.getRequiredProperty("room.report.output-new-dir");
		String outputReDir = env.getRequiredProperty("room.report.output-re-dir");
		String outputLoginFile = env.getRequiredProperty("room.report.output-login-file");

		// Resource resource = null;
		byte[] fileContent = null;

		// 新規発行か再発行か
		if ("new".equals(kbn)) {
			// ファイルが存在する場合のみ
			if (new File(outputNewDir + outputLoginFile).exists()) {
				fileContent = FileUtil.convertToByteArray(outputNewDir + outputLoginFile);
			} else {
				redirectAttributes.addFlashAttribute("message", "QRコードが発行されていません。");
				return "redirect:/admin/room/qrcode/new";
			}

		} else if ("re".equals(kbn)) {
			if (new File(outputReDir + outputLoginFile).exists()) {
				fileContent = FileUtil.convertToByteArray(outputReDir + outputLoginFile);
			} else {
				redirectAttributes.addFlashAttribute("message", "QRコードが発行されていません。");
				return "redirect:/admin/room/qrcode/edit";
			}

		} else {
			// 何もしない
			// TOPにリダイレクト
			return "redirect:/admin/";
		}

		// ファイル書き込み
		response.setContentType(MediaType.APPLICATION_PDF_VALUE);
		response.setHeader("Content-Disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(outputLoginFile, "UTF-8"));
		FileUtil.outputSreamWrite(response, fileContent);

        return null;
	}
}
