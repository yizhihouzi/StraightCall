package com.arvin.straightcall.activity;

import android.content.Context;

import android.content.SharedPreferences;
import android.os.Build;
import android.preference.PreferenceManager;
import android.speech.tts.TextToSpeech;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBar;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.util.Log;
import android.view.MenuItem;

import com.arvin.straightcall.BuildConfig;
import com.arvin.straightcall.R;
import com.arvin.straightcall.fragment.CallFragment;
import com.arvin.straightcall.fragment.ContactFragment;
import com.arvin.straightcall.fragment.PhoneStateFragment;
import com.arvin.straightcall.util.PhoneUtil;
import com.litesuits.common.receiver.PhoneReceiver;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class CallActivity extends BaseActivity implements PhoneReceiver.PhoneListener {

    private PhoneReceiver phoneReceiver;
    private Map<Object, PhoneStateListener> phoneStateListeners = new HashMap<>();
    FragmentManager fragmentManager = getSupportFragmentManager();
    PhoneStateFragment phoneStateFragment;
    private TextToSpeech speech;
    private Boolean ttsIsYes = false;
    private boolean soundOpen = true;
    private CallFragment callFragment;
    private boolean moveTaskToBack = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        phoneReceiver = new PhoneReceiver();
        phoneReceiver.registerReceiver(this, this);
        initActionBar();
        setContentView(R.layout.activity_call);
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        callFragment = CallFragment.newInstance();
        transaction.add(R.id.call_fragment, callFragment, CallFragment.TAG);
        phoneStateFragment = PhoneStateFragment.newInstance("1", "2");
        transaction.add(R.id.call_fragment, phoneStateFragment, PhoneStateFragment.TAG);
        transaction.commit();
//        fragmentManager.addOnBackStackChangedListener(this::invalidateOptionsMenu);
        initTTS();
        SharedPreferences mySharedPreferences = PreferenceManager.getDefaultSharedPreferences(this);
        soundOpen = mySharedPreferences.getBoolean("sound", true);
        Log.d("soundOpen", soundOpen + "");
    }

    public void initTTS() {
        speech = new TextToSpeech(this, status -> {
            if (status == TextToSpeech.SUCCESS) {
                int result = speech.setLanguage(Locale.CHINESE);
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    if (BuildConfig.DEBUG) Log.d("CallActivity", getString(R.string.test2));
                } else {
                    ttsIsYes = true;
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
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            speech.speak(speakStr, TextToSpeech.QUEUE_FLUSH, null, null);
        } else {
            speech.speak(speakStr, TextToSpeech.QUEUE_FLUSH, null);
        }
    }

    @Override
    protected void onStart() {
        moveTaskToBack = false;
        // 获得TelephonyManager对象
        TelephonyManager telManager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
        // 获得通话网络类型信息
        int phoneType = telManager.getCallState();
        Log.d("CallActivity", phoneType + "");
        switch (phoneType) {
            case TelephonyManager.CALL_STATE_IDLE:
                fragmentManager
                        .beginTransaction()
                        .hide(phoneStateFragment)
                        .commit();
                break;
            default:
                fragmentManager
                        .beginTransaction()
                        .show(phoneStateFragment)
                        .commit();
                break;
            //TelephonyManager.CALL_STATE_RINGING:
            //TelephonyManager.CALL_STATE_OFFHOOK:
        }
        super.onStart();
    }


    @Override
    protected void onDestroy() {
        phoneReceiver.unRegisterReceiver(this);
        phoneReceiver = null;
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
            soundOpen = false;
            callFragment.moveViewPagerToFirstPage();
            soundOpen = true;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        Log.i("Activity--->Permissions", requestCode + "");
        List<Fragment> fragments = getSupportFragmentManager().getFragments();
        if (fragments != null) {
            for (Fragment fragment : fragments) {
                if ((fragment instanceof ContactFragment) && (PhoneUtil.CONTACT_INDEX == requestCode)) {
                    fragment.onRequestPermissionsResult(requestCode, permissions, grantResults);
                    return;
                }
            }
        }
    }

    @Override
    public void onPhoneStateChanged(PhoneReceiver.CallState state, String number) {
        switch (state) {
            case Outgoing:
                Log.d("CallActivity", "Outgoing");
                break;
            case OutgoingEnd:
                Log.d("CallActivity", "OutgoingEnd");
                fragmentManager
                        .beginTransaction()
                        .hide(phoneStateFragment)
                        .commit();
                break;
            case Incoming:
                Log.d("CallActivity", "Incoming");
                break;
            case IncomingEnd:
                Log.d("CallActivity", "IncomingEnd");
                fragmentManager
                        .beginTransaction()
                        .hide(phoneStateFragment)
                        .commitAllowingStateLoss();
                break;
            case IncomingRing:
                Log.d("CallActivity", "IncomingRing");
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
        void onPhoneStateChanged(PhoneReceiver.CallState state, String number);
    }
}
