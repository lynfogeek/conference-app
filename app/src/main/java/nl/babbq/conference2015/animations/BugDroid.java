package nl.babbq.conference2015.animations;

import android.animation.Animator;
import android.annotation.TargetApi;
import android.graphics.Color;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.support.annotation.NonNull;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.ImageView;

import java.util.Random;

import nl.babbq.conference2015.R;
import nl.babbq.conference2015.utils.Utils;

/**
 * Created by nono on 11/3/15.
 */
public class BugDroid implements View.OnClickListener {

    public interface OnRefreshClickListener {
        void onRefreshClick();
    }

    public static final int[] ANIMATED_DRAWABLES = {
            R.drawable.animated_android_shrug,
            R.drawable.animated_android_wave
    };

    private ImageView mBugDroid;
    private View mLoadingFrame;
    private View mRefreshButton;

    private OnRefreshClickListener mListener;
    private Runnable mCheckAnimation;
    private boolean mLoading = false;
    private boolean mIsAnimating = false;

    public BugDroid(@NonNull ImageView bugDroid,
                    @NonNull View loadingFrame,
                    @NonNull View refreshButton,
                    @NonNull OnRefreshClickListener listener) {
        mBugDroid = bugDroid;
        mLoadingFrame = loadingFrame;
        mRefreshButton = refreshButton;
        mRefreshButton.setOnClickListener(this);
        mListener = listener;
    }

    public void stopAnimation() {
        mRefreshButton.setEnabled(true);
        mLoadingFrame.setVisibility(View.GONE);
        mLoadingFrame.setBackgroundColor(Color.WHITE);
        if (mRefreshButton.getAnimation() != null) {
            mRefreshButton.getAnimation().cancel();
        }
        mIsAnimating = false;
        if (mCheckAnimation != null) {
            mBugDroid.removeCallbacks(mCheckAnimation);
            mCheckAnimation = null;
        }
    }

    public void startAnimation() {
        mIsAnimating = true;
        mRefreshButton.setEnabled(false);
        if (Utils.isLollipop()) {
            mBugDroid.setImageResource(ANIMATED_DRAWABLES[new Random().nextInt(2)]);

            int twelve = Utils.dpToPx(12, mRefreshButton.getContext()); //totally arbitrary ;)
            Animator anim = ViewAnimationUtils.createCircularReveal(mLoadingFrame,
                    mRefreshButton.getRight() - twelve,
                    mRefreshButton.getTop() + twelve,
                    twelve,
                    (float) Utils.getScreenDiagonal(mRefreshButton.getContext()));
            anim.setDuration(500);
            anim.addListener(new SimpleAnimatorListener() {
                @TargetApi(Build.VERSION_CODES.LOLLIPOP)
                public void onAnimationStart(Animator animator) {
                    mLoadingFrame.setVisibility(View.VISIBLE);
                }

                public void onAnimationEnd(Animator animator) {
                    renewAnimation();
                }

            });
            anim.start();
        } else {
            final Animation fadeIn = new AlphaAnimation(0, 1);
            fadeIn.setInterpolator(new DecelerateInterpolator());
            fadeIn.setDuration(200);
            fadeIn.setAnimationListener(new SimpleAnimationListener(){
                @Override
                public void onAnimationEnd(Animation animation) {
                    renewAnimation();
                }
            });
            mLoadingFrame.setVisibility(View.VISIBLE);
            mLoadingFrame.startAnimation(fadeIn);
        }
        mRefreshButton.startAnimation(
                AnimationUtils.loadAnimation(mRefreshButton.getContext(), R.anim.rotation));

        Drawable drawable = mBugDroid.getDrawable();
        if (drawable instanceof Animatable) {
            ((Animatable) drawable).start();
        }
    }

    private void renewAnimation() {
        mCheckAnimation = new Runnable() {
            @Override
            public void run() {
                if (!isLoading()) {
                    hideOrStop();
                } else {
                    renewAnimation();
                }
            }
        };
        mBugDroid.postDelayed(mCheckAnimation, 400);
    }

    private void hideAnimation() {
        if (Utils.isLollipop()) {
            int smallBugDroid = Utils.dpToPx(100, mRefreshButton.getContext());
            Animator anim = ViewAnimationUtils.createCircularReveal(mLoadingFrame,
                    Utils.getScreenWidth(mRefreshButton.getContext()) / 2,
                    Utils.getScreenHeight(mRefreshButton.getContext()) / 2,
                    (float) Utils.getScreenDiagonal(mRefreshButton.getContext()),
                    smallBugDroid);
            anim.setDuration(600);
            anim.addListener(new SimpleAnimatorListener() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoadingFrame.setBackgroundColor(Color.TRANSPARENT);
                    Animation outAnimation = AnimationUtils.loadAnimation(mRefreshButton.getContext(), R.anim.fades_out_slides_up);
                    outAnimation.setAnimationListener(new SimpleAnimationListener() {
                        @Override
                        public void onAnimationEnd(Animation animation) {
                            stopAnimation();
                        }
                    });
                    mBugDroid.startAnimation(outAnimation);
                }
            });
            anim.start();
        }
    }

    @Override
    public void onClick(View v) {
        if (!mIsAnimating) {
            mListener.onRefreshClick();
            startAnimation();
        }
    }

    private void hideOrStop() {
        if (Utils.isLollipop()) {
            hideAnimation();
        } else { //simply finishes the animation without being fancy
            stopAnimation();
        }
    }

    ///////////////////////
    //  Getter / setter  //
    ///////////////////////

    public View getBugDroid() {
        return mBugDroid;
    }

    public void setBugDroid(ImageView bugDroid) {
        mBugDroid = bugDroid;
    }

    public View getLoadingFrame() {
        return mLoadingFrame;
    }

    public void setLoadingFrame(View loadingFrame) {
        mLoadingFrame = loadingFrame;
    }

    public View getRefreshButton() {
        return mRefreshButton;
    }

    public void setRefreshButton(View refreshButton) {
        mRefreshButton = refreshButton;
    }

    public boolean isLoading() {
        return mLoading;
    }

    public void setLoading(boolean loading) {
        mLoading = loading;
    }

    public boolean isAnimating() {
        return mIsAnimating;
    }

}
