package se.hyperlab.tigcm;

import org.appcelerator.titanium.TiApplication;
import android.app.IntentService;
import android.content.Intent;
import org.appcelerator.kroll.common.Log;

import com.google.android.gms.gcm.GoogleCloudMessaging;
import com.google.android.gms.iid.InstanceID;

public class RegistrationIntentService extends IntentService {

    private static final String TAG = "RegistrationIntentService";

    public RegistrationIntentService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        try {
            String senderId = TiApplication.getInstance().getAppProperties().getString(TiGCMModule.PROPERTY_SENDER_ID, "");
            InstanceID instanceID = InstanceID.getInstance(this);
            String token = instanceID.getToken(senderId, GoogleCloudMessaging.INSTANCE_ID_SCOPE, null);
            Log.i(TAG, "GCM Registration Token: " + token);
            TiGCMModule.getInstance().fireRegister(token);
        } catch (Exception e) {
            Log.d(TAG, "Failed to complete token refresh", e);
            e.printStackTrace();
            TiGCMModule.getInstance().fireError("Failed to complete token refresh.");
        }
    }
}
