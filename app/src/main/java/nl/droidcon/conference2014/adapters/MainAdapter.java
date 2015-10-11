package nl.droidcon.conference2014.adapters;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import nl.droidcon.conference2014.BaseApplication;
import nl.droidcon.conference2014.R;
import nl.droidcon.conference2014.objects.Conference;
import nl.droidcon.conference2014.utils.WordColor;

/**
 * Adapter for the {@link nl.droidcon.conference2014.MainActivity},
 * display either a Conference slot or a break.
 * @author Arnaud Camus
 */
public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    public static final int VIEW_HEADER = 0;
    public static final int VIEW_CONFERENCE = 1;

    private List<Conference> mData;
    private Context mContext;
    private SimpleDateFormat simpleDateFormat;
    private SimpleDateFormat simpleDateFormat2;

    public MainAdapter(Context context, List<Conference> objects) {
        mData = objects;
        mContext = context.getApplicationContext();
        simpleDateFormat = new SimpleDateFormat("E, HH:mm", Locale.ENGLISH);
        simpleDateFormat2 = new SimpleDateFormat(" - HH:mm", Locale.ENGLISH);
    }

    @Override
    public int getItemCount() {
        return mData == null ? 0 : mData.size();
    }

    @Override
    public int getItemViewType(int position) {
        return mData.get(position).getSpeaker() != null
                    && !TextUtils.isEmpty(mData.get(position).getSpeaker())
                        ? VIEW_CONFERENCE
                        : VIEW_HEADER;
    }


    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(final ViewGroup parent, int viewType) {
        if (viewType == VIEW_HEADER) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_header, parent, false);
            return new ViewHolderHeader(v);
        }
        View v = LayoutInflater.from(mContext).inflate(R.layout.adapter_conference, parent, false);
        return new ViewHolderConference(v);
    }


    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof ViewHolderHeader) {
            ((ViewHolderHeader)holder).dateStart.setText(
                    new StringBuilder()
                            .append(simpleDateFormat.format(new Date(mData.get(position).getStartDate())))
                            .append(simpleDateFormat2.format(new Date(mData.get(position).getEndDate())))
                            .toString());
            ((ViewHolderHeader)holder).headline.setText(mData.get(position).getHeadeline());

            if (mData.get(position).getHeadeline().equals(mContext.getString(R.string.coffee_break))) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.babbq_orange));
                ((ViewHolderHeader)holder).iconHeader.setImageResource(R.drawable.coffee);
            } else if (mData.get(position).getHeadeline().equals(mContext.getString(R.string.lunch))) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.babbq_purple));
                ((ViewHolderHeader)holder).iconHeader.setImageResource(R.drawable.toast);
            } else if (mData.get(position).getHeadeline().equals(mContext.getString(R.string.tba))) {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.windowBackground));
                ((ViewHolderHeader)holder).iconHeader.setImageDrawable(null);
            } else {
                holder.itemView.setBackgroundColor(ContextCompat.getColor(mContext, R.color.babbq_yellow));
                ((ViewHolderHeader)holder).iconHeader.setImageResource(R.drawable.microphone);
            }

        } else if (holder instanceof ViewHolderConference) {
            ((ViewHolderConference)holder).dateStart
                    .setText(simpleDateFormat.format(new Date(mData.get(position).getStartDate())));
            ((ViewHolderConference)holder).location
                    .setText(String.format(mContext.getString(R.string.location),
                                mData.get(position).getLocation()));
            ((ViewHolderConference)holder).location.setTextColor(
                    WordColor.generateColor(mData.get(position).getLocation()));
            ((ViewHolderConference)holder).headline.setText(mData.get(position).getHeadeline());
            ((ViewHolderConference)holder).speaker.setText(mData.get(position).getSpeaker());

            // picasso
            Picasso.with(mContext.getApplicationContext())
                    .load(mData.get(position).getSpeakerImageUrl())
                    .transform(((BaseApplication)mContext.getApplicationContext()).mPicassoTransformation)
                    .into(((ViewHolderConference)holder).image);
            ((ViewHolderConference)holder).favorite
                    .setImageResource(mData.get(position).isFavorite(mContext)
                        ? R.drawable.ic_favorite_grey600_18dp
                        : R.drawable.ic_favorite_outline_grey600_18dp);

        }
    }
}