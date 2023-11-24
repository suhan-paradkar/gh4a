package com.decadroid.activities.home;

import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;

import com.decadroid.R;
import com.decadroid.fragment.SearchFragment;

public class SearchFactory extends FragmentFactory {
    private static final int[] TITLES = new int[] {
        R.string.search
    };

    public SearchFactory(HomeActivity activity) {
        super(activity);
    }

    @Override
    protected @StringRes int getTitleResId() {
        return R.string.search;
    }

    @Override
    protected int[] getTabTitleResIds() {
        return TITLES;
    }

    @Override
    protected Fragment makeFragment(int position) {
        return SearchFragment.newInstance(SearchFragment.SEARCH_TYPE_REPO, null, false);
    }
}
