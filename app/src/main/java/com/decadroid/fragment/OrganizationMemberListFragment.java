package com.decadroid.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import com.decadroid.R;
import com.decadroid.ServiceFactory;
import com.decadroid.activities.UserActivity;
import com.decadroid.adapter.RootAdapter;
import com.decadroid.adapter.UserAdapter;
import com.meisolsson.githubsdk.model.Page;
import com.meisolsson.githubsdk.model.User;
import com.meisolsson.githubsdk.service.organizations.OrganizationMemberService;

import io.reactivex.Single;
import retrofit2.Response;

public class OrganizationMemberListFragment extends PagedDataBaseFragment<User> implements
        RootAdapter.OnItemClickListener<User> {
    public static OrganizationMemberListFragment newInstance(String organization) {
        OrganizationMemberListFragment f = new OrganizationMemberListFragment();
        Bundle args = new Bundle();
        args.putString("org", organization);
        f.setArguments(args);
        return f;
    }

    @Override
    protected Single<Response<Page<User>>> loadPage(int page, boolean bypassCache) {
        String organization = getArguments().getString("org");
        final OrganizationMemberService service =
                ServiceFactory.get(OrganizationMemberService.class, bypassCache);
        return service.getMembers(organization, page);
    }

    @Override
    protected RootAdapter<User, ? extends RecyclerView.ViewHolder> onCreateAdapter() {
        UserAdapter adapter = new UserAdapter(getActivity());
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected int getEmptyTextResId() {
        return R.string.no_org_members_found;
    }

    @Override
    public void onItemClick(User item) {
        startActivity(UserActivity.makeIntent(getActivity(), item));
    }
}
