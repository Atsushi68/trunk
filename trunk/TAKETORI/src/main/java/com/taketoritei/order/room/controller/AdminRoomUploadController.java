package com.taketoritei.order.room.controller;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.taketoritei.order.common.FileUtil;
import com.taketoritei.order.common.controller.AdminBaseController;
import com.taketoritei.order.common.validation.ValidateUtil;
import com.taketoritei.order.jooq.tables.records.MRoomRecord;
import com.taketoritei.order.login.service.UserService;
import com.taketoritei.order.room.form.AdminRoomForm;
import com.taketoritei.order.room.form.AdminRoomListForm;
import com.taketoritei.order.room.service.RoomService;

/**
 * 客室管理
 */
@Controller
public class AdminRoomUploadController extends AdminBaseController {

	@Autowired
	private UserService userService;

	@Autowired
	RoomService roomService;

	/**
	 * 画面からのCSVアップロード
	 */
	@PostMapping("/admin/room/csv/upload")
	public String uploadAdmin(@RequestParam("upload_file") MultipartFile multipartFile, Model model,
			RedirectAttributes redirectAttributes) throws Exception {

		String errorMessage = upload(multipartFile);

		if (errorMessage == null) {
			redirectAttributes.addFlashAttribute("message", "QRコードを発行しました。ダウンロードボタンからダウンロードしてください。");
		} else {
			List<String> check = new ArrayList<>();
			check.add(errorMessage);
			redirectAttributes.addFlashAttribute("check", errorMessage);
		}

		// リダイレクト
		return "redirect:/admin/room/qrcode/new";
	}

	/**
	 * バッチからのCSVアップロード
	 */
	@PostMapping("/api/room/csv/upload")
	@ResponseBody
	public String uploadBatch(@RequestParam("file") MultipartFile multipartFile) throws Exception {

		String errorMessage = upload(multipartFile);

		if (errorMessage == null) {
			return "SUCCESS";
		} else {
			return errorMessage;
		}
	}

	/**
	 * CSVアップロード
	 */
	private String upload(MultipartFile multipartFile) throws Exception {

		List<String> textList = FileUtil.fileContents(multipartFile, "Shift-JIS");

		// チェック
		String errorMessage = null;
		if (textList == null) {
			errorMessage = "ファイル読込でエラーが発生しました";
		} else if (textList.size() == 0) {
			errorMessage = "データが0件です";
		} else {

			// 部屋の取得
			List<MRoomRecord> roomList = roomService.getRoomList();
			HashSet<String> roomSet = new HashSet<>();
			for (MRoomRecord roomRec : roomList)
				roomSet.add(roomRec.getRoomNo());

			List<String> errorList = new ArrayList<>();
			for (int i = 0; i < textList.size(); i++) {
				String[] cols = textList.get(i).split(",", -1);
				if (cols.length == 0)
					continue;
				// 列数チェック
				if (cols.length < 3) {
					errorList.add((i + 1) + "行目の列数が不足しています");
					continue;
				}
				// 部屋存在チェック
				if (!roomSet.contains(cols[0])) {
					errorList.add((i + 1) + "行目の部屋は存在しません");
					continue;
				}
				// 日付チェック
				if (StringUtils.isEmpty(cols[2]) || !ValidateUtil.isDate(cols[2])) {
					errorList.add((i + 1) + "行目のチェックイン日の日付型式が不正です");
					continue;
				}
				if (StringUtils.isEmpty(cols[3]) || !ValidateUtil.isDate(cols[3])) {
					errorList.add((i + 1) + "行目のチェックアウト日の日付型式が不正です");
					continue;
				}
				// 日付範囲チェック
				if (!ValidateUtil.isDateRange(cols[2], cols[3], "yyyy/MM/dd")) {
					errorList.add((i + 1) + "行目のチェックイン～アウトの範囲が不正です");
					continue;
				}
			}
			if (errorList.size() != 0)
				errorMessage = String.join("\n", errorList);
		}

		//
		if (errorMessage == null) {
			logger.info("check-in-csv upload");
			List<AdminRoomForm> list = new ArrayList<AdminRoomForm>();
			for (String line : textList) {
				AdminRoomForm form = new AdminRoomForm();
				String[] cols = line.split(",", -1);
				form.setRoomNo(cols[0]);
				form.setFromDtStr(cols[2].replace("/", "-"));
				form.setToDtStr(cols[3].replace("/", "-"));
				form.setCheck("check");
				list.add(form);
				logger.info(line);
			}

			AdminRoomListForm formList = new AdminRoomListForm();
			formList.setAdminRoomListForm(list);
			userService.registUserByForm(formList);
		}

		return errorMessage;
	}
}
