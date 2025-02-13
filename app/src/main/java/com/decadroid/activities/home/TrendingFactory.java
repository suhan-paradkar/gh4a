package com.decadroid.activities.home;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.decadroid.R;
import com.decadroid.fragment.TrendingFragment;

public class TrendingFactory extends FragmentFactory {
    private static final int[] TAB_TITLES = new int[] {
        R.string.trend_today, R.string.trend_week, R.string.trend_month
    };

    public TrendingFactory(HomeActivity activity) {
        super(activity);
    }

    @Override
    protected @StringRes int getTitleResId() {
        return R.string.trend;
    }

    @Override
    protected int[] getTabTitleResIds() {
        return TAB_TITLES;
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
}
