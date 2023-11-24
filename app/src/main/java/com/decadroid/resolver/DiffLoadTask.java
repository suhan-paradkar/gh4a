package com.decadroid.resolver;

import android.content.Intent;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.decadroid.activities.FileViewerActivity;
import com.decadroid.utils.ApiHelpers;
import com.decadroid.utils.FileUtils;
import com.decadroid.utils.Optional;
import com.decadroid.utils.RxUtils;
import com.meisolsson.githubsdk.model.GitHubFile;
import com.meisolsson.githubsdk.model.PositionalCommentBase;

import java.util.List;

import io.reactivex.Single;

public abstract class DiffLoadTask<C extends PositionalCommentBase> extends UrlLoadTask {
    protected final String mRepoOwner;
    protected final String mRepoName;
    protected final DiffHighlightId mDiffId;

    public DiffLoadTask(FragmentActivity activity, Uri urlToResolve, String repoOwner,
            String repoName, DiffHighlightId diffId) {
        super(activity, urlToResolve);
        mRepoOwner = repoOwner;
        mRepoName = repoName;
        mDiffId = diffId;
    }

    @Override
    protected Single<Optional<Intent>> getSingle() {
        Single<Optional<GitHubFile>> fileSingle = getFiles()
                .compose(RxUtils.filterAndMapToFirst(
                        f -> ApiHelpers.md5(f.filename()).equalsIgnoreCase(mDiffId.fileHash)));
        return Single.zip(getSha(), fileSingle, (sha, fileOpt) -> {
            final Intent intent;
            GitHubFile file = fileOpt.orNull();
            if (file != null && FileUtils.isImage(file.filename())) {
                intent = FileViewerActivity.makeIntent(mActivity, mRepoOwner, mRepoName,
                        sha, file.filename());
            } else if (file != null) {
                intent = getLaunchIntent(sha, file, getComments().blockingGet(), mDiffId);
            } else {
                intent = getFallbackIntent(sha);
            }
            return Optional.of(intent);
        });
    }

    protected abstract Single<List<GitHubFile>> getFiles();
    protected abstract Single<String> getSha();
    protected abstract Single<List<C>> getComments();
    protected abstract @NonNull Intent getLaunchIntent(String sha, @NonNull GitHubFile file,
            List<C> comments, DiffHighlightId diffId);
    protected abstract @NonNull Intent getFallbackIntent(String sha);
}
