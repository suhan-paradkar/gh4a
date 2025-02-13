/*
 * Copyright 2011 Azwan Adli Abdullah
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.decadroid.fragment;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import android.text.TextUtils;

import com.decadroid.Gh4Application;
import com.decadroid.R;
import com.decadroid.ServiceFactory;
import com.decadroid.activities.RepositoryActivity;
import com.decadroid.adapter.RepositoryAdapter;
import com.decadroid.adapter.RootAdapter;
import com.decadroid.utils.ApiHelpers;
import com.meisolsson.githubsdk.model.Page;
import com.meisolsson.githubsdk.model.Repository;
import com.meisolsson.githubsdk.service.repositories.RepositoryService;

import io.reactivex.Single;
import retrofit2.Response;

public class RepositoryListFragment extends PagedDataBaseFragment<Repository> {
    private String mLogin;
    private String mRepoType;
    private final Map<String, String> mFilterData = new HashMap<>();

    public static RepositoryListFragment newInstance(String login, boolean isOrg,
            String repoType, String sortOrder, String sortDirection) {
        RepositoryListFragment f = new RepositoryListFragment();

        Bundle args = new Bundle();
        args.putString("user", login);
        args.putBoolean("is_org", isOrg);
        args.putString("repo_type", repoType);
        args.putString("sort_order", sortOrder);
        args.putString("sort_direction", sortDirection);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        final Bundle args = getArguments();

        mLogin = args.getString("user");
        mRepoType = args.getString("repo_type");

        final boolean isSelf = ApiHelpers.loginEquals(mLogin, Gh4Application.get().getAuthLogin());

        // We're operating on the limit of what Github's repo API supports. Specifically,
        // it doesn't support sorting for the organization repo list endpoint, so we're using
        // the user repo list endpoint for organizations as well. Doing so has a few quirks though:
        // - the 'all' filter returns an empty list when querying organization repos, so we
        //   need to omit the filter in that case
        // - 'sources' and 'forks' filter types are only supported for the org repo list endpoint,
        //   but not for the user repo list endpoint, hence we emulate it by querying for 'all'
        //   and filtering the result
        // Additionally, using affiliation together with type is not supported, so omit
        // type when adding affiliation.

        String actualFilterType = "sources".equals(mRepoType) || "forks".equals(mRepoType)
                ? "all" : mRepoType;

        if (isSelf && TextUtils.equals(actualFilterType, "all")) {
            mFilterData.put("affiliation", "owner,collaborator");
        } else if (!TextUtils.equals(actualFilterType, "all") || !args.getBoolean("is_org")) {
            mFilterData.put("type", actualFilterType);
        }

        final String sortOrder = args.getString("sort_order");
        if (sortOrder != null) {
            mFilterData.put("sort", sortOrder);
            mFilterData.put("direction", args.getString("sort_direction"));
        }
    }

    @Override
    protected RootAdapter<Repository, ? extends RecyclerView.ViewHolder> onCreateAdapter() {
        return new RepositoryAdapter(getActivity());
    }

    @Override
    protected int getEmptyTextResId() {
        return R.string.no_repos_found;
    }

    @Override
    protected void onAddData(RootAdapter<Repository, ? extends RecyclerView.ViewHolder> adapter,
            Collection<Repository> repositories) {
        if ("sources".equals(mRepoType) || "forks".equals(mRepoType)) {
            for (Repository repository : repositories) {
                if ("sources".equals(mRepoType) && !repository.isFork()) {
                    adapter.add(repository);
                } else if ("forks".equals(mRepoType) && repository.isFork()) {
                    adapter.add(repository);
                }
            }
            adapter.notifyDataSetChanged();
        } else {
            adapter.addAll(repositories);
        }
    }

    @Override
    public void onItemClick(Repository repository) {
        startActivity(RepositoryActivity.makeIntent(getActivity(), repository));
    }

    @Override
    protected Single<Response<Page<Repository>>> loadPage(int page, boolean bypassCache) {
        final RepositoryService service = ServiceFactory.get(RepositoryService.class, bypassCache);
        return ApiHelpers.loginEquals(mLogin, Gh4Application.get().getAuthLogin())
                ? service.getUserRepositories(mFilterData, page)
                : service.getUserRepositories(mLogin, mFilterData, page);
    }
}