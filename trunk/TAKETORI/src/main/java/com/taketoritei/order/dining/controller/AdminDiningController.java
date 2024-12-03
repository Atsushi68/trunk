package com.taketoritei.order.dining.controller;

import java.sql.Timestamp;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.lang3.StringUtils;
import org.jooq.Record;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.taketoritei.order.common.Consts;
import com.taketoritei.order.common.DateUtil;
import com.taketoritei.order.common.controller.AdminBaseController;
import com.taketoritei.order.config.IllegalException;
import com.taketoritei.order.dining.form.AdminDiningForm;
import com.taketoritei.order.dining.form.AdminDiningMessageForm;
import com.taketoritei.order.dining.service.DiningService;
import com.taketoritei.order.jooq.tables.records.DDiningMessageRecord;
import com.taketoritei.order.jooq.tables.records.DDiningRecord;
import com.taketoritei.order.jooq.tables.records.MDiningPlaceRecord;
import com.taketoritei.order.room.service.RoomService;

/**
 * 朝食・夕食管理
 */
@Controller
public class AdminDiningController extends AdminBaseController {

	@Autowired
	DiningService diningService;

	@Autowired
	RoomService roomService;

	private void setAttributeModel(Model model) {
		model.addAttribute("nowDt", new Timestamp(System.currentTimeMillis()));
	}

	/**
	 * 朝食・夕食登録画面
	 * 
	 * @throws ParseException
	 */
	@GetMapping("/admin/dining/regist")
	public String adminDiningRegist(HttpServletRequest request, Model model) throws ParseException {

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

		return "dining/admin_dining_regist";
	}

	/**
	 * 朝食・夕食編集画面
	 * 
	 * @throws ParseException
	 */
	@GetMapping("/admin/dining/edit")
	public String adminDiningEdit(HttpServletRequest request, Model model) throws ParseException {

		// 更新対象の部屋と日付を取得
		String days = request.getParameter("days");
		String room = request.getParameter("room");
		if (StringUtils.isEmpty(days) || StringUtils.isEmpty(room)) {
			throw new IllegalException("不正なアクセスです。");
		}

		String displayDateStr = days;
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		Date displayDate = dateFormat.parse(displayDateStr);

		// 朝食・夕食情報を取得
		DDiningRecord record = diningService.getDiningByUniq(displayDateStr, room);

		AdminDiningForm diningForm = new AdminDiningForm();
		diningForm.setDays(displayDate);
		diningForm.setRoomNo(room);
		diningForm.setDisplayDays(displayDateStr);

		if (record != null) {
			// 編集の場合は登録されている情報をセット
			diningForm.setCustomerNum(record.getCustomerNum());
			diningForm.setDinner(record.get("dinner") == null ? null : record.get("dinner").toString());
			diningForm.setDinnerTime(record.get("dinner_time") == null ? null : record.get("dinner_time").toString());
			diningForm
					.setDinnerPlace(record.get("dinner_place") == null ? null : record.get("dinner_place").toString());
			diningForm.setBreakfastJapanese(record.get("breakfast_japanese") == null ? null
					: Integer.parseInt(record.get("breakfast_japanese").toString()));
			diningForm.setBreakfastWestern(record.get("breakfast_western") == null ? null
					: Integer.parseInt(record.get("breakfast_western").toString()));
			diningForm.setBreakfastTime(
					record.get("breakfast_time") == null ? null : record.get("breakfast_time").toString());
			diningForm.setBreakfastPlace(
					record.get("breakfast_place") == null ? null : record.get("breakfast_place").toString());
			diningForm.setLunchNum(
					record.get("lunch_num") == null ? null : Integer.parseInt(record.get("lunch_num").toString()));
			diningForm.setLunchTime(record.get("lunch_time") == null ? null : record.get("lunch_time").toString());
			diningForm.setMemo(record.get("memo") == null ? null : record.get("memo").toString());
		}

		// 検索した朝食・夕食
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
		model.addAttribute("errorMsg", "");
		return "dining/admin_dining_regist";
	}

