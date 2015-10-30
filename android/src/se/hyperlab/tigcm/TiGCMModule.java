/**
 * This file was auto-generated by the Titanium Module SDK helper for Android
 * Appcelerator Titanium Mobile
 * Copyright (c) 2009-2010 by Appcelerator, Inc. All Rights Reserved.
 * Licensed under the terms of the Apache Public License
 * Please see the LICENSE included with this distribution for details.
 *
 */
package se.hyperlab.tigcm;

import org.appcelerator.titanium.TiApplication;
import org.appcelerator.kroll.annotations.Kroll;
import org.appcelerator.kroll.common.Log;
import org.appcelerator.kroll.KrollDict;
import org.appcelerator.kroll.KrollFunction;
import org.appcelerator.kroll.KrollModule;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;

import org.json.JSONObject;
import org.json.JSONException;

import java.util.HashMap;


@Kroll.module(name="TiGCM", id="se.hyperlab.tigcm")
public class TiGCMModule extends KrollModule
{
    private static final String TAG = "TiGCMModule";

    private static final String KEY_DEVICE_TOKEN = "token";
    private static final String KEY_DATA = "data";
    private static final String KEY_ERROR = "error";

    public static final String NTF_KEY_TITLE = "title";
    public static final String NTF_KEY_CONTENT = "message";
    public static final String NTF_KEY_DATA = KEY_DATA;

    public static final String PROPERTY_SENDER_ID = "TiGCM.senderId";
    public static final String PROPERTY_PENDING_DATA = "TiGCM.pendingData";

    private static final String KEY_ERROR_CALLBACK = "onError";
    private static final String KEY_MESSAGE_CALLBACK = "onMessage";
    private static final String KEY_REGISTER_CALLBACK = "onRegister";

    private static final String PROPERTY_NOTIFICATION_DATA = "data";
    private static final String PROPERTY_NOTIFICATION_COUNTER = "TiGCM.ntfCounter";

    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 9000;

    private static KrollFunction errorCallback;
    private static KrollFunction messageCallback;
    private static KrollFunction registerCallback;

    private static String launchData = null;

    private static TiGCMModule instance = null;

    public static TiGCMModule getInstance() {
        return instance;
    }

    public TiGCMModule()
    {
        super();

        messageCallback = null;
        registerCallback = null;
        errorCallback = null;

        instance = this;
    }

    @Kroll.onAppCreate
    public static void onAppCreate(TiApplication app)
    {
        if(!app.getAppProperties().hasProperty(PROPERTY_NOTIFICATION_COUNTER)) {
            app.getAppProperties().setInt(PROPERTY_NOTIFICATION_COUNTER, 0);
        }
    }

    @Kroll.method
    public void registerForPushNotifications(KrollDict options) {
        if(TiApplication.getInstance().getAppProperties().hasProperty(PROPERTY_PENDING_DATA)) {
            launchData = TiApplication.getInstance().getAppProperties().getString(PROPERTY_PENDING_DATA, "");
            Log.d(TAG, "Get data from props: " + launchData);
            TiApplication.getInstance().getAppProperties().removeProperty(PROPERTY_PENDING_DATA);
        }

        if (options.containsKey(KEY_ERROR_CALLBACK)) {
            Object error = options.get(KEY_ERROR_CALLBACK);
            if (error instanceof KrollFunction) {
                Log.d(TAG, "Setting error callback");
                errorCallback = (KrollFunction) error;
            }
        }

        if(options.containsKey(KEY_REGISTER_CALLBACK)) {
            Object register = options.get(KEY_REGISTER_CALLBACK);
            if(register instanceof KrollFunction) {
                Log.d(TAG, "Setting register callback");
                registerCallback = (KrollFunction) register;
            }
        }

        if(options.containsKey(KEY_MESSAGE_CALLBACK)) {
            Object message = options.get(KEY_MESSAGE_CALLBACK);
            if(message instanceof KrollFunction) {
                Log.d(TAG, "Setting message callback");
                messageCallback = (KrollFunction) message;

                if(launchData != null) {
                    Log.d(TAG, "Have pending data");
                    fireMessage(getData(), false);
                }
            }
        }

        if (checkPlayServices(TiApplication.getAppCurrentActivity())) {
            Intent intent = new Intent(TiApplication.getInstance().getApplicationContext(), RegistrationIntentService.class);
            TiApplication.getInstance().startService(intent);
        }
    }

