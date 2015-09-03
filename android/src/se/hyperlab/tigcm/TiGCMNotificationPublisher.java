package se.hyperlab.tigcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import org.appcelerator.titanium.util.TiRHelper;
import android.support.v4.app.NotificationCompat;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.HashMap;

import org.appcelerator.kroll.common.Log;

public class TiGCMNotificationPublisher extends BroadcastReceiver {

  private static final String TAG = "TiGCMNotificationPublisher";
  private static final AtomicInteger id = new AtomicInteger(0);

  @Override
  public void onReceive(Context context, Intent intent) {
    HashMap<String, Object> data = (HashMap<String, Object>)intent.getSerializableExtra(TiGCMModule.PROPERTY_NOTIFICATION_DATA);

    int ntfId = id.getAndIncrement();

    Log.d(TAG, "Creating notification");
    
    Intent ntfIntent = new Intent(context, TiGCMNotificationActivity.class);
    ntfIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    ntfIntent.putExtra(TiGCMModule.PROPERTY_NOTIFICATION_DATA, data);
    PendingIntent pendingIntent = PendingIntent.getActivity(context, ntfId, ntfIntent, PendingIntent.FLAG_ONE_SHOT);

    int appIcon = 0;

    try {
      appIcon = TiRHelper.getApplicationResource("drawable.ic_stat_notification");
    } catch (TiRHelper.ResourceNotFoundException ex) {
      Log.e(TAG, "Resource not found; make sure it's in platform/android/res/drawable");
    }

    int color = 0;

    try {
      color = context.getResources().getColor(TiRHelper.getApplicationResource("color.notification_icon_background"));
    } catch (TiRHelper.ResourceNotFoundException ex) {
      Log.e(TAG, "Resource not found; make sure it's in platform/android/res/values");
    }

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context)
      .setSmallIcon(appIcon)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent)
      .setColor(color);

    if(data.containsKey(TiGCMModule.PROPERTY_NOTIFICATION_TITLE)) {
      notificationBuilder.setContentTitle(data.get(TiGCMModule.PROPERTY_NOTIFICATION_TITLE).toString());
    }

    if(data.containsKey(TiGCMModule.PROPERTY_NOTIFICATION_CONTENT)) {
      notificationBuilder.setContentText(data.get(TiGCMModule.PROPERTY_NOTIFICATION_CONTENT).toString());
    }

    NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(ntfId, notificationBuilder.build());
  }
}
