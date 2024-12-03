package com.taketoritei.order.room.service;

import static com.taketoritei.order.jooq.tables.MRoom.*;

import java.util.ArrayList;
import java.util.List;

import org.jooq.Record;
import org.jooq.Result;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taketoritei.order.common.service.BaseService;
import com.taketoritei.order.jooq.tables.records.MRoomRecord;

@Service
@Transactional(rollbackFor = Exception.class)
public class RoomService extends BaseService {

	/**
	 * m_roomの全件を取得する
	 */
	public List<MRoomRecord> getRoomList() {

		Result<MRoomRecord> reserveRecs = jooq.selectFrom(M_ROOM) //
				.orderBy(M_ROOM.FLOOR.asc(), M_ROOM.ROOM_NO.asc())
				.fetch();

		List<MRoomRecord> list = new ArrayList<>();
		reserveRecs.forEach(i -> {
			list.add(i);
	        });

		return list;
	}


	/**
	 * 各部屋とそのログイン情報の一覧を取得
	 * @return
	 */
	public Result<Record> getRoomUserList() {
		String sql =
			"select" +
			"    r.room_no" +
			"    , r.floor" +
			"    , u.token" +
			"    , u.from_dt" +
			"    , u.to_dt" +
			"    , u.action_name" +
			"    , u.del_flg" +
			"    , u.last_date" +
			"    , u.last_user " +
			"from" +
			"    m_room r " +
			"    left join ( " +
			"        select" +
			"            ROW_NUMBER() OVER ( " +
			"                PARTITION BY" +
			"                    a.room_no " +
			"                ORDER BY" +
			"                    (a.last_date) DESC" +
			"            ) AS rank" +
			"            , a.* " +
			"        from" +
			"            m_user a" +
			"    ) as u " +
			"        on r.room_no = u.room_no " +
			"        and u.rank = 1 order by r.room_no";

		return jooq.resultQuery(sql).fetch();
	}


	/**
	 * 対象の部屋のユーザー情報を取得
	 * @param roomNo
	 * @return
	 */
	public Record getUserByRoomNo(String roomNo) {

		String sql =
			  "select"
			+ "    * "
			+ "from"
			+ "    m_user a "
			+ "    inner join ( "
			+ "        SELECT"
			+ "            b.room_no"
			+ "            , MAX(b.last_date) AS max "
			+ "        FROM"
			+ "            m_user b "
			+ "        GROUP BY"
			+ "            b.room_no"
			+ "    ) c "
			+ "        on a.room_no = c.room_no "
			+ "where"
			+ "    a.room_no = ? "
			+ "    and a.last_date = c.max"
			+ "    and a.from_dt <= NOW()"
			+ "    and a.to_dt > NOW()";
		List<Record> userList = jooq.resultQuery(sql, roomNo).fetch();

		if (userList == null || userList.size() == 0) {
			return null;
		}
		return userList.get(0);
	}
}