	/**
	 * 朝食・夕食の登録処理
	 * 
	 * @throws ParseException
	 */
	@PostMapping("/admin/dining")
	public String adminDiningRegist(
			HttpServletRequest request,
			Model model,
			@ModelAttribute("adminDiningForm") @Validated AdminDiningForm adminDiningForm,
			BindingResult result,
			RedirectAttributes redirectAttributes) throws ParseException {

		// エラー
		String errorMsg = "";

		// 表示する日付
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String displayDateStr = adminDiningForm.getDisplayDays();
		Date displayDate = dateFormat.parse(displayDateStr);
		adminDiningForm.setDays(displayDate);

		setAttributeModel(model);

		// 同じ場所に登録されていたらエラー
		if (!StringUtils.isEmpty(adminDiningForm.getDinnerPlace()) && !diningService.registCheck(adminDiningForm, 1)) {
			// エラー
			errorMsg += "夕食の場所は既に予約が入っています。<br>";
		}
		if (!StringUtils.isEmpty(adminDiningForm.getBreakfastPlace()) && !diningService.registCheck(adminDiningForm, 2)) {
			// エラー
			errorMsg += "朝食の場所は既に予約が入っています。<br>";
		}

		// エラーがあればエラー表示
		if (!StringUtils.isEmpty(errorMsg)) {
			// 検索した朝食・夕食
			model.addAttribute("diningForm", adminDiningForm);

			// 各マスタ情報取得
			model.addAttribute("roomList", roomService.getRoomList());
			model.addAttribute("breakfastEnum", Consts.BreakfastEnum.values());
			model.addAttribute("breakfastTimeEnum", Consts.BreakfastTimeEnum.values());
			model.addAttribute("dinnerEnum", Consts.DinnerEnum.values());
			model.addAttribute("dinnerTimeEnum", Consts.DinnerTimeEnum.values());
			model.addAttribute("diningPlaceList", diningService.getDiningPlaceList(null));
			model.addAttribute("breakfastLunchTimeEnum", Consts.BreakfastLunchTimeEnum.values());

			setAttributeModel(model);
			model.addAttribute("errorMsg", errorMsg);
			return "dining/admin_dining_regist";
		}

		// 入力内容登録
		diningService.upsertDiningByForm(adminDiningForm);

		// リダイレクト
		redirectAttributes.addFlashAttribute("message", "更新しました。");
		return "redirect:/admin/dining/regist";
	}

	/**
	 * 朝食・夕食の削除
	 * 
	 * @throws ParseException
	 */
	@PostMapping("/admin/dining/delete")
	public String adminDiningDelete(
			HttpServletRequest request,
			Model model,
			RedirectAttributes redirectAttributes) throws ParseException {

		String delDays = request.getParameter("delDays");
		String delRoom = request.getParameter("delRoom");

		// データが存在するか
		DDiningRecord rec = diningService.getDiningByUniq(delDays, delRoom);
		if (rec != null) {
			// 入力内容登録
			diningService.deleteDining(delDays, delRoom);
		}
		// リダイレクト
		redirectAttributes.addFlashAttribute("message", "データ削除しました。");
		return "redirect:/admin/dining/place";
	}

