package se.hyperlab.tigcm;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import org.appcelerator.kroll.common.Log;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.KrollDict;

import java.util.HashMap;

public class NotificationActivity extends Activity {

    private static final String TAG = "NotificationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        HashMap<String, Object> data = (HashMap)intent.getSerializableExtra(TiGCMModule.NTF_KEY_DATA);

        TiGCMModule module = TiGCMModule.getInstance();
        TiApplication instance = TiApplication.getInstance();

        if(module != null) {
            module.fireMessage(data, false);
        } else {
            KrollDict kdata = new KrollDict(data);

            TiApplication.getInstance().getAppProperties().setString(TiGCMModule.PROPERTY_PENDING_DATA, kdata.toString());
            Log.d(TAG, "Saving data in props: " + kdata.toString());
        }

        String pkg = instance.getApplicationContext().getPackageName();
        Intent launcherIntent = instance.getApplicationContext().getPackageManager().getLaunchIntentForPackage(pkg);

        startActivity(launcherIntent);
        finish();
    }
}
