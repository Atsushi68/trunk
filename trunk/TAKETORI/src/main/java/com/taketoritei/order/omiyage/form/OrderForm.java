package com.taketoritei.order.omiyage.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class OrderForm {
	String token;
	int omiyageId;
	int price;
	String name;
	int num;
	String imageExt;
}
