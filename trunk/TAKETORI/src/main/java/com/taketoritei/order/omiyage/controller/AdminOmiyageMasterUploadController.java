package com.taketoritei.order.omiyage.controller;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.apache.commons.lang3.StringUtils;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.taketoritei.order.common.Consts.OmiyageCategoryEnum;
import com.taketoritei.order.common.controller.AdminBaseController;
import com.taketoritei.order.common.validation.ValidateUtil;
import com.taketoritei.order.omiyage.service.OmiyageMasterService;

/**
 * お土産マスタアップロード
 */
@Controller
public class AdminOmiyageMasterUploadController extends AdminBaseController {

	@Autowired
	private OmiyageMasterService service;

	@Autowired
    private MessageSource messages;

	/**
	 * 管理画面 お土産マスタアップロード 初期表示
	 */
	@GetMapping("/admin/omiyage/master/upload")
	public String adminMasterUpload(Model model) {

		return "omiyage/admin_omiyage_master_upload";
	}

	/**
	 * 管理画面 お土産マスタアップロード アップロード
	 */
	@PostMapping("/admin/omiyage/master/upload")
	public String adminUpload(@RequestParam("upload_file") MultipartFile multipartFile, Model model) {

		List<List<String>> list = new ArrayList<>();
		List<String> check = new ArrayList<String>();
		try {

			InputStream in = multipartFile.getInputStream();
			Workbook book = WorkbookFactory.create(in);

			// 最初のワークシート
			Sheet sheet = book.getSheetAt(0);

			int rowCnt = 2;
			while (true) {

				Row row = sheet.getRow(rowCnt);
				if (row == null)
					break;

				boolean b = false;
				List<String> lineList = new ArrayList<>();

				lineList.add(String.valueOf(rowCnt + 1));
				for (int col = 0; col < 35; col++) {

					Cell cell = row.getCell(col);
					String val = cell.getStringCellValue();
					lineList.add(val);

					if (!StringUtils.isEmpty(val))
						b = true;
				}

				if (!b)
					break;

				// 空を埋める
				while (lineList.size() == 36)
					lineList.add("");

				list.add(lineList);
				rowCnt++;
			}

			in.close();
		} catch (Exception e) {
			e.printStackTrace();
			check.add("Excel解析でエラーが発生しました");
		}

		// チェック
		if (check.size() == 0) {
			for (List<String> lineList : list) {

				String line = lineList.get(0);
				String omiyageId = lineList.get(1);
				String categorys = lineList.get(2);
				String price = lineList.get(4);
				String omomi = lineList.get(5);

				// お土産ID
				if (!ValidateUtil.isPositiveInteger(omiyageId))
					check.add(line + "行目 : " + messages.getMessage("error.integer.positive", new Object[] {"お土産ID"}, Locale.getDefault()));
				// カテゴリ
				String[] category = categorys.split(",", -1);
				for (String cat : category) {
					boolean b = false;
					for (OmiyageCategoryEnum catEnum : OmiyageCategoryEnum.values())
						if (catEnum.getCode().equals(cat))
							b = true;
					if (!b) {
						check.add(line + "行目 : " + messages.getMessage("error.category", new Object[] {}, Locale.getDefault()));
						break;
					}
				}
				// 金額
				if (StringUtils.isEmpty(price))
					check.add(line + "行目 : " + messages.getMessage("error.require", new Object[] {"金額"}, Locale.getDefault()));
				else if (!ValidateUtil.isPositiveInteger(price))
					check.add(line + "行目 : " + messages.getMessage("error.integer.positive", new Object[] {"金額"}, Locale.getDefault()));
				// 重み
				if (StringUtils.isEmpty(omomi))
					check.add(line + "行目 : " + messages.getMessage("error.require", new Object[] {"表示優先度"}, Locale.getDefault()));
				else if (!ValidateUtil.isPositiveInteger(omomi))
					check.add(line + "行目 : " + messages.getMessage("error.integer.positive", new Object[] {"表示優先度"}, Locale.getDefault()));
			}
		}


		if (check.size() == 0) {
			// 登録
			service.uploadOmiyageMasterExcel(list);
			// メッセージ
			model.addAttribute("message", "お土産マスタを更新しました");
		} else {
			// エラーメッセージ
			model.addAttribute("check", check);
		}

		return "omiyage/admin_omiyage_master_upload";
	}

}
