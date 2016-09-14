package in.vasudev.capstone_stage_2;

import net.dean.jraw.RedditClient;
import net.dean.jraw.models.Listing;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

import android.app.IntentService;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import in.vasudev.capstone_stage_2.appwidget.MyAppWidgetProvider;
import in.vasudev.capstone_stage_2.model.SubmissionModel;
import in.vasudev.capstone_stage_2.model.SubmissionsTable;

public class RefreshDataIntentService extends IntentService {

    public RefreshDataIntentService() {
        super("RefreshDataIntentService");
    }

    public static void startService(Context context) {
        Intent intent = new Intent(context, RefreshDataIntentService.class);
        context.startService(intent);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d(AppConstants.LOG_TAG, "in.vasudev.capstone_stage_2.RefreshDataIntentService.onHandleIntent");
        
        RedditClient reddit = MyApp.getRedditClient();
        if (reddit.isAuthenticated()) {
            SubredditPaginator mListings = new SubredditPaginator(reddit);
            if (mListings.hasNext()) {
                getContentResolver()
                        .delete(SubmissionsTable.CONTENT_URI, null, null);
            }

            int id = 0;
            Listing<Submission> submissions = mListings.next();
            for (Submission submission : submissions) {
                if (!submission.isNsfw()) {
                    getContentResolver().insert(SubmissionsTable.CONTENT_URI,
                            SubmissionsTable
                                    .getContentValues(new SubmissionModel(id, submission),
                                            false));
                    id++;
                }
            }

            updateAppWidgets();
        }
    }

    private void updateAppWidgets() {
        Context context = getApplicationContext();
        AppWidgetManager appWidgetManager = AppWidgetManager.getInstance(context);
        ComponentName thisWidget = new ComponentName(context, MyAppWidgetProvider.class);
        int[] appWidgetIds = appWidgetManager.getAppWidgetIds(thisWidget);
        appWidgetManager.notifyAppWidgetViewDataChanged(appWidgetIds, R.id.widget_list);
    }

}
