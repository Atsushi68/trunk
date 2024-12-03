package com.taketoritei.order.omiyage.controller;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.taketoritei.order.common.Consts.LangEnum;
import com.taketoritei.order.common.Consts.OmiyageCategoryEnum;
import com.taketoritei.order.common.controller.AdminBaseController;
import com.taketoritei.order.common.validation.ValidateUtil;
import com.taketoritei.order.jooq.tables.records.VmOmiyageRecord;
import com.taketoritei.order.omiyage.form.AdminOmiyageMasterForm;
import com.taketoritei.order.omiyage.form.AdminOmiyageMasterImageForm;
import com.taketoritei.order.omiyage.form.AdminOmiyageMasterSearchForm;
import com.taketoritei.order.omiyage.service.OmiyageMasterService;

/**
 * お土産マスタ
 */
@Controller
public class AdminOmiyageMasterController extends AdminBaseController {

	@Autowired
	private OmiyageMasterService service;

	@Autowired
    private MessageSource messages;

	/**
	 * 管理画面 お土産マスタ 初期表示
	 */
	@GetMapping("/admin/omiyage/master")
	public String adminOmiyageMaster(Model model) {

		AdminOmiyageMasterSearchForm form = new AdminOmiyageMasterSearchForm();

		return adminOmiyageMasterSearch(model, form);
	}

	/**
	 * 管理画面 お土産マスタ 検索
	 */
	@PostMapping("/admin/omiyage/master")
	public String adminOmiyageMasterSearch(Model model, AdminOmiyageMasterSearchForm form) {

		List<VmOmiyageRecord> list = service.getOmiyageMasterList(form, LangEnum.LANG_JP.getCode());
		model.addAttribute("list", list);
		model.addAttribute("form", form);

		return "omiyage/admin_omiyage_master";
	}

	/**
	 * 管理画面 お土産マスタ新規作成 初期表示
	 */
	@GetMapping("/admin/omiyage/master/edit")
	public String adminOmiyageMasterEdit(Model model) {

		AdminOmiyageMasterForm form = new AdminOmiyageMasterForm();
		model.addAttribute("form", form);
		model.addAttribute("categorys", OmiyageCategoryEnum.values());

		return "omiyage/admin_omiyage_master_edit";
	}

	/**
	 * 管理画面 お土産マスタ編集 初期表示
	 */
	@GetMapping("/admin/omiyage/master/edit/{id}")
	public String adminOmiyageMasterEdit(@PathVariable("id") String id, Model model) {

		AdminOmiyageMasterForm form = service.getOmiyageMasterForm(Integer.parseInt(id));
		model.addAttribute("form", form);
		model.addAttribute("categorys", OmiyageCategoryEnum.values());

		return "omiyage/admin_omiyage_master_edit";
	}

	/**
	 * 管理画面 お土産マスタ編集 登録
	 */
	@PostMapping("/admin/omiyage/master/regist")
	public String adminMasterOmiyageRegistPost(Model model, AdminOmiyageMasterForm form) {

		// チェック
		List<String> check = new ArrayList<String>();
		// 金額
		if (StringUtils.isEmpty(form.getPrice()))
			check.add(messages.getMessage("error.require", new Object[] {"金額"}, Locale.getDefault()));
		else if (!ValidateUtil.isPositiveInteger(form.getPrice()))
			check.add(messages.getMessage("error.integer.positive", new Object[] {"金額"}, Locale.getDefault()));
		// 重み
		if (StringUtils.isEmpty(form.getOmomi()))
			check.add(messages.getMessage("error.require", new Object[] {"表示優先度"}, Locale.getDefault()));
		else if (!ValidateUtil.isPositiveInteger(form.getOmomi()))
			check.add(messages.getMessage("error.integer.positive", new Object[] {"表示優先度"}, Locale.getDefault()));

		if (check.size() == 0) {
			// 登録
			service.registOmiyageMaster(form);
			// メッセージ
			model.addAttribute("message", "お土産マスタを編集しました");
		} else {
			// エラーメッセージ
			model.addAttribute("check", check);
		}

		model.addAttribute("form", form);
		model.addAttribute("categorys", OmiyageCategoryEnum.values());

		return "omiyage/admin_omiyage_master_edit";
	}

	/**
	 * 管理画面 お土産マスタ画像編集 初期表示
	 */
	@GetMapping("/admin/omiyage/master/image/{id}")
	public String adminOmiyageMasterImage(@PathVariable("id") String id, Model model) {

		AdminOmiyageMasterForm form = service.getOmiyageMasterForm(Integer.parseInt(id));

		AdminOmiyageMasterImageForm imageForm = new AdminOmiyageMasterImageForm();
		imageForm.setOmiyageId(form.getOmiyageId());
		imageForm.setImageExt(form.getImageExt());

		model.addAttribute("form", imageForm);

		return "omiyage/admin_omiyage_master_image";
	}

	/**
	 * 管理画面 お土産マスタ画像編集 画像アップロード
	 */
	@PostMapping("/admin/omiyage/master/image/regist")
	public String imageUpload(@RequestParam("upload_file") MultipartFile multipartFile, AdminOmiyageMasterImageForm form, Model model) {

		// チェック
		List<String> check = new ArrayList<String>();
		if (!ValidateUtil.isImageFile(multipartFile))
			check.add(messages.getMessage("error.image", new Object[] {}, Locale.getDefault()));

		if (check.size() == 0) {
			// 登録
			service.registOmiyageImage(multipartFile, form.getOmiyageId());
			// メッセージ
			model.addAttribute("message", "お土産画像を登録しました");

		} else {
			// エラーメッセージ
			model.addAttribute("check", check);

		}

		return adminOmiyageMasterImage(form.getOmiyageId(), model);
	}

	/**
	 * 管理画面 お土産マスタ画像編集 画像アップロード
	 */
	@PostMapping("/admin/omiyage/master/image/delete")
	public String imageDelete(AdminOmiyageMasterImageForm form, Model model) {

		// 登録
		service.deleteOmiyageImage(form.getOmiyageId());
		// メッセージ
		model.addAttribute("message", "お土産画像を削除しました");

		return adminOmiyageMasterImage(form.getOmiyageId(), model);
	}
}
