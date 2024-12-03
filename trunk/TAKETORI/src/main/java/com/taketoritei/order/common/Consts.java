package com.taketoritei.order.common;

import java.util.Arrays;

import lombok.AllArgsConstructor;
import lombok.Getter;

public class Consts {

    public static <E extends Enum<?> & CodeInterface> E valueOf(Class<E> target, String code) {
        return Arrays.stream(target.getEnumConstants())
                .filter(data -> data.getCode().equals(code))
                .findFirst()
                .orElse(null);
    }

    public interface CodeInterface {
        public String getCode();
    }

	/** お土産の大カテゴリ */
	@AllArgsConstructor
	@Getter
	public enum OmiyageCategoryEnum implements CodeInterface {
		ZAKKA("雑貨", "雑貨"),
		SHOKUHIN("食品", "食品"),
		JUICE("飲料", "飲料");

		private final String code;
		private final String label;
	}

	/** 貸切風呂風呂 */
	@AllArgsConstructor
	@Getter
	public enum BathEnum implements CodeInterface {
		BATH1("1", "一の湯"), //
		BATH2("2", "二の湯"), //
		BATH3("3", "三の湯"), //
		BATH4("4", "四の湯"), //
		BATH5("5", "五の湯"), //
		BATH6("6", "六の湯"), //
		BATH7("7", "七の湯"), //
		BATH8("8", "八の湯"); //

		private final String code;
		private final String label;
	}

	/** ロケール */
	@AllArgsConstructor
	@Getter
	public enum LangEnum implements CodeInterface {
		LANG_JP("ja", "日本語"), //
		LANG_EN("en", "英語"), //
		LANG_CN("zh-cn", "簡体字"), //
		LANG_TW("zh-tw", "繁体字"), //
		LANG_KO("ko", "韓国語"); //

		private final String code;
		private final String label;
	}


    /** 夕食タイプ */
	@AllArgsConstructor
	@Getter
	public enum DinnerEnum implements CodeInterface {
		DINNER1("✕", "✕"), //
		DINNER2("B", "B"),   //
		DINNER3("S", "S"),   //
		DINNER4("SA", "SA"), //
		DINNER5("SG", "SG"), //
		DINNER6("AG", "AG"), //
		DINNER7("G", "G"),   //
		DINNER8("A", "A");   //

		private final String code;
		private final String label;
	}

    /** 夕食時間 */
	@AllArgsConstructor
	@Getter
	public enum DinnerTimeEnum implements CodeInterface {
		DINNER1("18:00", "18:00"), //
		DINNER2("18:30", "18:30"), //
		DINNER3("19:00", "19:00"), //
		DINNER4("19:30", "19:30"); //

		private final String code;
		private final String label;
	}

    /** 朝食タイプ */
	@AllArgsConstructor
	@Getter
	public enum BreakfastEnum implements CodeInterface {
		DINNER1("1", "✕　"), //
		DINNER2("2", "和食"), //
		DINNER3("3", "洋食"), //
		DINNER4("4", "弁当"); //

		private final String code;
		private final String label;
	}

    /** 朝食時間 */
	@AllArgsConstructor
	@Getter
	public enum BreakfastTimeEnum implements CodeInterface {
		DINNER1("7:30", "7:30"), //
		DINNER2("8:00", "8:00"), //
		DINNER3("8:30", "8:30"), //
		DINNER4("9:00", "9:00"); //

		private final String code;
		private final String label;
	}

	/** 弁当時間 */
	@AllArgsConstructor
	@Getter
	public enum BreakfastLunchTimeEnum implements CodeInterface {
		DINNER2("8:00", "8:00"), //
		DINNER3("8:30", "8:30"), //
		DINNER4("9:00", "9:00"); //

		private final String code;
		private final String label;
	}

}
