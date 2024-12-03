package com.taketoritei.order.reserve.bath.form;

import com.taketoritei.order.jooq.tables.records.MTimeRecord;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class TimeForm extends MTimeRecord {

	public String next;
}
