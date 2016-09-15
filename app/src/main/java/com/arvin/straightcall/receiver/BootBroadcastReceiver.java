package com.arvin.straightcall.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.arvin.straightcall.activity.CallActivity;

/**
 * Created with IntelliJ IDEA.
 * User: 郏高阳
 * Date: 13-6-5
 * Time: 下午8:25
 * To change this template use File | Settings | File Templates.
 */
public class BootBroadcastReceiver extends BroadcastReceiver {
    static final String ACTION = "android.intent.action.BOOT_COMPLETED";

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("BootBroadcastReceiver",intent.getAction());
        if (intent.getAction().equals(ACTION)) {
            Intent mainActivityIntent = new Intent(context, CallActivity.class);  // 要启动的Activity
            mainActivityIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mainActivityIntent);
        }
    }
}