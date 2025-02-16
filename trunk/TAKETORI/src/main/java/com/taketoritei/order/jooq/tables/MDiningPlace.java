/*
 * This file is generated by jOOQ.
*/
package com.taketoritei.order.jooq.tables;


import com.taketoritei.order.jooq.Indexes;
import com.taketoritei.order.jooq.Keys;
import com.taketoritei.order.jooq.Taketori;
import com.taketoritei.order.jooq.tables.records.MDiningPlaceRecord;

import java.util.Arrays;
import java.util.List;

import javax.annotation.Generated;

import org.jooq.Field;
import org.jooq.Identity;
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
public class MDiningPlace extends TableImpl<MDiningPlaceRecord> {

    private static final long serialVersionUID = 470210703;

    /**
     * The reference instance of <code>taketori.m_dining_place</code>
     */
    public static final MDiningPlace M_DINING_PLACE = new MDiningPlace();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MDiningPlaceRecord> getRecordType() {
        return MDiningPlaceRecord.class;
    }

    /**
     * The column <code>taketori.m_dining_place.id</code>.
     */
    public final TableField<MDiningPlaceRecord, Integer> ID = createField("id", org.jooq.impl.SQLDataType.INTEGER.nullable(false).defaultValue(org.jooq.impl.DSL.field("nextval('m_dining_place_id_seq'::regclass)", org.jooq.impl.SQLDataType.INTEGER)), this, "");

    /**
     * The column <code>taketori.m_dining_place.kbn</code>.
     */
    public final TableField<MDiningPlaceRecord, Integer> KBN = createField("kbn", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>taketori.m_dining_place.dinner_place</code>.
     */
    public final TableField<MDiningPlaceRecord, String> DINNER_PLACE = createField("dinner_place", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>taketori.m_dining_place.sort_order</code>.
     */
    public final TableField<MDiningPlaceRecord, Integer> SORT_ORDER = createField("sort_order", org.jooq.impl.SQLDataType.INTEGER, this, "");

    /**
     * The column <code>taketori.m_dining_place.del_flg</code>.
     */
    public final TableField<MDiningPlaceRecord, Boolean> DEL_FLG = createField("del_flg", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * Create a <code>taketori.m_dining_place</code> table reference
     */
    public MDiningPlace() {
        this(DSL.name("m_dining_place"), null);
    }

    /**
     * Create an aliased <code>taketori.m_dining_place</code> table reference
     */
    public MDiningPlace(String alias) {
        this(DSL.name(alias), M_DINING_PLACE);
    }

    /**
     * Create an aliased <code>taketori.m_dining_place</code> table reference
     */
    public MDiningPlace(Name alias) {
        this(alias, M_DINING_PLACE);
    }

    private MDiningPlace(Name alias, Table<MDiningPlaceRecord> aliased) {
        this(alias, aliased, null);
    }

    private MDiningPlace(Name alias, Table<MDiningPlaceRecord> aliased, Field<?>[] parameters) {
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
        return Arrays.<Index>asList(Indexes.M_DINING_PLACE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Identity<MDiningPlaceRecord, Integer> getIdentity() {
        return Keys.IDENTITY_M_DINING_PLACE;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public UniqueKey<MDiningPlaceRecord> getPrimaryKey() {
        return Keys.M_DINING_PLACE_PKEY;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<UniqueKey<MDiningPlaceRecord>> getKeys() {
        return Arrays.<UniqueKey<MDiningPlaceRecord>>asList(Keys.M_DINING_PLACE_PKEY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MDiningPlace as(String alias) {
        return new MDiningPlace(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MDiningPlace as(Name alias) {
        return new MDiningPlace(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MDiningPlace rename(String name) {
        return new MDiningPlace(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MDiningPlace rename(Name name) {
        return new MDiningPlace(name, null);
    }
}
