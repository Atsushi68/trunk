package com.taketoritei.order.omiyage.controller;

import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import javax.servlet.http.HttpServletResponse;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.core.env.Environment;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.taketoritei.order.common.Consts.LangEnum;
import com.taketoritei.order.common.FileUtil;
import com.taketoritei.order.common.controller.AdminBaseController;
import com.taketoritei.order.common.validation.ValidateUtil;
import com.taketoritei.order.omiyage.form.AdminOmiyageInputForm;
import com.taketoritei.order.omiyage.form.AdminOmiyageMasterSearchForm;
import com.taketoritei.order.omiyage.form.AdminOmiyageOrderPdfDownloadForm;
import com.taketoritei.order.omiyage.service.AdminOmiyageService;
import com.taketoritei.order.omiyage.service.OmiyageMasterService;
import com.taketoritei.order.room.service.RoomService;

/**
 * お土産管理
 */
@Controller
public class AdminOmiyageController extends AdminBaseController {

	@Autowired
	private RoomService roomService;

	@Autowired
	private OmiyageMasterService omiyageMasterService;

	@Autowired
	private AdminOmiyageService omiyageService;

	@Autowired
	private MessageSource messages;

	@Autowired
	private Environment env;

	/**
	 * 管理画面お土産初期表示
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/omiyage")
	public String adminOmiyage(Model model, AdminOmiyageOrderPdfDownloadForm form) {
		model.addAttribute("roomList", roomService.getRoomList());
		model.addAttribute("form", new AdminOmiyageOrderPdfDownloadForm());
		return "omiyage/admin_omiyage";
	}

	/**
	 * お土産注文PDFダウンロード
	 * @param model
	 * @param form
	 * @param selectRangeCheck
	 * @param response
	 * @return
	 * @throws Exception
	 */
	@PostMapping("/admin/omiyage/order/pdf/download")
	public String omiyageChecklistDownload(Model model,
			AdminOmiyageOrderPdfDownloadForm form,
			@RequestParam(name = "select_range_check", required = false) Boolean selectRangeCheck,
			HttpServletResponse response) throws Exception {

		Date now = new Date();
		SimpleDateFormat sdfDate = new SimpleDateFormat("yyyy-MM-dd");
		SimpleDateFormat sdfTime = new SimpleDateFormat("HH:mm:00");

		// PDFの種類
		String pdfType = form.getPdfType();

		// yamlから取得
		String outputPdfFile = "";
		if ("invoice".equals(pdfType)) {
			outputPdfFile = env.getRequiredProperty("omiyage.report.output-invoice-file");
		} else if ("check_list".equals(pdfType)) {
			outputPdfFile = env.getRequiredProperty("omiyage.report.output-checklist-file");
		}

		// PDFファイル読み込み用
		byte[] fileContent = null;

		// 入力チェック
		// ----------------------------------------------
		List<String> check = new ArrayList<String>();
		if (StringUtils.isEmpty(pdfType))
			check.add(messages.getMessage("error.require", new Object[] {"ダウンロード種類"}, Locale.getDefault()));
		else if ("invoice".equals(pdfType) && "check_list".equals(pdfType))
			check.add("ダウンロード種類が不正です。");

		// 範囲指定かどうか
		if ("check_list".equals(pdfType) || null == selectRangeCheck) {
			// 範囲指定ではない場合、from：なし to:現在日時をセット
			form.setFromDate("");
			form.setFromTime("");
			form.setToDate(sdfDate.format(now));
			form.setToTime(sdfTime.format(now));

		} else {

			if (StringUtils.isEmpty(form.getFromDate()))
				check.add(messages.getMessage("error.require", new Object[] {"注文日from"}, Locale.getDefault()));
			else if (!ValidateUtil.isDate(form.getFromDate()))
				check.add(messages.getMessage("error.isDate", new Object[] {"注文日from", "yyyy-MM-dd"}, Locale.getDefault()));

			if (StringUtils.isEmpty(form.getFromTime()))
				check.add(messages.getMessage("error.require", new Object[] {"注文時間from"}, Locale.getDefault()));
			else if (!ValidateUtil.isTime(form.getFromTime()))
				check.add(messages.getMessage("error.isTime", new Object[] {"注文時間from", "HH:mm"}, Locale.getDefault()));

			if (StringUtils.isEmpty(form.getToDate()))
				check.add(messages.getMessage("error.require", new Object[] {"注文日to"}, Locale.getDefault()));
			else if (!ValidateUtil.isDate(form.getToDate()))
				check.add(messages.getMessage("error.isDate", new Object[] {"注文日to", "yyyy-MM-dd"}, Locale.getDefault()));

			if (StringUtils.isEmpty(form.getToTime()))
				check.add(messages.getMessage("error.require", new Object[] {"注文時間to"}, Locale.getDefault()));
			else if (!ValidateUtil.isTime(form.getToTime()))
				check.add(messages.getMessage("error.isTime", new Object[] {"注文時間to", "HH:mm"}, Locale.getDefault()));
		}

		// エラーがある場合はエラー表示
		if (0 != check.size()) {
			model.addAttribute("roomList", roomService.getRoomList());
			model.addAttribute("form", form);
			model.addAttribute("check", check);
			model.addAttribute("selectRangeCheck", selectRangeCheck);
			return "omiyage/admin_omiyage";
		}
		// ----------------------------------------------

		// PDFを作成
		fileContent = omiyageService.createOmiyagePdf(form);
		if (null == fileContent) {
			// ファイルが無い場合
			model.addAttribute("roomList", roomService.getRoomList());
			model.addAttribute("form", form);
			model.addAttribute("check", "ダウンロードに失敗しました。");
			model.addAttribute("selectRangeCheck", selectRangeCheck);
			return "omiyage/admin_omiyage";
		}

		// 作成したPDFをダウンロード
		response.setContentType(MediaType.APPLICATION_PDF_VALUE);
		response.setHeader("Content-Disposition","attachment;filename*=UTF-8''" + URLEncoder.encode(outputPdfFile, "UTF-8"));
		FileUtil.outputSreamWrite(response, fileContent);

        return null;
	}

