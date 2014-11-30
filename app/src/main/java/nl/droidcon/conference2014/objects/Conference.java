package nl.droidcon.conference2014.objects;

import android.content.Context;

import java.io.Serializable;

import nl.droidcon.conference2014.utils.PreferenceManager;

/**
 * Conference object, created by the CSV file
 * @author Arnaud Camus
 */
public class Conference implements Serializable {

    private String startDate;
    private String endDate;
    private String headeline;
    private String speaker;
    private String speakerImageUrl;
    private String text;
    private String location;

    public Conference(String[] fromCSV) {
        startDate = fromCSV[0];
        endDate = fromCSV[1];
        headeline = fromCSV[2];
        speaker = fromCSV[3];
        speakerImageUrl = fromCSV[4];
        text = fromCSV[5];
        location = fromCSV[6];
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
}
