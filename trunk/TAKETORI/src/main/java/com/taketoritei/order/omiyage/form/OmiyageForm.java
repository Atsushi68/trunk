package com.taketoritei.order.omiyage.form;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OmiyageForm {

	@NotBlank
	String[] mainCategory;
	String subCategory;
	String category; // 最終的にDBに登録するカテゴリ


	@NotBlank
	String name;

	@NotNull
	MultipartFile imagePath;

	@NotNull
	Integer price;

	String detail;

	String amountProduct;
	String rawMaterials;
	String allergie;

	@NotNull
	Integer sortOmomi;
}
