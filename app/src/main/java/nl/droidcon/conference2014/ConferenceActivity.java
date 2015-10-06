package nl.droidcon.conference2014;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;

import nl.droidcon.conference2014.objects.Conference;
import nl.droidcon.conference2014.utils.Utils;
import nl.droidcon.conference2014.utils.ViewAnimations;
import nl.droidcon.conference2014.utils.WordColor;
import nl.droidcon.conference2014.views.FABView;

/**
 * Display the detail for one {@link Conference}
 * Must receive one {@link Conference} object in its {@link android.content.Intent}
 *
 * @author Arnaud Camus
 */
public class ConferenceActivity extends AppCompatActivity {

    Conference mConference;
    SimpleDateFormat simpleDateFormat;
    SimpleDateFormat simpleDateFormat2;

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
        setContentView(R.layout.activity_conference);

        simpleDateFormat = new SimpleDateFormat("E, HH:mm");
        simpleDateFormat2 = new SimpleDateFormat(" - HH:mm");

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(getString(R.string.app_name));
            toolbar.setNavigationIcon(getResources()
                        .getDrawable(R.drawable.ic_arrow_back_white_24dp));
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            toolbar.setNavigationOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
        }

        mConference = getIntent().getParcelableExtra("conference");

        ((TextView)findViewById(R.id.headline)).setText(mConference.getHeadeline());
        ((TextView)findViewById(R.id.speaker)).setText(mConference.getSpeaker());
        ((TextView)findViewById(R.id.text)).setText(mConference.getText());
        ((TextView)findViewById(R.id.location)).setText(String.format(getString(R.string.location),
                mConference.getLocation()));
        ((TextView)findViewById(R.id.location)).setTextColor(
                                                WordColor.generateColor(mConference.getLocation()));
        ((TextView)findViewById(R.id.date)).setText(
                simpleDateFormat.format(new Date(mConference.getStartDate()))
                + simpleDateFormat2.format(new Date(mConference.getEndDate())));

        Picasso.with(getApplicationContext())
                .load(mConference.getSpeakerImageUrl())
                .transform(((BaseApplication) getApplicationContext()).mPicassoTransformation)
                .into((ImageView)findViewById(R.id.image));

        findViewById(R.id.text).postDelayed(new Runnable() {
            @Override
            public void run() {
                ViewAnimations.expand(findViewById(R.id.text));
            }
        }, 50);

        setupFAB();
    }

    /**
     * Setup a {@link nl.droidcon.conference2014.views.FABView} to allow the
     * user to favorite the current {@link nl.droidcon.conference2014.objects.Conference}
     */
    private void setupFAB() {
        final FABView fab = new FABView.Builder(this)
                .withDrawable(getResources().getDrawable(
                        (mConference.isFavorite(getBaseContext())
                                ? R.drawable.ic_favorite_white_24dp
                                : R.drawable.ic_favorite_outline_white_24dp)
                )).withButtonColor(getResources().getColor(R.color.accentColor))
                .withGravity(Gravity.TOP| Gravity.LEFT)
                .withMargins(14, 80, 0, 0)
                .create();

        /**
         * On click on the FAB, we save the new state in a
         * {@link android.content.SharedPreferences} file and
         * toggle the heart icon.
         */
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConference.toggleFavorite(getBaseContext())) {
                    fab.setFloatingActionButtonDrawable(
                            getResources().getDrawable(R.drawable.ic_favorite_white_24dp));
                } else {
                    fab.setFloatingActionButtonDrawable(
                            getResources().getDrawable(R.drawable.ic_favorite_outline_white_24dp));
                }
            }
        });
    }

}
