package nl.droidcon.conference2014;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.ActivityOptionsCompat;
import android.support.v4.util.Pair;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.Window;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ListView;

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nl.droidcon.conference2014.adapters.MainAdapter;
import nl.droidcon.conference2014.objects.Conference;
import nl.droidcon.conference2014.utils.PreferenceManager;
import nl.droidcon.conference2014.utils.SendNotification;
import nl.droidcon.conference2014.utils.Utils;


/**
 * Main activity of the application, list all conferences slots into a listView
 * @author Arnaud Camus
 */
public class MainActivity extends ActionBarActivity implements AdapterView.OnItemClickListener,
                                                                AbsListView.OnScrollListener {

    private MainAdapter mAdapter;
    private List<Conference> mConferences = new ArrayList<Conference>();
    private Toolbar mToolbar;

    private final static String SCHEDULE_FILENAME = "timeline.tsv";

    /**
     * Enable to share views across activities with animation
     * on Android 5.0 Lollipop
     */
    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setupLollipop() {
        getWindow().requestFeature(Window.FEATURE_ACTIVITY_TRANSITIONS);
        getWindow().setSharedElementExitTransition(new ChangeBounds());
        getWindow().setSharedElementEnterTransition(new ChangeBounds());
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Utils.isLollipop()) {
            setupLollipop();
        }
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setTitle(getString(R.string.app_name));
            setSupportActionBar(mToolbar);
        }

        ListView mListView = (ListView) findViewById(R.id.listView);
        mAdapter = new MainAdapter(this, 0x00, mConferences);
        mListView.setAdapter(mAdapter);

        readCalendar();

        mListView.setOnScrollListener(this);
        mListView.setOnItemClickListener(this);

        trackOpening();
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mAdapter != null) {
            //we refresh the views in case a conference has been (un)favorite
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (mConferences.get(position).getSpeaker().length()==0) {
            // if the speaker field is empty, it's probably a coffee break or lunch
            return;
        }

        // On Lollipop we animate the speaker's name & picture and conference title
        // to the second activity
        Pair<View, String> toolbar = Pair.create((View) mToolbar,
                                    getString(R.string.toolbar));
        Pair<View, String> image = Pair.create(view.findViewById(R.id.image),
                                    getString(R.string.image));
        Pair<View, String> headline = Pair.create(view.findViewById(R.id.headline),
                                    getString(R.string.headline));
        Pair<View, String> speaker = Pair.create(view.findViewById(R.id.speaker),
                                    getString(R.string.speaker));
        Bundle bundle = ActivityOptionsCompat.makeSceneTransitionAnimation(this, toolbar,
                                                               image, headline, speaker).toBundle();
        Intent intent = new Intent(this, ConferenceActivity.class);
        intent.putExtra("conference", mConferences.get(position));
        ActivityCompat.startActivity(this, intent, bundle);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {}

    /**
     * ScrollListener used only on Lollipop to smoothly elevate the {@link Toolbar}
     * when the user scroll.
     * @param view
     * @param firstVisibleItem
     * @param visibleItemCount
     * @param totalItemCount
     */
    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount,
                         int totalItemCount) {
        if (firstVisibleItem == 0 && view != null && view.getChildCount() > 0) {
            Rect rect = new Rect();
            view.getChildAt(0).getLocalVisibleRect(rect);
            final float ratio = (float) Math.min(Math.max(rect.top, 0),
                    Utils.dpToPx(48, getBaseContext()))
                    / Utils.dpToPx(48, getBaseContext());
            final int newElevation = (int) (ratio * Utils.dpToPx(8, getBaseContext()));

            setToolbarElevation(newElevation);
        }
    }


    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void setToolbarElevation(int elevation) {
        if (Utils.isLollipop()) {
            mToolbar.setElevation(elevation);
        }
    }

    /**
     * Open and read the asset SCHEDULE_FILENAME and generate the list
     * of {@link Conference}.
     */
    private void readCalendar() {
        try {
            mConferences.clear();
            InputStream is = getAssets().open(SCHEDULE_FILENAME);
            CSVReader reader = new CSVReader(new InputStreamReader(is), '\t');
            reader.readNext(); // file headline
            String [] nextLine;
            while ((nextLine = reader.readNext()) != null) {
                mConferences.add(new Conference(nextLine));
            }
            mAdapter.notifyDataSetChanged();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_more) {
            startActivity(new Intent(this, AboutActivity.class));
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Track how many times the Activity is launched and
     * send a push notification {@link nl.droidcon.conference2014.utils.SendNotification}
     * to ask the user for feedback on the event.
     */
    private void trackOpening() {
        PreferenceManager prefManager =
                new PreferenceManager(getSharedPreferences("MyPref", Context.MODE_PRIVATE));
        long nb = prefManager.openingApp().getOr(0L);
        prefManager.openingApp()
                .put(++nb)
                .apply();

        if (nb == 10) {
            SendNotification.feedbackForm(this);
        }
    }
}
