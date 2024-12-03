package com.taketoritei.order.room.form;

import java.sql.Timestamp;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import javax.validation.Valid;

import org.jooq.Record;
import org.jooq.Result;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminRoomListForm {

	public AdminRoomListForm() {
	}

	public AdminRoomListForm(Result<Record> roomUserList) {

		Timestamp now = new Timestamp(System.currentTimeMillis());
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");

		// 次の日を設定
		Calendar calendar = Calendar.getInstance();
		calendar.add(Calendar.DAY_OF_MONTH, 1);

		for(Record roomUser : roomUserList) {

			AdminRoomForm adminRoomForm = new AdminRoomForm();
			adminRoomForm.setRoomNo(roomUser.get("room_no").toString());
			adminRoomForm.setFromDt(roomUser.get("from_dt") == null ? null : (Timestamp)roomUser.get("from_dt"));
			adminRoomForm.setToDt(roomUser.get("from_dt") == null ? null : (Timestamp)roomUser.get("to_dt"));

			// 期限が過ぎているものはfrom:今日の日付、to:明日の日付をデフォルトでセット
			// --------------------------
			String fromDtStr = "";
			String toDtStr = "";
			if (roomUser.get("to_dt") == null) {
				fromDtStr = sdf.format(now);
			}
			if (roomUser.get("to_dt") == null || ((Timestamp)roomUser.get("to_dt")).compareTo(now) < 0) {
				fromDtStr = sdf.format(now);
				toDtStr = sdf.format(calendar.getTime());
			}
			if ("".equals(fromDtStr)) {
				fromDtStr = sdf.format(roomUser.get("from_dt"));
			}
			if ("".equals(toDtStr)) {
				toDtStr = sdf.format(roomUser.get("to_dt"));
			}
			adminRoomForm.setFromDtStr(fromDtStr);
			adminRoomForm.setToDtStr(toDtStr);
			// --------------------------

			adminRoomForm.setDelFlg(roomUser.get("del_flg") == null ? false : Boolean.parseBoolean(roomUser.get("del_flg").toString()));
			adminRoomForm.setLastDate((Timestamp)roomUser.get("last_date"));
			adminRoomForm.setToken(roomUser.get("token") == null ? "" : roomUser.get("token").toString());

			this.adminRoomListForm.add(adminRoomForm);
		}
	}

	@Valid
	private List<AdminRoomForm> adminRoomListForm = new ArrayList<AdminRoomForm>();
}
