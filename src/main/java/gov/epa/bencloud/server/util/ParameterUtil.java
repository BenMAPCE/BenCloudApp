package gov.epa.bencloud.server.util;

public class ParameterUtil {


	public static int getParameterValueAsInteger(String parametersValue, int defaultValue) {

		int value = defaultValue;

		if (null != parametersValue) {
			try {
				value = Integer.parseInt(parametersValue);
			} catch (NumberFormatException e) {
				value = defaultValue;
			}
		}

		return value;
	}	

	public static String getParameterValueAsString(String parametersValue, String defaultValue) {

		String value = "";

		if (null != parametersValue) {
			value = parametersValue;
		}

		return value;
	}

	public static boolean getParameterValueAsBoolean(String parametersValue, boolean defaultValue) {

		boolean value = defaultValue;

		if (null != parametersValue) {
			try {
				value = Boolean.valueOf(parametersValue);
			} catch (Exception e) {
				value = defaultValue;
			}
		}

		return value;
	}

}
