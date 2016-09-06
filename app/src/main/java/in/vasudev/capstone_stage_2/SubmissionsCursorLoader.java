package in.vasudev.capstone_stage_2;

/**
 * Created by vineet on 05-Sep-16.
 */

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.content.AsyncTaskLoader;

import java.util.UUID;

/**
 * Created by vineet on 20/05/2015.
 */
public class SubmissionsCursorLoader extends AsyncTaskLoader<Cursor> {

    public static final String ID = "_id";

    public static final String THUMBNAIL = "thumbnail";

    public static final String POSTHINT = "posthint";

    public static final String DOMAIN = "domain";

    public static final String TITLE = "title";

    public static final String SUBREDDIT_NAME = "subreddit_name";

    public static final String CREATED_TIME = "created_time";

    public static final String AUTHOR = "author";

    public static final String VOTE_VALUE = "vote_value";

    public static final String SCORE = "score";

    public static final String COMMENT_COUNT = "comment_count";

    private static final String SHORT_URL = "short_url";

    private String mSubreddit;

    private Cursor mList;

    public SubmissionsCursorLoader(Context context, String subreddit) {
        super(context);
        mSubreddit = subreddit;
    }

    @Override
    public Cursor loadInBackground() {
//        Credentials credentials = Credentials
//                .installedApp(RedditCredentials.CLIENT_ID, RedditCredentials.REDIRECT_URL);
        final RedditClient reddit = new RedditClient(
                UserAgent.of("android:in.vasudev.capstone_stage_2:v0.1"));
        reddit.setLoggingMode(LoggingMode.ALWAYS);

        try {
            Credentials credentials = Credentials
                    .userlessApp(RedditCredentials.CLIENT_ID, UUID
                            .randomUUID());
            OAuthData authData = reddit.getOAuthHelper().easyAuth(credentials);
            reddit.authenticate(authData);

            SubredditPaginator listings;
            if (mSubreddit.toLowerCase().equals("all")) {
                listings = new SubredditPaginator(reddit);
            } else {
                listings = new SubredditPaginator(reddit, mSubreddit);
            }

            String[] columns = new String[]{ID, THUMBNAIL, POSTHINT, DOMAIN, TITLE,
                    SUBREDDIT_NAME, CREATED_TIME, AUTHOR, VOTE_VALUE, SCORE,
                    COMMENT_COUNT, SHORT_URL};
            MatrixCursor matrixCursor = new MatrixCursor(columns);
            for (Submission submission : listings.next()) {
                matrixCursor.addRow(new Object[]{1, submission.getThumbnail(), submission.getPostHint(),
                        submission.getDomain(), submission.getTitle(), submission.getSubredditName(),
                        submission.getCreated().getTime(), submission.getAuthor(), submission.getVote().getValue(),
                        submission.getScore(), submission.getCommentCount(), submission.getShortURL()});
            }

            return matrixCursor;

        } catch (OAuthException e) {
            e.printStackTrace();
        } catch (NetworkException e) {

        }
        return null;
    }

    @Override
    public void deliverResult(Cursor list) {
        if (isReset()) {
            // An async query came in while the loader is stopped.  We
            // don't need the result.
            if (list != null) {
                onReleaseResources(list);
            }
        }
        Cursor oldList = mList;
        mList = list;

        if (isStarted()) {
            // If the Loader is currently started, we can immediately
            // deliver its results.
            super.deliverResult(list);
        }

        // At this point we can release the resources associated with
        // 'oldList' if needed; now that the new result is delivered we
        // know that it is no longer in use.
        if (oldList != null) {
            onReleaseResources(oldList);
        }
    }

    @Override
    protected void onStartLoading() {
        if (mList != null) {
            // If we currently have a result available, deliver it
            // immediately.
            deliverResult(mList);
        }

        if (takeContentChanged() || mList == null) {
            // If the data has changed since the last time it was loaded
            // or is not currently available, start a load.
            forceLoad();
        }
    }

    @Override
    protected void onStopLoading() {
        // Attempt to cancel the current load task if possible.
        cancelLoad();
    }

    @Override
    public void onCanceled(Cursor apps) {
        super.onCanceled(apps);

        // At this point we can release the resources associated with 'apps'
        // if needed.
        onReleaseResources(apps);
    }

    @Override
    protected void onReset() {
        super.onReset();

        // Ensure the loader is stopped
        onStopLoading();

        // At this point we can release the resources associated with 'apps'
        // if needed.
        if (mList != null) {
            onReleaseResources(mList);
            mList = null;
        }

    }

    /**
     * Helper function to take care of releasing resources associated
     * with an actively loaded data set.
     */
    protected void onReleaseResources(Cursor apps) {
        // For a simple List<> there is nothing to do.  For something
        // like a Cursor, we would close it here.
    }
}