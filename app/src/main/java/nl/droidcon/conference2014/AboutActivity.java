package nl.droidcon.conference2014;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import com.mikepenz.aboutlibraries.Libs;
import com.mikepenz.aboutlibraries.ui.LibsActivity;

import java.util.ArrayList;
import java.util.List;

import nl.droidcon.conference2014.adapters.AboutAdapter;
import nl.droidcon.conference2014.objects.AboutItem;

/**
 * This activity gives access to:
 *  - A navigation mode to join the conference site.
 *  - The list of Open Source libraries used.
 *  - A link to my Twitter :)
 *  - An access to the feedback form for the Organization
 *
 *  @author Arnaud Camus
 */
public class AboutActivity extends ActionBarActivity implements AdapterView.OnItemClickListener {

    private List<AboutItem> mAboutItems = new ArrayList<AboutItem>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_about);
        Toolbar mToolbar = (Toolbar) findViewById(R.id.toolbar);
        if (mToolbar != null) {
            mToolbar.setNavigationIcon(getResources().getDrawable(R.drawable.ic_arrow_back_white_24dp));
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        ListView mListView = (ListView) findViewById(R.id.listView);
        mListView.setOnItemClickListener(this);
        generateList();
        AboutAdapter mAdapter = new AboutAdapter(this, 0x00, mAboutItems);
        mListView.setAdapter(mAdapter);
    }

    private void generateList() {
        mAboutItems.clear();
        mAboutItems.add(new AboutItem(getString(R.string.venue),
                getString(R.string.venue_text),
                R.drawable.ic_map_grey600_48dp));
        mAboutItems.add(new AboutItem(getString(R.string.opensource),
                getString(R.string.opensource_text),
                R.drawable.ic_storage_grey600_48dp));
        mAboutItems.add(new AboutItem(getString(R.string.author),
                getString(R.string.author_text),
                R.drawable.ic_mood_grey600_48dp));
        mAboutItems.add(new AboutItem(getString(R.string.evaluation),
                getString(R.string.evaluation_text),
                R.drawable.ic_check_box_grey600_48dp));
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0: //Launch Navigation to the conference site
                Intent i = new Intent(android.content.Intent.ACTION_VIEW,
                        Uri.parse("https://www.google.com/maps/dir//Hotel+Casa+400,+Eerste+Ringdijkstraat+4,+1097+BC+Amsterdam,+Netherlands/@52.349904,4.844722,12z"));
                startActivity(i);
                break;
            case 1: // Display the Open Source projects used for this application
                Intent i1 = new Intent(getApplicationContext(), LibsActivity.class);
                i1.putExtra(Libs.BUNDLE_FIELDS, Libs.toStringArray(R.string.class.getFields()));
                i1.putExtra(Libs.BUNDLE_LIBS, new String[]{"OpenCSV", "PrettySharedPreferences"});
                i1.putExtra(Libs.BUNDLE_VERSION, true);
                i1.putExtra(Libs.BUNDLE_LICENSE, true);
                i1.putExtra(Libs.BUNDLE_TITLE, "Open Source");
                i1.putExtra(Libs.BUNDLE_THEME, R.style.AppThemeBar);
                startActivity(i1);
                break;
            case 2: //Open my twitter ;)
                Intent i2 = new Intent(Intent.ACTION_VIEW);
                i2.setData(Uri.parse("http://twitter.com/lynfogeek"));
                startActivity(i2);
                break;
            case 3: //Open the feedback form
                Intent i3 = new Intent(Intent.ACTION_VIEW);
                i3.setData(Uri.parse("https://m.docs.google.com/forms/d/1KpwGcRarNoSa1J2-lZSqNSizuZDj2dSy2txg66FSpsQ/viewform"));
                startActivity(i3);
                break;
        }
    }
}
