/*
 * This file is generated by jOOQ.
*/
package com.taketoritei.order.jooq.tables.records;


import com.taketoritei.order.jooq.tables.DDiningMessage;

import java.sql.Date;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


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
public class DDiningMessageRecord extends UpdatableRecordImpl<DDiningMessageRecord> implements Record3<Date, Integer, String> {

    private static final long serialVersionUID = 1040255211;

    /**
     * Setter for <code>taketori.d_dining_message.days</code>.
     */
    public void setDays(Date value) {
        set(0, value);
    }

    /**
     * Getter for <code>taketori.d_dining_message.days</code>.
     */
    public Date getDays() {
        return (Date) get(0);
    }

    /**
     * Setter for <code>taketori.d_dining_message.dining_place_id</code>.
     */
    public void setDiningPlaceId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>taketori.d_dining_message.dining_place_id</code>.
     */
    public Integer getDiningPlaceId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>taketori.d_dining_message.message</code>.
     */
    public void setMessage(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>taketori.d_dining_message.message</code>.
     */
    public String getMessage() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<Date, Integer> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Date, Integer, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Date, Integer, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Date> field1() {
        return DDiningMessage.D_DINING_MESSAGE.DAYS;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return DDiningMessage.D_DINING_MESSAGE.DINING_PLACE_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return DDiningMessage.D_DINING_MESSAGE.MESSAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date component1() {
        return getDays();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getDiningPlaceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Date value1() {
        return getDays();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getDiningPlaceId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getMessage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DDiningMessageRecord value1(Date value) {
        setDays(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DDiningMessageRecord value2(Integer value) {
        setDiningPlaceId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DDiningMessageRecord value3(String value) {
        setMessage(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DDiningMessageRecord values(Date value1, Integer value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DDiningMessageRecord
     */
    public DDiningMessageRecord() {
        super(DDiningMessage.D_DINING_MESSAGE);
    }

    /**
     * Create a detached, initialised DDiningMessageRecord
     */
    public DDiningMessageRecord(Date days, Integer diningPlaceId, String message) {
        super(DDiningMessage.D_DINING_MESSAGE);

        set(0, days);
        set(1, diningPlaceId);
        set(2, message);
    }
}
