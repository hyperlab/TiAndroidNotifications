package se.hyperlab.tigcm;

import org.appcelerator.titanium.TiApplication;
import android.app.IntentService;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import org.appcelerator.kroll.common.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

import java.io.IOException;

public class TiGCMRegistrationIntentService extends IntentService {

  private static final String TAG = "TiGCMRegistrationIntentService";

  public TiGCMRegistrationIntentService() {
    super(TAG);
  }

  @Override
  protected void onHandleIntent(Intent intent) {
    try {
      String senderId = TiApplication.getInstance().getAppProperties().getString(TiGCMModule.PROPERTY_SENDER_ID, "");
      InstanceID instanceID = InstanceID.getInstance(this);
      String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
      Log.i(TAG, "GCM Registration Token: " + token);
      TiGCMModule.getInstance().onRegister(token);
    } catch (Exception e) {
      Log.d(TAG, "Failed to complete token refresh", e);
    }

    Intent registrationComplete = new Intent(TiGCMModule.INTENT_FILTER_REGISTRATION_COMPLETE);
    LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
  }
}
