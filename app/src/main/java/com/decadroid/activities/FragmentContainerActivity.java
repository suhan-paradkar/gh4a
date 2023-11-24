package com.decadroid.activities;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.decadroid.BaseActivity;
import com.decadroid.R;
import com.decadroid.fragment.LoadingFragmentBase;
import com.decadroid.widget.SwipeRefreshLayout;

public abstract class FragmentContainerActivity extends BaseActivity {
    private Fragment mFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final FragmentManager fm = getSupportFragmentManager();
        if (savedInstanceState == null) {
            mFragment = onCreateFragment();
            fm.beginTransaction().add(R.id.content_container, mFragment).commit();
        } else {
            mFragment = fm.findFragmentById(R.id.content_container);
        }

        if (mFragment instanceof SwipeRefreshLayout.ChildScrollDelegate) {
            setChildScrollDelegate((SwipeRefreshLayout.ChildScrollDelegate) mFragment);
        }
    }

    protected abstract Fragment onCreateFragment();

    protected Fragment getFragment() {
        return mFragment;
    }

    @Override
    public void onRefresh() {
        super.onRefresh();
        if (mFragment instanceof RefreshableChild) {
            ((RefreshableChild) mFragment).onRefresh();
        }
    }

    @Override
    public void onBackPressed() {
        if (mFragment instanceof LoadingFragmentBase
                && ((LoadingFragmentBase) mFragment).onBackPressed()) {
            return;
        }
        super.onBackPressed();
    }
}