package com.taketoritei.order.omiyage.form;

import com.taketoritei.order.jooq.tables.records.MOmiyageLangRecord;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOmiyageLangMasterForm {

	private String tags;

	private String name;

	private String detail;

	private String amountProduct;

	private String rawMaterials;

	private String allergie;

	public AdminOmiyageLangMasterForm() {
	}

	public AdminOmiyageLangMasterForm(MOmiyageLangRecord rec) {
		if (rec != null) {
			this.tags = rec.getTags();
			this.name = rec.getName();
			this.detail = rec.getDetail();
			this.amountProduct = rec.getAmountProduct();
			this.rawMaterials = rec.getRawMaterials();
			this.allergie = rec.getAllergie();
		}
	}
}
