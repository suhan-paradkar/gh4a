package com.decadroid.activities;

import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.decadroid.BaseFragmentPagerActivity;
import com.decadroid.R;
import com.decadroid.fragment.TrendingFragment;

public class TrendingActivity extends BaseFragmentPagerActivity {
    private static final int[] TITLES = new int[] {
        R.string.trend_today, R.string.trend_week, R.string.trend_month
    };

    @Nullable
    @Override
    protected String getActionBarTitle() {
        return getString(R.string.trend);
    }

    @Override
    protected int[] getTabTitleResIds() {
        return TITLES;
    }

    @Override
    protected Fragment makeFragment(int position) {
        switch (position) {
            case 0: return TrendingFragment.newInstance(TrendingFragment.TYPE_DAILY);
            case 1: return TrendingFragment.newInstance(TrendingFragment.TYPE_WEEKLY);
            case 2: return TrendingFragment.newInstance(TrendingFragment.TYPE_MONTHLY);
        }
        return null;
    }

    @Override
    protected Intent navigateUp() {
        return getToplevelActivityIntent();
    }
}
