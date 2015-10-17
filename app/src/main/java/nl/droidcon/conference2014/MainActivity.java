package nl.droidcon.conference2014;

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

import com.opencsv.CSVReader;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import nl.droidcon.conference2014.fragments.ListingFragment;
import nl.droidcon.conference2014.objects.Conference;
import nl.droidcon.conference2014.objects.ConferenceDay;
import nl.droidcon.conference2014.utils.PreferenceManager;
import nl.droidcon.conference2014.utils.SendNotification;
import nl.droidcon.conference2014.utils.Utils;


/**
 * Main activity of the application, list all conferences slots into a listView
 * @author Arnaud Camus
 */
public class MainActivity extends AppCompatActivity {

    private final static String SCHEDULE_FILENAME = "timeline.tsv";
    public static final String CONFERENCES = "conferences";

    private MainPagerAdapter mAdapter;
    private ViewPager mViewPager;
    private ArrayList<Conference> mConferences = new ArrayList<>();
    private Toolbar mToolbar;
    private TabLayout mTabLayout;

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

        if (mToolbar != null) {
            setSupportActionBar(mToolbar);
            mToolbar.setTitle(getString(R.string.app_name));
        }

        if (savedInstanceState != null) {
            // Restore value of members from saved state
            mConferences = savedInstanceState.getParcelableArrayList(CONFERENCES);
        } else {
            readCalendar();
        }

        setupViewPager();
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

    private void setupViewPager() {
        mAdapter = new MainPagerAdapter(getSupportFragmentManager());
        ConferenceDay day1 = new ConferenceDay(1, "11/12/2015");
        ConferenceDay day2 = new ConferenceDay(2, "11/13/2015");
        mAdapter.addFragment(ListingFragment.newInstance(mConferences, day1), getString(R.string.day, 1));
        mAdapter.addFragment(ListingFragment.newInstance(mConferences, day2), getString(R.string.day, 2));
        mViewPager.setAdapter(mAdapter);
        mTabLayout.setupWithViewPager(mViewPager);
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
                if (nextLine.length >= 12
                        && !Utils.arrayContains(nextLine, getString(R.string.program_d2))) {
                    mConferences.add(new Conference(nextLine));
                }
            }
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

    private final class MainPagerAdapter extends FragmentPagerAdapter {


        private List<Fragment> mFragments;
        private List<String> mFragmentTitles;


        public MainPagerAdapter(FragmentManager fm) {
            super(fm);
            mFragmentTitles = new ArrayList<>();
            mFragments = new ArrayList<>();
        }


        public void addFragment(Fragment fragment, String title) {
            mFragments.add(fragment);
            mFragmentTitles.add(title);
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
