package nl.babbq.conference2015;

import android.annotation.TargetApi;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.transition.ChangeBounds;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import nl.babbq.conference2015.objects.Conference;
import nl.babbq.conference2015.utils.Utils;

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

    FloatingActionButton fab;

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

        simpleDateFormat = new SimpleDateFormat("E, HH:mm", Locale.ENGLISH);
        simpleDateFormat2 = new SimpleDateFormat(" - HH:mm", Locale.ENGLISH);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        if (toolbar != null) {
            toolbar.setTitle(null);
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

        fab = (FloatingActionButton)findViewById(R.id.fab);
        ((TextView)findViewById(R.id.headline)).setText(mConference.getHeadeline());
        ((TextView)findViewById(R.id.speaker)).setText(mConference.getSpeaker());
        ((TextView)findViewById(R.id.text)).setText(mConference.getText());
        ((TextView)findViewById(R.id.location)).setText(String.format(getString(R.string.location),
                mConference.getLocation()));
        ((TextView)findViewById(R.id.date)).setText(
                simpleDateFormat.format(new Date(mConference.getStartDate()))
                        + simpleDateFormat2.format(new Date(mConference.getEndDate())));

        Picasso.with(getApplicationContext())
                .load(mConference.getSpeakerImageUrl())
                .transform(((BaseApplication) getApplicationContext()).mPicassoTransformation)
                .into((ImageView) findViewById(R.id.image));
        setupFAB();
    }

    /**
     * Setup a fab to allow the
     * user to favorite the current {@link nl.babbq.conference2015.objects.Conference}
     */
    private void setupFAB() {
        fab.setImageResource((mConference.isFavorite(getBaseContext())
                        ? R.drawable.ic_favorite_white_24dp
                        : R.drawable.ic_favorite_outline_white_24dp));
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mConference.toggleFavorite(getBaseContext())) {
                    fab.setImageResource(R.drawable.ic_favorite_white_24dp);
                } else {
                    fab.setImageResource(R.drawable.ic_favorite_outline_white_24dp);
                }
            }
        });
    }

}
