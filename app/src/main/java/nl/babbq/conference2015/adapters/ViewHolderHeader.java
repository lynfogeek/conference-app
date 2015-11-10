package nl.babbq.conference2015.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import nl.babbq.conference2015.R;


public class ViewHolderHeader extends RecyclerView.ViewHolder {

    TextView dateStart;
    TextView headline;
    ImageView iconHeader;

    public ViewHolderHeader(View v) {
        super(v);
        dateStart = (TextView) v.findViewById(R.id.dateStart);
        headline = (TextView) v.findViewById(R.id.headline);
        iconHeader = (ImageView) v.findViewById(R.id.iconHeader);
    }
}

