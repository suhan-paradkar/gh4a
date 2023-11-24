package com.decadroid.widget;

import android.content.Context;
import android.content.res.TypedArray;
import androidx.annotation.Nullable;
import androidx.core.widget.NestedScrollView;
import android.util.AttributeSet;

import com.decadroid.R;
import com.decadroid.utils.UiUtils;

public class HeightLimitedNestedScrollView extends NestedScrollView {
    private int mMaxHeight = -1;

    public HeightLimitedNestedScrollView(Context context) {
        super(context);
    }

    public HeightLimitedNestedScrollView(Context context,
            @Nullable AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HeightLimitedNestedScrollView(Context context,
            @Nullable AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        if (attrs != null) {
            TypedArray a = context.obtainStyledAttributes(attrs,
                    R.styleable.HeightLimitedNestedScrollView, defStyleAttr, 0);
            int n = a.getIndexCount();

            for (int i = 0; i < n; i++) {
                int attr = a.getIndex(i);

                switch (attr) {
                    case R.styleable.HeightLimitedNestedScrollView_maxHeight:
                        mMaxHeight = a.getDimensionPixelSize(attr, -1);
                        break;
                }
            }
            a.recycle();
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        if (mMaxHeight > 0) {
            heightMeasureSpec = UiUtils.limitViewHeight(heightMeasureSpec, mMaxHeight);
        }

        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }
}
