package com.taketoritei.order.dining.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taketoritei.order.common.Consts;
import com.taketoritei.order.common.controller.FrontBaseController;
import com.taketoritei.order.dining.form.AdminDiningForm;
import com.taketoritei.order.dining.service.DiningService;
import com.taketoritei.order.login.form.User;
import com.taketoritei.order.room.service.RoomService;

/**
 * 朝食・夕食確認
 */
@Controller
public class DiningController extends FrontBaseController {

	@Autowired
	DiningService diningService;

	@Autowired
	RoomService roomService;

	private void setAttributeModel(Model model) {
		model.addAttribute("nowDt", new Timestamp(System.currentTimeMillis()));
	}

	/**
	 * 初期表示
	 * 
	 * @throws ParseException
	 */
	@GetMapping("/{locale}/dining")
	public String bath(@RequestParam(name = "date", required = false) String reserveDate, Locale locale, Model model,
			UsernamePasswordAuthenticationToken token) throws ParseException {

		User user = (User) token.getPrincipal();
		String roomnumber = "100";
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date displayDate = new Date();
		String displayDateStr = dateFormat.format(displayDate);

		AdminDiningForm diningForm = new AdminDiningForm();
		diningForm.setDays(displayDate);
		diningForm.setDisplayDays(displayDateStr);

		// 朝食・夕食
		model.addAttribute("diningForm", diningForm);

		// 各マスタ情報取得
		model.addAttribute("roomList", roomService.getRoomList());
		model.addAttribute("breakfastEnum", Consts.BreakfastEnum.values());
		model.addAttribute("breakfastTimeEnum", Consts.BreakfastTimeEnum.values());
		model.addAttribute("dinnerEnum", Consts.DinnerEnum.values());
		model.addAttribute("dinnerTimeEnum", Consts.DinnerTimeEnum.values());
		model.addAttribute("diningPlaceList", diningService.getDiningPlaceList(null));
		model.addAttribute("breakfastLunchTimeEnum", Consts.BreakfastLunchTimeEnum.values());

		setAttributeModel(model);

		return "dining/dining";
	}

}
