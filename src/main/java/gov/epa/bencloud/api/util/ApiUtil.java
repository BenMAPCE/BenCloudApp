package gov.epa.bencloud.api.util;

/**
 * @author jimanderton
 *
 */
public class ApiUtil {

	
	/**
	 * @param columnIdx
	 * @param rowIdx
	 * @return unique long integer using the Cantor pairing function to combine column and row indices
	 * https://en.wikipedia.org/wiki/Pairing_function
	 */
	public static long getCellId(int columnIdx, int rowIdx) {
		return (long)(((columnIdx+rowIdx)*(columnIdx+rowIdx+1)*0.5)+rowIdx);
	}

}
