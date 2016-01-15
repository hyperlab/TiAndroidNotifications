package se.hyperlab.tigcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import org.appcelerator.titanium.util.TiRHelper;
import android.support.v4.app.NotificationCompat;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.appcelerator.kroll.common.Log;

public class NotificationPublisher extends BroadcastReceiver {

    private static final String TAG = "NotificationPublisher";
    private static final AtomicInteger id = new AtomicInteger(0);

    public static void createNotification(Context context, HashMap data) {
        int ntfId = id.getAndIncrement();

        Log.d(TAG, "Creating notification");

        Intent ntfIntent = new Intent(context, NotificationActivity.class);
        ntfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ntfIntent.putExtra(TiGCMModule.NTF_KEY_DATA, data);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ntfId, ntfIntent, PendingIntent.FLAG_ONE_SHOT);

        int appIcon = 0;
        boolean empty = true;

        try {
            appIcon = TiRHelper.getApplicationResource("drawable.ic_stat_notification");
        } catch (TiRHelper.ResourceNotFoundException ex) {
            Log.e(TAG, "Resource drawable.ic_stat_notification not found, trying with drawable.appicon");
            try {
                appIcon = TiRHelper.getApplicationResource("drawable.appicon");
            } catch (TiRHelper.ResourceNotFoundException e) {
                Log.e(TAG, "Couldn't find drawable.appicon");
            }
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(appIcon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent);

        // Color
        if (data.containsKey(TiGCMModule.NTF_KEY_COLOR)) {
            notificationBuilder.setColor(Color.parseColor(data.get(TiGCMModule.NTF_KEY_COLOR).toString()));
        } else {
            try {
                notificationBuilder.setColor(
                    context.getResources().getColor(
                        TiRHelper.getApplicationResource("color.notification_icon_background")
                    )
                );
            } catch (TiRHelper.ResourceNotFoundException ex) {
                Log.e(TAG, "Resource color.notification_icon_background not found");
            }
        }

        // Title
        if (data.containsKey(TiGCMModule.NTF_KEY_TITLE)) {
            notificationBuilder.setContentTitle(data.get(TiGCMModule.NTF_KEY_TITLE).toString());
            empty = false;
        }

        // Text
        if (data.containsKey(TiGCMModule.NTF_KEY_CONTENT)) {
            notificationBuilder.setContentText(data.get(TiGCMModule.NTF_KEY_CONTENT).toString());
            empty = false;
        }

        // Number
        if (data.containsKey(TiGCMModule.NTF_KEY_NUMBER)) {
            notificationBuilder.setNumber(Integer.parseInt(data.get(TiGCMModule.NTF_KEY_NUMBER).toString()));
        }

        if (!empty) {
            NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(ntfId, notificationBuilder.build());
        } else {
            Log.d(TAG, "Wont create empty notification.");
        }
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        HashMap data = (HashMap)intent.getSerializableExtra(TiGCMModule.NTF_KEY_DATA);

        createNotification(context, data);
    }
}
