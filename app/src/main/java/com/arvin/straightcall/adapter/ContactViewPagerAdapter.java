package com.arvin.straightcall.adapter;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.arvin.straightcall.R;
import com.arvin.straightcall.bean.Contact;
import com.arvin.straightcall.fragment.ContactFragment;

import java.util.List;

public class ContactViewPagerAdapter extends FragmentStatePagerAdapter {
    private final Activity activity;
    private List<Contact> contactList;

    public ContactViewPagerAdapter(FragmentActivity activity, List<Contact> contactList) {
        super(activity.getSupportFragmentManager());
        this.activity = activity;
        this.contactList = contactList;
    }

    public void notifyDataSetChanged(List<Contact> contactList) {
        this.contactList = contactList;
        super.notifyDataSetChanged();
    }

    public String getContactName(int position) {
        Contact contact = contactList.get(position);
        String phoneNum = contact.getPhone_num();
        phoneNum = phoneNum.replace(" ", "").replace("-", "").replace("+86", "");
        String name = contact.getName();
        if (!(phoneNum.startsWith("1") && phoneNum.length() == 11)) {
            name = name + activity.getString(R.string.home_tel);
        }
        return name;
    }

    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        Contact contact = contactList.get(position);
        return ContactFragment.newInstance(contact);
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        return contactList.size();
    }
}
