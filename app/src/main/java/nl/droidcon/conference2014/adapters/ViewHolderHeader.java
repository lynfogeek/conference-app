package nl.droidcon.conference2014.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import nl.droidcon.conference2014.R;


public class ViewHolderHeader extends RecyclerView.ViewHolder {

    TextView dateStart;
    TextView headline;

    public ViewHolderHeader(View v) {
        super(v);
        dateStart = (TextView) v.findViewById(R.id.dateStart);
        headline = (TextView) v.findViewById(R.id.headline);
    }
}

