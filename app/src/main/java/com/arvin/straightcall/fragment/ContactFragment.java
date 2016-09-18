package com.arvin.straightcall.fragment;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.FragmentManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.arvin.straightcall.activity.CallActivity;
import com.arvin.straightcall.bean.Contact;
import com.arvin.straightcall.layoutmanager.SlowLinearLayoutManager;
import com.arvin.straightcall.R;
import com.arvin.straightcall.adapter.ContactRecyclerViewAdapter;
import com.arvin.straightcall.util.PhoneUtil;
import com.arvin.straightcall.view.SlowFlingRecyclerView;
import com.litesuits.common.receiver.PhoneReceiver;

public class ContactFragment extends BaseFragment implements CallActivity.PhoneStateListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String CONTACT_INOF = "param1";
    public static String TAG = "ContactFragment";
    RecyclerView mRecyclerView;
    private Contact contactInfo;
    private FragmentManager fragmentManager;
    private PhoneListener phoneListener;
    private static final int PERMISSIONS_REQUEST_CALL = 100;

    public ContactFragment() {
        // Required empty public constructor
    }

    public static ContactFragment newInstance(Contact param1) {
        ContactFragment fragment = new ContactFragment();
        Bundle args = new Bundle();
        args.putSerializable(CONTACT_INOF, param1);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentManager = getFragmentManager();
        if (getArguments() != null) {
            contactInfo = (Contact) getArguments().getSerializable(CONTACT_INOF);
        }
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_contact, container, false);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.contact_control);
        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    if (!recyclerView.canScrollVertically(1)) {
                        //onScrollToBottom();
                        if (checkPermission(Manifest.permission.CALL_PHONE, PERMISSIONS_REQUEST_CALL, getActivity().getString(R.string.call_phone_tip))) {
                            PhoneUtil.callPhone(getActivity(), contactInfo.getPhone_num());
                        }
                    }
                    if (!recyclerView.canScrollVertically(-1)) {
                        //onScrollToTop();
                        PhoneUtil.endCall(getActivity());
                    }
                }
            }
        });
        ((SlowFlingRecyclerView) mRecyclerView).setflingScale(0.7);
        SlowLinearLayoutManager linearLayoutManager = new SlowLinearLayoutManager(getActivity());
        linearLayoutManager.setSpeedRatio(1.1);
        mRecyclerView.setLayoutManager(linearLayoutManager);//这里用线性显示 类似于listview
        RecyclerView.Adapter adapter = new ContactRecyclerViewAdapter(this, contactInfo);
//        AlphaInAnimationAdapter alphaAdapter = new AlphaInAnimationAdapter(adapter);
//        alphaAdapter.setDuration(900);
//        alphaAdapter.setFirstOnly(false);
//        ScaleInAnimationAdapter scaleInAnimationAdapter=new ScaleInAnimationAdapter(alphaAdapter);
//        scaleInAnimationAdapter.setDuration(800);
//        scaleInAnimationAdapter.setStartPosition(1);
//        mRecyclerView.setAdapter(scaleInAnimationAdapter);
        mRecyclerView.setAdapter(adapter);
        //联系人详情
        LinearLayout contactDetail = (LinearLayout) view.findViewById(R.id.contact_datail);
        setContactDetail(contactDetail, contactInfo);
        ((CallActivity) getActivity()).registerPhoneStateListener(this, this);
        return view;
    }

    @Override
    public void onDestroyView() {
        ((CallActivity) getActivity()).unRegisterPhoneStateListener(this);
        super.onDestroyView();
    }

    private void setContactDetail(LinearLayout detailView, Contact contactInfo) {
        TextView contactName = ((TextView) detailView.findViewById(R.id.contact_name));
        contactName.setText(contactInfo.getName());
        TextView contactIndex = ((TextView) detailView.findViewById(R.id.contact_telnum));
        contactIndex.setText(contactInfo.getPhone_num());
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        int index = 0;
        for (String permission : permissions) {
            if (grantResults[index++] == PackageManager.PERMISSION_GRANTED) {
                //授权通过啦
                if (permission.equals(Manifest.permission.CALL_PHONE)) {
                    PhoneUtil.callPhone(getActivity(), contactInfo.getPhone_num());
                }
            }/* else {
                //授权拒绝
            }*/
        }
    }

    public void setPhoneListener(PhoneListener phoneListener) {
        this.phoneListener = phoneListener;
    }

    @Override
    public void onPhoneStateChanged(PhoneReceiver.CallState state, String number) {
        CallFragment callFragment = (CallFragment) fragmentManager.findFragmentByTag(CallFragment.TAG);
        switch (state) {
            case Outgoing:
                callFragment.setViewPagerPaging(false);
                break;
            case OutgoingEnd:
                callFragment.setViewPagerPaging(true);
                mRecyclerView.smoothScrollToPosition(0);
                break;
            case Incoming:
                callFragment.setViewPagerPaging(false);
                break;
            case IncomingEnd:
                callFragment.setViewPagerPaging(true);
                mRecyclerView.smoothScrollToPosition(0);
                break;
            case IncomingRing:
                callFragment.setViewPagerPaging(false);
                break;
        }
        if (phoneListener != null) {
            phoneListener.onPhoneStateChanged(state, number);
        }
    }

    public interface PhoneListener {
        void onPhoneStateChanged(PhoneReceiver.CallState state, String number);
    }
}
