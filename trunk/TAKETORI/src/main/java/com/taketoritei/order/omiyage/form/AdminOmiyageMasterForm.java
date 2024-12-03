package com.taketoritei.order.omiyage.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOmiyageMasterForm {

	private String omiyageId;

	private String[] category;

	private String imageExt;

	private String price;

	private boolean delFlg;

	private String omomi;

	private AdminOmiyageLangMasterForm ja = new AdminOmiyageLangMasterForm();

	private AdminOmiyageLangMasterForm en = new AdminOmiyageLangMasterForm();

	private AdminOmiyageLangMasterForm zhCn = new AdminOmiyageLangMasterForm();

	private AdminOmiyageLangMasterForm zhTw = new AdminOmiyageLangMasterForm();

	private AdminOmiyageLangMasterForm ko = new AdminOmiyageLangMasterForm();

	public AdminOmiyageMasterForm() {
	}

}
