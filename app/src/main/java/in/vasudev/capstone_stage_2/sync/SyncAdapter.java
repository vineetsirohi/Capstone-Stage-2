package in.vasudev.capstone_stage_2.sync;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

import android.accounts.Account;
import android.content.AbstractThreadedSyncAdapter;
import android.content.ContentProviderClient;
import android.content.Context;
import android.content.SyncResult;
import android.os.Bundle;
import android.util.Log;

import in.vasudev.capstone_stage_2.AppConstants;
import in.vasudev.capstone_stage_2.MyApp;
import in.vasudev.capstone_stage_2.appwidget.MyAppWidgetProvider;
import in.vasudev.capstone_stage_2.model.SubmissionModel;
import in.vasudev.capstone_stage_2.model.SubmissionsTable;

/**
 * Created by vineet on 15-Sep-16.
 */
public class SyncAdapter extends AbstractThreadedSyncAdapter {

    public SyncAdapter(Context context, boolean autoInitialize) {
        super(context, autoInitialize);
        Log.d(AppConstants.LOG_TAG, "in.vasudev.capstone_stage_2.sync.SyncAdapter.SyncAdapter");


    }

    public SyncAdapter(Context context, boolean autoInitialize, boolean allowParallelSyncs) {
        super(context, autoInitialize, allowParallelSyncs);
        Log.d(AppConstants.LOG_TAG,
                "in.vasudev.capstone_stage_2.sync.SyncAdapter.SyncAdapter" + ": 2");


    }

    @Override
    public void onPerformSync(Account account, Bundle extras, String authority,
            ContentProviderClient provider, SyncResult syncResult) {

        Log.d(AppConstants.LOG_TAG, "in.vasudev.capstone_stage_2.sync.SyncAdapter.onPerformSync");

        RedditClient reddit = MyApp.getRedditClient();
        if (reddit.isAuthenticated()) {
            SubredditPaginator mListings = new SubredditPaginator(reddit);
            if (mListings.hasNext()) {
                getContext().getContentResolver()
                        .delete(SubmissionsTable.CONTENT_URI, null, null);
            }

            int id = 0;
            Listing<Submission> submissions = mListings.next();
            for (Submission submission : submissions) {
                if (!submission.isNsfw()) {
                    getContext().getContentResolver().insert(SubmissionsTable.CONTENT_URI,
                            SubmissionsTable
                                    .getContentValues(new SubmissionModel(id, submission),
                                            false));
                    id++;
                }
            }

            MyAppWidgetProvider.updateAppWidgets(getContext());
        }
    }


}