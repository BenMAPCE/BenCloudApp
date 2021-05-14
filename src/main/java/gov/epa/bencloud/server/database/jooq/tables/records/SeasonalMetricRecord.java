/*
 * This file is generated by jOOQ.
 */
package gov.epa.bencloud.server.database.jooq.tables.records;


import gov.epa.bencloud.server.database.jooq.tables.SeasonalMetric;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record3;
import org.jooq.Row3;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class SeasonalMetricRecord extends UpdatableRecordImpl<SeasonalMetricRecord> implements Record3<Integer, Integer, String> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>data.seasonal_metric.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>data.seasonal_metric.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>data.seasonal_metric.metric_id</code>.
     */
    public void setMetricId(Integer value) {
        set(1, value);
    }

    /**
     * Getter for <code>data.seasonal_metric.metric_id</code>.
     */
    public Integer getMetricId() {
        return (Integer) get(1);
    }

    /**
     * Setter for <code>data.seasonal_metric.name</code>.
     */
    public void setName(String value) {
        set(2, value);
    }

    /**
     * Getter for <code>data.seasonal_metric.name</code>.
     */
    public String getName() {
        return (String) get(2);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record3 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row3<Integer, Integer, String> fieldsRow() {
        return (Row3) super.fieldsRow();
    }

    @Override
    public Row3<Integer, Integer, String> valuesRow() {
        return (Row3) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return SeasonalMetric.SEASONAL_METRIC.ID;
    }

    @Override
    public Field<Integer> field2() {
        return SeasonalMetric.SEASONAL_METRIC.METRIC_ID;
    }

    @Override
    public Field<String> field3() {
        return SeasonalMetric.SEASONAL_METRIC.NAME;
    }

    @Override
    public Integer component1() {
        return getId();
    }

    @Override
    public Integer component2() {
        return getMetricId();
    }

    @Override
    public String component3() {
        return getName();
    }

    @Override
    public Integer value1() {
        return getId();
    }

    @Override
    public Integer value2() {
        return getMetricId();
    }

    @Override
    public String value3() {
        return getName();
    }

    @Override
    public SeasonalMetricRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public SeasonalMetricRecord value2(Integer value) {
        setMetricId(value);
        return this;
    }

    @Override
    public SeasonalMetricRecord value3(String value) {
        setName(value);
        return this;
    }

    @Override
    public SeasonalMetricRecord values(Integer value1, Integer value2, String value3) {
        value1(value1);
        value2(value2);
        value3(value3);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached SeasonalMetricRecord
     */
    public SeasonalMetricRecord() {
        super(SeasonalMetric.SEASONAL_METRIC);
    }

    /**
     * Create a detached, initialised SeasonalMetricRecord
     */
    public SeasonalMetricRecord(Integer id, Integer metricId, String name) {
        super(SeasonalMetric.SEASONAL_METRIC);

        setId(id);
        setMetricId(metricId);
        setName(name);
    }
}
