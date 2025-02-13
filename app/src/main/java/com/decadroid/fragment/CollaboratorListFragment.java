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
import com.meisolsson.githubsdk.service.repositories.RepositoryCollaboratorService;


import io.reactivex.Single;
import retrofit2.Response;

public class CollaboratorListFragment extends PagedDataBaseFragment<User> implements
        RootAdapter.OnItemClickListener<User> {
    public static CollaboratorListFragment newInstance(String owner, String repo) {
        CollaboratorListFragment f = new CollaboratorListFragment();
        Bundle args = new Bundle();
        args.putString("owner", owner);
        args.putString("repo", repo);
        f.setArguments(args);
        return f;
    }

    @Override
    protected Single<Response<Page<User>>> loadPage(int page, boolean bypassCache) {
        String owner = getArguments().getString("owner");
        String repo = getArguments().getString("repo");
        final RepositoryCollaboratorService service =
                ServiceFactory.get(RepositoryCollaboratorService.class, bypassCache);
        return service.getCollaborators(owner, repo, page);
    }

    @Override
    protected RootAdapter<User, ? extends RecyclerView.ViewHolder> onCreateAdapter() {
        UserAdapter adapter = new UserAdapter(getActivity());
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected int getEmptyTextResId() {
        return R.string.no_collaborators_found;
    }

    @Override
    public void onItemClick(User item) {
        startActivity(UserActivity.makeIntent(getActivity(), item));
    }
}
