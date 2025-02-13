package com.decadroid.fragment;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.decadroid.R;
import com.decadroid.ServiceFactory;
import com.decadroid.activities.RepositoryActivity;
import com.decadroid.adapter.RepositoryAdapter;
import com.decadroid.adapter.RootAdapter;
import com.decadroid.utils.ApiHelpers;
import com.decadroid.utils.RxUtils;
import com.meisolsson.githubsdk.model.Page;
import com.meisolsson.githubsdk.model.Repository;
import com.meisolsson.githubsdk.service.search.SearchService;

import io.reactivex.Single;
import retrofit2.Response;

public class RepositorySearchFragment extends PagedDataBaseFragment<Repository> {
    public static RepositorySearchFragment newInstance(String userLogin) {
        RepositorySearchFragment f = new RepositorySearchFragment();

        Bundle args = new Bundle();
        args.putString("user", userLogin);
        f.setArguments(args);

        return f;
    }

    public void setQuery(String query) {
        getArguments().putString("query", query);
        if (isAdded()) {
            onRefresh();
        }
    }

    @Override
    protected Single<Response<Page<Repository>>> loadPage(int page, boolean bypassCache) {
        String login = getArguments().getString("user");
        String query = getArguments().getString("query");

        if (TextUtils.isEmpty(query)) {
            return Single.just(Response.success(new ApiHelpers.DummyPage<>()));
        }

        SearchService service = ServiceFactory.get(SearchService.class, bypassCache);
        String params = query + " fork:true user:" + login;

        return service.searchRepositories(params, null, null, page)
                .compose(RxUtils::searchPageAdapter)
                // With that status code, Github wants to tell us there are no
                // repositories to search in. Just pretend no error and return
                // an empty list in that case.
                .compose(RxUtils.mapFailureToValue(422, Response.success(new ApiHelpers.DummyPage<>())));
    }

    @Override
    protected int getEmptyTextResId() {
        return R.string.no_search_repos_found;
    }

    @Override
    protected RootAdapter<Repository, ? extends RecyclerView.ViewHolder> onCreateAdapter() {
        return new RepositoryAdapter(getActivity());
    }

    @Override
    public void onItemClick(Repository item) {
        startActivity(RepositoryActivity.makeIntent(getActivity(), item));
    }
}
