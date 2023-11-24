package com.decadroid.resolver;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.FragmentActivity;

import com.decadroid.activities.RepositoryListActivity;
import com.decadroid.fragment.RepositoryListContainerFragment;
import com.meisolsson.githubsdk.model.User;
import com.meisolsson.githubsdk.model.UserType;

public class UserReposLoadTask extends UserLoadTask {
    @VisibleForTesting
    protected final boolean mShowStars;

    public UserReposLoadTask(FragmentActivity activity, Uri urlToResolve,
            String userLogin, boolean showStars) {
        super(activity, urlToResolve, userLogin);
        mShowStars = showStars;
    }

    @Override
    protected Intent getIntent(User user) {
        boolean isOrg = user.type() == UserType.Organization;
        String filter = mShowStars && !isOrg
                ? RepositoryListContainerFragment.FILTER_TYPE_STARRED
                : null;
        return RepositoryListActivity.makeIntent(mActivity, user.login(), isOrg, filter);
    }
}
