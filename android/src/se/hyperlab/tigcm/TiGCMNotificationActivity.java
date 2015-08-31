package se.hyperlab.tigcm;

import android.content.Intent;
import android.app.Activity;
import android.os.Bundle;
import org.appcelerator.kroll.common.Log;

import org.appcelerator.titanium.TiApplication;
import org.json.JSONObject;

public class TiGCMNotificationActivity extends Activity {

  private static final String TAG = "TiGCMNotificationActivity";

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);

    Intent intent = getIntent();
    Bundle dataBundle = intent.getBundleExtra(TiGCMModule.PROPERTY_INTENT_EXTRA_KEY);

    TiGCMModule module = TiGCMModule.getInstance();
    TiApplication instance = TiApplication.getInstance();

    JSONObject data = TiGCMModule.bundleToJSON(dataBundle);

    if(module != null) {
      module.setData(data);
    } else {
      TiApplication.getInstance().getAppProperties().setString(TiGCMModule.PROPERTY_PENDING_DATA, data.toString());
      Log.d(TAG, "Saving data in props: " + data.toString());
    }

    String pkg = instance.getApplicationContext().getPackageName();
    Intent launcherIntent = instance.getApplicationContext().getPackageManager().getLaunchIntentForPackage(pkg);

    startActivity(launcherIntent);
    finish();
  }
}
