package br.com.fiap.util;

import java.sql.Timestamp;
import java.util.Calendar;

public class DateUtils {

    public static long clearTime(Timestamp date) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(date.getTime());
        cal.set(Calendar.HOUR_OF_DAY, 0);
        cal.set(Calendar.MINUTE, 0);
        cal.set(Calendar.SECOND, 0);
        cal.set(Calendar.MILLISECOND, 0);
        long time = cal.getTimeInMillis();

        return time;
    }
}