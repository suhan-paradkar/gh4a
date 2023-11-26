package com.decadroid.activities;

import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.AttrRes;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentActivity;
import androidx.appcompat.app.AppCompatActivity;
import android.widget.ImageView;

import com.decadroid.R;
import com.decadroid.widget.EditorBottomSheet;
import com.meisolsson.githubsdk.model.GitHubCommentBase;

import io.reactivex.Single;

public abstract class EditCommentActivity extends AppCompatActivity implements
        EditorBottomSheet.Callback {

    protected static Intent fillInIntent(Intent baseIntent, String repoOwner, String repoName,
            long id, long replyToId, String body, @AttrRes int highlightColorAttr) {
        return baseIntent.putExtra("owner", repoOwner)
                .putExtra("repo", repoName)
                .putExtra("id", id)
                .putExtra("reply_to", replyToId)
                .putExtra("body", body)
                .putExtra("highlight_color_attr", highlightColorAttr);
    }

    private CoordinatorLayout mRootLayout;
    protected EditorBottomSheet mEditorSheet;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        setTheme(R.style.BottomSheetTheme);
        super.onCreate(savedInstanceState);

        setContentView(R.layout.comment_editor);

        mRootLayout = findViewById(R.id.coordinator_layout);
        mEditorSheet = findViewById(R.id.bottom_sheet);

        if (getIntent().getLongExtra("id", 0L) != 0L) {
            ImageView saveButton = mEditorSheet.findViewById(R.id.send_button);
            saveButton.setImageResource(R.drawable.save);
        }

        mEditorSheet.setCallback(this);
        if (getIntent().hasExtra("body")) {
            mEditorSheet.setCommentText(getIntent().getStringExtra("body"), false);
            getIntent().removeExtra("body");
        }

        @AttrRes int highlightColorAttr = getIntent().getIntExtra("highlight_color_attr", 0);
        if (highlightColorAttr != 0) {
            mEditorSheet.setHighlightColor(highlightColorAttr);
        }

        setResult(RESULT_CANCELED);
    }

    @Override
    public int getCommentEditorHintResId() {
        return 0;
    }

    @Override
    public Single<?> onEditorDoSend(String body) {
        Bundle extras = getIntent().getExtras();
        String repoOwner = extras.getString("owner");
        String repoName = extras.getString("repo");
        long id = extras.getLong("id", 0L);

        if (id == 0L) {
            return createComment(repoOwner, repoName, body, extras.getLong("reply_to"));
        } else {
            return editComment(repoOwner, repoName, id, body);
        }
    }

    @Override
    public void onEditorTextSent() {
        setResult(RESULT_OK);
        finish();
    }

    @Override
    public int getEditorErrorMessageResId() {
        return R.string.issue_error_comment;
    }

    @Override
    public FragmentActivity getActivity() {
        return this;
    }

    @Override
    public CoordinatorLayout getRootLayout() {
        return mRootLayout;
    }

    protected abstract Single<GitHubCommentBase> createComment(
            String repoOwner, String repoName, String body, long replyToCommentId);
    protected abstract Single<GitHubCommentBase> editComment(
            String repoOwner, String repoName, long commentId, String body);
}