    private boolean checkPlayServices(Activity activity) {
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(TiApplication.getInstance().getApplicationContext());
        if (resultCode != ConnectionResult.SUCCESS) {
            if (GooglePlayServicesUtil.isUserRecoverableError(resultCode)) {
                GooglePlayServicesUtil.getErrorDialog(resultCode, activity, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Log.i(TAG, "This device is not supported.");
                TiApplication.getAppCurrentActivity().finish();
            }
            return false;
        }
        return true;
    }

    private static HashMap<String, Object> stringToHashMap(String source) {
        JSONObject jsonData;
        KrollDict data = null;
        try {
            jsonData = new JSONObject(source);
            data = new KrollDict(jsonData);
        } catch(JSONException e) {
            Log.d("stringToKrollDict", "Failed to convert string to KrollDict");
        }

        return data;
    }

    public static HashMap<String, Object> bundleToHashMap(Bundle source) {
        HashMap<String, Object> result = new HashMap<String, Object>();
        if(source != null) {
            for(String key : source.keySet()) {
                result.put(key, source.get(key));
            }
        }

        return result;
    }

    public void fireError(String error) {
        Log.d(TAG, "Fire error callback");
        if (errorCallback != null) {
            KrollDict e = new KrollDict();
            e.put(KEY_ERROR, error);
            errorCallback.call(getKrollObject(), e);
            Log.d(TAG, "onError was called");
        }
    }

    public void fireRegister(String token) {
        Log.d(TAG, "Fire register callback");
        if(registerCallback != null) {
            KrollDict data = new KrollDict();
            data.put(KEY_DEVICE_TOKEN, token);
            registerCallback.call(getKrollObject(), data);
            Log.d(TAG, "onRegister was called");
        }
    }

    public void fireMessage(HashMap<String, Object> message, boolean appInForeground) {
        Log.d(TAG, "Fire message callback");
        if(messageCallback != null) {
            HashMap<String, Object> push = new HashMap<String, Object>();
            push.put("appInForeground", appInForeground);
            push.put(KEY_DATA, message);
            messageCallback.call(getKrollObject(), push);
            Log.d(TAG, "onMessage was called");
        }
    }

    private HashMap<String, Object> getData() {
        if(launchData != null) {
            HashMap<String, Object> data = stringToHashMap(launchData);
            launchData = null;

            return data;
        } else {
            return null;
        }
    }

    @Kroll.method
    public void clearSchedule() {
        TiApplication app = TiApplication.getInstance();

        int ntfCount = app.getAppProperties().getInt(PROPERTY_NOTIFICATION_COUNTER, 0);

        Log.d(TAG, "Clearing " + ntfCount + " notifications");

        if(ntfCount > 0) {
            Intent intent = new Intent(app.getApplicationContext(), NotificationPublisher.class);
            for(int i = 0; i < ntfCount; i++) {
                PendingIntent pendingIntent = PendingIntent.getBroadcast(app.getApplicationContext(), i, intent, PendingIntent.FLAG_ONE_SHOT);
                AlarmManager alarmManager = (AlarmManager)app.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                alarmManager.cancel(pendingIntent);
                pendingIntent.cancel();
            }

            app.getAppProperties().setInt(PROPERTY_NOTIFICATION_COUNTER, 0);
        }
    }

    @Kroll.method
    public void schedule(long time, HashMap data) {
        TiApplication app = TiApplication.getInstance();
        int ntfId = app.getAppProperties().getInt(PROPERTY_NOTIFICATION_COUNTER, 0);

        Log.d(TAG, "Scheduling notification " + ntfId + " at " + time);

        Intent intent = new Intent(app.getApplicationContext(), NotificationPublisher.class);
        intent.putExtra(PROPERTY_NOTIFICATION_DATA, data);
        PendingIntent pendingIntent = PendingIntent.getBroadcast(app.getApplicationContext(), ntfId, intent, PendingIntent.FLAG_ONE_SHOT);

        AlarmManager alarmManager = (AlarmManager)app.getApplicationContext().getSystemService(Context.ALARM_SERVICE);
        alarmManager.set(AlarmManager.RTC_WAKEUP, time, pendingIntent);

        app.getAppProperties().setInt(PROPERTY_NOTIFICATION_COUNTER, ntfId+1);
    }

}
