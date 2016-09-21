package com.arvin.straightcall.listener;

import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

public class TelStateListener extends PhoneStateListener {

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
        super.onCallStateChanged(state, incomingNumber);
    }
}
