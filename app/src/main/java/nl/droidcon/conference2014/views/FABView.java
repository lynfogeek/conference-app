package nl.droidcon.conference2014.views;

import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;

import nl.droidcon.conference2014.utils.Utils;

/**
 * Implementation of a Floating Action Button
 *
 * @author Arnaud Camus
 */
public class FABView extends View {

    Context context;
    Paint mButtonPaint;
    Paint mDrawablePaint;
    Bitmap mBitmap;

    public FABView(Context context) {
        super(context);
        this.context = context;
        init(Color.WHITE);
    }

    public void setFloatingActionButtonColor(int FloatingActionButtonColor) {
        init(FloatingActionButtonColor);
    }

    public void setFloatingActionButtonDrawable(Drawable FloatingActionButtonDrawable) {
        Bitmap b = ((BitmapDrawable) FloatingActionButtonDrawable).getBitmap();
        mBitmap =  Bitmap.createScaledBitmap(b, Utils.dpToPx(16, getContext()),
                    Utils.dpToPx(16, getContext()), true);
        invalidate();
    }

    public void init(int FloatingActionButtonColor) {
        setWillNotDraw(false);
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        mButtonPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
        mButtonPaint.setColor(FloatingActionButtonColor);
        mButtonPaint.setStyle(Paint.Style.FILL);
        mButtonPaint.setShadowLayer(4.0f, 0.0f, 3.5f, Color.argb(100, 0, 0, 0));
        mDrawablePaint = new Paint(Paint.ANTI_ALIAS_FLAG);

        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        setClickable(true);
        canvas.drawCircle(getWidth() / 2, getHeight() / 2, (float) (getWidth() / 2.6), mButtonPaint);
        canvas.drawBitmap(mBitmap, (getWidth() - mBitmap.getWidth()) / 2,
                (getHeight() - mBitmap.getHeight()) / 2, mDrawablePaint);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            setAlpha(1.0f);
        } else if (event.getAction() == MotionEvent.ACTION_DOWN) {
            setAlpha(0.6f);
        }
        return super.onTouchEvent(event);
    }

    static public class Builder {
        private FrameLayout.LayoutParams params;
        private final Activity activity;
        int gravity = Gravity.BOTTOM | Gravity.END; // default bottom right
        Drawable drawable;
        int color = Color.WHITE;
        int size = 0;

        public Builder(Activity context) {
            size = Utils.dpToPx(44, context);
            params = new FrameLayout.LayoutParams(size, size);
            params.gravity = gravity;

            this.activity = context;
        }

        /**
         * Sets the gravity for the FAB
         */
        public Builder withGravity(int gravity) {
            this.gravity = gravity;
            return this;
        }

        /**
         * Sets the margins for the FAB in dp
         */
        public Builder withMargins(int left, int top, int right, int bottom) {
            params.setMargins(
                    Utils.dpToPx(left, activity),
                    Utils.dpToPx(top, activity),
                    Utils.dpToPx(right, activity),
                    Utils.dpToPx(bottom, activity));
            return this;
        }

        /**
         * Sets the FAB drawable
         */
        public Builder withDrawable(final Drawable drawable) {
            this.drawable = drawable;
            return this;
        }

        /**
         * Sets the FAB color
         */
        public Builder withButtonColor(final int color) {
            this.color = color;
            return this;
        }

        /**
         * Sets the FAB size in dp
         */
        public Builder withButtonSize(int size) {
            size = Utils.dpToPx(size, activity);
            params = new FrameLayout.LayoutParams(size, size);
            return this;
        }

        public FABView create() {
            final FABView button = new FABView(activity);
            button.setFloatingActionButtonColor(this.color);
            button.setFloatingActionButtonDrawable(this.drawable);
            params.gravity = this.gravity;
            ViewGroup root = (ViewGroup) activity.findViewById(android.R.id.content);
            root.addView(button, params);
            return button;
        }

    }
}