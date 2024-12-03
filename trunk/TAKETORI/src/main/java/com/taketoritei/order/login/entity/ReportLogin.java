package com.taketoritei.order.login.entity;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ReportLogin {

	// 2個ずつレポートに出す
	private String roomNo1;
	private String token1;
	private String fromDtStr1;
	private String toDtStr1;

	private String roomNo2;
	private String token2;
	private String fromDtStr2;
	private String toDtStr2;

}
