package nl.droidcon.conference2014.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.text.SimpleDateFormat;
import java.util.Date;

import nl.droidcon.conference2014.R;
import nl.droidcon.conference2014.objects.Conference;

/**
 * Render a break/lunch for a Conference object given, every time {@link this#getView(
 * nl.droidcon.conference2014.objects.Conference, int, android.view.View, android.view.ViewGroup)}
 * is called.
 *
 * @author Arnaud Camus
 */
public class ViewHeaderInflater extends ItemInflater<Conference> {

    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat simpleDateFormat2;

    public ViewHeaderInflater(Context ctx) {
        super(ctx);
        simpleDateFormat = new SimpleDateFormat("E, HH:mm");
        simpleDateFormat2 = new SimpleDateFormat(" - HH:mm");
    }

    @Override
    public View getView(Conference object, int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.adapter_header, parent, false);

            holder = new ViewHolder();
            holder.dateStart = (TextView) v.findViewById(R.id.dateStart);
            holder.headline = (TextView) v.findViewById(R.id.headline);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.dateStart.setText(simpleDateFormat.format(new Date(object.getStartDate()))
                            + simpleDateFormat2.format(new Date(object.getEndDate())));
        holder.headline.setText(object.getHeadeline());

        return v;
    }

    private class ViewHolder {
        TextView dateStart;
        TextView headline;
    }
}
