package com.taketoritei.order.omiyage.service;

import static com.taketoritei.order.jooq.tables.MOmiyage.*;
import static com.taketoritei.order.jooq.tables.MOmiyageLang.*;
import static com.taketoritei.order.jooq.tables.VmOmiyage.*;

import java.io.File;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.StringJoiner;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.jooq.impl.DSL;
import org.json.JSONArray;
import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import com.taketoritei.order.common.Consts.LangEnum;
import com.taketoritei.order.common.FileUtil;
import com.taketoritei.order.common.service.BaseService;
import com.taketoritei.order.jooq.tables.records.MOmiyageLangRecord;
import com.taketoritei.order.jooq.tables.records.MOmiyageRecord;
import com.taketoritei.order.jooq.tables.records.VmOmiyageRecord;
import com.taketoritei.order.omiyage.form.AdminOmiyageLangMasterForm;
import com.taketoritei.order.omiyage.form.AdminOmiyageMasterForm;
import com.taketoritei.order.omiyage.form.AdminOmiyageMasterSearchForm;

@Service
@Transactional(rollbackFor = Exception.class)
public class OmiyageMasterService extends BaseService {

	@Autowired
	private Environment env;

	/**
	 * お土産マスタを取得
	 */
	public VmOmiyageRecord getOmiyageMaster(String omiyageId, String locale) {

		VmOmiyageRecord rec = jooq //
				.selectFrom(VM_OMIYAGE) //
				.where(VM_OMIYAGE.LANGUAGE.eq(locale)) //
				.and(VM_OMIYAGE.OMIYAGE_ID.eq(Integer.parseInt(omiyageId))) //
				.orderBy(VM_OMIYAGE.SORT_OMOMI, VM_OMIYAGE.LAST_DATE) //
				.fetchOne();

		return rec;
	}


	/**
	 * お土産マスタ検索件数を取得
	 */
	public int getCountOmiyageMasterList(AdminOmiyageMasterSearchForm form, String locale) {

		SelectConditionStep<VmOmiyageRecord> step = jooq //
				.selectFrom(VM_OMIYAGE) //
				.where(VM_OMIYAGE.LANGUAGE.eq(locale));
		if (!StringUtils.isEmpty(form.getCategory())) {
			String category = form.getCategory();
			step.and(VM_OMIYAGE.CATEGORY.contains(category)); //

		} else 	if (!StringUtils.isEmpty(form.getFreeword())) {
			String freeword = form.getFreeword();
			freeword = freeword.replace("　", "");

			String[] freewords = freeword.split(" ", -1);
			for (String w : freewords) {
				step.and(VM_OMIYAGE.CATEGORY.contains(w) //
						.or(VM_OMIYAGE.TAGS.contains(w)) //
						.or(VM_OMIYAGE.NAME.contains(w)) //
						.or(VM_OMIYAGE.DETAIL.contains(w)) //
						.or(VM_OMIYAGE.AMOUNT_PRODUCT.contains(w)) //
						.or(VM_OMIYAGE.RAW_MATERIALS.contains(w)) //
						.or(VM_OMIYAGE.ALLERGIE.contains(w))); //
			}
		}
		if (!form.isDel()) {
			step.and(VM_OMIYAGE.DEL_FLG.eq(false));
		}
		return jooq.fetchCount(step.orderBy(VM_OMIYAGE.SORT_OMOMI.desc(), VM_OMIYAGE.LAST_DATE.desc()));
	}


	/**
	 * お土産マスタ全件を取得
	 */
	public List<VmOmiyageRecord> getOmiyageMasterList(AdminOmiyageMasterSearchForm form, String locale) {

		SelectConditionStep<VmOmiyageRecord> step = jooq //
				.selectFrom(VM_OMIYAGE) //
				.where(VM_OMIYAGE.LANGUAGE.eq(locale));
		if (!StringUtils.isEmpty(form.getCategory())) {
			String category = form.getCategory();
			step.and(VM_OMIYAGE.CATEGORY.contains(category)); //

		} else 	if (!StringUtils.isEmpty(form.getFreeword())) {
			String freeword = form.getFreeword();
			freeword = freeword.replace("　", "");

			String[] freewords = freeword.split(" ", -1);
			for (String w : freewords) {
				step.and(VM_OMIYAGE.CATEGORY.contains(w) //
						.or(VM_OMIYAGE.TAGS.contains(w)) //
						.or(VM_OMIYAGE.NAME.contains(w)) //
						.or(VM_OMIYAGE.DETAIL.contains(w)) //
						.or(VM_OMIYAGE.AMOUNT_PRODUCT.contains(w)) //
						.or(VM_OMIYAGE.RAW_MATERIALS.contains(w)) //
						.or(VM_OMIYAGE.ALLERGIE.contains(w))); //
			}
		}
		if (!form.isDel()) {
			step.and(VM_OMIYAGE.DEL_FLG.eq(false));
		}

		// limitとoffsetがあるかで処理を分ける
		Result<VmOmiyageRecord> recs = null;
		if (form.getLimit() == 0 && form.getOffset() == 0) {
			recs = step //
					.orderBy(VM_OMIYAGE.SORT_OMOMI.desc(), VM_OMIYAGE.LAST_DATE.desc()) //
					.fetch();
		} else {
			recs = step //
					.orderBy(VM_OMIYAGE.SORT_OMOMI.desc(), VM_OMIYAGE.LAST_DATE.desc()) //
					.limit(form.getLimit())
					.offset(form.getOffset())
					.fetch();
		}

		List<VmOmiyageRecord> list = new ArrayList<>();
		recs.forEach(rec -> {
			list.add(rec);
		});
		return list;
	}

