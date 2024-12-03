package com.taketoritei.order.omiyage.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOmiyageReportInvoiceMeisai {

	private String roomNo; // 客室番号
	private String omiyageName; // お土産名
	private String num; // 個数
	private String price; // 単価
	private String shokeiPrice; // 個数×単価
}
