package gov.epa.bencloud.api.util;

import static gov.epa.bencloud.server.database.jooq.Tables.*;

import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.impl.DSL;
import org.mariuszgromada.math.mxparser.*;

import gov.epa.bencloud.api.model.HIFConfig;
import gov.epa.bencloud.api.model.HIFTaskConfig;
import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.tables.records.HealthImpactFunctionRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.HifResultDatasetRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.HifResultRecord;
import gov.epa.bencloud.server.tasks.model.Task;

public class HIFUtil {

	public static Expression[] getFunctionAndBaselineExpression(Integer id) {

		Expression[] functionAndBaselineExpressions = new Expression[2];
		// Load the function by id
		DSLContext create = DSL.using(JooqUtil.getJooqConfiguration());

		HealthImpactFunctionRecord record = create
				.selectFrom(HEALTH_IMPACT_FUNCTION)
				.where(HEALTH_IMPACT_FUNCTION.ID.eq(id))
				.fetchOne();
		
		// Populate/create the necessary arguments
		//{ a, b, c, beta, deltaq, q0, q1, incidence, pop, prevalence };
		Argument a = new Argument("A", record.getValA().doubleValue());
		Argument b = new Argument("B", record.getValB().doubleValue());
		Argument c = new Argument("C", record.getValC().doubleValue());
		Argument beta = new Argument("BETA", record.getBeta().doubleValue());
		Argument deltaQ = new Argument("DELTAQ", 0.0);
		Argument q1 = new Argument("Q0", 0.0);
		Argument q2 = new Argument("Q1", 0.0);
		Argument incidence = new Argument("INCIDENCE", 0.0);
		Argument prevalence = new Argument("PREVALENCE", 0.0);
		Argument population = new Argument("POPULATION", 0.0);
		
		// return the expression
		functionAndBaselineExpressions[0] = new Expression(record.getFunctionText(), a, b, c, beta, deltaQ, q1, q2, incidence, prevalence, population);		
		functionAndBaselineExpressions[1] = new Expression(record.getBaselineFunctionText(), a, b, c, beta, deltaQ, q1, q2, incidence, prevalence, population);		

		return functionAndBaselineExpressions;
	}

	public static HealthImpactFunctionRecord getFunctionDefinition(Integer id) {

		// Load the function by id
		DSLContext create = DSL.using(JooqUtil.getJooqConfiguration());

		HealthImpactFunctionRecord record = create
				.selectFrom(HEALTH_IMPACT_FUNCTION)
				.where(HEALTH_IMPACT_FUNCTION.ID.eq(id))
				.fetchOne();
				
		return record;
	}
	
	public static void storeResults(Task task, HIFTaskConfig hifTaskConfig, ArrayList<HifResultRecord> hifResults) {
		DSLContext create = DSL.using(JooqUtil.getJooqConfiguration());
		
		// HIF result dataset record links the result dataset id to the task uuid
		HifResultDatasetRecord hifResultDatasetRecord = create.insertInto(
				HIF_RESULT_DATASET
				, HIF_RESULT_DATASET.TASK_UUID
				, HIF_RESULT_DATASET.NAME
				, HIF_RESULT_DATASET.POPULATION_DATASET_ID
				, HIF_RESULT_DATASET.POPULATION_YEAR
				, HIF_RESULT_DATASET.BASELINE_AQ_LAYER_ID
				, HIF_RESULT_DATASET.SCENARIO_AQ_LAYER_ID
				)
		.values(
				task.getUuid()
				, hifTaskConfig.name
				, hifTaskConfig.popId
				, hifTaskConfig.popYear
				, hifTaskConfig.aqBaselineId
				, hifTaskConfig.aqScenarioId)
		.returning(HIF_RESULT_DATASET.ID)
		.fetchOne();
		
		// Each HIF result function config contains the details of how the function was configured
		for(HIFConfig hif : hifTaskConfig.hifs) {
			create.insertInto(HIF_RESULT_FUNCTION_CONFIG
					, HIF_RESULT_FUNCTION_CONFIG.HIF_RESULT_DATASET_ID
					, HIF_RESULT_FUNCTION_CONFIG.HIF_ID
					, HIF_RESULT_FUNCTION_CONFIG.START_AGE
					, HIF_RESULT_FUNCTION_CONFIG.END_AGE
					, HIF_RESULT_FUNCTION_CONFIG.RACE_ID
					, HIF_RESULT_FUNCTION_CONFIG.GENDER_ID
					, HIF_RESULT_FUNCTION_CONFIG.ETHNICITY_ID
					, HIF_RESULT_FUNCTION_CONFIG.INCIDENCE_DATASET_ID
					, HIF_RESULT_FUNCTION_CONFIG.PREVALENCE_DATASET_ID
					, HIF_RESULT_FUNCTION_CONFIG.VARIABLE_DATASET_ID)
			.values(hifResultDatasetRecord.getId()
					, hif.hifId
					, hif.startAge
					, hif.endAge
					, hif.race
					, hif.gender
					, hif.ethnicity
					, hif.incidence
					, hif.prevalence
					, hif.variable)
			.execute();
			
		}

		// Finally, store the actual estimates
		for(HifResultRecord hifResult : hifResults) {
			hifResult.setHifResultDatasetId(hifResultDatasetRecord.getId());
		}
		
		create
		.batchInsert(hifResults)
		.execute();	
	}
	
	
}
