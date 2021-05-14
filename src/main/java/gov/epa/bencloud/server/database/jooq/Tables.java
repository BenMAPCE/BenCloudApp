/*
 * This file is generated by jOOQ.
 */
package gov.epa.bencloud.server.database.jooq;


import gov.epa.bencloud.server.database.jooq.tables.AgeRange;
import gov.epa.bencloud.server.database.jooq.tables.Endpoint;
import gov.epa.bencloud.server.database.jooq.tables.EndpointGroup;
import gov.epa.bencloud.server.database.jooq.tables.Ethnicity;
import gov.epa.bencloud.server.database.jooq.tables.Gender;
import gov.epa.bencloud.server.database.jooq.tables.GridDefinition;
import gov.epa.bencloud.server.database.jooq.tables.IncidenceDataset;
import gov.epa.bencloud.server.database.jooq.tables.IncidenceEntry;
import gov.epa.bencloud.server.database.jooq.tables.IncidenceValue;
import gov.epa.bencloud.server.database.jooq.tables.IncomeGrowthAdjDataset;
import gov.epa.bencloud.server.database.jooq.tables.IncomeGrowthAdjFactor;
import gov.epa.bencloud.server.database.jooq.tables.Pollutant;
import gov.epa.bencloud.server.database.jooq.tables.PollutantMetric;
import gov.epa.bencloud.server.database.jooq.tables.PopConfig;
import gov.epa.bencloud.server.database.jooq.tables.PopConfigEthnicity;
import gov.epa.bencloud.server.database.jooq.tables.PopConfigGender;
import gov.epa.bencloud.server.database.jooq.tables.PopConfigRace;
import gov.epa.bencloud.server.database.jooq.tables.PopulationDataset;
import gov.epa.bencloud.server.database.jooq.tables.PopulationEntry;
import gov.epa.bencloud.server.database.jooq.tables.Race;
import gov.epa.bencloud.server.database.jooq.tables.SeasonalMetric;
import gov.epa.bencloud.server.database.jooq.tables.SeasonalMetricSeason;
import gov.epa.bencloud.server.database.jooq.tables.TaskComplete;
import gov.epa.bencloud.server.database.jooq.tables.TaskQueue;


/**
 * Convenience access to all tables in data.
 */
@SuppressWarnings({ "all", "unchecked", "rawtypes" })
public class Tables {

    /**
     * The table <code>data.age_range</code>.
     */
    public static final AgeRange AGE_RANGE = AgeRange.AGE_RANGE;

    /**
     * The table <code>data.endpoint</code>.
     */
    public static final Endpoint ENDPOINT = Endpoint.ENDPOINT;

    /**
     * The table <code>data.endpoint_group</code>.
     */
    public static final EndpointGroup ENDPOINT_GROUP = EndpointGroup.ENDPOINT_GROUP;

    /**
     * The table <code>data.ethnicity</code>.
     */
    public static final Ethnicity ETHNICITY = Ethnicity.ETHNICITY;

    /**
     * The table <code>data.gender</code>.
     */
    public static final Gender GENDER = Gender.GENDER;

    /**
     * The table <code>data.grid_definition</code>.
     */
    public static final GridDefinition GRID_DEFINITION = GridDefinition.GRID_DEFINITION;

    /**
     * The table <code>data.incidence_dataset</code>.
     */
    public static final IncidenceDataset INCIDENCE_DATASET = IncidenceDataset.INCIDENCE_DATASET;

    /**
     * The table <code>data.incidence_entry</code>.
     */
    public static final IncidenceEntry INCIDENCE_ENTRY = IncidenceEntry.INCIDENCE_ENTRY;

    /**
     * The table <code>data.incidence_value</code>.
     */
    public static final IncidenceValue INCIDENCE_VALUE = IncidenceValue.INCIDENCE_VALUE;

    /**
     * The table <code>data.income_growth_adj_dataset</code>.
     */
    public static final IncomeGrowthAdjDataset INCOME_GROWTH_ADJ_DATASET = IncomeGrowthAdjDataset.INCOME_GROWTH_ADJ_DATASET;

    /**
     * The table <code>data.income_growth_adj_factor</code>.
     */
    public static final IncomeGrowthAdjFactor INCOME_GROWTH_ADJ_FACTOR = IncomeGrowthAdjFactor.INCOME_GROWTH_ADJ_FACTOR;

    /**
     * The table <code>data.pollutant</code>.
     */
    public static final Pollutant POLLUTANT = Pollutant.POLLUTANT;

    /**
     * The table <code>data.pollutant_metric</code>.
     */
    public static final PollutantMetric POLLUTANT_METRIC = PollutantMetric.POLLUTANT_METRIC;

    /**
     * The table <code>data.pop_config</code>.
     */
    public static final PopConfig POP_CONFIG = PopConfig.POP_CONFIG;

    /**
     * The table <code>data.pop_config_ethnicity</code>.
     */
    public static final PopConfigEthnicity POP_CONFIG_ETHNICITY = PopConfigEthnicity.POP_CONFIG_ETHNICITY;

    /**
     * The table <code>data.pop_config_gender</code>.
     */
    public static final PopConfigGender POP_CONFIG_GENDER = PopConfigGender.POP_CONFIG_GENDER;

    /**
     * The table <code>data.pop_config_race</code>.
     */
    public static final PopConfigRace POP_CONFIG_RACE = PopConfigRace.POP_CONFIG_RACE;

    /**
     * The table <code>data.population_dataset</code>.
     */
    public static final PopulationDataset POPULATION_DATASET = PopulationDataset.POPULATION_DATASET;

    /**
     * The table <code>data.population_entry</code>.
     */
    public static final PopulationEntry POPULATION_ENTRY = PopulationEntry.POPULATION_ENTRY;

    /**
     * The table <code>data.race</code>.
     */
    public static final Race RACE = Race.RACE;

    /**
     * The table <code>data.seasonal_metric</code>.
     */
    public static final SeasonalMetric SEASONAL_METRIC = SeasonalMetric.SEASONAL_METRIC;

    /**
     * The table <code>data.seasonal_metric_season</code>.
     */
    public static final SeasonalMetricSeason SEASONAL_METRIC_SEASON = SeasonalMetricSeason.SEASONAL_METRIC_SEASON;

    /**
     * The table <code>data.task_complete</code>.
     */
    public static final TaskComplete TASK_COMPLETE = TaskComplete.TASK_COMPLETE;

    /**
     * The table <code>data.task_queue</code>.
     */
    public static final TaskQueue TASK_QUEUE = TaskQueue.TASK_QUEUE;
}
