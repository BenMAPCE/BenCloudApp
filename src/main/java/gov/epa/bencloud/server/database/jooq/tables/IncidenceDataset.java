/*
 * This file is generated by jOOQ.
 */
package gov.epa.bencloud.server.database.jooq.tables;


import gov.epa.bencloud.server.database.jooq.Data;
import gov.epa.bencloud.server.database.jooq.tables.records.IncidenceDatasetRecord;

import org.jooq.Field;
import org.jooq.ForeignKey;
import org.jooq.Identity;
import org.jooq.Name;
import org.jooq.Record;
import org.jooq.Row3;
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
public class IncidenceDataset extends TableImpl<IncidenceDatasetRecord> {

    private static final long serialVersionUID = 1L;

    /**
     * The reference instance of <code>data.incidence_dataset</code>
     */
    public static final IncidenceDataset INCIDENCE_DATASET = new IncidenceDataset();

    /**
     * The class holding records for this type
     */
    @Override
    public Class<IncidenceDatasetRecord> getRecordType() {
        return IncidenceDatasetRecord.class;
    }

    /**
     * The column <code>data.incidence_dataset.id</code>.
     */
    public final TableField<IncidenceDatasetRecord, Integer> ID = createField(DSL.name("id"), SQLDataType.INTEGER.nullable(false).identity(true), this, "");

    /**
     * The column <code>data.incidence_dataset.name</code>.
     */
    public final TableField<IncidenceDatasetRecord, String> NAME = createField(DSL.name("name"), SQLDataType.CLOB.nullable(false), this, "");

    /**
     * The column <code>data.incidence_dataset.grid_definition_id</code>.
     */
    public final TableField<IncidenceDatasetRecord, Integer> GRID_DEFINITION_ID = createField(DSL.name("grid_definition_id"), SQLDataType.INTEGER, this, "");

    private IncidenceDataset(Name alias, Table<IncidenceDatasetRecord> aliased) {
        this(alias, aliased, null);
    }

    private IncidenceDataset(Name alias, Table<IncidenceDatasetRecord> aliased, Field<?>[] parameters) {
        super(alias, null, aliased, parameters, DSL.comment(""), TableOptions.table());
    }

    /**
     * Create an aliased <code>data.incidence_dataset</code> table reference
     */
    public IncidenceDataset(String alias) {
        this(DSL.name(alias), INCIDENCE_DATASET);
    }

    /**
     * Create an aliased <code>data.incidence_dataset</code> table reference
     */
    public IncidenceDataset(Name alias) {
        this(alias, INCIDENCE_DATASET);
    }

    /**
     * Create a <code>data.incidence_dataset</code> table reference
     */
    public IncidenceDataset() {
        this(DSL.name("incidence_dataset"), null);
    }

    public <O extends Record> IncidenceDataset(Table<O> child, ForeignKey<O, IncidenceDatasetRecord> key) {
        super(child, key, INCIDENCE_DATASET);
    }

    @Override
    public Schema getSchema() {
        return Data.DATA;
    }

    @Override
    public Identity<IncidenceDatasetRecord, Integer> getIdentity() {
        return (Identity<IncidenceDatasetRecord, Integer>) super.getIdentity();
    }

    @Override
    public IncidenceDataset as(String alias) {
        return new IncidenceDataset(DSL.name(alias), this);
    }

    @Override
    public IncidenceDataset as(Name alias) {
        return new IncidenceDataset(alias, this);
    }

    /**
     * Rename this table
     */
    @Override
    public IncidenceDataset rename(String name) {
        return new IncidenceDataset(DSL.name(name), null);
    }

    /**
     * Rename this table
     */
    @Override
    public IncidenceDataset rename(Name name) {
        return new IncidenceDataset(name, null);
    }

    // -------------------------------------------------------------------------
    // Row3 type methods
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, String, Integer> fieldsRow() {
        return (Row3) super.fieldsRow();
    }
}
