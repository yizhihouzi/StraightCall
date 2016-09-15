package com.arvin.straightcall.application;

import android.app.Application;

import com.facebook.common.logging.FLog;
import com.facebook.drawee.backends.pipeline.Fresco;
import com.facebook.imagepipeline.core.ImagePipelineConfig;
import com.facebook.imagepipeline.listener.RequestListener;
import com.facebook.imagepipeline.listener.RequestLoggingListener;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by arvin on 2016/8/27.
 */
public class StraightApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        Set<RequestListener> requestListeners = new HashSet<>();
        requestListeners.add(new RequestLoggingListener());
        ImagePipelineConfig config = ImagePipelineConfig.newBuilder(this)
                .setRequestListeners(requestListeners)
                .setDownsampleEnabled(true)
                .build();
        Fresco.initialize(this, config);
//        FLog.setMinimumLoggingLevel(FLog.VERBOSE);
    }
}
