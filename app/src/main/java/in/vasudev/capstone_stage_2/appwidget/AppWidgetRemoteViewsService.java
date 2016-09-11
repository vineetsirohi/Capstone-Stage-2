package in.vasudev.capstone_stage_2.appwidget;

import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.util.Log;
import android.view.View;
import android.widget.RemoteViews;
import android.widget.RemoteViewsService;

import in.vasudev.capstone_stage_2.R;
import in.vasudev.capstone_stage_2.model.SubmissionModel;
import in.vasudev.capstone_stage_2.model.SubmissionsTable;
import in.vasudev.capstone_stage_2.utils.MyTimeUtils;
import in.vasudev.capstone_stage_2.utils.StringUtils;

/**
 * Created by vineet on 11-Sep-16.
 */
public class AppWidgetRemoteViewsService extends RemoteViewsService {


    @Override
    public RemoteViewsFactory onGetViewFactory(Intent intent) {
        return (new ListProvider(this.getApplicationContext(), intent));
    }

    public static class ListProvider implements RemoteViewsService.RemoteViewsFactory {

        private static final String LOG_TAG = "Reddit Browser";

        private Cursor mCursor;

        private Context context = null;

        private int appWidgetId;

        public ListProvider(Context context, Intent intent) {
            this.context = context;
            appWidgetId = intent.getIntExtra(
                    AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
        }

        @Override
        public void onCreate() {

        }

        @Override
        public void onDataSetChanged() {
            if (mCursor != null) {
                mCursor.close();
            }

            mCursor = context.getContentResolver()
                    .query(SubmissionsTable.CONTENT_URI, null, null, null, null);

        }

        @Override
        public void onDestroy() {
            if (mCursor != null) {
                mCursor.close();
            }
        }

        @Override
        public int getCount() {
            return mCursor.getCount();
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }


        @Override
        public RemoteViews getViewAt(int position) {
            final RemoteViews remoteView = new RemoteViews(context.getPackageName(),
                    R.layout.list_item_appwidget);

            if (mCursor.moveToPosition(position)) {
                String title = mCursor
                        .getString(SubmissionModel.getColumnIndex(SubmissionModel.TITLE));
                Log.d(LOG_TAG,
                        "in.vasudev.capstone_stage_2.appwidget.AppWidgetRemoteViewsService.ListProvider.getViewAt "
                                + title);
                remoteView.setTextViewText(R.id.title, title);

                StringBuilder stringBuilder = new StringBuilder();
                stringBuilder.append("r/")
                        .append(mCursor.getString(
                                SubmissionModel.getColumnIndex(SubmissionModel.SUBREDDIT_NAME)))
                        .append(StringUtils.SPACE).append(context.getString(R.string.bullet_point)).append(StringUtils.SPACE)
                        .append(MyTimeUtils.timeElapsed(mCursor.getLong(
                                SubmissionModel.getColumnIndex(SubmissionModel.CREATED_TIME))))
                        .append(StringUtils.SPACE).append(context.getString(R.string.bullet_point)).append(StringUtils.SPACE)
                        .append("u/")
                        .append(mCursor
                                .getString(SubmissionModel.getColumnIndex(SubmissionModel.AUTHOR)));
                remoteView.setTextViewText(R.id.subtext, stringBuilder.toString());

                remoteView.setTextViewText(R.id.upvotes_text, mCursor
                        .getString(SubmissionModel.getColumnIndex(SubmissionModel.SCORE)));
                remoteView.setTextViewText(R.id.comments_text, mCursor
                        .getString(SubmissionModel.getColumnIndex(SubmissionModel.COMMENT_COUNT)));

//                remoteView.setViewVisibility(R.id.thumbnail, View.VISIBLE);
//                String url = mCursor
//                        .getString(SubmissionModel.getColumnIndex(SubmissionModel.THUMBNAIL));
//                if (url != null) {
//                    Picasso.with(context)
//                            .load(url)
//                            .into(remoteView, R.id.thumbnail, new int[]{appWidgetId});
//                } else {
                    remoteView.setViewVisibility(R.id.thumbnail, View.GONE);
//                }
            }

            return remoteView;
        }

        @Override
        public RemoteViews getLoadingView() {
            return null;
        }

        @Override
        public int getViewTypeCount() {
            return 1;
        }


    }
}