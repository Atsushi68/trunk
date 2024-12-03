package com.taketoritei.order.common;

import java.util.Calendar;
import java.util.Date;

import org.apache.commons.lang3.StringUtils;
import org.springframework.context.annotation.Configuration;

@Configuration(value="thymeleafUtil")
public class ThymeleafUtil {

	public static String getDayOfWeek(Date date) {

		String result = "";

		// 無い場合は処理しない
		if (null == date) {
			return result;
		}

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		String dayOfWeek = "";

		switch (cal.get(Calendar.DAY_OF_WEEK)) {
		case Calendar.SUNDAY:
			dayOfWeek = "(日)";
	        break;
	    case Calendar.MONDAY:
			dayOfWeek = "(月)";
	        break;
	    case Calendar.TUESDAY:
			dayOfWeek = "(火)";
	        break;
	    case Calendar.WEDNESDAY:
			dayOfWeek = "(水)";
	        break;
	    case Calendar.THURSDAY:
			dayOfWeek = "(木)";
	        break;
	    case Calendar.FRIDAY:
			dayOfWeek = "(金)";
	        break;
	    case Calendar.SATURDAY:
			dayOfWeek = "(土)";
	        break;
		}
		return dayOfWeek;
	}

	/**
	 * 指定した文字数分のみ表示 省略は...をつける
	 * @return
	 */
	public static String displayTextMaxLength(String text) {
		return displayTextMaxLength(text, 50);
	}
	public static String displayTextMaxLength(String text, int displayLength) {
		return StringUtils.abbreviate(text, displayLength);
	}
}
