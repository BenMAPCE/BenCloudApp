package gov.epa.bencloud.api.util;

import org.apache.commons.math3.distribution.NormalDistribution;

public class Scratchpad {

	public static void main(String[] args) {
		NormalDistribution normalDistribution = new NormalDistribution(10, 3);
		for(int i=0; i < 10; i++) {
			System.out.println(normalDistribution.sample());
		}
	}

}
