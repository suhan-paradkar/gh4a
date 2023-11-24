package com.decadroid.activities;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.decadroid.R;
import com.decadroid.fragment.BookmarkListFragment;

public class BookmarkListActivity extends FragmentContainerActivity {
    @Nullable
    @Override
    protected String getActionBarTitle() {
        return getString(R.string.bookmarks);
    }

    @Override
    protected Fragment onCreateFragment() {
        return BookmarkListFragment.newInstance();
    }

    @Override
    protected boolean canSwipeToRefresh() {
        // content can't change while we're in foreground
        return false;
    }
}