	/**
	 * お土産マスタ編集用に全言語分レコードを含んだformを取得する
	 */
	public AdminOmiyageMasterForm getOmiyageMasterForm(int id) {

		AdminOmiyageMasterForm form = new AdminOmiyageMasterForm();

		MOmiyageRecord rec = jooq //
				.selectFrom(M_OMIYAGE) //
				.where(M_OMIYAGE.OMIYAGE_ID.eq(id)) //
				.fetchOne();

		if (rec != null) {
			form.setOmiyageId(String.valueOf(rec.getOmiyageId()));
			form.setCategory(rec.getCategory().split(",", -1));
			form.setImageExt(rec.getImageExt());
			form.setPrice(String.valueOf(rec.getPrice()));
			form.setOmomi(String.valueOf(rec.getSortOmomi()));
			form.setDelFlg(rec.getDelFlg());
			form.setJa(getOmiyageLangForm(id, LangEnum.LANG_JP.getCode()));
			form.setEn(getOmiyageLangForm(id, LangEnum.LANG_EN.getCode()));
			form.setZhCn(getOmiyageLangForm(id, LangEnum.LANG_CN.getCode()));
			form.setZhTw(getOmiyageLangForm(id, LangEnum.LANG_TW.getCode()));
			form.setKo(getOmiyageLangForm(id, LangEnum.LANG_KO.getCode()));
		}

		return form;
	}

	private AdminOmiyageLangMasterForm getOmiyageLangForm(int id, String lang) {

		MOmiyageLangRecord rec = jooq //
				.selectFrom(M_OMIYAGE_LANG) //
				.where(M_OMIYAGE_LANG.OMIYAGE_ID.eq(id)) //
				.and(M_OMIYAGE_LANG.LANGUAGE.eq(lang)) //
				.fetchOne();

		AdminOmiyageLangMasterForm form = new AdminOmiyageLangMasterForm(rec);

		return form;
	}

	/**
	 * お土産マスタを登録する
	 */
	public void registOmiyageMaster(AdminOmiyageMasterForm form) {
		//
		int omiyageId;

		// カテゴリをカンマ区切りで結合する
		StringJoiner sj = new StringJoiner(",");
		Arrays.stream(form.getCategory()).forEach(i -> sj.add(i));

		if (StringUtils.isEmpty(form.getOmiyageId())) {
			// 新規
			MOmiyageRecord rec = jooq //
					.insertInto(
							M_OMIYAGE, //
							M_OMIYAGE.CATEGORY, //
							M_OMIYAGE.PRICE, //
							M_OMIYAGE.SORT_OMOMI, //
							M_OMIYAGE.DEL_FLG, //
							M_OMIYAGE.LAST_DATE, //
							M_OMIYAGE.LAST_USER) //
					.values(
							sj.toString(), //
							Integer.parseInt(form.getPrice()), //
							Integer.parseInt(form.getOmomi()), //
							form.isDelFlg(), //
							getLastDate(), //
							getLastUser()) //
					.returning(M_OMIYAGE.OMIYAGE_ID) //
					.fetchOne();
			omiyageId = rec.getOmiyageId();

		} else {
			// 更新
			omiyageId = Integer.parseInt(form.getOmiyageId());
			jooq.update(M_OMIYAGE) //
					.set(M_OMIYAGE.CATEGORY, sj.toString()) //
					.set(M_OMIYAGE.PRICE, Integer.parseInt(form.getPrice())) //
					.set(M_OMIYAGE.SORT_OMOMI, Integer.parseInt(form.getOmomi())) //
					.set(M_OMIYAGE.DEL_FLG, form.isDelFlg()) //
					.set(M_OMIYAGE.LAST_DATE, getLastDate()) //
					.set(M_OMIYAGE.LAST_USER, getLastUser()) //
					.where(M_OMIYAGE.OMIYAGE_ID.eq(omiyageId)) //
					.execute(); //
		}

		// langはdelete insert
		jooq.delete(M_OMIYAGE_LANG) //
				.where(M_OMIYAGE_LANG.OMIYAGE_ID.eq(omiyageId)) //
				.execute(); //
		insertOmiyageLang(omiyageId, LangEnum.LANG_JP.getCode(), form.getJa());
		insertOmiyageLang(omiyageId, LangEnum.LANG_EN.getCode(), form.getEn());
		insertOmiyageLang(omiyageId, LangEnum.LANG_CN.getCode(), form.getZhCn());
		insertOmiyageLang(omiyageId, LangEnum.LANG_TW.getCode(), form.getZhTw());
		insertOmiyageLang(omiyageId, LangEnum.LANG_KO.getCode(), form.getKo());
	}

