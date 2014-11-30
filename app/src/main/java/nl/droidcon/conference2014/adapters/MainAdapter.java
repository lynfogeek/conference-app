package nl.droidcon.conference2014.adapters;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

import java.util.List;

import nl.droidcon.conference2014.objects.Conference;

/**
 * Adapter for the {@link nl.droidcon.conference2014.MainActivity},
 * display either a Conference slot or a break.
 * @author Arnaud Camus
 */
public class MainAdapter extends ArrayAdapter<Conference> {
    public static final int COUNT_VIEWS = 2;

    public static final int VIEW_HEADER = 0;
    public static final int VIEW_CONFERENCE = 1;

    private ViewHeaderInflater viewHeaderInflater;
    private ViewConferenceInflater viewConferenceInflater;

    public MainAdapter(Context context, int resource, List<Conference> objects) {
        super(context, resource, objects);
        viewHeaderInflater = new ViewHeaderInflater(context);
        viewConferenceInflater = new ViewConferenceInflater(context);
    }

    @Override
    public int getViewTypeCount() {
        return COUNT_VIEWS;
    }

    @Override
    public int getItemViewType(int position) {
        return getItem(position).getSpeaker().length() == 0 ? VIEW_HEADER : VIEW_CONFERENCE;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        int viewType = this.getItemViewType(position);

        switch (viewType) {
            //To keep the code clean and short in the adapter, the views are generated
            //through these ItemInflater:
            case VIEW_HEADER:
                return viewHeaderInflater.getView(getItem(position), position, convertView, parent);
            case VIEW_CONFERENCE:
                return viewConferenceInflater.getView(getItem(position), position, convertView, parent);
        }
        return convertView;
    }
}
