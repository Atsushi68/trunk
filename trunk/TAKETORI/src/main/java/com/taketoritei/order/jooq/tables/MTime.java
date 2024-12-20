/*
 * This file is generated by jOOQ.
*/
package com.taketoritei.order.jooq.tables;


import com.taketoritei.order.jooq.Taketori;
import com.taketoritei.order.jooq.tables.records.MTimeRecord;

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
public class MTime extends TableImpl<MTimeRecord> {

    private static final long serialVersionUID = -1868502827;

    /**
     * The reference instance of <code>taketori.m_time</code>
     */
    public static final MTime M_TIME = new MTime();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MTimeRecord> getRecordType() {
        return MTimeRecord.class;
    }

    /**
     * The column <code>taketori.m_time.time_cd</code>.
     */
    public final TableField<MTimeRecord, String> TIME_CD = createField("time_cd", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>taketori.m_time.disp_text</code>.
     */
    public final TableField<MTimeRecord, String> DISP_TEXT = createField("disp_text", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>taketori.m_time.bath_type</code>.
     */
    public final TableField<MTimeRecord, String> BATH_TYPE = createField("bath_type", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>taketori.m_time.selectable</code>.
     */
    public final TableField<MTimeRecord, Boolean> SELECTABLE = createField("selectable", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>taketori.m_time.next_day</code>.
     */
    public final TableField<MTimeRecord, Boolean> NEXT_DAY = createField("next_day", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>taketori.m_time.hm_from</code>.
     */
    public final TableField<MTimeRecord, String> HM_FROM = createField("hm_from", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>taketori.m_time.hm_to</code>.
     */
    public final TableField<MTimeRecord, String> HM_TO = createField("hm_to", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>taketori.m_time</code> table reference
     */
    public MTime() {
        this(DSL.name("m_time"), null);
    }

    /**
     * Create an aliased <code>taketori.m_time</code> table reference
     */
    public MTime(String alias) {
        this(DSL.name(alias), M_TIME);
    }

    /**
     * Create an aliased <code>taketori.m_time</code> table reference
     */
    public MTime(Name alias) {
        this(alias, M_TIME);
    }

    private MTime(Name alias, Table<MTimeRecord> aliased) {
        this(alias, aliased, null);
    }

    private MTime(Name alias, Table<MTimeRecord> aliased, Field<?>[] parameters) {
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
    public MTime as(String alias) {
        return new MTime(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MTime as(Name alias) {
        return new MTime(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MTime rename(String name) {
        return new MTime(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MTime rename(Name name) {
        return new MTime(name, null);
    }
}
