package gov.epa.bencloud.server.util;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

public class DataUtil {

	public static String getHumanReadableTime(LocalDateTime start, LocalDateTime end) {
		
		if (null == start || null == end) {
			return "";
		}
		
		long totalSeconds = ChronoUnit.SECONDS.between(start, end);
		
		StringBuilder humanReadableTime = new StringBuilder();

		getHumanReadableTime(totalSeconds, humanReadableTime);
		return humanReadableTime.toString();
		
	}
	
	public static void getHumanReadableTime(long totalSeconds, StringBuilder humanReadableTime) {
	
		long hours = ((totalSeconds/60) / 60);
		long minutes = ((totalSeconds / 60) % 60);
		long seconds = (totalSeconds % 60);

		if (hours > 0) {
			if (hours < 2) {
				humanReadableTime.append(hours).append(" hour").append(" ");
			} else {
				humanReadableTime.append(hours).append(" hours").append(" ");
			}
		}
		
		if (minutes > 0) {
			if (minutes < 2) {
				humanReadableTime.append(minutes).append(" minute").append(" ");
			} else {
				humanReadableTime.append(minutes).append(" minutes").append(" ");
			}
		}
		
		if (seconds > 0) {
			if (seconds < 2) {
				humanReadableTime.append(seconds).append(" second").append(" ");
			} else {
				humanReadableTime.append(seconds).append(" seconds").append(" ");
			}
		}
		
		if(hours==0 && minutes==0 && seconds==0) {
			humanReadableTime.append("0 seconds");
		}
	}

}
