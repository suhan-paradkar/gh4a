package com.decadroid.activities.home;

import android.content.Context;
import androidx.annotation.StringRes;
import androidx.fragment.app.Fragment;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;

import com.decadroid.Gh4Application;
import com.decadroid.R;
import com.decadroid.ServiceFactory;
import com.decadroid.fragment.PrivateEventListFragment;
import com.decadroid.utils.ApiHelpers;
import com.decadroid.utils.AvatarHandler;
import com.meisolsson.githubsdk.model.User;
import com.meisolsson.githubsdk.service.organizations.OrganizationService;

import java.util.List;

import io.reactivex.disposables.Disposable;

public class NewsFeedFactory extends FragmentFactory implements Spinner.OnItemSelectedListener {
    private final String mUserLogin;
    private User mSelf;
    private User mSelectedOrganization;
    private List<User> mUserScopes;
    private Disposable mOrganizationSubscription;

    private static final int ID_LOADER_ORGS = 100;

    private static final int[] TAB_TITLES = new int[] {
        R.string.user_news_feed
    };

    public NewsFeedFactory(HomeActivity activity, String userLogin) {
        super(activity);
        mUserLogin = userLogin;
    }

    @Override
    public @StringRes int getTitleResId() {
        return R.string.user_news_feed;
    }

    @Override
    protected int[] getTabTitleResIds() {
        return TAB_TITLES;
    }

    @Override
    protected Fragment makeFragment(int position) {
        return PrivateEventListFragment.newInstance(mUserLogin,
                mSelectedOrganization != null ? mSelectedOrganization.login() : null);
    }

    @Override
    protected void setUserInfo(User user) {
        mSelf = user;
        mActivity.supportInvalidateOptionsMenu();
    }

    @Override
    protected void onStartLoadingData() {
        loadOrganizations(false);
    }

    @Override
    protected boolean onCreateOptionsMenu(Menu menu) {
        if (mUserScopes == null || mSelf == null) {
            return super.onCreateOptionsMenu(menu);
        }

        mActivity.getMenuInflater().inflate(R.menu.user_selector, menu);

        Spinner spinner = (Spinner) menu.findItem(R.id.selector).getActionView();
        UserAdapter adapter = new UserAdapter(mActivity, mSelf, mUserScopes);
        spinner.setAdapter(adapter);
        spinner.setGravity(Gravity.RIGHT);
        spinner.setOnItemSelectedListener(this);

        return true;
    }

    @Override
    protected void onRefresh() {
        mSelf = null;
        mUserScopes = null;
        loadOrganizations(true);
        mActivity.supportInvalidateOptionsMenu();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mOrganizationSubscription != null) {
            mOrganizationSubscription.dispose();
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        User selectedOrganization = position != 0 ? mUserScopes.get(position - 1) : null;
        boolean isSameUser = selectedOrganization == null || mSelectedOrganization == null
                ? selectedOrganization == mSelectedOrganization
                : selectedOrganization.equals(mSelectedOrganization);
        if (!isSameUser) {
            mSelectedOrganization = selectedOrganization;
            mActivity.invalidateFragments();
        }

    }

    @Override
    public void onNothingSelected(AdapterView<?> view) {
        if (mSelectedOrganization != null) {
            mSelectedOrganization = null;
            mActivity.invalidateFragments();
        }
    }

    private void loadOrganizations(boolean force) {
        final Gh4Application app = Gh4Application.get();
        final OrganizationService service = ServiceFactory.get(OrganizationService.class, force);
        mOrganizationSubscription = ApiHelpers.PageIterator
                .toSingle(page -> ApiHelpers.loginEquals(mUserLogin, app.getAuthLogin())
                        ? service.getMyOrganizations(page)
                        : service.getUserPublicOrganizations(mUserLogin, page))
                .compose(mActivity.makeLoaderSingle(ID_LOADER_ORGS, force))
                .subscribe(result -> {
                    mUserScopes = result.isEmpty() ? null : result;
                    mActivity.supportInvalidateOptionsMenu();
                }, mActivity::handleLoadFailure);
    }

    private static class UserAdapter extends BaseAdapter {
        private final User mSelf;
        private final List<User> mUsers;
        private final LayoutInflater mInflater;

        public UserAdapter(Context context, User self, List<User> users) {
            super();
            mInflater = LayoutInflater.from(context);
            mSelf = self;
            mUsers = users;
        }

        @Override
        public int getCount() {
            return mUsers.size() + 1;
        }

        @Override
        public User getItem(int position) {
            return position == 0 ? mSelf : mUsers.get(position - 1);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.user_type_small, parent, false);
            }

            User user = getItem(position);
            ImageView avatar = (ImageView) convertView;
            AvatarHandler.assignAvatar(avatar, user);

            return convertView;
        }

        @Override
        public View getDropDownView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.user_type_popup, parent, false);
            }

            User user = getItem(position);

            ImageView avatar = convertView.findViewById(R.id.iv_gravatar);
            AvatarHandler.assignAvatar(avatar, user);

            TextView nameView = convertView.findViewById(R.id.tv_title);
            nameView.setText(user.login());

            return convertView;
        }
    }
}