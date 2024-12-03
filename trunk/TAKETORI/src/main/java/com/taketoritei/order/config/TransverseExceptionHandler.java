package com.taketoritei.order.config;

import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

/**
 * 特定のExceptionが発生した場合の処理
 */
@ControllerAdvice
public class TransverseExceptionHandler {

	/**
	 * 自作Exception　不正なアクセスの場合のException
	 * @param exception
	 * @return
	 */
	@ExceptionHandler(IllegalException.class)
    public String handleOriginalWebException(Model model, IllegalException exception) {

		model.addAttribute("errorMsg", exception.getMessage());

		// エラーページを表示
        return "error.html";
    }
}
