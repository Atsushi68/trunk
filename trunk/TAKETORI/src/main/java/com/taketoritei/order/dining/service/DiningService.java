package com.taketoritei.order.dining.service;

import static com.taketoritei.order.jooq.tables.DDining.*;
import static com.taketoritei.order.jooq.tables.MDiningPlace.*;
import static com.taketoritei.order.jooq.tables.DDiningMessage.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;

import org.jooq.Record;
import org.jooq.Result;
import org.jooq.SelectConditionStep;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.taketoritei.order.common.service.BaseService;
import com.taketoritei.order.dining.form.AdminDiningForm;
import com.taketoritei.order.dining.form.AdminDiningMessageForm;
import com.taketoritei.order.jooq.tables.records.DDiningMessageRecord;
import com.taketoritei.order.jooq.tables.records.DDiningRecord;
import com.taketoritei.order.jooq.tables.records.MDiningPlaceRecord;

@Service
@Transactional(rollbackFor = Exception.class)
public class DiningService extends BaseService {

    /**
     * 各部屋すべてと朝食・夕食の情報を取得
     * 
     * @return
     */
    public List<DDiningRecord> getDining(String days) {

        // sqlのdateに変換
        java.sql.Date sqlDate = java.sql.Date.valueOf(days);
        return jooq.selectFrom(D_DINING).where(D_DINING.DAYS.eq(sqlDate)).fetch();
    }

    /**
     * 各部屋すべてと朝食・夕食の情報を取得
     * 
     * @return
     */
    public DDiningRecord getDiningByUniq(String days, String roomNo) {

        // sqlのdateに変換
        java.sql.Date sqlDate = java.sql.Date.valueOf(days);
        return jooq.selectFrom(D_DINING).where(D_DINING.DAYS.eq(sqlDate)).and(D_DINING.ROOM_NO.eq(roomNo)).fetchOne();
    }

    /**
     * 各部屋すべてと朝食・夕食の情報を取得
     * 
     * @return
     */
    public Result<Record> getRoomDiningList(String strDate) {
        String sql = " select" +
                "     r.room_no " +
                "     , d.days " +
                "     , d.breakfast_japanese " +
                "     , d.breakfast_western " +
                "     , d.breakfast_time " +
                "     , d.breakfast_place " +
                "     , d.memo " +
                "     , d.dinner " +
                "     , d.customer_num " +
                "     , d.dinner_time " +
                "     , d.dinner_place " +
                "     , d.lunch_num " +
                "     , d.lunch_time " +
                " from " +
                "     m_room r " +
                "     left join ( " +
                "         select " +
                "             * " +
                "         from " +
                "             d_dining dd " +
                "         where " +
                "             dd.days = '" + strDate + "'" +
                "     ) d on r.room_no = d.room_no " +
                " order by r.room_no";

        return jooq.resultQuery(sql).fetch();
    }

    // 朝食・夕食情報の新規登録または更新
    public void upsertDiningByForm(AdminDiningForm form) {

        // sqlのdateに変換
        java.sql.Date sqlDate = java.sql.Date.valueOf(form.getDisplayDays());

        // 既にデータが存在するか
        DDiningRecord rec = getDiningByUniq(form.getDisplayDays(), form.getRoomNo());
        if (rec == null) {
            jooq //
                    .insertInto(
                            D_DINING //
                            , D_DINING.DAYS, D_DINING.ROOM_NO, D_DINING.CUSTOMER_NUM, D_DINING.BREAKFAST_JAPANESE,
                            D_DINING.BREAKFAST_WESTERN, D_DINING.BREAKFAST_TIME, D_DINING.BREAKFAST_PLACE,
                            D_DINING.DINNER, D_DINING.DINNER_TIME, D_DINING.DINNER_PLACE, D_DINING.LUNCH_NUM,
                            D_DINING.LUNCH_TIME, D_DINING.MEMO)
                    .values(
                            sqlDate, form.getRoomNo(), form.getCustomerNum(), form.getBreakfastJapanese(),
                            form.getBreakfastWestern(), form.getBreakfastTime(), form.getBreakfastPlace(),
                            form.getDinner(), form.getDinnerTime(), form.getDinnerPlace(), form.getLunchNum(),
                            form.getLunchTime(), form.getMemo())
                    .execute();

        } else {
            jooq //
                    .update(D_DINING) //
                    .set(D_DINING.CUSTOMER_NUM, form.getCustomerNum()) //
                    .set(D_DINING.BREAKFAST_JAPANESE, form.getBreakfastJapanese()) //
                    .set(D_DINING.BREAKFAST_WESTERN, form.getBreakfastWestern()) //
                    .set(D_DINING.BREAKFAST_TIME, form.getBreakfastTime()) //
                    .set(D_DINING.BREAKFAST_PLACE, form.getBreakfastPlace()) //
                    .set(D_DINING.DINNER, form.getDinner()) //
                    .set(D_DINING.DINNER_TIME, form.getDinnerTime()) //
                    .set(D_DINING.DINNER_PLACE, form.getDinnerPlace()) //
                    .set(D_DINING.LUNCH_NUM, form.getLunchNum()) //
                    .set(D_DINING.LUNCH_TIME, form.getLunchTime()) //
                    .set(D_DINING.MEMO, form.getMemo()) //
                    .where(D_DINING.DAYS.eq(sqlDate)) //
                    .and(D_DINING.ROOM_NO.eq(form.getRoomNo())) //
                    .execute(); //
        }
    }

