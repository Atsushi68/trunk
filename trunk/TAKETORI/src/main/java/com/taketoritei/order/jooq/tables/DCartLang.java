/*
 * This file is generated by jOOQ.
*/
package com.taketoritei.order.jooq.tables;


import com.taketoritei.order.jooq.Indexes;
import com.taketoritei.order.jooq.Keys;
import com.taketoritei.order.jooq.Taketori;
import com.taketoritei.order.jooq.tables.records.DCartLangRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Index;
import org.jooq.Name;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.UniqueKey;
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
public class DCartLang extends TableImpl<DCartLangRecord> {

    private static final long serialVersionUID = -202770036;

    /**
     * The reference instance of <code>taketori.d_cart_lang</code>
     */
    public static final DCartLang D_CART_LANG = new DCartLang();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<DCartLangRecord> getRecordType() {
        return DCartLangRecord.class;
    }

    /**
     * The column <code>taketori.d_cart_lang.cart_id</code>.
     */
    public final TableField<DCartLangRecord, Integer> CART_ID = createField("cart_id", org.jooq.impl.SQLDataType.INTEGER.nullable(false), this, "");

    /**
     * The column <code>taketori.d_cart_lang.language</code>.
     */
    public final TableField<DCartLangRecord, String> LANGUAGE = createField("language", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>taketori.d_cart_lang.name</code>.
     */
    public final TableField<DCartLangRecord, String> NAME = createField("name", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>taketori.d_cart_lang</code> table reference
     */
    public DCartLang() {
        this(DSL.name("d_cart_lang"), null);
    }

    /**
     * Create an aliased <code>taketori.d_cart_lang</code> table reference
     */
    public DCartLang(String alias) {
        this(DSL.name(alias), D_CART_LANG);
    }

    /**
     * Create an aliased <code>taketori.d_cart_lang</code> table reference
     */
    public DCartLang(Name alias) {
        this(alias, D_CART_LANG);
    }

    private DCartLang(Name alias, Table<DCartLangRecord> aliased) {
        this(alias, aliased, null);
    }

    private DCartLang(Name alias, Table<DCartLangRecord> aliased, Field<?>[] parameters) {
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
    public List<Index> getIndexes() {
        return Arrays.<Index>asList(Indexes.D_CART_LANG_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<DCartLangRecord> getPrimaryKey() {
        return Keys.D_CART_LANG_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<DCartLangRecord>> getKeys() {
        return Arrays.<UniqueKey<DCartLangRecord>>asList(Keys.D_CART_LANG_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DCartLang as(String alias) {
        return new DCartLang(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public DCartLang as(Name alias) {
        return new DCartLang(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public DCartLang rename(String name) {
        return new DCartLang(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public DCartLang rename(Name name) {
        return new DCartLang(name, null);
    }
}