	/**
	 * 朝食・夕食の一覧画面(時間別)
	 * 
	 * @throws ParseException
	 */
	@GetMapping("/admin/dining/time")
	public String adminDiningTime(HttpServletRequest request, Model model) throws ParseException {

		// 表示する日付 デフォルトは今日の日付
		// 日付は風呂と同じく昼で日付切り替え
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String displayDateStr = DateUtil.getBussinessDate().replace("/", "-");
		Date displayDate;

		String days = request.getParameter("days");
		if (StringUtils.isEmpty(days)) {
			// 初期表示の日数はビジネス時間
			displayDate = dateFormat.parse(displayDateStr);

		} else {
			displayDateStr = days;
			displayDate = dateFormat.parse(displayDateStr);
		}

		// 朝食・夕食情報を取得
		// 期間で取得する
		List<Record> roomDining = diningService.getRoomDiningList(displayDateStr);
		Map<String, String> dinnerMap = new HashMap<>();
		Map<String, String> breakfastMap = new HashMap<>();
		Map<String, String> lunchMap = new HashMap<>();
		Map<String, AdminDiningForm> roomMap = new HashMap<>();

		for (Record record : roomDining) {

			AdminDiningForm dining = new AdminDiningForm();
			dining.setDays(displayDate);
			dining.setRoomNo(record.get("room_no").toString());
			dining.setDinner(record.get("dinner") == null ? "" : record.get("dinner").toString());
			dining.setDinnerTime(record.get("dinner_time") == null ? "" : record.get("dinner_time").toString());
			dining.setCustomerNum(record.get("customer_num") == null ? null
					: Integer.parseInt(record.get("customer_num").toString()));
			dining.setDinnerPlace(record.get("dinner_place") == null ? "" : record.get("dinner_place").toString());
			dining.setBreakfastJapanese(record.get("breakfast_japanese") == null ? null
					: Integer.parseInt(record.get("breakfast_japanese").toString()));
			dining.setBreakfastWestern(record.get("breakfast_western") == null ? null
					: Integer.parseInt(record.get("breakfast_western").toString()));
			dining.setBreakfastTime(
					record.get("breakfast_time") == null ? "" : record.get("breakfast_time").toString());
			dining.setBreakfastPlace(
					record.get("breakfast_place") == null ? "" : record.get("breakfast_place").toString());
			dining.setLunchNum(
					record.get("lunch_num") == null ? null : Integer.parseInt(record.get("lunch_num").toString()));
			dining.setLunchTime(record.get("lunch_time") == null ? "" : record.get("lunch_time").toString());
			dining.setMemo(record.get("memo") == null ? "" : record.get("memo").toString());

			// 部屋ごとに格納
			roomMap.put(dining.getRoomNo(), dining);

			// 時間ごとに格納（夕食）
			if (!StringUtils.isEmpty(dining.getDinnerTime())) {
				String time = record.get("dinner_time").toString();

				// あれば取得
				String text = "";
				if (dinnerMap.containsKey(time)) {
					text = dinnerMap.get(time);
					text = createRoomText(text + "　", dining, 1, 1);
				} else {
					text = createRoomText(text, dining, 1, 1);
				}
				dinnerMap.put(time, text);
			}
			// 時間ごとに格納（朝食）
			if (!StringUtils.isEmpty(dining.getBreakfastTime())) {
				String time = record.get("breakfast_time").toString();

				// 朝食（和・洋）
				String text = "";
				if (breakfastMap.containsKey(time)) {
					text = breakfastMap.get(time);
					text = createRoomText(text + "　", dining, 2, 1);
				} else {
					text = createRoomText(text, dining, 2, 1);
				}
				breakfastMap.put(time, text);
			}
			// 時間ごとに格納（弁当）
			if (!StringUtils.isEmpty(dining.getLunchTime())) {
				String time = dining.getLunchTime();

				String text = "";
				if (dining.getLunchTime() != null) {
					if (lunchMap.containsKey(time)) {
						text = lunchMap.get(time);
						text = createRoomText(text + "　", dining, 3, 1);
					} else {
						text = createRoomText(text, dining, 3, 1);
					}
					lunchMap.put(time, text);
				}
			}
		}

		// 指定日
		AdminDiningForm form = new AdminDiningForm();
		form.setDays(displayDate);
		form.setDisplayDays(displayDateStr);
		model.addAttribute("form", form);

		// 朝食・夕食
		model.addAttribute("dinnerMap", dinnerMap);
		model.addAttribute("breakfastMap", breakfastMap);
		model.addAttribute("lunchMap", lunchMap);
		model.addAttribute("roomMap", roomMap);

		// 各マスタ情報取得
		model.addAttribute("roomList", roomService.getRoomList());
		model.addAttribute("breakfastEnum", Consts.BreakfastEnum.values());
		model.addAttribute("breakfastTimeEnum", Consts.BreakfastTimeEnum.values());
		model.addAttribute("dinnerEnum", Consts.DinnerEnum.values());
		model.addAttribute("dinnerTimeEnum", Consts.DinnerTimeEnum.values());
		model.addAttribute("breakfastLunchTimeEnum", Consts.BreakfastLunchTimeEnum.values());

		setAttributeModel(model);

		return "dining/admin_dining_time";
	}

