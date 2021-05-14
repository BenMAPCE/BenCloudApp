/*
 * This file is generated by jOOQ.
 */
package gov.epa.bencloud.server.database.jooq.tables.records;


import gov.epa.bencloud.server.database.jooq.tables.EndpointGroup;

import org.jooq.Field;
import org.jooq.Record2;
import org.jooq.Row2;
import org.jooq.impl.TableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class EndpointGroupRecord extends TableRecordImpl<EndpointGroupRecord> implements Record2<Integer, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>data.endpoint_group.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>data.endpoint_group.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>data.endpoint_group.name</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>data.endpoint_group.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    // -------------------------------------------------------------------------
    // Record2 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row2<Integer, String> fieldsRow() {
        return (Row2) super.fieldsRow();
    }

    @Override
    public Row2<Integer, String> valuesRow() {
        return (Row2) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return EndpointGroup.ENDPOINT_GROUP.ID;
    }

    @Override
    public Field<String> field2() {
        return EndpointGroup.ENDPOINT_GROUP.NAME;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public String component2() {
        return getName();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public String value2() {
        return getName();
    }

    @Override
    public EndpointGroupRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public EndpointGroupRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public EndpointGroupRecord values(Integer value1, String value2) {
        value1(value1);
        value2(value2);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached EndpointGroupRecord
     */
    public EndpointGroupRecord() {
        super(EndpointGroup.ENDPOINT_GROUP);
    }

    /**
     * Create a detached, initialised EndpointGroupRecord
     */
    public EndpointGroupRecord(Integer id, String name) {
        super(EndpointGroup.ENDPOINT_GROUP);

        setId(id);
        setName(name);
    }
}
