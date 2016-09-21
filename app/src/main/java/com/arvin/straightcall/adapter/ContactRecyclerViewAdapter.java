package com.arvin.straightcall.adapter;
import android.net.Uri;
import android.support.v7.widget.RecyclerView;
import android.telephony.TelephonyManager;
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

        public MyViewHolder(View itemView, ContactFragment contactFragment) {
            super(itemView);
            this.contactFragment = contactFragment;
            draweeView = (SimpleDraweeView) itemView.findViewById(R.id.contact_user_img);
            detailView = itemView.findViewById(R.id.contact_datail);
            stateView = detailView.findViewById(R.id.spin_kit);
            stateView.setOnClickListener(v -> {
                Log.d("MyViewHolder","setOnClickListener");
                PhoneUtil.endCall(contactFragment.getActivity());
            });
        }

        private void setDraweeViewImg(String srcFileUriPath, final SimpleDraweeView draweeView) {
            draweeView.post(new Runnable() {
                @Override
                public void run() {
                    Map widthAndHeightMap = getWidthAndHeight(draweeView.getMeasuredWidth(), draweeView.getMeasuredHeight());
                    Uri uri = Uri.parse(srcFileUriPath);
                    Log.d("widthAndHeightMap", widthAndHeightMap.toString() + draweeView.getMeasuredWidth() + "ddd" + draweeView.getMeasuredHeight());
                    ImageRequest request = ImageRequestBuilder.newBuilderWithSource(uri)
                            .setResizeOptions(new ResizeOptions((int) widthAndHeightMap.get("width"), (int) widthAndHeightMap.get("height")))
                            //.setResizeOptions(new ResizeOptions(1000, 1000))
                            .build();
                    DraweeController controller = Fresco.newDraweeControllerBuilder()
                            .setImageRequest(request)
                            .setOldController(draweeView.getController())
                            .setImageRequest(request)
                            .build();
                    draweeView.setController(controller);
                }
            });
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
        public void onPhoneStateChanged(int state, String number) {
            switch (state) {
                case TelephonyManager.CALL_STATE_RINGING:
                    stateView.setVisibility(View.VISIBLE);
                    break;
                case TelephonyManager.CALL_STATE_OFFHOOK:
                    stateView.setVisibility(View.VISIBLE);
                    break;
                case TelephonyManager.CALL_STATE_IDLE:
                    stateView.setVisibility(View.GONE);
                    break;
            }
        }
    }
}
