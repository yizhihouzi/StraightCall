package com.arvin.straightcall.listener;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;

import java.util.HashMap;
import java.util.Map;

public class TelStateListener extends PhoneStateListener {
    private Map<Object, CallStateChangeListener> callStateChangeListeners = new HashMap<>();

    @Override
    public void onCallStateChanged(int state, String incomingNumber) {
        switch (state) {
            case TelephonyManager.CALL_STATE_IDLE:
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                break;
            case TelephonyManager.CALL_STATE_RINGING:
                break;
        }
        for (Map.Entry<Object, CallStateChangeListener> entry : callStateChangeListeners.entrySet()) {
            entry.getValue().onPhoneStateChanged(state, incomingNumber);
        }
        super.onCallStateChanged(state, incomingNumber);
    }

    public void registerCallStateListener(Object key, CallStateChangeListener phoneStateListener) {
        callStateChangeListeners.put(key, phoneStateListener);
    }

    public void unregisterCallStateListener(Object key) {
        try {
            callStateChangeListeners.remove(key);
        } catch (Exception e) {
            Log.d("unRegister", e.toString());
        }
    }

    public interface CallStateChangeListener {
        void onPhoneStateChanged(int stat, String incomingNumbere);
    }
}
