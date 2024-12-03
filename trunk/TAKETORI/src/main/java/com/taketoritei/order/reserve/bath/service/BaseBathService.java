package com.taketoritei.order.reserve.bath.service;

import static com.taketoritei.order.jooq.tables.MTime.*;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import org.jooq.Result;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.stereotype.Service;

import com.taketoritei.order.common.service.BaseService;
import com.taketoritei.order.jooq.tables.records.MTimeRecord;
import com.taketoritei.order.reserve.bath.form.TimeForm;

@Service
public class BaseBathService extends BaseService {

	@Autowired
	MessageSource messages;

	/**
	 * 選択可能なm_timeの全件を取得
	 */
	public List<TimeForm> getSelectableTimeList() {
		return getSelectableTimeList(Locale.getDefault());
	}
	public List<TimeForm> getSelectableTimeList(Locale locale) {

		Result<MTimeRecord> timeRecs = jooq.selectFrom(M_TIME) //
				.where(M_TIME.SELECTABLE.eq(true))
				.orderBy(M_TIME.TIME_CD.asc())
				.fetch();

		List<TimeForm> list = new ArrayList<>();
		timeRecs.forEach(rec -> {
			list.add(createTimeForm(rec, locale));
		});

		return list;
	}

	/**
	 * MTimeRecordをTimeFormに変換する
	 */
	protected TimeForm createTimeForm(MTimeRecord rec) {
		return createTimeForm(rec, Locale.getDefault());
	}
	protected TimeForm createTimeForm(MTimeRecord rec, Locale locale) {
		TimeForm form = new TimeForm();
		// コピー
		BeanUtils.copyProperties(rec, form);
		// "翌"の設定
		if (form.getNextDay())
			form.setNext(messages.getMessage("bath.37", null, locale) + "  ");
		else
			form.setNext("");

		return form;
	}
}
