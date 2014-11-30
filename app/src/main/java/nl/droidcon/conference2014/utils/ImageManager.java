package nl.droidcon.conference2014.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Shader;

/**
 * @author Arnaud Camus
 */
public class ImageManager {

    /**
     * Transform a bitmap into a circle bitmap
     *
     * @param bitmap the bitmap to transform
     * @return a cropped bitmap
     */
    public static Bitmap cropBitmapToCircle(Bitmap bitmap, final Context ctx) {
        if (bitmap == null) {
            return null;
        }

        final int size = Utils.dpToPx(40, ctx);

        Bitmap scaled = Bitmap.createScaledBitmap(bitmap, size, size, true);
        Bitmap circleBitmap = Bitmap.createBitmap(size, size, Bitmap.Config.ARGB_8888);
        BitmapShader shader = new BitmapShader(scaled, Shader.TileMode.CLAMP, Shader.TileMode.CLAMP);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setShader(shader);
        Canvas c = new Canvas(circleBitmap);
        c.drawCircle(size / 2, size / 2, size / 2, paint);
        return circleBitmap;
    }

}