/*
 * This file is generated by jOOQ.
*/
package com.taketoritei.order.jooq.tables.records;


import com.taketoritei.order.jooq.tables.DCartLang;

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
public class DCartLangRecord extends UpdatableRecordImpl<DCartLangRecord> implements Record3<Integer, String, String> {

    private static final long serialVersionUID = 768685135;

    /**
     * Setter for <code>taketori.d_cart_lang.cart_id</code>.
     */
    public void setCartId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>taketori.d_cart_lang.cart_id</code>.
     */
    public Integer getCartId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>taketori.d_cart_lang.language</code>.
     */
    public void setLanguage(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>taketori.d_cart_lang.language</code>.
     */
    public String getLanguage() {
        return (String) get(1);
    }

    /**
     * Setter for <code>taketori.d_cart_lang.name</code>.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>taketori.d_cart_lang.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Record2<Integer, String> key() {
        return (Record2) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Integer, String, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Row3<Integer, String, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<Integer> field1() {
        return DCartLang.D_CART_LANG.CART_ID;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field2() {
        return DCartLang.D_CART_LANG.LANGUAGE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Field<String> field3() {
        return DCartLang.D_CART_LANG.NAME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer component1() {
        return getCartId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component2() {
        return getLanguage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String component3() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Integer value1() {
        return getCartId();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value2() {
        return getLanguage();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String value3() {
        return getName();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DCartLangRecord value1(Integer value) {
        setCartId(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DCartLangRecord value2(String value) {
        setLanguage(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DCartLangRecord value3(String value) {
        setName(value);
        return this;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DCartLangRecord values(Integer value1, String value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached DCartLangRecord
     */
    public DCartLangRecord() {
        super(DCartLang.D_CART_LANG);
    }

    /**
     * Create a detached, initialised DCartLangRecord
     */
    public DCartLangRecord(Integer cartId, String language, String name) {
        super(DCartLang.D_CART_LANG);

        set(0, cartId);
        set(1, language);
        set(2, name);
    }
}