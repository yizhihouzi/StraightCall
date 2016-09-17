package com.arvin.straightcall.fragment;

import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceScreen;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;

import com.arvin.straightcall.R;
import com.arvin.straightcall.activity.CallActivity;
import com.github.machinarius.preferencefragment.PreferenceFragment;

public class SettingFragment extends PreferenceFragment {

    public static final String TAG = "SettingFragment";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preferences);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        if (view != null) {
            view.setBackgroundColor(getResources().getColor(R.color.mainColor));
        }
        return view;
    }

    @Override
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        //如果“保存个人信息”这个按钮被选中，将进行括号里面的操作
        /*
        EditTextPreference editTextPreference = (EditTextPreference) findPreference("individual_name");
        //让editTextPreference和checkBoxPreference的状态保持一致
        editTextPreference.setEnabled(checkBoxPreference.isChecked());*/
        CheckBoxPreference soundCheckBox = (CheckBoxPreference) findPreference("sound");
        ((CallActivity) getActivity()).setSoundOpen(soundCheckBox.isChecked());
        // TODO Auto-generated method stub
        return super.onPreferenceTreeClick(preferenceScreen, preference);
    }

    public void onPrepareOptionsMenu(Menu menu) {
        menu.findItem(R.id.refresh).setVisible(false);
        menu.findItem(R.id.setting).setVisible(false);
        super.onPrepareOptionsMenu(menu);
    }
}
