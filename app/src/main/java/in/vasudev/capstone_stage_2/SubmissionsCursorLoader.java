package in.vasudev.capstone_stage_2;

/**
 * Created by vineet on 05-Sep-16.
 */

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

import android.content.Context;
import android.database.Cursor;
import android.database.MatrixCursor;
import android.support.v4.content.AsyncTaskLoader;

import in.vasudev.capstone_stage_2.model.SubmissionModel;
import in.vasudev.capstone_stage_2.model.SubmissionsTable;
import in.vasudev.capstone_stage_2.model.SubredditsModel;

/**
 * Created by vineet on 20/05/2015.
 */
public class SubmissionsCursorLoader extends AsyncTaskLoader<Cursor> {

    private String mSubreddit;

    private Cursor mList;


    public SubmissionsCursorLoader(Context context, String subreddit) {
        super(context);
        mSubreddit = subreddit;
    }

    @Override
    public Cursor loadInBackground() {

        RedditClient reddit = MyApp.getRedditClient();

        if (reddit.isAuthenticated()) {
            SubredditPaginator listings;
            if (mSubreddit.toLowerCase().equals(SubredditsModel.DEFAULT_SUB_ALL)) {
                listings = new SubredditPaginator(reddit);
                getContext().getContentResolver().delete(SubmissionsTable.CONTENT_URI, null, null);
            } else {
                listings = new SubredditPaginator(reddit, mSubreddit);
            }

            MatrixCursor matrixCursor = new MatrixCursor(SubmissionModel.COLUMNS);
            int id = 0;
            for (Submission submission : listings.next()) {
                if(!submission.isNsfw()) {
                    matrixCursor.addRow(new Object[]{id, submission.getThumbnail(),
                            submission.getPostHint(),
                            submission.getDomain(), submission.getTitle(),
                            submission.getSubredditName(),
                            submission.getCreated().getTime(), submission.getAuthor(),
                            submission.getVote().getValue(),
                            submission.getScore(), submission.getCommentCount(),
                            submission.getShortURL()});

                    if (mSubreddit.toLowerCase().equals("all")) {
                        getContext().getContentResolver().insert(SubmissionsTable.CONTENT_URI,
                                SubmissionsTable
                                        .getContentValues(new SubmissionModel(id, submission),
                                                false));
                    }
                    id++;
                }
            }

            return matrixCursor;
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