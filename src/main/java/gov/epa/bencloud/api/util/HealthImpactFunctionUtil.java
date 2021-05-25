package gov.epa.bencloud.api.util;

import org.mariuszgromada.math.mxparser.*;

public class HealthImpactFunctionUtil {

	public static Double evalFunction(String f) {

		Expression e = new Expression(f);
		mXparser.consolePrintln("Res: " + e.getExpressionString() + " = " + e.calculate()); 
		return e.calculate();
	}

}
