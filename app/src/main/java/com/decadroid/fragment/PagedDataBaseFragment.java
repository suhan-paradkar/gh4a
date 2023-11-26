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

import androidx.loader.app.LoaderManager;
import androidx.recyclerview.widget.RecyclerView;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;

import com.decadroid.R;
import com.decadroid.adapter.RootAdapter;
import com.decadroid.utils.ApiHelpers;
import com.decadroid.utils.RxUtils;
import com.meisolsson.githubsdk.model.Page;
import com.philosophicalhacker.lib.RxLoader;

import java.net.HttpURLConnection;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import io.reactivex.Single;
import io.reactivex.disposables.Disposable;
import io.reactivex.subjects.BehaviorSubject;
import io.reactivex.subjects.Subject;
import retrofit2.Response;

public abstract class PagedDataBaseFragment<T> extends LoadingListFragmentBase implements
        RootAdapter.OnItemClickListener<T>, RootAdapter.OnScrolledToFooterListener {
    private RootAdapter<T, ? extends RecyclerView.ViewHolder> mAdapter;
    private RxLoader mRxLoader;
    private Subject<Integer> mPageSubject;
    private Integer mNextPage;
    private View mLoadingView;
    private Disposable mSubscription;

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mRxLoader = new RxLoader(getActivity(), LoaderManager.getInstance(this));
        resetSubject();
        if (shouldDoInitialLoad(savedInstanceState)) {
            setContentShown(false);
            load(false);
        }
    }

    @Override
    public void onRefresh() {
        if (mAdapter != null) {
            mAdapter.clear();
        }
        if (mSubscription != null) {
            mSubscription.dispose();
            mSubscription = null;
        }
        if (mRxLoader != null) {
            resetSubject();
            setContentShown(false);
            load(true);
        }
    }

    @Override
    protected void onRecyclerViewInflated(RecyclerView view, LayoutInflater inflater) {
        super.onRecyclerViewInflated(view, inflater);
        mAdapter = onCreateAdapter();

        mLoadingView = inflater.inflate(R.layout.list_loading_view, view, false);
        mAdapter.setFooterView(mLoadingView, this);
        mAdapter.setOnItemClickListener(this);
        view.setAdapter(mAdapter);
        updateEmptyState();
    }

    @Override
    protected boolean hasDividers() {
        return mAdapter.hasDividers() && !mAdapter.isCardStyle();
    }

    @Override
    protected boolean hasCards() {
        return mAdapter.isCardStyle();
    }

    protected void resetSubject() {
        mNextPage = null;
        mPageSubject = BehaviorSubject.createDefault(1);
        mPageSubject.onNext(1);
    }

    protected boolean shouldDoInitialLoad(Bundle savedInstanceState) {
        return true;
    }

    private void load(boolean force) {
        mSubscription = mPageSubject
                .flatMap(page -> loadPage(page, force)
                        .map(response -> {
                            if (response.code() == HttpURLConnection.HTTP_NO_CONTENT) {
                                return Response.success(new ApiHelpers.DummyPage<T>());
                            }
                            return response;
                        })
                        .map(ApiHelpers::throwOnFailure)
                        .compose(RxUtils::doInBackground)
                        .toObservable())
                .scan(Pair.create(new ArrayList<T>(), 0), (pair, page) -> {
                    pair.first.addAll(page.items());
                    return Pair.create(pair.first, page.next());
                })
                // filter out initial value
                .filter(pair -> pair.second == null || pair.second != 0)
                .compose(mRxLoader.makeObservableTransformer(0, force))
                .subscribe(result -> {
                    fillData(result.first, result.second);
                    setContentShown(true);
                    updateEmptyState();
                }, this::handleLoadFailure);
    }

    private void fillData(List<T> data, Integer nextPage) {
        mNextPage = nextPage;
        mLoadingView.setVisibility(nextPage != null ? View.VISIBLE : View.GONE);

        if (mAdapter.getCount() > 0 && !data.isEmpty() && data.iterator().next() == mAdapter.getItem(0)) {
            // there are common items, preserve them in order to keep the scroll position
            ArrayList<T> newData = new ArrayList<>();
            int index = 0, count = mAdapter.getCount();
            for (T item : data) {
                if (index < count && item == mAdapter.getItem(index++)) {
                    // we already know about the item
                    continue;
                }
                newData.add(item);
            }
            onAddData(mAdapter, newData);
        } else {
            mAdapter.clear();
            onAddData(mAdapter, data);
        }
    }

    protected void onAddData(RootAdapter<T, ? extends RecyclerView.ViewHolder> adapter, Collection<T> data) {
        adapter.addAll(data);
    }

    @Override
    public void onScrolledToFooter() {
        if (mNextPage != null && mLoadingView.getVisibility() == View.VISIBLE) {
            // Even if our subscription above is active, the page subject might not be subscribed
            // to yet - this is the case if the data comes from the RX loader's cache. In that case,
            // updating the page subject is pointless at this point. Force a load to subscribe to
            // the page subject, which will cause the data to be delivered again, after which
            // onScrolledToFooter will be called again.
            if (mPageSubject.hasObservers()) {
                mPageSubject.onNext(mNextPage);
            } else {
                load(true);
            }
            mNextPage = null;
        }
    }

    protected abstract RootAdapter<T, ? extends RecyclerView.ViewHolder> onCreateAdapter();
    protected abstract Single<Response<Page<T>>> loadPage(int page, boolean bypassCache);
    public abstract void onItemClick(T item);
}
