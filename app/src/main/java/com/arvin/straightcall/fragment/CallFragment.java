package com.arvin.straightcall.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.arvin.straightcall.R;
import com.arvin.straightcall.activity.CallActivity;
import com.arvin.straightcall.adapter.ContactViewPagerAdapter;
import com.arvin.straightcall.view.CustomViewPager;

public class CallFragment extends Fragment {
    public static String TAG = "CallFragment";
    ContactViewPagerAdapter contactViewPagerAdapter;

    CustomViewPager viewPager;

    public CallFragment() {
        // Required empty public constructor
    }

    public static CallFragment newInstance() {
        return new CallFragment();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_call, container, false);
        viewPager = (CustomViewPager) view.findViewById(R.id.contact_viewpager);
        contactViewPagerAdapter = new ContactViewPagerAdapter(getActivity());
        viewPager.setAdapter(contactViewPagerAdapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {

            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                CallActivity callActivity = ((CallActivity) getActivity());
                if (callActivity.canSpeak()) {
                    String name = contactViewPagerAdapter.getContactName(position);
                    callActivity.speak(name);
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        return view;
    }

    public void moveViewPagerToFirstPage() {
        if(viewPager.getChildCount()>=1){
            viewPager.setCurrentItem(0, false);
        }
    }

    @Override
    public void onPrepareOptionsMenu(Menu menu) {
        super.onPrepareOptionsMenu(menu);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        FragmentTransaction transaction = fragmentManager.beginTransaction();
        ActionBar actionBar = ((AppCompatActivity) getActivity()).getSupportActionBar();
        switch (id) {
            case R.id.setting:
                transaction.add(R.id.call_fragment, new SettingFragment(), SettingFragment.TAG);
                transaction.addToBackStack(SettingFragment.TAG);
                transaction.commit();
                if (actionBar != null) {
                    actionBar.setTitle(R.string.setting);
                    actionBar.setDisplayHomeAsUpEnabled(true);
                }
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public void setViewPagerPaging(Boolean enable) {
        if (viewPager != null) {
            viewPager.setPagingEnabled(enable);
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
