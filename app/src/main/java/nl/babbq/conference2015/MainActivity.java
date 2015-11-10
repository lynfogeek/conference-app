package nl.babbq.conference2015;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.Menu;
import android.view.MenuItem;
import android.view.Window;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;

import java.util.ArrayList;
import java.util.List;

import nl.babbq.conference2015.animations.BugDroid;
import nl.babbq.conference2015.fragments.ListingFragment;
import nl.babbq.conference2015.network.TSVRequest;
import nl.babbq.conference2015.network.VolleySingleton;
import nl.babbq.conference2015.objects.Conference;
import nl.babbq.conference2015.objects.ConferenceDay;
import nl.babbq.conference2015.utils.PreferenceManager;
import nl.babbq.conference2015.utils.SendNotification;
import nl.babbq.conference2015.utils.Utils;


/**
 * Main activity of the application, list all conferences slots into a listView
 * @author Arnaud Camus
 */
public class MainActivity extends AppCompatActivity
        implements Response.Listener<List<Conference>>,
            Response.ErrorListener,
            BugDroid.OnRefreshClickListener {

    public static final String CONFERENCES = "conferences";
    public static final String URL = "https://docs.google.com/spreadsheets/d/1a6UtL_YiKu2j6TgrnhpPcxfwuFX69Ht_UMOzdcb08Zs/pub?gid=0&single=true&output=tsv";

    private MainPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private ArrayList<Conference> mConferences = new ArrayList<>();
    private Toolbar mToolbar;
    private TabLayout mTabLayout;

    private VolleySingleton mVolley;
    private BugDroid mAnimatedBugDroid;

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
        mViewPager = (ViewPager) findViewById(R.id.viewpager);
        mTabLayout = (TabLayout) findViewById(R.id.tabs);
        mTabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mAnimatedBugDroid = new BugDroid((ImageView)findViewById(R.id.bugDroid),
                findViewById(R.id.loadingFrame),
                findViewById(R.id.refreshButton),
                this);

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(getString(R.string.app_name));
        }

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mConferences.addAll(savedInstanceState.<Conference>getParcelableArrayList(CONFERENCES));
        } else {
            mConferences.addAll(Conference.loadFromPreferences(this));
        }
        setupViewPager(savedInstanceState);
        initVolley(this);
        if (mConferences.size() == 0) {
            mToolbar.post(new Runnable() {
                @Override
                public void run() {
                    update();
                }
            });
        }
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
    protected void onSaveInstanceState(Bundle bundle) {
        super.onSaveInstanceState(bundle);
        bundle.putParcelableArrayList(CONFERENCES, mConferences);
    }

    private void setupViewPager(Bundle savedInstanceState) {
        mAdapter = new MainPagerAdapter(getSupportFragmentManager());
        ConferenceDay day1 = new ConferenceDay(1, "11/12/2015");
        ConferenceDay day2 = new ConferenceDay(2, "11/13/2015");
        mAdapter.addFragment(ListingFragment.newInstance(mConferences, day1), getString(R.string.day, 1));
        mAdapter.addFragment(ListingFragment.newInstance(mConferences, day2), getString(R.string.day, 2));
        mViewPager.setAdapter(mAdapter);
        mViewPager.setPageMargin(Utils.dpToPx(8, getBaseContext()));
        mTabLayout.setupWithViewPager(mViewPager);
        if (savedInstanceState == null && day2.isToday()) {
            mViewPager.setCurrentItem(1);
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

    @Override
    public void onPause() {
        mAnimatedBugDroid.stopAnimation();
        super.onPause();
    }

    private void initVolley(Context context) {
        if (mVolley == null) {
            mVolley = VolleySingleton.getInstance(context);
        }
    }

    @Override
    public void onRefreshClick() {
        update();
    }

    private void update() {
        if (!mAnimatedBugDroid.isLoading()) {
            mVolley.addToRequestQueue(new TSVRequest(this, Request.Method.GET, URL, this, this));
            mAnimatedBugDroid.setLoading(true);
            mAnimatedBugDroid.startAnimation();
        }
    }

    /**
     * Track how many times the Activity is launched and
     * send a push notification {@link nl.babbq.conference2015.utils.SendNotification}
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

    @Override
    public void onErrorResponse(VolleyError error) {
        error.printStackTrace();
        onUpdateDone();
    }

    @Override
    public void onResponse(List<Conference> response) {
        mConferences.clear();
        mConferences.addAll(response);
        mAdapter.notifyDataSetChanged(); //might not work
        onUpdateDone();
    }

    private void onUpdateDone() {
        mAnimatedBugDroid.setLoading(false);
    }

    @Override
    public void onBackPressed() {
        if (mAnimatedBugDroid != null && mAnimatedBugDroid.isAnimating()) {
            mAnimatedBugDroid.setLoading(false);
        } else {
            super.onBackPressed();
        }
    }

    public ArrayList<Conference> getConferences() {
        return mConferences;
    }

    private final class MainPagerAdapter extends FragmentPagerAdapter {


        private List<ListingFragment> mFragments;
        private List<String> mFragmentTitles;


        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentTitles = new ArrayList<>();
            mFragments = new ArrayList<>();
        }


        public void addFragment(ListingFragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
        }

        @Override
        public void notifyDataSetChanged() {
            super.notifyDataSetChanged();
            if (mFragments != null) {
                for (ListingFragment fragment: mFragments) {
                    fragment.notifyDataSetChanged();
                }
            }
        }

        @Override
        public Fragment getItem(int position) {
            return mFragments.get(position);
        }


        @Override
        public int getCount() {
            return mFragments.size();
        }


        @Override
        public CharSequence getPageTitle(int position) {
            return mFragmentTitles.get(position);
        }
    }
}
