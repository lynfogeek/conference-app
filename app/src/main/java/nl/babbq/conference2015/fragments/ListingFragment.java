package nl.babbq.conference2015.fragments;

import android.content.Intent;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.app.Fragment;
import android.support.v4.util.Pair;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.internal.util.Predicate;

import java.util.ArrayList;
import java.util.List;

import nl.babbq.conference2015.ConferenceActivity;
import nl.babbq.conference2015.R;
import nl.babbq.conference2015.adapters.HasAdapter;
import nl.babbq.conference2015.adapters.MainAdapter;
import nl.babbq.conference2015.objects.Conference;
import nl.babbq.conference2015.objects.ConferenceDay;
import nl.babbq.conference2015.utils.DividerItemDecoration;
import nl.babbq.conference2015.utils.ItemClickSupport;
import nl.babbq.conference2015.utils.Utils;

/**
 * Created by nono on 10/6/15.
 */
public class ListingFragment extends Fragment implements HasAdapter {

    private final static String DATA = "data";

    private RecyclerView mRecyclerView;
    private List<Conference> mData;
    private MainAdapter mAdapter;

    public static ListingFragment newInstance(ArrayList<Conference> conferences, final ConferenceDay day) {
        Bundle args = new Bundle();
        Predicate<Conference> aDay = new Predicate<Conference>() {
            public boolean apply(Conference conference) {
                return conference.getStartDate().startsWith(day.getDay());
            }
        };
        args.putParcelableArrayList(DATA, (ArrayList<? extends Parcelable>) Utils.filter(conferences, aDay));
        ListingFragment fragment = new ListingFragment();
        fragment.setArguments(args);
        return fragment;
    }


    public ListingFragment() {
    }

    @Override
    public void onCreate(Bundle bundle) {
        super.onCreate(bundle);
        mData = getArguments().getParcelableArrayList(DATA);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup parent, Bundle savedInstaceState) {
        mRecyclerView = (RecyclerView)inflater
                .inflate(R.layout.fragment_listing, parent, false);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getContext(),
                DividerItemDecoration.VERTICAL_LIST));
        ItemClickSupport.addTo(mRecyclerView).setOnItemClickListener(new ItemClickSupport.OnItemClickListener() {
            @Override
            public void onItemClicked(RecyclerView recyclerView, int position, View v) {
                if (mData.get(position).getSpeaker().length() == 0) {
                    // if the speaker field is empty, it's probably a rre break or lunch
                    return;
                }

                // On Lollipop and above we animate the conference title
                // to the second activity
                Pair<View, String> headline = Pair.create(v.findViewById(R.id.headline),
                        getString(R.string.headline));
                Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(getActivity(),
                        headline).toBundle();
                Intent intent = new Intent(getActivity(), ConferenceActivity.class);
                intent.putExtra("conference", (Parcelable)mData.get(position));
                ActivityCompat.startActivity(getActivity(), intent, bundle);
            }
        });
        return mRecyclerView;
    }


    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mAdapter = new MainAdapter(getActivity(), mData);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void notifyDataSetChanged() {
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }
}