    // m_dining_place
    public List<MDiningPlaceRecord> getDiningPlaceList(Integer kbn) {
        if (kbn == null) {
            return jooq.selectFrom(M_DINING_PLACE).orderBy(M_DINING_PLACE.SORT_ORDER).fetch();
        } else {
            return jooq.selectFrom(M_DINING_PLACE).where(M_DINING_PLACE.KBN.eq(kbn)).orderBy(M_DINING_PLACE.SORT_ORDER)
                    .fetch();
        }
    }

    // m_dining_place
    public void deleteDining(String days, String roomNo) {
        // sqlのdateに変換
        java.sql.Date sqlDate = java.sql.Date.valueOf(days);
        jooq.deleteFrom(D_DINING).where(D_DINING.DAYS.eq(sqlDate)).and(D_DINING.ROOM_NO.eq(roomNo)).execute();
    }

    // 登録前チェック
    public boolean registCheck(AdminDiningForm form, int placeKbn) {
        // 既にデータが存在するか
        // sqlのdateに変換
        java.sql.Date sqlDate = java.sql.Date.valueOf(form.getDisplayDays());
        SelectConditionStep<DDiningRecord> cond = jooq
                .selectFrom(D_DINING)
                .where(D_DINING.DAYS.eq(sqlDate))
                .and(D_DINING.ROOM_NO.notEqual(form.getRoomNo()));

        // 分岐
        if (placeKbn == 1) {
            // 夕方の場所のみ
            cond.and(D_DINING.DINNER_PLACE.eq(form.getDinnerPlace()));
        } else if (placeKbn == 2) {
            // 朝の場所のみ
            cond.and(D_DINING.BREAKFAST_PLACE.eq(form.getBreakfastPlace()));
        } else {
            // 夕方と朝の場所
            cond.and(D_DINING.BREAKFAST_PLACE.eq(form.getBreakfastPlace())
                    .or(D_DINING.DINNER_PLACE.eq(form.getDinnerPlace())));
        }
        List<DDiningRecord> rec = cond.fetch();
        if (rec == null || rec.size() == 0) {
            return true;
        }
        return false;
    }

    // 伝言取得
    public List<Record> getDiningMessageList(String days) {

        // sqlのdateに変換
        java.sql.Date sqlDate = java.sql.Date.valueOf(days);
        String sql = "select " + //
                "* " + //
                "from " + //
                "m_dining_place dp " + //
                "left join d_dining_message dm on dp.id = dm.dining_place_id and dm.days = ? " + //
                "order by dp.id ";

        return jooq.resultQuery(sql, sqlDate).fetch();
    }

    // 伝言取得（ユニーク）
    public DDiningMessageRecord getDiningMessage(String days, int diningPlaceId) {

        // sqlのdateに変換
        java.sql.Date sqlDate = java.sql.Date.valueOf(days);
        return jooq.selectFrom(D_DINING_MESSAGE)
                .where(D_DINING_MESSAGE.DAYS.eq(sqlDate).and(D_DINING_MESSAGE.DINING_PLACE_ID.eq(diningPlaceId)))
                .fetchOne();
    }

    // 朝食・夕食情報の伝言 新規登録または更新
    public void upsertDiningMessageByForm(AdminDiningMessageForm form) {

        // sqlのdateに変換
        java.sql.Date sqlDate = java.sql.Date.valueOf(form.getDisplayDays());

        // 既にデータが存在するか
        System.out.println("getDisplayDays" + form.getDisplayDays());
        System.out.println("getDiningPlaceId" + form.getDiningPlaceId());

        DDiningMessageRecord rec = getDiningMessage(form.getDisplayDays(), form.getDiningPlaceId());
        if (rec == null) {
            jooq //
                    .insertInto(
                            D_DINING_MESSAGE //
                            , D_DINING_MESSAGE.DAYS, D_DINING_MESSAGE.DINING_PLACE_ID, D_DINING_MESSAGE.MESSAGE)
                    .values(
                            sqlDate, form.getDiningPlaceId(), form.getMessage())
                    .execute();

        } else {
            jooq //
                    .update(D_DINING_MESSAGE) //
                    .set(D_DINING_MESSAGE.MESSAGE, form.getMessage()) //
                    .where(D_DINING_MESSAGE.DAYS.eq(sqlDate)) //
                    .and(D_DINING_MESSAGE.DINING_PLACE_ID.eq(form.getDiningPlaceId())) //
                    .execute(); //
        }
    }

    /**
     * 指定した部屋番号の食事データを取得
     * 
     * @param roomNo 部屋番号
     * @return DDiningRecord
     */

    public DDiningRecord getUserDining(String roomNo, String reserveDate) {
        // 日付を取得
        // String todayStr =
        // LocalDate.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));
        LocalDate todayStr = LocalDate.parse(reserveDate, DateTimeFormatter.ofPattern("yyyyMMdd"));
        java.sql.Date sqlDate = java.sql.Date.valueOf(todayStr);

        // SQLクエリで食事データを取得
        return jooq.selectFrom(D_DINING)
                .where(D_DINING.DAYS.eq(sqlDate))
                .and(D_DINING.ROOM_NO.eq(roomNo))
                .fetchOne();
    }
}
