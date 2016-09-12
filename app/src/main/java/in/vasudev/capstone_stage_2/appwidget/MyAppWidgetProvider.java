package in.vasudev.capstone_stage_2.appwidget;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.appwidget.AppWidgetProvider;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.widget.RemoteViews;

import in.vasudev.capstone_stage_2.R;
import in.vasudev.capstone_stage_2.utils.IntentUtils;

/**
 * Created by vineet on 11-Sep-16.
 */
public class MyAppWidgetProvider extends AppWidgetProvider {

    public static final String OPEN_URL_ACTION = "in.vasudev.capstone_stage_2.OPEN_URL_ACTION";

    @Override
    public void onReceive(Context context, Intent intent) {
        AppWidgetManager mgr = AppWidgetManager.getInstance(context);
        if (intent.getAction().equals(OPEN_URL_ACTION)) {
            int appWidgetId = intent.getIntExtra(AppWidgetManager.EXTRA_APPWIDGET_ID,
                    AppWidgetManager.INVALID_APPWIDGET_ID);
            String url = intent.getExtras().getString(IntentUtils.EXTRA_URL);
            IntentUtils.openWebPage(context, url);
        }
        super.onReceive(context, intent);
    }


    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        final int N = appWidgetIds.length;
        for (int i = 0; i < N; ++i) {

            //which layout to show on widget
            RemoteViews remoteViews = new RemoteViews(
                    context.getPackageName(), R.layout.appwidget_layout);

            //RemoteViews Service needed to provide adapter for ListView
            Intent svcIntent = new Intent(context, AppWidgetRemoteViewsService.class);
            //passing app widget id to that RemoteViews Service
            svcIntent.putExtra(AppWidgetManager.EXTRA_APPWIDGET_ID, appWidgetIds[i]);
            //setting a unique Uri to the intent
            svcIntent.setData(Uri.parse(svcIntent.toUri(Intent.URI_INTENT_SCHEME)));
            //setting adapter to listview of the widget
            remoteViews.setRemoteAdapter(appWidgetIds[i], R.id.widget_list, svcIntent);

            //setting an empty view in case of no data
            remoteViews.setEmptyView(R.id.widget_list, R.id.empty_view);

//            Pending Intent template
            Intent openUrlIntent = new Intent(context, MyAppWidgetProvider.class);
            openUrlIntent.setAction(MyAppWidgetProvider.OPEN_URL_ACTION);

            PendingIntent openUrlPendingIntent = PendingIntent
                    .getBroadcast(context, 0, openUrlIntent, PendingIntent.FLAG_UPDATE_CURRENT);
            remoteViews.setPendingIntentTemplate(R.id.widget_list, openUrlPendingIntent);

//            update widget
            appWidgetManager.updateAppWidget(appWidgetIds[i], remoteViews);
        }
        super.onUpdate(context, appWidgetManager, appWidgetIds);
    }

}