	private String createRoomText(String text, AdminDiningForm form, int flg, int marginON) {

		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String pDays = dateFormat.format(form.getDays());
		String pRoom = form.getRoomNo();
		String url = "/order/admin/dining/edit?days=" + pDays + "&room=" + pRoom;
		String margin = "";

		if (marginON == 1) {
			margin = "style='margin-left: 20px;'";
		}

		// 夕食・朝食・弁当
		if (flg == 1) {
			// 夕食
			// リンクにして編集画面へ
			text += "<a " + margin + " href='" + url + "'>" + form.getRoomNo() + "[" + form.getCustomerNum() + "]</a>";

		} else if (flg == 2) {
			// 朝食
			// リンクにして編集画面へ
			text += "<a " + margin + " href='" + url + "'>" + form.getRoomNo() + "[";

			String bf = "";
			if (form.getBreakfastJapanese() != null) {
				if (StringUtils.isEmpty(bf)) {
					bf += "和:" + form.getBreakfastJapanese();
				} else {
					bf += "／和:" + form.getBreakfastJapanese();
				}
			}
			if (form.getBreakfastWestern() != null) {
				if (StringUtils.isEmpty(bf)) {
					bf += "洋:" + form.getBreakfastWestern();
				} else {
					bf += "／ 洋:" + form.getBreakfastWestern();
				}
			}
			text += bf + "]</a>";

		} else if (flg == 3) {
			// 弁当
			text += "<a " + margin + " href='" + url + "'>" + form.getRoomNo() + "[" + form.getLunchNum() + "]</a>";
		} else if (flg == 4) {
			// 部屋のみ
			text += "<a " + margin + " href='" + url + "'>" + form.getRoomNo() + "</a>";
		}
		return text;
	}

