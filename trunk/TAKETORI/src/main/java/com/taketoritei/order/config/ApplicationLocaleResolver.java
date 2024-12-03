package com.taketoritei.order.config;

import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.i18n.SessionLocaleResolver;

import com.taketoritei.order.common.Consts.LangEnum;

@Configuration
public class ApplicationLocaleResolver extends SessionLocaleResolver {

	@Override
    public Locale resolveLocale(HttpServletRequest request) {

		String lang = "ja";
		String[] pathArray = request.getRequestURI().split("/", -1);

		if (pathArray.length >= 3) {
			String path2 = pathArray[2];

			if ("admin".equals(path2)) {
				// adminの場合はja

			} else {
				// adminでない場合は、LangEnumを含まれているかを確認する
				LangEnum[] langs = LangEnum.values();
				for (LangEnum l : langs)
					if (l.getCode().equals(path2))
						lang = path2;

			}
		}

		// ロケールを設定
		Locale userLocale = new Locale(lang);
        return userLocale;
    }

    @Override
    public void setLocale(HttpServletRequest request, HttpServletResponse response, Locale locale) {
        super.setLocale(request, response, locale);
    }
}
