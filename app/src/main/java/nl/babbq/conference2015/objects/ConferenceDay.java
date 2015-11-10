package nl.babbq.conference2015.objects;

import java.io.Serializable;
import java.util.Calendar;
import java.util.Date;

/**
 * Created by nono on 10/17/15.
 */
public class ConferenceDay implements Serializable {
    private int mDayNumeric;
    private String mDay;

    public ConferenceDay(int dayNumeric, String day) {
        mDayNumeric = dayNumeric;
        mDay = day;
    }

    public boolean isToday() {
        Calendar cal1 = Calendar.getInstance();
        Calendar cal2 = Calendar.getInstance();
        try {
            cal2.setTime(new Date(mDay));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return (cal1.get(Calendar.ERA) == cal2.get(Calendar.ERA) &&
                cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR) &&
                cal1.get(Calendar.DAY_OF_YEAR) == cal2.get(Calendar.DAY_OF_YEAR));
    }

    public int getDayNumeric() {
        return mDayNumeric;
    }

    public String getDay() {
        return mDay;
    }
}
