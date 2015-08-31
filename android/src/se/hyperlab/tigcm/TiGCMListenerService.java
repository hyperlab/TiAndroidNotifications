package se.hyperlab.tigcm;

import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.titanium.TiApplication;
import org.appcelerator.titanium.util.TiRHelper;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicInteger;

import com.google.android.gms.gcm.GcmListenerService;

public class TiGCMListenerService extends GcmListenerService {

  private static final String TAG = "TiGCMListenerService";
  private static final AtomicInteger id = new AtomicInteger(0);

  @Override
  public void onMessageReceived(String from, Bundle data) {
    Log.d(TAG, "Received message from: " + from);

    TiGCMModule module = TiGCMModule.getInstance();
    if(module != null) {
      if(KrollRuntime.isInitialized() && TiApplication.isCurrentActivityInForeground()) {
        module.onMessage(data);
        return;
      }
    }

    int ntfId = id.getAndIncrement();

    // create notification
    Log.d(TAG, "Creating notification");
    
    Intent intent = new Intent(this, TiGCMNotificationActivity.class);
    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
    intent.putExtra(TiGCMModule.PROPERTY_INTENT_EXTRA_KEY, data);
    PendingIntent pendingIntent = PendingIntent.getActivity(this, ntfId, intent, PendingIntent.FLAG_ONE_SHOT);

    int appIcon = 0;

    try {
      appIcon = TiRHelper.getApplicationResource("drawable.ic_stat_notification");
    } catch (TiRHelper.ResourceNotFoundException ex) {
      Log.e(TAG, "Resource not found; make sure it's in platform/android/res/drawable");
    }

    NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this)
      .setSmallIcon(appIcon)
      .setAutoCancel(true)
      .setContentIntent(pendingIntent);

    if(data.containsKey(TiGCMModule.PROPERTY_NOTIFICATION_TITLE)) {
      notificationBuilder.setContentTitle(data.getString(TiGCMModule.PROPERTY_NOTIFICATION_TITLE));
    }

    if(data.containsKey(TiGCMModule.PROPERTY_NOTIFICATION_CONTENT)) {
      notificationBuilder.setContentText(data.getString(TiGCMModule.PROPERTY_NOTIFICATION_CONTENT));
    }

    NotificationManager notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
    notificationManager.notify(ntfId, notificationBuilder.build());
  }
}
