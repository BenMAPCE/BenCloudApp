package gov.epa.bencloud.api.util;

import static gov.epa.bencloud.server.database.jooq.Tables.*;

import java.util.ArrayList;

import org.jooq.DSLContext;
import org.jooq.InsertResultStep;
import org.jooq.impl.DSL;
import org.mariuszgromada.math.mxparser.*;

import gov.epa.bencloud.server.database.JooqUtil;
import gov.epa.bencloud.server.database.jooq.tables.records.HifResultsRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.HealthImpactFunctionRecord;
import gov.epa.bencloud.server.database.jooq.tables.records.HifResultDatasetsRecord;
import gov.epa.bencloud.server.tasks.model.Task;

public class HIFUtil {

	public static Expression getFunctionExpression(Integer id) {

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
		Expression e = new Expression(record.getFunctionText(), a, b, c, beta, deltaQ, q1, q2, incidence, prevalence, population);		
		return e;
	}

	public static void storeResults(Task task, ArrayList<HifResultsRecord> hifResults) {
		DSLContext create = DSL.using(JooqUtil.getJooqConfiguration());
		
		HifResultDatasetsRecord record = create.insertInto(HIF_RESULT_DATASETS, HIF_RESULT_DATASETS.TASK_UUID)
		.values(task.getUuid())
		.returning(HIF_RESULT_DATASETS.ID)
		.fetchOne();
		
		for(HifResultsRecord hifResult : hifResults) {
			hifResult.setHifResultDatasetId(record.getId());
		}
		
		create
		.batchInsert(hifResults)
		.execute();	
	}
	
	public static void main(String[] args) {
		Argument a = new Argument("A", 0.98148);
		Argument b = new Argument("B", 0.0);
		Argument c = new Argument("C", 0.0);
		Argument beta = new Argument("BETA", 0.024121307);
		Argument deltaQ = new Argument("DELTAQ", 0.5);
		Argument q1 = new Argument("Q0", 1.0);
		Argument q2 = new Argument("Q1", 0.5);
		Argument incidence = new Argument("INCIDENCE", 0.5);
		Argument prevalence = new Argument("PREVALENCE", 0.0);
		Argument population = new Argument("POPULATION", 500.0);
		
		// return the expression
		mXparser.setToOverrideBuiltinTokens();
		Expression e = new Expression("(1-(1/((1-INCIDENCE)*exp(BETA*DELTAQ)+INCIDENCE)))*INCIDENCE*POPULATION*A", a, b, c, beta, deltaQ, q1, q2, incidence, prevalence, population);					
		Double d = e.calculate();
		System.out.println(d);

	}
}
