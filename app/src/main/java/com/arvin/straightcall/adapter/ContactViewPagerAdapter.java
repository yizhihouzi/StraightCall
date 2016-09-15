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
    Cursor contactCursor;

    public ContactViewPagerAdapter(FragmentActivity activity) {
        super(activity.getSupportFragmentManager());
//        contactList = getAllRecords(activity);
        this.activity = activity;
        contactCursor = getContacts();
    }

    @Override
    public void notifyDataSetChanged() {
//        contactList = getAllRecords(activity);
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
/*
        while (cursor.moveToNext()) {
            //获取联系人姓名,手机号码
            String cName = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME));
            String cNum = cursor.getString(cursor.getColumnIndex(ContactsContract.CommonDataKinds.Phone.NUMBER));
            System.out.println("姓名:" + cName);
            System.out.println("号码:" + cNum);
            System.out.println("======================");
        }
        cursor.close();*/
    }

    /*List getAllRecords(Context context) {
        // 第一个参数为 activity，第二个参数为数据库表名，第三个参数通常为 null
        DaoMaster.DevOpenHelper mHelper = new DaoMaster.DevOpenHelper(context, "straightcall-db", null);
        SQLiteDatabase db = mHelper.getWritableDatabase();
        DaoMaster mDaoMaster = new DaoMaster(db);
        DaoSession mDaoSession = mDaoMaster.newSession();
        // 得到 Dao 对象，数据库的 CRUD 操作都是通过此对象来进行
        ContactDao mContactDao = mDaoSession.getContactDao();
        if (mContactDao.count() == 4) {
            mContactDao.deleteAll();
        }
        Log.d("num", String.valueOf(mContactDao.count()));
        return mContactDao.loadAll();
    }*/

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
        return contactCursor.getCount();
    }
}
