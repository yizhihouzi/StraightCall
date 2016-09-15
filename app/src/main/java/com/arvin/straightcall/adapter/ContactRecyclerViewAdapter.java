package com.arvin.straightcall.adapter;

import android.content.Context;
import android.media.AudioManager;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.arvin.straightcall.R;
import com.arvin.straightcall.bean.Contact;
import com.arvin.straightcall.fragment.ContactFragment;
import com.arvin.straightcall.util.PhoneUtil;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.drawee.interfaces.DraweeController;
import com.facebook.drawee.view.SimpleDraweeView;
import com.facebook.imagepipeline.common.ResizeOptions;
import com.facebook.imagepipeline.request.ImageRequest;
import com.facebook.imagepipeline.request.ImageRequestBuilder;
import com.litesuits.common.receiver.PhoneReceiver;

import java.util.HashMap;
import java.util.Map;

public class ContactRecyclerViewAdapter extends RecyclerView.Adapter {
    private final Contact contactInfo;
    private ContactFragment contactFragment;

    public ContactRecyclerViewAdapter(ContactFragment fragment, Contact contactInfo) {
        this.contactFragment = fragment;
        this.contactInfo = contactInfo;
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.contact_item, parent, false);
        return new MyViewHolder(v, contactFragment);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        ((MyViewHolder) holder).autoDisplay(position, contactInfo);
    }

    /**
     * Returns the total number of items in the data set hold by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 2;
    }

    private static class MyViewHolder extends RecyclerView.ViewHolder implements ContactFragment.PhoneListener {
        SimpleDraweeView draweeView;
        View detailView;
        View stateView;
        ContactFragment contactFragment;
        private AudioManager audioManager;

        public MyViewHolder(View itemView, ContactFragment contactFragment) {
            super(itemView);
            Log.d("widthAndHeightMap", itemView.getMeasuredWidth() + "ddd" + itemView.getMeasuredHeight());
            this.contactFragment = contactFragment;
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.contact_user_img);
            detailView = itemView.findViewById(R.id.contact_datail);
            stateView = detailView.findViewById(R.id.spin_kit);
            audioManager =
                    (AudioManager) contactFragment.getActivity().getSystemService(Context.AUDIO_SERVICE);
            stateView.setOnClickListener(v -> {
                PhoneUtil.endCall(contactFragment.getActivity());
            });
//            TextView contactIndex = ((TextView) detailView.findViewById(R.id.contact_index));
//            contactIndex.setText(TEL_PHONE);
//            contactIndex.setOnClickListener(
//                    v -> {
//                        Toast.makeText(context, TEL_PHONE, Toast.LENGTH_LONG).show();
//                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
//                            if (!ActivityCompat.shouldShowRequestPermissionRationale((Activity) context, Manifest.permission.CALL_PHONE)) {
//                                ActivityCompat.requestPermissions((Activity) context, new String[]{Manifest.permission.CALL_PHONE}, contactFragment.CONTACT_INDEX);
//                            } else {
//                                new MaterialDialog.Builder(context)
//                                        .content("主人，您没有授权我打电话！")
//                                        .positiveText("知道了")
//                                        .positiveColorRes(R.color.colorPrimary)
//                                        .show();
//                            }
//                        } else {
//                            contactFragment.callPhone(TEL_PHONE, (Activity) context);
//                        }
//                    });
        }


        /**
         * 扬声器与听筒切换
         *
         * @param isSpeakerphoneOn
         */
        public void setSpeakerphoneOn(boolean isSpeakerphoneOn) {
            audioManager.setSpeakerphoneOn(isSpeakerphoneOn);
            if (!isSpeakerphoneOn) {
                audioManager.setMode(AudioManager.MODE_NORMAL);
            }
        }

        private void setDraweeViewImg(String srcFileUriPath, SimpleDraweeView draweeView) {
            Map widthAndHeightMap = getWidthAndHeight(draweeView.getMeasuredWidth(), draweeView.getMeasuredHeight());
            Uri uri = Uri.parse(srcFileUriPath);
            Log.d("widthAndHeightMap", widthAndHeightMap.toString() + draweeView.getMeasuredWidth() + "ddd" + draweeView.getMeasuredHeight());
            ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                    //.setResizeOptions(new ResizeOptions((int) widthAndHeightMap.get("width"), (int) widthAndHeightMap.get("height")))
                    .setResizeOptions(new ResizeOptions(1000, 1000))
                    .build();
            DraweeController controller = Fresco.newDraweeControllerBuilder()
                    .setImageRequest(request)
                    .setOldController(draweeView.getController())
                    .setImageRequest(request)
                    .build();
            draweeView.setController(controller);
        }

        private Map getWidthAndHeight(int width, int height) {
            Map<String, Integer> map = new HashMap<>();
            map.put("width", width);
            map.put("height", height);
            if ((width + height) > 4096) {
                double proportion = width / height;
                if (proportion > 1) {
                    map.put("width", 2048);
                    map.put("height", (int) (2048 / proportion));
                } else {
                    map.put("height", 2048);
                    map.put("width", (int) (2048 * proportion));
                }
            }
            return map;
        }

        public void autoDisplay(int position, Contact contactInfo) {
            if (position % 2 == 0) {
                draweeView.setVisibility(View.VISIBLE);
                detailView.setVisibility(View.GONE);
                String imgUrl = contactInfo.getPhoto_native_url();
                if (imgUrl != null && !imgUrl.trim().equals("")) {
                    setDraweeViewImg(imgUrl, draweeView);
                }
            } else {
                detailView.setVisibility(View.VISIBLE);
                contactFragment.setPhoneListener(this);
                draweeView.setVisibility(View.GONE);
            }
        }

        @Override
        public void onPhoneStateChanged(PhoneReceiver.CallState state, String number) {
            switch (state) {
                case Outgoing:
                    stateView.setVisibility(View.VISIBLE);
                    break;
                case OutgoingEnd:
                    stateView.setVisibility(View.GONE);
                    break;
                case Incoming:
                    stateView.setVisibility(View.VISIBLE);
                    break;
                case IncomingEnd:
                    stateView.setVisibility(View.GONE);
                    break;
                case IncomingRing:
                    stateView.setVisibility(View.VISIBLE);
                    break;
            }
            Log.d("callstate", state.toString());
        }
    }
}
