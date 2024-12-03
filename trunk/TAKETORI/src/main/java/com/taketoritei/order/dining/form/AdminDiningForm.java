package com.taketoritei.order.dining.form;

import java.util.Date;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDiningForm {

	private Date days;

	private String roomNo;
	
	private Integer customerNum;

    private String dinner;

	private String dinnerTime;

	private String dinnerPlace;

    private Integer breakfastJapanese;

    private Integer breakfastWestern;
    
	private String breakfastTime;

	private String breakfastPlace;

	private Integer lunchNum;

	private String lunchTime;

	private String memo;

    
    //---
    private String displayDays;

	private Integer totalNum;

	private String lunchText;
}
