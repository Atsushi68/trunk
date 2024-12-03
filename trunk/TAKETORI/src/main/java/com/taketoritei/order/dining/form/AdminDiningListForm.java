package com.taketoritei.order.dining.form;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.validation.Valid;

import org.jooq.Record;
import org.jooq.Result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminDiningListForm {

	public AdminDiningListForm() {
	}

	public AdminDiningListForm(Result<Record> roomUserList) {

		Timestamp now = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		// 次の日を設定
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);
	}

	@Valid
	private List<AdminDiningForm> adminDiningListForm = new ArrayList<AdminDiningForm>();
}
