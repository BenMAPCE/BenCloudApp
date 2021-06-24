package gov.epa.bencloud.api.util;

public class AirQualityUtil {

	public static String validateModelColumnHeadings(int columnIdx, int rowIdx, int metricIdx, int seasonalMetricIdx, int statisticIdx, int valuesIdx) {
		StringBuilder b = new StringBuilder();
		if(columnIdx == -999) {
			b.append((b.length()==0 ? "" : ", ") + "Column");
		}
		if(rowIdx == -999) {
			b.append((b.length()==0 ? "" : ", ") + "Row");
		}
		if(metricIdx == -999) {
			b.append((b.length()==0 ? "" : ", ") + "Metric");
		}
		if(seasonalMetricIdx == -999) {
			b.append((b.length()==0 ? "" : ", ") + "Seasonal Metric");
		}
		if(statisticIdx == -999) {
			b.append((b.length()==0 ? "" : ", ") + "Annual Metric");
		}
		if(valuesIdx == -999) {
			b.append((b.length()==0 ? "" : ", ") + "Values");
		}
		
		return b.toString();
	}

}
