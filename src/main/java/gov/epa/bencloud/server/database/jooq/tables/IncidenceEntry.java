/*
 * This file is generated by jOOQ.
 */
package gov.epa.bencloud.server.database.jooq.tables;


import gov.epa.bencloud.server.database.jooq.Data;
import gov.epa.bencloud.server.database.jooq.tables.records.IncidenceEntryRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row11;
import org.jooq.Schema;
import org.jooq.Table;
import org.jooq.TableField;
import org.jooq.TableOptions;
import org.jooq.impl.DSL;
import org.jooq.impl.SQLDataType;
import org.jooq.impl.TableImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class IncidenceEntry extends TableImpl<IncidenceEntryRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>data.incidence_entry</code>
     */
    public static final IncidenceEntry INCIDENCE_ENTRY = new IncidenceEntry();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<IncidenceEntryRecord> getRecordType() {
        return IncidenceEntryRecord.class;
    }

    /**
     * The column <code>data.incidence_entry.id</code>.
     */
    public final TableField<IncidenceEntryRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>data.incidence_entry.incidence_dataset_id</code>.
     */
    public final TableField<IncidenceEntryRecord, Integer> INCIDENCE_DATASET_ID = createField(DSL.name("incidence_dataset_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>data.incidence_entry.grid_definition_id</code>.
     */
    public final TableField<IncidenceEntryRecord, Integer> GRID_DEFINITION_ID = createField(DSL.name("grid_definition_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>data.incidence_entry.endpoint_group_id</code>.
     */
    public final TableField<IncidenceEntryRecord, Integer> ENDPOINT_GROUP_ID = createField(DSL.name("endpoint_group_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>data.incidence_entry.endpoint_id</code>.
     */
    public final TableField<IncidenceEntryRecord, Integer> ENDPOINT_ID = createField(DSL.name("endpoint_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>data.incidence_entry.race_id</code>.
     */
    public final TableField<IncidenceEntryRecord, Integer> RACE_ID = createField(DSL.name("race_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>data.incidence_entry.gender_id</code>.
     */
    public final TableField<IncidenceEntryRecord, Integer> GENDER_ID = createField(DSL.name("gender_id"), SQLDataType.INTEGER, this, "");

    /**
     * The column <code>data.incidence_entry.start_age</code>.
     */
    public final TableField<IncidenceEntryRecord, Short> START_AGE = createField(DSL.name("start_age"), SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>data.incidence_entry.end_age</code>.
     */
    public final TableField<IncidenceEntryRecord, Short> END_AGE = createField(DSL.name("end_age"), SQLDataType.SMALLINT, this, "");

    /**
     * The column <code>data.incidence_entry.prevalence</code>.
     */
    public final TableField<IncidenceEntryRecord, String> PREVALENCE = createField(DSL.name("prevalence"), SQLDataType.VARCHAR(1), this, "");

    /**
     * The column <code>data.incidence_entry.ethnicity_id</code>.
     */
    public final TableField<IncidenceEntryRecord, Integer> ETHNICITY_ID = createField(DSL.name("ethnicity_id"), SQLDataType.INTEGER, this, "");

    private IncidenceEntry(Name alias, Table<IncidenceEntryRecord> aliased) {
        this(alias, aliased, null);
    }

    private IncidenceEntry(Name alias, Table<IncidenceEntryRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>data.incidence_entry</code> table reference
     */
    public IncidenceEntry(String alias) {
        this(DSL.name(alias), INCIDENCE_ENTRY);
    }

    /**
     * Create an aliased <code>data.incidence_entry</code> table reference
     */
    public IncidenceEntry(Name alias) {
        this(alias, INCIDENCE_ENTRY);
    }

    /**
     * Create a <code>data.incidence_entry</code> table reference
     */
    public IncidenceEntry() {
        this(DSL.name("incidence_entry"), null);
    }

    public <O extends Record> IncidenceEntry(Table<O> child, ForeignKey<O, IncidenceEntryRecord> key) {
        super(child, key, INCIDENCE_ENTRY);
    }

    @Override
    public Schema getSchema() {
        return Data.DATA;
    }

    @Override
    public Identity<IncidenceEntryRecord, Integer> getIdentity() {
        return (Identity<IncidenceEntryRecord, Integer>) super.getIdentity();
    }

    @Override
    public IncidenceEntry as(String alias) {
        return new IncidenceEntry(DSL.name(alias), this);
    }

    @Override
    public IncidenceEntry as(Name alias) {
        return new IncidenceEntry(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public IncidenceEntry rename(String name) {
        return new IncidenceEntry(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public IncidenceEntry rename(Name name) {
        return new IncidenceEntry(name, null);
    }

    // -------------------------------------------------------------------------
    // Row11 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row11<Integer, Integer, Integer, Integer, Integer, Integer, Integer, Short, Short, String, Integer> fieldsRow() {
        return (Row11) super.fieldsRow();
    }
}
