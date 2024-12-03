package com.taketoritei.order.common;

import java.util.Calendar;
import java.util.Date;

public class DateUtil {

	public static String getBussinessUserDateYmd() {
		return getBussinessUserDateYmd(new Date());
	}
	public static String getBussinessUserDateYmd(Date date) {
		return getBussinessUserDate(date).replace("/", "");
	}
	public static String getBussinessUserDate() {

		return getBussinessUserDate(new Date());
	}

	public static String getBussinessUserDate(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		// 12時より前の場合は営業日は前日で扱う
		if (cal.get(Calendar.HOUR_OF_DAY) < 10)
			cal.add(Calendar.DAY_OF_MONTH, -1);

		String y = String.format("%04d", cal.get(Calendar.YEAR));
		String m = String.format("%02d", cal.get(Calendar.MONTH) + 1);
		String d = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

		return y + "/" +  m + "/" + d;
	}


	/**
	 * 営業日を取得する。12時までは前日扱い (スラッシュなし)
	 * @return
	 */
	public static String getBussinessDateYmd() {
		return getBussinessDateYmd(new Date());
	}

	public static String getBussinessDateYmd(Date date) {
		return getBussinessDate(date).replace("/", "");
	}

	/**
	 * 営業日を取得する。12時までは前日扱い
	 * @return
	 */
	public static String getBussinessDate() {

		return getBussinessDate(new Date());
	}

	public static String getBussinessDate(Date date) {

		Calendar cal = Calendar.getInstance();
		cal.setTime(date);

		// 12時より前の場合は営業日は前日で扱う
		if (cal.get(Calendar.HOUR_OF_DAY) < 11)
			cal.add(Calendar.DAY_OF_MONTH, -1);

		String y = String.format("%04d", cal.get(Calendar.YEAR));
		String m = String.format("%02d", cal.get(Calendar.MONTH) + 1);
		String d = String.format("%02d", cal.get(Calendar.DAY_OF_MONTH));

		return y + "/" +  m + "/" + d;
	}

	/**
	 * 現在時間の時間コード比較用の時間(12～36時間表記)を取得する。
	 * @return
	 */
	public static String getHmForTimeCode() {

		Calendar cal = Calendar.getInstance();
		cal.setTime(new Date());

		// 0～11時は +24時表記
		int h = cal.get(Calendar.HOUR_OF_DAY);
		if (h < 11)
			h += 24;

		String hh = String.format("%02d", h);
		String mm = String.format("%02d", cal.get(Calendar.MINUTE));

		return hh + mm;
	}

}
