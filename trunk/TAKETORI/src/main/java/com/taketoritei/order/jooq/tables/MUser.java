/*
 * This file is generated by jOOQ.
*/
package com.taketoritei.order.jooq.tables;


import com.taketoritei.order.jooq.Taketori;
import com.taketoritei.order.jooq.tables.records.MUserRecord;

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
public class MUser extends TableImpl<MUserRecord> {

    private static final long serialVersionUID = 1299927394;

    /**
     * The reference instance of <code>taketori.m_user</code>
     */
    public static final MUser M_USER = new MUser();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<MUserRecord> getRecordType() {
        return MUserRecord.class;
    }

    /**
     * The column <code>taketori.m_user.room_no</code>.
     */
    public final TableField<MUserRecord, String> ROOM_NO = createField("room_no", org.jooq.impl.SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>taketori.m_user.token</code>.
     */
    public final TableField<MUserRecord, String> TOKEN = createField("token", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>taketori.m_user.from_dt</code>.
     */
    public final TableField<MUserRecord, Timestamp> FROM_DT = createField("from_dt", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>taketori.m_user.to_dt</code>.
     */
    public final TableField<MUserRecord, Timestamp> TO_DT = createField("to_dt", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>taketori.m_user.action_name</code>.
     */
    public final TableField<MUserRecord, String> ACTION_NAME = createField("action_name", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * The column <code>taketori.m_user.del_flg</code>.
     */
    public final TableField<MUserRecord, Boolean> DEL_FLG = createField("del_flg", org.jooq.impl.SQLDataType.BOOLEAN, this, "");

    /**
     * The column <code>taketori.m_user.last_date</code>.
     */
    public final TableField<MUserRecord, Timestamp> LAST_DATE = createField("last_date", org.jooq.impl.SQLDataType.TIMESTAMP, this, "");

    /**
     * The column <code>taketori.m_user.last_user</code>.
     */
    public final TableField<MUserRecord, String> LAST_USER = createField("last_user", org.jooq.impl.SQLDataType.CLOB, this, "");

    /**
     * Create a <code>taketori.m_user</code> table reference
     */
    public MUser() {
        this(DSL.name("m_user"), null);
    }

    /**
     * Create an aliased <code>taketori.m_user</code> table reference
     */
    public MUser(String alias) {
        this(DSL.name(alias), M_USER);
    }

    /**
     * Create an aliased <code>taketori.m_user</code> table reference
     */
    public MUser(Name alias) {
        this(alias, M_USER);
    }

    private MUser(Name alias, Table<MUserRecord> aliased) {
        this(alias, aliased, null);
    }

    private MUser(Name alias, Table<MUserRecord> aliased, Field<?>[] parameters) {
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
    public MUser as(String alias) {
        return new MUser(DSL.name(alias), this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MUser as(Name alias) {
        return new MUser(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public MUser rename(String name) {
        return new MUser(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public MUser rename(Name name) {
        return new MUser(name, null);
    }
}