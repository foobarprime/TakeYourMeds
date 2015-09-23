package nikhanch.com.takeyourmeds.Utils;

import java.util.Calendar;

/**
 * Created by nikhanch on 9/20/2015.
 */
public class DateTimeUtils {
    public static boolean isOnSameDay(long when, Calendar day) {

        Calendar then = Calendar.getInstance();
        then.setTimeInMillis(when);

        int thenYear = then.get(Calendar.YEAR);
        int thenMonth = then.get(Calendar.MONTH);
        int thenMonthDay = then.get(Calendar.DAY_OF_MONTH);

        int nowYear = day.get(Calendar.YEAR);
        int nowMonth = day.get(Calendar.MONTH);
        int nowMonthDay = day.get(Calendar.DAY_OF_MONTH);

        return (thenYear == nowYear)
                && (thenMonth == nowMonth)
                && (thenMonthDay == nowMonthDay);
    }

}
