package se.hyperlab.tigcm;

import android.content.Intent;

import com.google.android.gms.iid.InstanceIDListenerService;

public class TiGCMInstanceIDListenerService extends InstanceIDListenerService {

    private static final String TAG = "TiGCMInstanceIDListenerService";

    @Override
    public void onTokenRefresh() {
        Intent intent = new Intent(this, TiGCMRegistrationIntentService.class);
        startService(intent);
    }
}
