/*
 * This file is generated by jOOQ.
*/
package com.taketoritei.order.jooq.tables;


import com.taketoritei.order.jooq.Taketori;
import com.taketoritei.order.jooq.tables.records.VdCartRecord;

import java.sql.Timestamp;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.impl.DSL;
import org.jooq.impl.TableImpl;


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
public class VdCart extends TableImpl<VdCartRecord> {

    private static final long serialVersionUID = 1700273583;

    /**
     * The reference instance of <code>taketori.vd_cart</code>
     */
    public static final VdCart VD_CART = new VdCart();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<VdCartRecord> getRecordType() {
        return VdCartRecord.class;
    }

    /**
     * The column <code>taketori.vd_cart.cart_id</code>.
     */
    public final TableField<VdCartRecord, Integer> CART_ID = createField("cart_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>taketori.vd_cart.token</code>.
     */
    public final TableField<VdCartRecord, String> TOKEN = createField("token", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>taketori.vd_cart.omiyage_id</code>.
     */
    public final TableField<VdCartRecord, Integer> OMIYAGE_ID = createField("omiyage_id", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>taketori.vd_cart.price</code>.
     */
    public final TableField<VdCartRecord, Integer> PRICE = createField("price", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>taketori.vd_cart.num</code>.
     */
    public final TableField<VdCartRecord, Integer> NUM = createField("num", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>taketori.vd_cart.cart_token</code>.
     */
    public final TableField<VdCartRecord, String> CART_TOKEN = createField("cart_token", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>taketori.vd_cart.del_flg</code>.
     */
    public final TableField<VdCartRecord, Boolean> DEL_FLG = createField("del_flg", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>taketori.vd_cart.last_date</code>.
     */
    public final TableField<VdCartRecord, Timestamp> LAST_DATE = createField("last_date", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>taketori.vd_cart.last_user</code>.
     */
    public final TableField<VdCartRecord, String> LAST_USER = createField("last_user", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>taketori.vd_cart.language</code>.
     */
    public final TableField<VdCartRecord, String> LANGUAGE = createField("language", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>taketori.vd_cart.name</code>.
     */
    public final TableField<VdCartRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>taketori.vd_cart</code> table reference
     */
    public VdCart() {
        this(DSL.name("vd_cart"), null);
    }

    /**
     * Create an aliased <code>taketori.vd_cart</code> table reference
     */
    public VdCart(String alias) {
        this(DSL.name(alias), VD_CART);
    }

    /**
     * Create an aliased <code>taketori.vd_cart</code> table reference
     */
    public VdCart(Name alias) {
        this(alias, VD_CART);
    }

    private VdCart(Name alias, Table<VdCartRecord> aliased) {
        this(alias, aliased, null);
    }

    private VdCart(Name alias, Table<VdCartRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, "");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Schema getSchema() {
        return Taketori.TAKETORI;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VdCart as(String alias) {
        return new VdCart(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public VdCart as(Name alias) {
        return new VdCart(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public VdCart rename(String name) {
        return new VdCart(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public VdCart rename(Name name) {
        return new VdCart(name, null);
    }
}