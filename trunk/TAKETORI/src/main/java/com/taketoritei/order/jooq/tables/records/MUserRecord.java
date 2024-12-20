/*
 * This file is generated by jOOQ.
*/
package com.taketoritei.order.jooq.tables.records;


import com.taketoritei.order.jooq.tables.MUser;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record8;
import org.jooq.Row8;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@Generated(
    value = {
        "http://www.jooq.org",
        "jOOQ version:3.10.6"
    },
    comments = "This class is generated by jOOQ"
)
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class MUserRecord extends TableRecordImpl<MUserRecord> implements Record8<String, String, Timestamp, Timestamp, String, Boolean, Timestamp, String> {

    private static final long serialVersionUID = -367912483;

    /**
     * Setter for <code>taketori.m_user.room_no</code>.
     */
    public void setRoomNo(String value) {
        set(0, value);
    }

    /**
     * Getter for <code>taketori.m_user.room_no</code>.
     */
    public String getRoomNo() {
        return (String) get(0);
    }

    /**
     * Setter for <code>taketori.m_user.token</code>.
     */
    public void setToken(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>taketori.m_user.token</code>.
     */
    public String getToken() {
        return (String) get(1);
    }

    /**
     * Setter for <code>taketori.m_user.from_dt</code>.
     */
    public void setFromDt(Timestamp value) {
        set(2, value);
    }

    /**
     * Getter for <code>taketori.m_user.from_dt</code>.
     */
    public Timestamp getFromDt() {
        return (Timestamp) get(2);
    }

    /**
     * Setter for <code>taketori.m_user.to_dt</code>.
     */
    public void setToDt(Timestamp value) {
        set(3, value);
    }

    /**
     * Getter for <code>taketori.m_user.to_dt</code>.
     */
    public Timestamp getToDt() {
        return (Timestamp) get(3);
    }

    /**
     * Setter for <code>taketori.m_user.action_name</code>.
     */
    public void setActionName(String value) {
        set(4, value);
    }

    /**
     * Getter for <code>taketori.m_user.action_name</code>.
     */
    public String getActionName() {
        return (String) get(4);
    }

    /**
     * Setter for <code>taketori.m_user.del_flg</code>.
     */
    public void setDelFlg(Boolean value) {
        set(5, value);
    }

    /**
     * Getter for <code>taketori.m_user.del_flg</code>.
     */
    public Boolean getDelFlg() {
        return (Boolean) get(5);
    }

    /**
     * Setter for <code>taketori.m_user.last_date</code>.
     */
    public void setLastDate(Timestamp value) {
        set(6, value);
    }

    /**
     * Getter for <code>taketori.m_user.last_date</code>.
     */
    public Timestamp getLastDate() {
        return (Timestamp) get(6);
    }

    /**
     * Setter for <code>taketori.m_user.last_user</code>.
     */
    public void setLastUser(String value) {
        set(7, value);
    }

    /**
     * Getter for <code>taketori.m_user.last_user</code>.
     */
    public String getLastUser() {
        return (String) get(7);
    }

    // -------------------------------------------------------------------------
    // Record8 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<String, String, Timestamp, Timestamp, String, Boolean, Timestamp, String> fieldsRow() {
        return (Row8) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row8<String, String, Timestamp, Timestamp, String, Boolean, Timestamp, String> valuesRow() {
        return (Row8) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field1() {
        return MUser.M_USER.ROOM_NO;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return MUser.M_USER.TOKEN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field3() {
        return MUser.M_USER.FROM_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field4() {
        return MUser.M_USER.TO_DT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field5() {
        return MUser.M_USER.ACTION_NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field6() {
        return MUser.M_USER.DEL_FLG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Timestamp> field7() {
        return MUser.M_USER.LAST_DATE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field8() {
        return MUser.M_USER.LAST_USER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component1() {
        return getRoomNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component3() {
        return getFromDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component4() {
        return getToDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component5() {
        return getActionName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component6() {
        return getDelFlg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp component7() {
        return getLastDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component8() {
        return getLastUser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value1() {
        return getRoomNo();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getToken();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value3() {
        return getFromDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value4() {
        return getToDt();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value5() {
        return getActionName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value6() {
        return getDelFlg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Timestamp value7() {
        return getLastDate();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value8() {
        return getLastUser();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MUserRecord value1(String value) {
        setRoomNo(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MUserRecord value2(String value) {
        setToken(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MUserRecord value3(Timestamp value) {
        setFromDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MUserRecord value4(Timestamp value) {
        setToDt(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MUserRecord value5(String value) {
        setActionName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MUserRecord value6(Boolean value) {
        setDelFlg(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MUserRecord value7(Timestamp value) {
        setLastDate(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MUserRecord value8(String value) {
        setLastUser(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MUserRecord values(String value1, String value2, Timestamp value3, Timestamp value4, String value5, Boolean value6, Timestamp value7, String value8) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        value6(value6);
        value7(value7);
        value8(value8);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MUserRecord
     */
    public MUserRecord() {
        super(MUser.M_USER);
    }

    /**
     * Create a detached, initialised MUserRecord
     */
    public MUserRecord(String roomNo, String token, Timestamp fromDt, Timestamp toDt, String actionName, Boolean delFlg, Timestamp lastDate, String lastUser) {
        super(MUser.M_USER);

        set(0, roomNo);
        set(1, token);
        set(2, fromDt);
        set(3, toDt);
        set(4, actionName);
        set(5, delFlg);
        set(6, lastDate);
        set(7, lastUser);
    }
}
