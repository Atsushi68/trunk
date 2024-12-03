package com.taketoritei.order.room.form;

import java.sql.Timestamp;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRoomForm {

	private String check;

	private String roomNo;

	private Timestamp fromDt;

	private Timestamp toDt;

	private String fromDtStr;

	private String toDtStr;

	private Boolean delFlg;

	private Timestamp lastDate;

	// TEST
	private String token;
}
