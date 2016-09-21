package com.arvin.straightcall.activity;

import android.content.Context;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;

import com.arvin.straightcall.BuildConfig;
import com.arvin.straightcall.R;
import com.arvin.straightcall.fragment.CallFragment;
import com.arvin.straightcall.fragment.PhoneStateFragment;
import com.arvin.straightcall.listener.TelStateListener;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class CallActivity extends BaseActivity implements TelStateListener.CallStateChangeListener {

    private Map<Object, PhoneStateListener> phoneStateListeners = new HashMap<>();
    FragmentManager fragmentManager = getSupportFragmentManager();
    PhoneStateFragment phoneStateFragment;
    private TextToSpeech speech;
    private Boolean ttsIsYes = false;
    private boolean soundOpen = true;
    private CallFragment callFragment;
    private boolean moveTaskToBack = false;
    TelephonyManager telManager;
    TelStateListener telStateListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initActionBar();
        setContentView(R.layout.activity_call);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        callFragment = CallFragment.newInstance();
        transaction.add(R.id.call_fragment, callFragment, CallFragment.TAG);
        phoneStateFragment = PhoneStateFragment.newInstance();
        transaction.add(R.id.call_fragment, phoneStateFragment, PhoneStateFragment.TAG);
        transaction.commit();
//        fragmentManager.addOnBackStackChangedListener(this::invalidateOptionsMenu);
        initTTS(null);
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        soundOpen = mySharedPreferences.getBoolean("sound", true);
        telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        telStateListener = new TelStateListener();
        telStateListener.registerCallStateListener(this, this);
        telManager.listen(telStateListener, android.telephony.PhoneStateListener.LISTEN_CALL_STATE);
    }

    public void initTTS(String speakStr) {
        speech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = speech.setLanguage(Locale.CHINESE);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    if (BuildConfig.DEBUG)
                        Log.d("CallActivity", CallActivity.this.getString(R.string.test2));
                    ttsIsYes = false;
                } else {
                    ttsIsYes = true;
                    if (speakStr != null) {
                        int speakState = ttsSpeak(speakStr);
                        if (speakState == TextToSpeech.ERROR) {
                            Log.d("speakState", speakStr + " not speak ");
                        }
                    }
                }
            }
        });
    }

    public void setSoundOpen(boolean soundOpen) {
        this.soundOpen = soundOpen;
    }

    public boolean canSpeak() {
        return ttsIsYes && soundOpen;
    }

    public void speak(String speakStr) {
        int speakState = ttsSpeak(speakStr);
        if (speakState == TextToSpeech.ERROR) {
            initTTS(speakStr);
        }
    }

    private int ttsSpeak(String speakStr) {
        int speakState;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            speakState = speech.speak(speakStr, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            speakState = speech.speak(speakStr, TextToSpeech.QUEUE_FLUSH, null);
        }
        return speakState;
    }

    @Override
    protected void onStart() {
        moveTaskToBack = false;
        // 获得通话网络类型信息
        int phoneType = telManager.getCallState();
        Log.d("CallActivity", phoneType + "");
        switch (phoneType) {
            case TelephonyManager.CALL_STATE_IDLE:
                fragmentManager
                        .beginTransaction()
                        .hide(phoneStateFragment)
                        .commitNowAllowingStateLoss();
                break;
            default:
                fragmentManager
                        .beginTransaction()
                        .show(phoneStateFragment)
                        .commitNowAllowingStateLoss();
                break;
            //TelephonyManager.CALL_STATE_RINGING:
            //TelephonyManager.CALL_STATE_OFFHOOK:
        }
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        telStateListener.unregisterCallStateListener(this);
        telStateListener=null;
        if (speech != null) {
            speech.stop();
            speech.shutdown();
        }
        super.onDestroy();
    }

    public void initActionBar() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            getSupportActionBar().setTitle(R.string.app_name);
            getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        }
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }*/

    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fragmentManager = getSupportFragmentManager();
        ActionBar actionBar = getSupportActionBar();
        switch (id) {
            case android.R.id.home:
                if (fragmentManager.popBackStackImmediate()) {
                    if (fragmentManager.getBackStackEntryCount() == 0 && actionBar != null) {
                        actionBar.setTitle(R.string.app_name);
                        actionBar.setDisplayHomeAsUpEnabled(false);
                    }
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (fragmentManager.getBackStackEntryCount() == 0) {
            moveTaskToBack(true);
            moveTaskToBack = true;
            return;
        }
        super.onBackPressed();
        ActionBar actionBar = getSupportActionBar();
        if (fragmentManager.getBackStackEntryCount() == 0 && actionBar != null) {
            actionBar.setTitle(R.string.app_name);
            actionBar.setDisplayHomeAsUpEnabled(false);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (moveTaskToBack) {
            boolean tmp = soundOpen;
            soundOpen = false;
            callFragment.moveViewPagerToFirstPage();
            soundOpen = tmp;
        }
    }

    @Override
    public void onPhoneStateChanged(int state, String number) {
        switch (state) {
            case TelephonyManager.CALL_STATE_RINGING:
                Log.d("CallActivity", "CALL_STATE_RINGING");
                fragmentManager
                        .beginTransaction()
                        .show(phoneStateFragment)
                        .commitNowAllowingStateLoss();
                break;
            case TelephonyManager.CALL_STATE_OFFHOOK:
                Log.d("CallActivity", "CALL_STATE_OFFHOOK");
                fragmentManager
                        .beginTransaction()
                        .show(phoneStateFragment)
                        .commitNowAllowingStateLoss();
                break;
            case TelephonyManager.CALL_STATE_IDLE:
                Log.d("CallActivity", "CALL_STATE_IDLE");
                fragmentManager
                        .beginTransaction()
                        .hide(phoneStateFragment)
                        .commitNowAllowingStateLoss();
                break;
        }
        for (Map.Entry<Object, PhoneStateListener> entry : phoneStateListeners.entrySet()) {
            entry.getValue().onPhoneStateChanged(state, number);
        }
    }

    public void registerPhoneStateListener(Object key, PhoneStateListener phoneStateListener) {
        phoneStateListeners.put(key, phoneStateListener);
    }

    public void unRegisterPhoneStateListener(Object key) {
        try {
            phoneStateListeners.remove(key);
        } catch (Exception e) {
            Log.d("unRegister", e.toString());
        }
    }

    public interface PhoneStateListener {
        void onPhoneStateChanged(int state, String number);
    }
}
