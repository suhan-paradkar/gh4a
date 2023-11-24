package com.decadroid.resolver;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.VisibleForTesting;
import androidx.fragment.app.FragmentActivity;

import com.decadroid.activities.FollowerFollowingListActivity;
import com.decadroid.activities.UserActivity;
import com.meisolsson.githubsdk.model.User;
import com.meisolsson.githubsdk.model.UserType;

public class UserFollowersLoadTask extends UserLoadTask {
    @VisibleForTesting
    protected final boolean mShowFollowers;

    public UserFollowersLoadTask(FragmentActivity activity, Uri urlToResolve,
            String userLogin, boolean showFollowers) {
        super(activity, urlToResolve, userLogin);
        mShowFollowers = showFollowers;
    }

    @Override
    protected Intent getIntent(User user) {
        if (user.type() == UserType.Organization) {
            return UserActivity.makeIntent(mActivity, user);
        }
        return FollowerFollowingListActivity.makeIntent(mActivity, user.login(), mShowFollowers);
    }
}
