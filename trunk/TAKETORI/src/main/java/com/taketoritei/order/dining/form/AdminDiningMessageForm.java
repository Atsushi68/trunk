package com.taketoritei.order.dining.form;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDiningMessageForm {

	private Date days;

	private Integer diningPlaceId;

    private String message;

	private String displayDays;

}
