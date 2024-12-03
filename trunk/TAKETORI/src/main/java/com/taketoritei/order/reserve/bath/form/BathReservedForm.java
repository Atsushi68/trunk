package com.taketoritei.order.reserve.bath.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class BathReservedForm {

	private String timeCd;

	private String timeNm;

	private String bathCd;

	private String bathNm;

	private boolean overTime;

	// 時間中フラグ（ユーザー側でキャンセルボタンを押せなくする）
	private boolean inTime = false;
}
