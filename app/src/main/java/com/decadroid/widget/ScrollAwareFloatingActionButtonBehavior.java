package com.decadroid.widget;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import androidx.core.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import com.decadroid.R;

public class ScrollAwareFloatingActionButtonBehavior extends FloatingActionButton.Behavior {
    private final FloatingActionButton.OnVisibilityChangedListener mVisibilityChangedListener =
            new FloatingActionButton.OnVisibilityChangedListener() {
        @Override
        public void onHidden(FloatingActionButton fab) {
            super.onHidden(fab);
            fab.setVisibility(View.INVISIBLE);
        }
    };

    public ScrollAwareFloatingActionButtonBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    public boolean onStartNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
            @NonNull FloatingActionButton child, @NonNull View directTargetChild,
            @NonNull View target, int axes, int type) {
        if (target.getTag(R.id.FloatingActionButtonScrollEnabled) == null) {
            return super.onStartNestedScroll(coordinatorLayout, child, directTargetChild, target,
                    axes, type);
        }
        return axes == ViewCompat.SCROLL_AXIS_VERTICAL;
    }

    @Override
    public void onNestedScroll(@NonNull CoordinatorLayout coordinatorLayout,
            @NonNull FloatingActionButton child, @NonNull View target,
            int dxConsumed, int dyConsumed, int dxUnconsumed, int dyUnconsumed,
            int type, @NonNull int[] consumed) {
        super.onNestedScroll(coordinatorLayout, child, target, dxConsumed, dyConsumed, dxUnconsumed, dyUnconsumed, type, consumed);

        if (dyConsumed > 0 && child.getVisibility() == View.VISIBLE) {
            child.hide(mVisibilityChangedListener);
        } else if (dyConsumed < 0 && child.getVisibility() != View.VISIBLE) {
            child.show();
        }
    }
}
