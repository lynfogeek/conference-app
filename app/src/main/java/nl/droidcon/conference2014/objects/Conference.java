package nl.droidcon.conference2014.objects;

import android.content.Context;
import android.os.Parcel;
import android.os.Parcelable;
import android.support.annotation.NonNull;

import java.io.Serializable;

import nl.droidcon.conference2014.utils.PreferenceManager;

/**
 * Conference object, created by the CSV file
 * @author Arnaud Camus
 */
public class Conference implements Serializable, Parcelable {

    private String startDate;
    private String endDate;
    private String headeline;
    private String speaker;
    private String speakerImageUrl;
    private String text;
    private String location;

    public Conference(@NonNull String[] fromCSV) {
        startDate = fromCSV[0];
        endDate = fromCSV[1];
        headeline = fromCSV[2];
        location = fromCSV[9];
        speaker = fromCSV[10];
        speakerImageUrl = fromCSV[11];
        text = fromCSV[12];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.startDate);
        dest.writeString(this.endDate);
        dest.writeString(this.headeline);
        dest.writeString(this.speaker);
        dest.writeString(this.speakerImageUrl);
        dest.writeString(this.text);
        dest.writeString(this.location);
    }

    protected Conference(Parcel in) {
        this.startDate = in.readString();
        this.endDate = in.readString();
        this.headeline = in.readString();
        this.speaker = in.readString();
        this.speakerImageUrl = in.readString();
        this.text = in.readString();
        this.location = in.readString();
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
