package com.arvin.straightcall.layoutmanager;

import android.content.Context;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;

/**
 * Created by arvin on 2016/9/3.
 */
public class SlowLinearLayoutManager extends LinearLayoutManager {
    private double speedRatio = 1;

    public SlowLinearLayoutManager(Context context) {
        super(context);
    }

    public SlowLinearLayoutManager(Context context, int orientation, boolean reverseLayout) {
        super(context, orientation, reverseLayout);
    }

    public SlowLinearLayoutManager(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int scrollVerticallyBy(int dy, RecyclerView.Recycler recycler, RecyclerView.State state) {
        int a = super.scrollVerticallyBy((int) (speedRatio * dy), recycler, state);//屏蔽之后无滑动效果，证明滑动的效果就是由这个函数实现
        if (a == (int) (speedRatio * dy)) {
            return dy;
        }
        return a;
    }


    public void setSpeedRatio(double speedRatio) {
        this.speedRatio = speedRatio;
    }

}
