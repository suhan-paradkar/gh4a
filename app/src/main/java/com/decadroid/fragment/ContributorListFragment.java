package com.decadroid.fragment;

import android.content.Intent;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import com.decadroid.R;
import com.decadroid.ServiceFactory;
import com.decadroid.activities.UserActivity;
import com.decadroid.adapter.ContributorAdapter;
import com.decadroid.adapter.RootAdapter;
import com.meisolsson.githubsdk.model.Page;
import com.meisolsson.githubsdk.model.User;
import com.meisolsson.githubsdk.service.repositories.RepositoryService;

import io.reactivex.Single;
import retrofit2.Response;

public class ContributorListFragment extends PagedDataBaseFragment<User> implements
        RootAdapter.OnItemClickListener<User> {
    public static ContributorListFragment newInstance(String repoOwner, String repoName) {
        ContributorListFragment f = new ContributorListFragment();

        Bundle args = new Bundle();
        args.putString("owner", repoOwner);
        args.putString("repo", repoName);
        f.setArguments(args);

        return f;
    }

    @Override
    protected Single<Response<Page<User>>> loadPage(int page, boolean bypassCache) {
        String repoOwner = getArguments().getString("owner");
        String repoName = getArguments().getString("repo");
        RepositoryService service = ServiceFactory.get(RepositoryService.class, bypassCache);
        return service.getContributors(repoOwner, repoName, page);
    }

    @Override
    protected int getEmptyTextResId() {
        return R.string.no_contributors_found;
    }

    @Override
    protected RootAdapter<User, ? extends RecyclerView.ViewHolder> onCreateAdapter() {
        ContributorAdapter adapter = new ContributorAdapter(getActivity());
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    public void onItemClick(User item) {
        Intent intent = UserActivity.makeIntent(getActivity(), item);
        if (intent != null) {
            startActivity(intent);
        }
    }
}
