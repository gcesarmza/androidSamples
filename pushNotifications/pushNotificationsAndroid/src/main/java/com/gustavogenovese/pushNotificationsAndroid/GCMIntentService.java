package com.gustavogenovese.pushNotificationsAndroid;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.google.android.gcm.GCMBaseIntentService;

public class GCMIntentService extends GCMBaseIntentService{

	private final static String TAG = "pushNotificationsAndroid-GCMIntentService";
	
	@Override
	protected void onError(Context context, String errorId) {
		Log.i(TAG, "onError: " + errorId);
	}

	@Override
	protected void onMessage(Context context, Intent intent) {
		Log.i(TAG, "onMessage: " + intent.getStringExtra("message"));
	}

	@Override
	protected void onRegistered(Context context, String regId) {
		Log.i(TAG, "onRegistered: " + regId);		
	}

	@Override
	protected void onUnregistered(Context context, String regId) {
		Log.i(TAG, "onUnregistered: " + regId);
	}

}