	/**
	 * 朝食・夕食の一覧画面(場所別)
	 * 
	 * @throws ParseException
	 */
	@GetMapping("/admin/dining/place")
	public String adminDiningPlace(HttpServletRequest request, Model model) throws ParseException {

		// 表示する日付 デフォルトは今日のビジネス日付（昼12時に切り替え）

		String displayDateStr = DateUtil.getBussinessDate().replace("/", "-");
		Date displayDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String days = request.getParameter("days");
		if (StringUtils.isEmpty(days)) {
			displayDate = dateFormat.parse(displayDateStr);
		} else {
			displayDateStr = days;
			displayDate = dateFormat.parse(displayDateStr);
		}

		// 朝食・夕食情報を取得
		// 期間で取得する
		List<DDiningRecord> diningList = diningService.getDining(displayDateStr);
		Map<String, AdminDiningForm> dinnerPlaceMap = new HashMap<>();
		Map<String, AdminDiningForm> breakfastPlaceMap = new HashMap<>();
		Map<String, List<AdminDiningForm>> lunchMap = new HashMap<>();

		for (Record record : diningList) {

			AdminDiningForm dining = new AdminDiningForm();
			dining.setDays(displayDate);
			dining.setRoomNo(record.get("room_no").toString());
			dining.setCustomerNum(record.get("customer_num") == null ? null
					: Integer.parseInt(record.get("customer_num").toString()));
			dining.setDinner(record.get("dinner") == null ? "" : record.get("dinner").toString());
			dining.setDinnerTime(record.get("dinner_time") == null ? "" : record.get("dinner_time").toString());
			dining.setDinnerPlace(record.get("dinner_place") == null ? "" : record.get("dinner_place").toString());
			dining.setBreakfastJapanese(record.get("breakfast_japanese") == null ? null
					: Integer.parseInt(record.get("breakfast_japanese").toString()));
			dining.setBreakfastWestern(record.get("breakfast_western") == null ? null
					: Integer.parseInt(record.get("breakfast_western").toString()));
			dining.setBreakfastTime(
					record.get("breakfast_time") == null ? "" : record.get("breakfast_time").toString());
			dining.setBreakfastPlace(
					record.get("breakfast_place") == null ? "" : record.get("breakfast_place").toString());
			dining.setLunchNum(
					record.get("lunch_num") == null ? null : Integer.parseInt(record.get("lunch_num").toString()));
			dining.setLunchTime(record.get("lunch_time") == null ? "" : record.get("lunch_time").toString());
			dining.setMemo(record.get("memo") == null ? "" : record.get("memo").toString());

			if (dining.getBreakfastJapanese() != null && dining.getBreakfastWestern() != null) {
				dining.setTotalNum(dining.getBreakfastJapanese() + dining.getBreakfastWestern());
			} else if (dining.getBreakfastJapanese() != null && dining.getBreakfastWestern() == null) {
				dining.setTotalNum(dining.getBreakfastJapanese());
			} else if (dining.getBreakfastJapanese() == null && dining.getBreakfastWestern() != null) {
				dining.setTotalNum(dining.getBreakfastWestern());
			} else {
				dining.setTotalNum(0);
			}

			// 場所ごとに格納
			dinnerPlaceMap.put(dining.getDinnerPlace(), dining);
			breakfastPlaceMap.put(dining.getBreakfastPlace(), dining);

			// 弁当の文言作成
			String text = createRoomText("", dining, 3, 0);
			dining.setLunchText(text);

			// 弁当の時間ごとに格納
			if (lunchMap.containsKey(dining.getLunchTime())) {
				// 存在する場合は取得して追加
				List<AdminDiningForm> tmpList = lunchMap.get(dining.getLunchTime());
				tmpList.add(dining);
				lunchMap.put(dining.getLunchTime(), tmpList);
			} else {
				// 初回
				List<AdminDiningForm> tmpList = new ArrayList<>();
				tmpList.add(dining);
				lunchMap.put(dining.getLunchTime(), tmpList);
			}

			// 編集へのリンクを付ける
			// 最後にすること
			dining.setRoomNo(createRoomText("", dining, 4, 0));
		}

		// 指定日
		AdminDiningForm form = new AdminDiningForm();
		form.setDays(displayDate);
		form.setDisplayDays(displayDateStr);
		model.addAttribute("form", form);

		// 朝食・夕食
		model.addAttribute("dinnerPlaceMap", dinnerPlaceMap);
		model.addAttribute("breakfastPlaceMap", breakfastPlaceMap);
		model.addAttribute("lunchMap", lunchMap);

		// 各マスタ情報取得
		model.addAttribute("diningPlace1List", diningService.getDiningPlaceList(1));
		model.addAttribute("diningPlace2List", diningService.getDiningPlaceList(2));
		model.addAttribute("roomList", roomService.getRoomList());
		model.addAttribute("breakfastEnum", Consts.BreakfastEnum.values());
		model.addAttribute("breakfastTimeEnum", Consts.BreakfastTimeEnum.values());
		model.addAttribute("dinnerEnum", Consts.DinnerEnum.values());
		model.addAttribute("dinnerTimeEnum", Consts.DinnerTimeEnum.values());
		model.addAttribute("breakfastLunchTimeEnum", Consts.BreakfastLunchTimeEnum.values());

		setAttributeModel(model);

		return "dining/admin_dining_place";
	}


