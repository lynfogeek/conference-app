package nl.babbq.conference2015.objects;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import nl.babbq.conference2015.R;
import nl.babbq.conference2015.utils.PreferenceManager;
import nl.babbq.conference2015.utils.Utils;

/**
 * Conference object, created by the CSV file
 * @author Arnaud Camus
 */
public class Conference implements Serializable, Parcelable {

    public static final String CONFERENCES = "conferences";
    private static final SimpleDateFormat SIMPLE_DATE_FORMAT
            = new SimpleDateFormat("d/M/yyy HH:mm:ss", Locale.ENGLISH);


    private String[] CSVLine;
    private String startDate;
    private String endDate;
    private String headeline;
    private String speaker;
    private String speakerImageUrl;
    private String text;
    private String location;
    private Calendar calendar;

    public Conference(@NonNull String[] fromCSV) {
        CSVLine = fromCSV;
        startDate = fromCSV[0];
        endDate = fromCSV[1];
        calendar = Calendar.getInstance();
        try {
            calendar.setTime(new Date(endDate));
        } catch (Exception e) {
            e.printStackTrace();
        }
        headeline = fromCSV[2];
        if (fromCSV.length > 12) {
            location = fromCSV[9];
            speaker = fromCSV[10];
            speakerImageUrl = fromCSV[11];
            text = fromCSV[12];
        } else {
            location = "";
            speaker = "";
            speakerImageUrl = "";
            text = "";
        }
    }

    /**
     * Save the new state of the conference.
     * @param ctx a valid context
     * @return true if the conference is favorite
     */
    public boolean toggleFavorite(Context ctx) {
        PreferenceManager prefManager =
                new PreferenceManager(ctx.getSharedPreferences("MyPref", Context.MODE_PRIVATE));
        boolean actual = prefManager.favorite(getHeadeline())
                .getOr(false);
        prefManager.favorite(getHeadeline())
                .put(!actual)
                .apply();
        return !actual;
    }

    /**
     * Determine if the talk has started or not
     * @return true if it is past
     */
    public boolean isPast() {
        Calendar cal1 = Calendar.getInstance();
        return calendar.before(cal1);
    }

    /**
     * Parse a inputStream reader and generate the list
     * of {@link Conference}.
     */
    public static List<Conference> parseInputStream(Context context, InputStreamReader stream) {
        List<Conference> conferences = new ArrayList<>();
        try {
            CSVReader reader = new CSVReader(stream, '\t');
            reader.readNext(); // file headline
            String[] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                if (nextLine.length > 1 && !Utils.arrayContains(nextLine, context.getString(R.string.program_d2))) {
                    conferences.add(new Conference(nextLine));
                }
            }
            saveInPreferences(context, conferences);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return conferences;
    }

    /**
     * Loads the conferences items from the sharedPreferences
     * @param context a valid context
     * @return the list of talks
     */
    public static List<Conference> loadFromPreferences(Context context) {
        List<Conference> list = new ArrayList<>();
        SharedPreferences prefs = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE);
        Set<String> conferences = prefs.getStringSet(CONFERENCES, new HashSet<String>());
        for (String conferenceLine: conferences) {
            list.add(new Conference(conferenceLine.split("\t")));
        }

        Collections.sort(list, new Comparator<Conference>() {
            @Override
            public int compare(Conference conference, Conference conference2) {
                try {
                    return SIMPLE_DATE_FORMAT.parse(conference.startDate)
                            .before(SIMPLE_DATE_FORMAT.parse(conference2.startDate)) ? -1 : 1;
                } catch (ParseException e) {
                    e.printStackTrace();
                }
                return 0;
            }
        });
        return list;
    }

    /**
     * Find the next available talk
     * @param data a list of Conference objects
     * @return the position of the next item, or 0
     */
    public static int findNextEventPosition(@NonNull List<Conference> data) {
        int position = 0;
        for (Conference c: data) {
            if (c.isPast()) {
                position++;
            } else {
                return position;
            }
        }
        return 0;
    }


    private static void saveInPreferences(Context context, List<Conference> conferences) {
        SharedPreferences.Editor prefsEditor
                = context.getSharedPreferences("MyPref", Context.MODE_PRIVATE).edit();
        Set<String> stringSet = new HashSet<>();
        for (Conference conference: conferences) {
            stringSet.add(TextUtils.join("\t", conference.getCSVLine()));
        }
        prefsEditor.putStringSet(CONFERENCES, stringSet);
        prefsEditor.apply();
    }

    //////////////////////////////////////
    //          GETTERS / SETTERS       //
    //////////////////////////////////////


    public boolean isFavorite(Context ctx) {
        PreferenceManager prefManager =
                new PreferenceManager(ctx.getSharedPreferences("MyPref", Context.MODE_PRIVATE));
        return prefManager.favorite(getHeadeline())
                .getOr(false);
    }

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public String getHeadeline() {
        return headeline;
    }

    public void setHeadeline(String headeline) {
        this.headeline = headeline;
    }

    public String getSpeaker() {
        return speaker;
    }

    public void setSpeaker(String speaker) {
        this.speaker = speaker;
    }

    public String getSpeakerImageUrl() {
        return speakerImageUrl;
    }

    public void setSpeakerImageUrl(String speakerImageUrl) {
        this.speakerImageUrl = speakerImageUrl;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public String[] getCSVLine() { return this.CSVLine; }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(this.CSVLine.length);
        dest.writeStringArray(this.CSVLine);
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeString(this.headeline);
        dest.writeString(this.speaker);
        dest.writeString(this.speakerImageUrl);
        dest.writeString(this.text);
        dest.writeString(this.location);
        dest.writeSerializable(this.calendar);
    }

    protected Conference(Parcel in) {
        CSVLine = new String[in.readInt()];
        in.readStringArray(this.CSVLine);
        this.startDate = in.readString();
        this.endDate = in.readString();
        this.headeline = in.readString();
        this.speaker = in.readString();
        this.speakerImageUrl = in.readString();
        this.text = in.readString();
        this.location = in.readString();
        this.calendar = (Calendar) in.readSerializable();
    }

    public static final Creator<Conference> CREATOR = new Creator<Conference>() {
        public Conference createFromParcel(Parcel source) {
            return new Conference(source);
        }

        public Conference[] newArray(int size) {
            return new Conference[size];
        }
    };
}
