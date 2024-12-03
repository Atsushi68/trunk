package com.taketoritei.order.reserve.bath.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminBathUsageForm {

	// 入力の日付
	private String reserveInputDate;

	// 一覧の表示日付
	private String reserveDate;

	private String timeCd;

	private String bathCd;

	private String roomNo;

	private String displayDate;
}