	private void insertOmiyageLang(int omiyageId, String lang, AdminOmiyageLangMasterForm form) {
		jooq.insertInto(
				M_OMIYAGE_LANG, //
				M_OMIYAGE_LANG.OMIYAGE_ID, //
				M_OMIYAGE_LANG.LANGUAGE, //
				M_OMIYAGE_LANG.TAGS, //
				M_OMIYAGE_LANG.NAME, //
				M_OMIYAGE_LANG.DETAIL, //
				M_OMIYAGE_LANG.AMOUNT_PRODUCT, //
				M_OMIYAGE_LANG.RAW_MATERIALS, //
				M_OMIYAGE_LANG.ALLERGIE) //
				.values(
						omiyageId, //
						lang, //
						form.getTags(), //
						form.getName(), //
						form.getDetail(), //
						form.getAmountProduct(), //
						form.getRawMaterials(), //
						form.getAllergie()) //
				.execute();
	}

	/**
	 * お土産画像を登録する
	 */
	public void registOmiyageImage(MultipartFile multipartFile, String omiyageId) {

		String dir = env.getRequiredProperty("omiyage.image-dir");

		String name = multipartFile.getOriginalFilename();
		String ext = name.substring(name.lastIndexOf(".")).replace(".", "");

		// 更新
		jooq.update(M_OMIYAGE) //
				.set(M_OMIYAGE.IMAGE_EXT, ext) //
				.set(M_OMIYAGE.LAST_DATE, getLastDate()) //
				.set(M_OMIYAGE.LAST_USER, getLastUser()) //
				.where(M_OMIYAGE.OMIYAGE_ID.eq(Integer.parseInt(omiyageId))) //
				.execute(); //

		// ファイル保存
		try {
			// ファイル削除
			File d = new File(dir);
			if(d.exists() && d.isDirectory()) {
				File[] files = d.listFiles();
				for(File f : files)
	                if (f.exists() && f.isFile() && f.getName().startsWith(omiyageId + "."))
	                	f.delete();
			}

			// フォルダ作成
			FileUtil.createFolder(dir);
			// パス
			Path uploadfile = Paths.get(dir + omiyageId + "." + ext);
			// 保存
	        OutputStream os = Files.newOutputStream(uploadfile, StandardOpenOption.CREATE);
	        byte[] bytes = multipartFile.getBytes();
	        os.write(bytes);

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * お土産画像を削除する
	 */
	public void deleteOmiyageImage(String omiyageId) {

		String dir = env.getRequiredProperty("omiyage.image-dir");

		// 更新
		jooq.update(M_OMIYAGE) //
				.set(M_OMIYAGE.IMAGE_EXT, DSL.val((String) null)) //
				.set(M_OMIYAGE.LAST_DATE, getLastDate()) //
				.set(M_OMIYAGE.LAST_USER, getLastUser()) //
				.where(M_OMIYAGE.OMIYAGE_ID.eq(Integer.parseInt(omiyageId))) //
				.execute(); //

		// ファイル保存
		try {
			// ファイル削除
			File d = new File(dir);
			if(d.exists() && d.isDirectory()) {
				File[] files = d.listFiles();
				for(File f : files)
	                if (f.exists() && f.isFile() && f.getName().startsWith(omiyageId + "."))
	                	f.delete();
			}

		} catch (Exception e) {
			throw new RuntimeException(e);
		}

	}

	/**
	 * 商品検索しjsonで返す
	 * @param form
	 * @param locale
	 * @return
	 */
	public JSONArray searchJson(AdminOmiyageMasterSearchForm form, String locale) {

		JSONArray jsonArray = new JSONArray();

		// 検索
		List<VmOmiyageRecord> result = this.getOmiyageMasterList(form, locale);

		for (VmOmiyageRecord omiyageRecord : result) {
			JSONObject omiyageJson = new JSONObject();
			omiyageJson.put("id", omiyageRecord.getOmiyageId());
			omiyageJson.put("name", omiyageRecord.getName());
			omiyageJson.put("category", omiyageRecord.getCategory());
			omiyageJson.put("price", omiyageRecord.getPrice());
			jsonArray.put(omiyageJson);
		}
		return jsonArray;
	}

	/**
	 * お土産マスタをExcelアップロードする
	 * @param list
	 */
	public void uploadOmiyageMasterExcel(List<List<String>> list) {

		for (List<String> lineList : list) {

			String omiyageId = lineList.get(1);
			MOmiyageRecord rec = null;
			if (!StringUtils.isEmpty(omiyageId)) {
				rec = jooq //
						.selectFrom(M_OMIYAGE) //
						.where(M_OMIYAGE.OMIYAGE_ID.eq(Integer.parseInt(omiyageId))) //
						.fetchOne();
			}

			if (rec == null) {
				// 新規
				MOmiyageRecord newRec = jooq //
						.insertInto(
								M_OMIYAGE, //
								M_OMIYAGE.CATEGORY, //
								M_OMIYAGE.IMAGE_EXT, //
								M_OMIYAGE.PRICE, //
								M_OMIYAGE.SORT_OMOMI, //
								M_OMIYAGE.DEL_FLG, //
								M_OMIYAGE.LAST_DATE, //
								M_OMIYAGE.LAST_USER) //
						.values(
								lineList.get(2), //
								lineList.get(3), //
								Integer.parseInt(lineList.get(4)), //
								Integer.parseInt(lineList.get(5)), //
								false, //
								getLastDate(), //
								getLastUser()) //
						.returning(M_OMIYAGE.OMIYAGE_ID) //
						.fetchOne();
				omiyageId = String.valueOf(newRec.getOmiyageId());
			} else {
				// 更新
				jooq.update(M_OMIYAGE) //
						.set(M_OMIYAGE.CATEGORY, lineList.get(2)) //
						.set(M_OMIYAGE.IMAGE_EXT, lineList.get(3)) //
						.set(M_OMIYAGE.PRICE, Integer.parseInt(lineList.get(4))) //
						.set(M_OMIYAGE.SORT_OMOMI, Integer.parseInt(lineList.get(5))) //
						.set(M_OMIYAGE.LAST_DATE, getLastDate()) //
						.set(M_OMIYAGE.LAST_USER, getLastUser()) //
						.where(M_OMIYAGE.OMIYAGE_ID.eq(Integer.parseInt(omiyageId))) //
						.execute(); //
			}

			// langはdelete insert
			jooq.delete(M_OMIYAGE_LANG) //
					.where(M_OMIYAGE_LANG.OMIYAGE_ID.eq(Integer.parseInt(omiyageId))) //
					.execute(); //
			insertOmiyageLang(omiyageId, LangEnum.LANG_JP.getCode(), lineList, 6);
			insertOmiyageLang(omiyageId, LangEnum.LANG_EN.getCode(), lineList, 7);
			insertOmiyageLang(omiyageId, LangEnum.LANG_CN.getCode(), lineList, 8);
			insertOmiyageLang(omiyageId, LangEnum.LANG_TW.getCode(), lineList, 9);
			insertOmiyageLang(omiyageId, LangEnum.LANG_KO.getCode(), lineList, 10);
		}
	}

	private void insertOmiyageLang(String omiyageId, String lang, List<String> lineList, int index) {
		jooq.insertInto(
				M_OMIYAGE_LANG, //
				M_OMIYAGE_LANG.OMIYAGE_ID, //
				M_OMIYAGE_LANG.LANGUAGE, //
				M_OMIYAGE_LANG.TAGS, //
				M_OMIYAGE_LANG.NAME, //
				M_OMIYAGE_LANG.DETAIL, //
				M_OMIYAGE_LANG.AMOUNT_PRODUCT, //
				M_OMIYAGE_LANG.RAW_MATERIALS, //
				M_OMIYAGE_LANG.ALLERGIE) //
				.values(
						Integer.parseInt(omiyageId), //
						lang, //
						lineList.get(index + 25), // tag
						lineList.get(index + 0), // name
						lineList.get(index + 5), // detail
						lineList.get(index + 10), // amout_product
						lineList.get(index + 15), // raw_materials
						lineList.get(index + 20)) // allergie
				.execute();
	}

}
