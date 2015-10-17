package nl.droidcon.conference2014.objects;

import java.io.Serializable;

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

    public int getDayNumeric() {
        return mDayNumeric;
    }

    public String getDay() {
        return mDay;
    }
}
