package com.taketoritei.order.omiyage.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOmiyageOrderPdfDownloadForm {

	private String pdfType;
	private String fromDate;
	private String fromTime;
	private String toDate;
	private String toTime;
	private String checked;
}
