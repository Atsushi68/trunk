package com.taketoritei.order.common.validation;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.lang3.StringUtils;
import org.springframework.web.multipart.MultipartFile;

/**
 * チェック
 */
public class ValidateUtil {

	/**
	 * 日付妥当性チェック
	 *
	 * @param str yyyy-MM-dd or yyyy/MM/dd 型式
	 * @return
	 */
	public static boolean isDate(String str) {
		if (StringUtils.isEmpty(str))
			return true;

		str = str.replace("/", "-");

		// 桁チェック
		if (str.length() != 10) {
			return false;
		}

		try {
			// フォーマットチェック
			DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
			df.setLenient(false);
			String convDateStr = df.format(df.parse(str));

			return str.equals(convDateStr);
		} catch (Exception p) {

		}
		return false;
	}

	/**
	 * 日付比較
	 * @param from
	 * @param to
	 * @param format
	 * @return
	 * @throws Exception
	 */
	public static boolean isDateRange(String from, String to, String format) {
		if (!StringUtils.isEmpty(from) && !StringUtils.isEmpty(to)) {
			if (isDate(from) && isDate(to)) {
				try {
					DateFormat df = new SimpleDateFormat(format);
					df.setLenient(false);

					Date fromDate = df.parse(from);
					Date toDate = df.parse(to);

					return toDate.after(fromDate);
				} catch (Exception p) {
				}
			}
		} else {
			return true;
		}

		return false;
	}


	/**
	 * 時間妥当性チェック
	 *
	 * @param str HH:mm型式
	 * @return
	 */
	public static boolean isTime(String str) {
		if (StringUtils.isEmpty(str))
			return true;

		// 桁チェック
		if (str.length() != 5) {
			return false;
		}

		try {
			// フォーマットチェック
			DateFormat df = new SimpleDateFormat("HH:mm");
			df.setLenient(false);
			String convDateStr = df.format(df.parse(str));

			return str.equals(convDateStr);
		} catch (Exception p) {

		}
		return false;
	}

	/**
	 * 正の整数妥当性チェック
	 *
	 * @param str
	 * @return
	 */
	public static boolean isPositiveInteger(String str) {
		if (StringUtils.isEmpty(str))
			return true;

		try {
			int i = Integer.parseInt(str);
			if (i >= 0) {
				return true;
			}
		} catch (Exception p) {
		}
		return false;
	}


	/**
	 * 整数妥当性チェック
	 *
	 * @param str
	 * @return
	 */
	public static boolean isInteger(String str) {
		if (StringUtils.isEmpty(str))
			return true;

		try {
			Integer.parseInt(str);
			return true;
		} catch (Exception p) {
		}
		return false;
	}

	/**
	 * アップロードファイルが画像形式かをチェックする
	 */
	public static boolean isImageFile(MultipartFile multipartFile) {

		if (multipartFile == null)
			return false;

		String name = multipartFile.getOriginalFilename();
		String dotExt = name.substring(name.lastIndexOf(".")).toUpperCase();

		// 拡張子チェック
		String[] exts = new String[] { ".JPG", ".JEPG", ".PNG", ".GIF", ".BMP", ".TIFF" };
		boolean extExists = false;
		for (String e : exts) {
			if (e.equals(dotExt)) {
				extExists = true;
				break;
			}
		}
		if (!extExists)
			return false;

		// 画像ファイルチェック
		try {
			InputStream st = new ByteArrayInputStream(multipartFile.getBytes());
			BufferedImage bi = ImageIO.read(st);
			return (bi != null);
		}catch (Exception e) {
			return false;
		}
	}

}