	/**
	 * 朝食・夕食の伝言事項初期表示
	 * 
	 * @throws ParseException
	 */
	@GetMapping("/admin/dining/message")
	public String adminDiningMessage(HttpServletRequest request, Model model) throws ParseException {

		// 表示する日付 デフォルトは今日のビジネス日付（昼12時に切り替え）
		String displayDateStr = DateUtil.getBussinessDate().replace("/", "-");
		Date displayDate = new Date();
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String days = request.getParameter("days");
		if (StringUtils.isEmpty(days)) {
			displayDate = dateFormat.parse(displayDateStr);
		} else {
			displayDateStr = days;
			displayDate = dateFormat.parse(displayDateStr);
		}

		// 伝言情報を取得
		// 期間で取得する
		List<Record> diningMessageList = diningService.getDiningMessageList(displayDateStr);

		// 指定日
		AdminDiningMessageForm form = new AdminDiningMessageForm();
		form.setDays(displayDate);
		form.setDisplayDays(displayDateStr);
		model.addAttribute("form", form);

		// 各マスタ情報取得
		model.addAttribute("diningMessageList", diningMessageList);

		setAttributeModel(model);

		return "dining/admin_dining_message";
	}

	/**
	 * 朝食・夕食の伝言事項登録画面
	 * 
	 * @throws ParseException
	 */
	@GetMapping("/admin/dining/message/edit")
	public String adminDiningMessageEdit(HttpServletRequest request, Model model) throws ParseException {

		// 表示する日付 デフォルトは今日のビジネス日付（昼12時に切り替え）
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String displayDateStr = request.getParameter("days");
		String place = request.getParameter("place");
		DDiningMessageRecord diningMessage = new DDiningMessageRecord();

		// 情報がそろっている場合DBから取得
		if (StringUtils.isEmpty(displayDateStr) || StringUtils.isEmpty(place)) {
			// エラー
		}
			
		int placeId = Integer.parseInt(place);
		Date displayDate = dateFormat.parse(displayDateStr);

		// 伝言情報を取得
		diningMessage = diningService.getDiningMessage(displayDateStr, placeId);

		// 指定日
		AdminDiningMessageForm form = new AdminDiningMessageForm();
		form.setDays(displayDate);
		form.setDisplayDays(displayDateStr);
		form.setDiningPlaceId(placeId);
		form.setMessage(diningMessage == null ? "" : diningMessage.getMessage());
		model.addAttribute("form", form);

		// 各マスタ情報取得
		List<MDiningPlaceRecord> diningPlaceList = diningService.getDiningPlaceList(null);
		model.addAttribute("diningPlaceList", diningPlaceList);

		// 選択している場所
		String selectedPlaceName = "";
		for(MDiningPlaceRecord diningPlace : diningPlaceList) {
			if (placeId == diningPlace.getId()) {
				selectedPlaceName = diningPlace.getDinnerPlace();
			}
		}
		model.addAttribute("selectedPlaceName", selectedPlaceName);
		setAttributeModel(model);

		return "dining/admin_dining_edit_message";
	}


	/**
	 * 朝食・夕食の伝言事項登録処理
	 * 
	 * @throws ParseException
	 */
	@PostMapping("/admin/dining/message/regist")
	public String adminDiningMessageRegist(
		HttpServletRequest request,
		Model model,
		@ModelAttribute("adminDiningMessageForm") @Validated AdminDiningMessageForm adminDiningMessageForm,
		BindingResult result,
		RedirectAttributes redirectAttributes) throws ParseException {
		
		// 表示する日付 デフォルトは今日のビジネス日付（昼12時に切り替え）
		SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
		String displayDateStr = adminDiningMessageForm.getDisplayDays();
		Date displayDate = dateFormat.parse(displayDateStr);
		adminDiningMessageForm.setDays(displayDate);
		setAttributeModel(model);

		// エラーが無い場合登録
		diningService.upsertDiningMessageByForm(adminDiningMessageForm);

		// リダイレクト
		redirectAttributes.addFlashAttribute("message", "更新しました。");
		return "redirect:/admin/dining/message?days=" + adminDiningMessageForm.getDisplayDays();
	}
}