	/**
	 * 管理画面お土産部屋表示
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/omiyage/{roomNo}")
	public String adminOmiyageRoom(@PathVariable("roomNo") String roomNo, Model model) {

		AdminOmiyageInputForm form = new AdminOmiyageInputForm();
		form.setRoomNo(roomNo);
		model.addAttribute("form", form);
		model.addAttribute("roomNo", roomNo);

		return "omiyage/admin_omiyage_room";
	}

	/**
	 * 管理画面からのお土産追加
	 * @param model
	 * @return
	 */
	@PostMapping("/admin/omiyage/regist")
	public String adminOmiyageRegist(Model model, AdminOmiyageInputForm form) {

		// チェック
		List<String> check = new ArrayList<String>();
		if (StringUtils.isEmpty(form.getRoomNo()))
			check.add(messages.getMessage("error.require", new Object[] {"部屋番号"}, Locale.getDefault()));
		if (StringUtils.isEmpty(form.getOmiyageName())) {
			check.add(messages.getMessage("error.require", new Object[] {"お土産名"}, Locale.getDefault()));
		} else {

			// お土産名から商品IDを取得する
			String[] split = form.getOmiyageName().split(":");
			if (1 < split.length) {
				form.setOmiyageId(split[0].trim());
			} else {
				check.add(messages.getMessage("error.require", new Object[] {"お土産ID"}, Locale.getDefault()));
			}
		}
		if (StringUtils.isEmpty(form.getNum()))
			check.add(messages.getMessage("error.require", new Object[] {"個数"}, Locale.getDefault()));
		else if (!ValidateUtil.isPositiveInteger(form.getNum()))
			check.add(messages.getMessage("error.integer.positive", new Object[] {"個数"}, Locale.getDefault()));

		if (check.size() == 0) {
			// 登録
			String error = omiyageService.registOmiyageOrderByform(form);
			if ("".equals(error)) {
				// メッセージ
				model.addAttribute("message", "お土産の注文を追加しました。");
			} else {
				// エラーメッセージ
				model.addAttribute("check", error);
			}

		} else {
			// エラーメッセージ
			model.addAttribute("check", check);
		}
		model.addAttribute("form", form);
		model.addAttribute("roomNo", form.getRoomNo());
		return "omiyage/admin_omiyage_room";
	}

	/**
	 * 管理画面からのお土産注文キャンセル
	 * @param model
	 * @return
	 */
	@PostMapping("/admin/omiyage/cancel")
	public String adminOmiyageCancel(
			@RequestParam(name = "order_id", required = true) Integer orderId,
			@RequestParam(name = "room_no", required = true) String roomNo,
			RedirectAttributes redirectAttributes) {

		omiyageService.deleteOmiyageOrder(orderId);
		redirectAttributes.addFlashAttribute("message", roomNo + "号室のお土産の注文を取り消しました。");
		return "redirect:/admin/omiyage/" + roomNo;
	}

	/**
	 * 管理画面からの迷子注文処理済
	 * @param model
	 * @return
	 */
	@PostMapping("/admin/omiyage/lost/order/processed")
	public String adminOmiyageLostOrderProcessed(
			@RequestParam(name = "lost_order_id", required = true) Integer orderId,
			@RequestParam(name = "lost_room_no", required = true) String roomNo,
			RedirectAttributes redirectAttributes) {

		omiyageService.deleteOmiyageOrderRoom(orderId);
		redirectAttributes.addFlashAttribute("message", roomNo + "号室のお土産の注文を処理済にしました。");
		return "redirect:/admin/omiyage/" + roomNo;
	}

	/**
	 * 管理画面お土産オートコンプリート取得API
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/omiyage/autocomplete")
	@ResponseBody
	public String adminOmiyageAutocomplete(@RequestParam("search") String search) {

		AdminOmiyageMasterSearchForm adminOmiyageMasterSearchForm = new AdminOmiyageMasterSearchForm();
		adminOmiyageMasterSearchForm.setFreeword(search);
		return omiyageMasterService.searchJson(adminOmiyageMasterSearchForm, LangEnum.LANG_JP.getCode()).toString();
	}

	/**
	 * 管理画面お土産注文一覧取得API
	 * 金額とお土産名で個数をまとめる
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/omiyage/order/{roomNo}")
	@ResponseBody
	public String adminOmiyageOrder(@PathVariable("roomNo") String roomNo) {

		if ("null".equals(roomNo)) {
			return omiyageService.getOmiyageListJson().toString();
		}
		return omiyageService.getOmiyageJsonByRoom(roomNo).toString();
	}

	/**
	 * 管理画面迷子注文一覧取得API
	 * 金額とお土産名で個数をまとめる
	 * @param model
	 * @return
	 */
	@GetMapping("/admin/omiyage/lost/order/{roomNo}")
	@ResponseBody
	public String adminOmiyageLostOrder(@PathVariable("roomNo") String roomNo) {

		// 迷子注文情報を取得
		if ("null".equals(roomNo)) {
			roomNo = null;
		}
		return omiyageService.getOmiyageLostOrderJson(LangEnum.LANG_JP.getCode(), roomNo).toString();
	}
}
