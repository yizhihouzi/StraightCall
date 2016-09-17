package com.arvin.straightcall.adapter;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentStatePagerAdapter;

import com.arvin.straightcall.R;
import com.arvin.straightcall.bean.Contact;
import com.arvin.straightcall.fragment.ContactFragment;

public class ContactViewPagerAdapter extends FragmentStatePagerAdapter {
    private final Activity activity;
    //    private List contactList;
    private Cursor contactCursor;

    public ContactViewPagerAdapter(FragmentActivity activity, Cursor contactCursor) {
        super(activity.getSupportFragmentManager());
//        contactList = getAllRecords(activity);
        this.activity = activity;
        this.contactCursor = contactCursor;
    }

    public void notifyDataSetChanged(Cursor contactCursor) {
        this.contactCursor = contactCursor;
        super.notifyDataSetChanged();
    }

    public String getContactName(int position) {
        contactCursor.moveToPosition(position);
        String phoneNum = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        phoneNum = phoneNum.replace(" ", "").replace("-", "").replace("+86", "");
        String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        if (!(phoneNum.startsWith("1") && phoneNum.length() == 11)) {
            name = name + activity.getString(R.string.home_tel);
        }
        return name;
    }

    private Cursor getContacts() {
        //①查询raw_contacts表获得联系人的id
        ContentResolver resolver = activity.getContentResolver();
        Uri uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI;
        //查询联系人数据
        return resolver.query(uri, null, null, null,
                android.provider.ContactsContract.Contacts.SORT_KEY_PRIMARY);
    }


    /**
     * Return the Fragment associated with a specified position.
     *
     * @param position
     */
    @Override
    public Fragment getItem(int position) {
        contactCursor.moveToPosition(position);
        String phoneNum = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
        phoneNum = phoneNum.replace(" ", "").replace("-", "").replace("+86", "");
        String name = contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
        Contact contact = new Contact((long) position, name, phoneNum,
                contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.PHOTO_URI)), null);
        //        Log.d("contact",contactCursor.getString(contactCursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.)));
        /*long photoId = contactCursor.getLong(contactCursor.getColumnIndex(
                ContactsContract.Contacts.PHOTO_ID));*/
        /*String photoUri = contactCursor.getString(contactCursor.getColumnIndex(
                ContactsContract.Contacts.PHOTO_URI));*/
        /*String photoThumbUri = contactCursor.getString(contactCursor.getColumnIndex(
                ContactsContract.Contacts.PHOTO_THUMBNAIL_URI));*/
        /*Log.d("photo_uri", (photoUri != null) ? photoUri : "null");
        Log.d("photoThumbUri", (photoThumbUri != null) ? photoThumbUri : "null");
        Log.d("photoId", photoId + "");*/
        return ContactFragment.newInstance(contact);
//        return ContactFragment.newInstance((Contact) contactList.get(position));
    }

    /**
     * Return the number of views available.
     */
    @Override
    public int getCount() {
        if (contactCursor != null) {
            return contactCursor.getCount();
        } else {
            return 0;
        }
    }
}
