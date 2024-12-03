package com.taketoritei.order.omiyage.entity;

import java.util.ArrayList;
import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOmiyageReportInvoice {

	private String roomNo;
	private String invoiceNo;
	private String totalPrice;
	private int totalNum = 0;
	private List<AdminOmiyageReportInvoiceMeisai> list = new ArrayList<AdminOmiyageReportInvoiceMeisai>();
}
