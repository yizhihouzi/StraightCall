package com.arvin.straightcall.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.arvin.straightcall.R;
import com.arvin.straightcall.util.PhoneUtil;

public class PhoneStateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    public static String TAG = "PhoneStateFragment";

    public PhoneStateFragment() {
        // Required empty public constructor
    }

    public static PhoneStateFragment newInstance() {
        return new PhoneStateFragment();
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_phone_control, container, false);
        View stateView = view.findViewById(R.id.spin_kit);
        stateView.setOnClickListener(v -> PhoneUtil.endCall(getActivity()));
        return view;
    }

    @Override
    public void onPause() {
        Log.d("PhoneStateFragment", "onPause");
        super.onPause();
    }

    @Override
    public void onStart() {
        Log.d("PhoneStateFragment", "onStart");
        super.onStart();
    }
}
