package se.hyperlab.tigcm;

import android.content.Intent;
import android.os.Bundle;

import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.KrollRuntime;
import org.appcelerator.titanium.TiApplication;

import java.util.HashMap;

import com.google.android.gms.gcm.GcmListenerService;

public class ListenerService extends GcmListenerService {

    private static final String TAG = "ListenerService";

    @Override
    public void onMessageReceived(String from, Bundle rawData) {
        Log.d(TAG, "Received message from: " + from);
        HashMap<String, Object> data = TiGCMModule.bundleToHashMap(rawData);

        TiGCMModule module = TiGCMModule.getInstance();
        if(module != null) {
            if(KrollRuntime.isInitialized() && TiApplication.isCurrentActivityInForeground()) {
                module.fireMessage(data, true);
                return;
            }
        }

        Intent intent = new Intent(this, NotificationPublisher.class);
        intent.putExtra(TiGCMModule.NTF_KEY_DATA, data);
        sendBroadcast(intent);
    }
}
