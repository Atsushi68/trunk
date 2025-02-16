/*
 * This file is generated by jOOQ.
*/
package com.taketoritei.order.jooq.tables.records;


import com.taketoritei.order.jooq.tables.MDiningPlace;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
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
public class MDiningPlaceRecord extends UpdatableRecordImpl<MDiningPlaceRecord> implements Record5<Integer, Integer, String, Integer, Boolean> {

    private static final long serialVersionUID = 1354289838;

    /**
     * Setter for <code>taketori.m_dining_place.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>taketori.m_dining_place.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>taketori.m_dining_place.kbn</code>.
     */
    public void setKbn(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>taketori.m_dining_place.kbn</code>.
     */
    public Integer getKbn() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>taketori.m_dining_place.dinner_place</code>.
     */
    public void setDinnerPlace(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>taketori.m_dining_place.dinner_place</code>.
     */
    public String getDinnerPlace() {
        return (String) get(2);
    }

    /**
     * Setter for <code>taketori.m_dining_place.sort_order</code>.
     */
    public void setSortOrder(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>taketori.m_dining_place.sort_order</code>.
     */
    public Integer getSortOrder() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>taketori.m_dining_place.del_flg</code>.
     */
    public void setDelFlg(Boolean value) {
        set(4, value);
    }

    /**
     * Getter for <code>taketori.m_dining_place.del_flg</code>.
     */
    public Boolean getDelFlg() {
        return (Boolean) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, Integer, String, Integer, Boolean> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row5<Integer, Integer, String, Integer, Boolean> valuesRow() {
        return (Row5) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return MDiningPlace.M_DINING_PLACE.ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field2() {
        return MDiningPlace.M_DINING_PLACE.KBN;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return MDiningPlace.M_DINING_PLACE.DINNER_PLACE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field4() {
        return MDiningPlace.M_DINING_PLACE.SORT_ORDER;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Boolean> field5() {
        return MDiningPlace.M_DINING_PLACE.DEL_FLG;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component2() {
        return getKbn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getDinnerPlace();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component4() {
        return getSortOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean component5() {
        return getDelFlg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value2() {
        return getKbn();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getDinnerPlace();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value4() {
        return getSortOrder();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Boolean value5() {
        return getDelFlg();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MDiningPlaceRecord value1(Integer value) {
        setId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MDiningPlaceRecord value2(Integer value) {
        setKbn(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MDiningPlaceRecord value3(String value) {
        setDinnerPlace(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MDiningPlaceRecord value4(Integer value) {
        setSortOrder(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MDiningPlaceRecord value5(Boolean value) {
        setDelFlg(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MDiningPlaceRecord values(Integer value1, Integer value2, String value3, Integer value4, Boolean value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached MDiningPlaceRecord
     */
    public MDiningPlaceRecord() {
        super(MDiningPlace.M_DINING_PLACE);
    }

    /**
     * Create a detached, initialised MDiningPlaceRecord
     */
    public MDiningPlaceRecord(Integer id, Integer kbn, String dinnerPlace, Integer sortOrder, Boolean delFlg) {
        super(MDiningPlace.M_DINING_PLACE);

        set(0, id);
        set(1, kbn);
        set(2, dinnerPlace);
        set(3, sortOrder);
        set(4, delFlg);
    }
}
