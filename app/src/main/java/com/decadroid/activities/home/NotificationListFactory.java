package com.decadroid.activities.home;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.decadroid.R;
import com.decadroid.fragment.NotificationListFragment;

public class NotificationListFactory extends FragmentFactory {
    private static final int[] TAB_TITLES =  new int[] {
        R.string.notifications
    };

    protected NotificationListFactory(HomeActivity activity) {
        super(activity);
    }

    @Override
    protected @StringRes int getTitleResId() {
        return R.string.notifications;
    }

    @Override
    protected int[] getTabTitleResIds() {
        return TAB_TITLES;
    }

    @Override
    protected Fragment makeFragment(int position) {
        return NotificationListFragment.newInstance();
    }
}