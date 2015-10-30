package se.hyperlab.tigcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.appcelerator.titanium.util.TiRHelper;
import android.support.v4.app.NotificationCompat;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.appcelerator.kroll.common.Log;

public class NotificationPublisher extends BroadcastReceiver {

    private static final String TAG = "NotificationPublisher";
    private static final AtomicInteger id = new AtomicInteger(0);

    @Override
    public void onReceive(Context context, Intent intent) {
        HashMap data = (HashMap)intent.getSerializableExtra(TiGCMModule.NTF_KEY_DATA);

        int ntfId = id.getAndIncrement();

        Log.d(TAG, "Creating notification");

        Intent ntfIntent = new Intent(context, NotificationActivity.class);
        ntfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        ntfIntent.putExtra(TiGCMModule.NTF_KEY_DATA, data);
        PendingIntent pendingIntent = PendingIntent.getActivity(context, ntfId, ntfIntent, PendingIntent.FLAG_ONE_SHOT);

        int appIcon = 0;

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

        int color = 0;

        try {
            color = context.getResources().getColor(TiRHelper.getApplicationResource("color.notification_icon_background"));
        } catch (TiRHelper.ResourceNotFoundException ex) {
            Log.e(TAG, "Resource color.notification_icon_background not found");
        }

        NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
                .setSmallIcon(appIcon)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setColor(color);

        if(data.containsKey(TiGCMModule.NTF_KEY_TITLE)) {
            notificationBuilder.setContentTitle(data.get(TiGCMModule.NTF_KEY_TITLE).toString());
        }

        if(data.containsKey(TiGCMModule.NTF_KEY_CONTENT)) {
            notificationBuilder.setContentText(data.get(TiGCMModule.NTF_KEY_CONTENT).toString());
        }

        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
        notificationManager.notify(ntfId, notificationBuilder.build());
    }
}
