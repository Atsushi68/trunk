package com.taketoritei.order.omiyage.form;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOmiyageMasterSearchForm {

	private String freeword;

	private boolean del;

	private String category;

	private int offset = 0;

	private int limit = 0;

}
