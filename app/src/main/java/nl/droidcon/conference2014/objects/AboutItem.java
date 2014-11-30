package nl.droidcon.conference2014.objects;

import android.support.annotation.DrawableRes;

import java.io.Serializable;

/**
 * Object use in the {@link nl.droidcon.conference2014.AboutActivity} for each
 * row item.
 * @author Arnaud Camus
 */
public class AboutItem implements Serializable {
    private String title;
    private String subtitle;
    @DrawableRes private int drawable;

    /**
     * Unique constructor for the object
     * @param title the title to display via the Adapter.
     * @param subtitle the text displayed as complement information.
     * @param drawable a {@link android.graphics.drawable.Drawable} icon.
     */
    public AboutItem(String title, String subtitle, @DrawableRes int drawable) {
        setTitle(title);
        setSubtitle(subtitle);
        setDrawable(drawable);
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getSubtitle() {
        return subtitle;
    }

    public void setSubtitle(String subtitle) {
        this.subtitle = subtitle;
    }

    public int getDrawable() {
        return drawable;
    }

    public void setDrawable(@DrawableRes int drawable) {
        this.drawable = drawable;
    }
}
