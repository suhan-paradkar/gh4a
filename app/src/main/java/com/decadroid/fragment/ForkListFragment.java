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

import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;

import com.decadroid.R;
import com.decadroid.ServiceFactory;
import com.decadroid.activities.RepositoryActivity;
import com.decadroid.adapter.RepositoryAdapter;
import com.decadroid.adapter.RootAdapter;
import com.meisolsson.githubsdk.model.Page;
import com.meisolsson.githubsdk.model.Repository;
import com.meisolsson.githubsdk.service.repositories.RepositoryForkService;

import io.reactivex.Single;
import retrofit2.Response;

public class ForkListFragment extends PagedDataBaseFragment<Repository> {
    private String mRepoOwner;
    private String mRepoName;

    public static ForkListFragment newInstance(String repoOwner, String repoName) {
        ForkListFragment f = new ForkListFragment();

        Bundle args = new Bundle();
        args.putString("owner", repoOwner);
        args.putString("repo", repoName);
        f.setArguments(args);

        return f;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mRepoOwner = getArguments().getString("owner");
        mRepoName = getArguments().getString("repo");
    }

    @Override
    protected RootAdapter<Repository, ? extends RecyclerView.ViewHolder> onCreateAdapter() {
        return new RepositoryAdapter(getActivity());
    }

    @Override
    protected int getEmptyTextResId() {
        return R.string.no_forks_found;
    }

    @Override
    public void onItemClick(Repository repo) {
        startActivity(RepositoryActivity.makeIntent(getActivity(), repo));
    }

    @Override
    protected Single<Response<Page<Repository>>> loadPage(int page, boolean bypassCache) {
        final RepositoryForkService service = ServiceFactory.get(RepositoryForkService.class, bypassCache);
        return service.getForks(mRepoOwner, mRepoName, page);
    }
}