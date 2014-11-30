package nl.droidcon.conference2014.adapters;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import nl.droidcon.conference2014.BaseApplication;
import nl.droidcon.conference2014.R;
import nl.droidcon.conference2014.objects.Conference;
import nl.droidcon.conference2014.utils.WordColor;

/**
 * Render a Conference object every time {@link this#getView(
 * nl.droidcon.conference2014.objects.Conference, int, android.view.View, android.view.ViewGroup)}
 * is called.
 *
 * @author Arnaud Camus
 */
public class ViewConferenceInflater extends ItemInflater<Conference> {

    SimpleDateFormat simpleDateFormat;

    public ViewConferenceInflater(Context ctx) {
        super(ctx);
        simpleDateFormat = new SimpleDateFormat("E, HH:mm");
    }

    @Override
    public View getView(Conference object, int position, View convertView, ViewGroup parent) {
        View v = convertView;
        ViewHolder holder;
        if (v == null) {
            v = LayoutInflater.from(mContext).inflate(R.layout.adapter_conference, parent, false);

            holder = new ViewHolder();
            holder.dateStart = (TextView) v.findViewById(R.id.dateStart);
            holder.location = (TextView) v.findViewById(R.id.location);
            holder.headline = (TextView) v.findViewById(R.id.headline);
            holder.speaker = (TextView) v.findViewById(R.id.speaker);
            holder.image = (ImageView) v.findViewById(R.id.image);
            holder.favorite = (ImageView) v.findViewById(R.id.favorite);
            v.setTag(holder);
        } else {
            holder = (ViewHolder) v.getTag();
        }

        holder.dateStart.setText(simpleDateFormat.format(new Date(object.getStartDate())));
        holder.location.setText(String.format(mContext.getString(R.string.location),
                                object.getLocation()));
        holder.location.setTextColor(WordColor.generateColor(object.getLocation()));
        holder.headline.setText(object.getHeadeline());
        holder.speaker.setText(object.getSpeaker());

        // picasso
        Picasso.with(mContext.getApplicationContext())
               .load(object.getSpeakerImageUrl())
               .transform(((BaseApplication)mContext.getApplicationContext()).mPicassoTransformation)
               .into(holder.image);
        holder.favorite.setImageResource(object.isFavorite(mContext)
                                            ? R.drawable.ic_favorite_grey600_18dp
                                            : R.drawable.ic_favorite_outline_grey600_18dp);
        return v;
    }

    private class ViewHolder {
        TextView dateStart;
        TextView location;
        TextView headline;
        TextView speaker;
        ImageView image;
        ImageView favorite;
    }
}
