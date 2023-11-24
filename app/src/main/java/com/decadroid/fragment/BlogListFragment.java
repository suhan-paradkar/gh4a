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

import java.util.List;

import android.net.Uri;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.decadroid.R;
import com.decadroid.activities.BlogActivity;
import com.decadroid.adapter.CommonFeedAdapter;
import com.decadroid.adapter.RootAdapter;
import com.decadroid.model.Feed;
import com.decadroid.utils.IntentUtils;
import com.decadroid.utils.SingleFactory;

import io.reactivex.Single;

public class BlogListFragment extends ListDataBaseFragment<Feed> implements
        RootAdapter.OnItemClickListener<Feed> {
    public static BlogListFragment newInstance() {
        return new BlogListFragment();
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.blog_list_menu, menu);
        super.onCreateOptionsMenu(menu, inflater);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.browser:
                IntentUtils.launchBrowser(getActivity(), Uri.parse("https://blog.github.com"));
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected RootAdapter<Feed, ? extends RecyclerView.ViewHolder> onCreateAdapter() {
        CommonFeedAdapter adapter = new CommonFeedAdapter(getActivity(), true);
        adapter.setOnItemClickListener(this);
        return adapter;
    }

    @Override
    protected int getEmptyTextResId() {
        return R.string.no_blogs_found;
    }

    @Override
    public void onItemClick(Feed blog) {
        startActivity(BlogActivity.makeIntent(getActivity(), blog));
    }

    @Override
    protected Single<List<Feed>> onCreateDataSingle(boolean bypassCache) {
        return SingleFactory.loadBlogFeed();
    }
}