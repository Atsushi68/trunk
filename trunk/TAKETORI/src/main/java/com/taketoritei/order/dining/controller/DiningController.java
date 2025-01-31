package com.taketoritei.order.dining.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.taketoritei.order.common.Consts;
import com.taketoritei.order.common.DateUtil;
import com.taketoritei.order.common.controller.FrontBaseController;
import com.taketoritei.order.config.IllegalException;
import com.taketoritei.order.dining.form.AdminDiningForm;
import com.taketoritei.order.dining.service.DiningService;
import com.taketoritei.order.jooq.tables.records.DDiningRecord;
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
	public String dining(Locale locale, Model model,
			UsernamePasswordAuthenticationToken token) throws ParseException {

		User user = (User) token.getPrincipal();

		// 部屋番号の指定
		String roomNo = user.getRoomNo();

		// 日付を指定
		AdminDiningForm diningForm = new AdminDiningForm();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
		String displayDays = diningForm.getDisplayDays();

		// reserveDateがnullの時、現在の日付を取得
		// if (reserveDate == null) {
		// reserveDate = dateFormat.format(new Date());
		// }

		Date displayDate = new Date();
		String displayDateStr = dateFormat.format(displayDate);

		String dateFrom = new SimpleDateFormat("yyyyMMdd").format(user.getFormDt());
		String dateTo = DateUtil.getBussinessUserDateYmd(user.getToDt());

		// 日付が範囲内かをチェック
		if (Integer.parseInt(displayDays) < Integer.parseInt(dateFrom) //
				|| Integer.parseInt(displayDays) > Integer.parseInt(dateTo))
			throw new IllegalException("不正なアクセスです。");

		// 日付設定
		model.addAttribute("reserveDispDate",
				displayDays.substring(0, 4) + "/" + displayDays.substring(4, 6) + "/" + displayDays.substring(6, 8));

		model.addAttribute("beforeReserveDate", null);

		if (Integer.parseInt(displayDays) > Integer.parseInt(dateFrom)) {
			// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = dateFormat.parse(displayDays);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, -1);
			model.addAttribute("beforeReserveDate", dateFormat.format(calendar.getTime()));
		}

		if (Integer.parseInt(displayDays) < Integer.parseInt(dateTo) - 1) {
			// SimpleDateFormat dateFormat = new SimpleDateFormat("yyyyMMdd");
			Date date = dateFormat.parse(displayDays);
			Calendar calendar = Calendar.getInstance();
			calendar.setTime(date);
			calendar.add(Calendar.DAY_OF_MONTH, 1);
			model.addAttribute("afterReserveDate", dateFormat.format(calendar.getTime()));
		}

		model.addAttribute("reserveDate", displayDays);

		// 指定した日付と部屋番号の朝食と夕食のデータを取得

		// String dateString = new
		// SimpleDateFormat("yyyy-MM-dd").format(diningForm.getDays());

		DDiningRecord diningRecord = diningService.getUserDining(
				roomNo, displayDays);
		model.addAttribute("diningRecord", diningRecord);

		if (diningRecord != null) {
			// diningForm.setDays(diningRecord.getDays());
			// diningForm.setDisplayDays(displayDateStr);
			diningForm.setDinnerTime(diningRecord.getDinnerTime());
			diningForm.setDinnerPlace(diningRecord.getDinnerPlace());
			diningForm.setBreakfastTime(diningRecord.getBreakfastTime());
			diningForm.setBreakfastPlace(diningRecord.getBreakfastPlace());
			diningForm.setBreakfastJapanese(diningRecord.getBreakfastJapanese());
			diningForm.setBreakfastWestern(diningRecord.getBreakfastWestern());
			diningForm.setLunchNum(diningRecord.getLunchNum());

			model.addAttribute("diningForm", diningForm);
		} else {
			model.addAttribute("diningForm", null);
		}

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
