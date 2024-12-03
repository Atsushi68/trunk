package com.taketoritei.order.omiyage.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class CartForm {
	String token;
	int omiyageId;
	int price;
	String name;
	int num;
	String cartToken;
	String imageExt;
}
