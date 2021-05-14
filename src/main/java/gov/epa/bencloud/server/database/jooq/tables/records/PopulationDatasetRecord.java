/*
 * This file is generated by jOOQ.
 */
package gov.epa.bencloud.server.database.jooq.tables.records;


import gov.epa.bencloud.server.database.jooq.tables.PopulationDataset;

import org.jooq.Field;
import org.jooq.Record1;
import org.jooq.Record5;
import org.jooq.Row5;
import org.jooq.impl.UpdatableRecordImpl;


/**
 * This class is generated by jOOQ.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class PopulationDatasetRecord extends UpdatableRecordImpl<PopulationDatasetRecord> implements Record5<Integer, String, Integer, Integer, Integer> {

    private static final long serialVersionUID = 1L;

    /**
     * Setter for <code>data.population_dataset.id</code>.
     */
    public void setId(Integer value) {
        set(0, value);
    }

    /**
     * Getter for <code>data.population_dataset.id</code>.
     */
    public Integer getId() {
        return (Integer) get(0);
    }

    /**
     * Setter for <code>data.population_dataset.name</code>.
     */
    public void setName(String value) {
        set(1, value);
    }

    /**
     * Getter for <code>data.population_dataset.name</code>.
     */
    public String getName() {
        return (String) get(1);
    }

    /**
     * Setter for <code>data.population_dataset.pop_config_id</code>.
     */
    public void setPopConfigId(Integer value) {
        set(2, value);
    }

    /**
     * Getter for <code>data.population_dataset.pop_config_id</code>.
     */
    public Integer getPopConfigId() {
        return (Integer) get(2);
    }

    /**
     * Setter for <code>data.population_dataset.grid_definition_id</code>.
     */
    public void setGridDefinitionId(Integer value) {
        set(3, value);
    }

    /**
     * Getter for <code>data.population_dataset.grid_definition_id</code>.
     */
    public Integer getGridDefinitionId() {
        return (Integer) get(3);
    }

    /**
     * Setter for <code>data.population_dataset.apply_growth</code>.
     */
    public void setApplyGrowth(Integer value) {
        set(4, value);
    }

    /**
     * Getter for <code>data.population_dataset.apply_growth</code>.
     */
    public Integer getApplyGrowth() {
        return (Integer) get(4);
    }

    // -------------------------------------------------------------------------
    // Primary key information
    // -------------------------------------------------------------------------

    @Override
    public Record1<Integer> key() {
        return (Record1) super.key();
    }

    // -------------------------------------------------------------------------
    // Record5 type implementation
    // -------------------------------------------------------------------------

    @Override
    public Row5<Integer, String, Integer, Integer, Integer> fieldsRow() {
        return (Row5) super.fieldsRow();
    }

    @Override
    public Row5<Integer, String, Integer, Integer, Integer> valuesRow() {
        return (Row5) super.valuesRow();
    }

    @Override
    public Field<Integer> field1() {
        return PopulationDataset.POPULATION_DATASET.ID;
    }

    @Override
    public Field<String> field2() {
        return PopulationDataset.POPULATION_DATASET.NAME;
    }

    @Override
    public Field<Integer> field3() {
        return PopulationDataset.POPULATION_DATASET.POP_CONFIG_ID;
    }

    @Override
    public Field<Integer> field4() {
        return PopulationDataset.POPULATION_DATASET.GRID_DEFINITION_ID;
    }

    @Override
    public Field<Integer> field5() {
        return PopulationDataset.POPULATION_DATASET.APPLY_GROWTH;
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
    public Integer component3() {
        return getPopConfigId();
    }

    @Override
    public Integer component4() {
        return getGridDefinitionId();
    }

    @Override
    public Integer component5() {
        return getApplyGrowth();
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
    public Integer value3() {
        return getPopConfigId();
    }

    @Override
    public Integer value4() {
        return getGridDefinitionId();
    }

    @Override
    public Integer value5() {
        return getApplyGrowth();
    }

    @Override
    public PopulationDatasetRecord value1(Integer value) {
        setId(value);
        return this;
    }

    @Override
    public PopulationDatasetRecord value2(String value) {
        setName(value);
        return this;
    }

    @Override
    public PopulationDatasetRecord value3(Integer value) {
        setPopConfigId(value);
        return this;
    }

    @Override
    public PopulationDatasetRecord value4(Integer value) {
        setGridDefinitionId(value);
        return this;
    }

    @Override
    public PopulationDatasetRecord value5(Integer value) {
        setApplyGrowth(value);
        return this;
    }

    @Override
    public PopulationDatasetRecord values(Integer value1, String value2, Integer value3, Integer value4, Integer value5) {
        value1(value1);
        value2(value2);
        value3(value3);
        value4(value4);
        value5(value5);
        return this;
    }

    // -------------------------------------------------------------------------
    // Constructors
    // -------------------------------------------------------------------------

    /**
     * Create a detached PopulationDatasetRecord
     */
    public PopulationDatasetRecord() {
        super(PopulationDataset.POPULATION_DATASET);
    }

    /**
     * Create a detached, initialised PopulationDatasetRecord
     */
    public PopulationDatasetRecord(Integer id, String name, Integer popConfigId, Integer gridDefinitionId, Integer applyGrowth) {
        super(PopulationDataset.POPULATION_DATASET);

        setId(id);
        setName(name);
        setPopConfigId(popConfigId);
        setGridDefinitionId(gridDefinitionId);
        setApplyGrowth(applyGrowth);
    }
}
