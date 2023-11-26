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
package com.decadroid.activities;

import android.content.Context;
import android.content.Intent;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.decadroid.R;
import com.decadroid.fragment.SearchFragment;

public class SearchActivity extends FragmentContainerActivity {
    public static final int SEARCH_TYPE_REPO = SearchFragment.SEARCH_TYPE_REPO;
    public static final int SEARCH_TYPE_USER = SearchFragment.SEARCH_TYPE_USER;
    public static final int SEARCH_TYPE_CODE = SearchFragment.SEARCH_TYPE_CODE;

    public static Intent makeIntent(Context context, String initialSearch,
            int searchType, boolean startSearchImmediately) {
        return makeIntent(context)
                .putExtra("initial_search", initialSearch)
                .putExtra("search_type", searchType)
                .putExtra("search_immediately", startSearchImmediately);
    }

    public static Intent makeIntent(Context context) {
        return new Intent(context, SearchActivity.class);
    }

    @Nullable
    @Override
    protected String getActionBarTitle() {
        return getString(R.string.search);
    }

    @Override
    protected Fragment onCreateFragment() {
        Intent intent = getIntent();
        int searchType = intent.getIntExtra("search_type", SEARCH_TYPE_REPO);
        String initialQuery = intent.getStringExtra("initial_search");
        boolean startSearchImmediately = intent.getBooleanExtra("search_immediately", true);

        return SearchFragment.newInstance(searchType, initialQuery, startSearchImmediately);
    }
}
