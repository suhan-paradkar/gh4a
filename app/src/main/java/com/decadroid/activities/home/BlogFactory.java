package com.decadroid.activities.home;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.decadroid.R;
import com.decadroid.fragment.BlogListFragment;

public class BlogFactory extends FragmentFactory {
    private static final int[] TAB_TITLES = new int[] {
        R.string.blog
    };

    public BlogFactory(HomeActivity activity) {
        super(activity);
    }

    @Override
    protected @StringRes int getTitleResId() {
        return R.string.blog;
    }

    @Override
    protected int[] getTabTitleResIds() {
        return TAB_TITLES;
    }

    @Override
    protected Fragment makeFragment(int position) {
        return BlogListFragment.newInstance();
    }
}
