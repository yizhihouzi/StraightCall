package com.arvin.straightcall.view;

import android.content.Context;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by arvin on 2016/9/3.
 */
public class SlowFlingRecyclerView extends RecyclerView {
    private double scale = 1;               //抛掷速度的缩放因子

    public SlowFlingRecyclerView(Context context) {
        this(context, null);
    }

    public SlowFlingRecyclerView(Context context, @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlowFlingRecyclerView(Context context, @Nullable AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setflingScale(double scale) {
        this.scale = scale;
    }

    @Override
    public int getScrollState() {
        return super.getScrollState();
    }

    @Override
    public boolean fling(int velocityX, int velocityY) {
        velocityY *= scale;
        return super.fling(velocityX, velocityY);
    }
}
