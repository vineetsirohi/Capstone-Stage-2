package in.vasudev.capstone_stage_2.utils;

/**
 * Created by vineet on 05-Sep-16.
 */
public class MyTimeUtils {

    public static String timeElapsed(long time) {

        long timeElapsedInMillis = System.currentTimeMillis() - time;

        long secondsInMilli = 1000;
        long minutesInMilli = secondsInMilli * 60;
        long hoursInMilli = minutesInMilli * 60;
        long daysInMilli = hoursInMilli * 24;

        long elapsedDays = timeElapsedInMillis / daysInMilli;
        if (elapsedDays > 0) {
            return elapsedDays + "d";
        }

        timeElapsedInMillis = timeElapsedInMillis % daysInMilli;
        long elapsedHours = timeElapsedInMillis / hoursInMilli;
        if (elapsedHours > 0) {
            return elapsedHours + "h";
        }

        timeElapsedInMillis = timeElapsedInMillis % hoursInMilli;
        long elapsedMinutes = timeElapsedInMillis / minutesInMilli;
        return elapsedMinutes + "m";
    }
}
