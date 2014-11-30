package nl.droidcon.conference2014.utils;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.NotificationCompat;

import nl.droidcon.conference2014.R;

/**
 *
 * @author Arnaud Camus
 */
public class SendNotification {

    /**
     * Configure and push a system notification leading to the feedback form.
     * @param context a valid context
     */
    public static void feedbackForm(Context context) {
        int notificationId = 001;
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse("https://m.docs.google.com/forms/d/1KpwGcRarNoSa1J2-lZSqNSizuZDj2dSy2txg66FSpsQ/viewform"));
        PendingIntent viewPendingIntent =
                PendingIntent.getActivity(context, 0, intent, 0);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(context)
                        .setSmallIcon(R.drawable.ic_favorite_outline_white_24dp)
                        .setContentTitle(context.getString(R.string.notification_title))
                        .setContentText(context.getString(R.string.notification_text))
                        .setAutoCancel(true)
                        .setContentIntent(viewPendingIntent);

        NotificationManager notificationManager = (NotificationManager)
                                        context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(notificationId, notificationBuilder.build());
    }

}
