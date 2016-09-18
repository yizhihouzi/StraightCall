package com.arvin.straightcall.fragment;

import android.app.Activity;
import android.content.pm.PackageManager;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;

import com.afollestad.materialdialogs.MaterialDialog;
import com.arvin.straightcall.R;

public class BaseFragment extends Fragment {

    protected boolean checkPermission(String permission, final int requestCode, String tips) {
        Activity context = getActivity();
        if (ActivityCompat.checkSelfPermission(context, permission) != PackageManager.PERMISSION_GRANTED) {
            if (!ActivityCompat.shouldShowRequestPermissionRationale(context, permission)) {
                requestPermissions(new String[]{permission}, requestCode);
            } else {
                new MaterialDialog.Builder(context)
                        .content(tips)
                        .positiveText(context.getResources().getString(R.string.yes_know))
                        .positiveColorRes(R.color.colorPrimary)
                        .show();
            }
            return false;
        } else {
            return true;
        }
    }

}
