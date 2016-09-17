package com.arvin.straightcall.fragment;

import android.Manifest;
import android.content.ContentResolver;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
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

public class CallFragment extends BaseFragment {
    private static final int PERMISSIONS_REQUEST_READ_CONTACTS = 100;
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
        Cursor contactCursor = getContactsCursor();
        viewPager = (CustomViewPager) view.findViewById(R.id.contact_viewpager);
        contactViewPagerAdapter = new ContactViewPagerAdapter(getActivity(), contactCursor);
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

    private Cursor getContactsCursor() {
        if (checkPermission(Manifest.permission.READ_CONTACTS, PERMISSIONS_REQUEST_READ_CONTACTS, getActivity().getString(R.string.read_contact_tip))) {
            ContentResolver resolver = getActivity().getContentResolver();
            Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
            //查询联系人数据
            return resolver.query(uri, null, null, null,
                    android.provider.ContactsContract.Contacts.SORT_KEY_PRIMARY);
        } else {
            return null;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        int index = 0;
        for (String permission : permissions) {
            if (grantResults[index++] == PackageManager.PERMISSION_GRANTED) {
                //授权通过啦
                if (permission.equals(Manifest.permission.READ_CONTACTS)) {
                    Cursor cursor = getContactsCursor();
                    contactViewPagerAdapter.notifyDataSetChanged(cursor);
                }
            } else {
                //授权拒绝
            }
        }
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
    }

    public void moveViewPagerToFirstPage() {
        if (viewPager.getChildCount() >= 1) {
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
