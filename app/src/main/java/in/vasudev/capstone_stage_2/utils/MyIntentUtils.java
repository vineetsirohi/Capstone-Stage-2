package in.vasudev.capstone_stage_2.utils;

import android.content.Context;

import in.vasudev.capstone_stage_2.WebViewActivity;

/**
 * Created by vineet on 06-Sep-16.
 */
public class MyIntentUtils {

    public static final String EXTRA_URL = "extra_url";

    public static final String EXTRA_TITLE = "extra_title";

    public static void openWebPage(Context context, String url, String title) {
//        Uri webpage = Uri.parse(url);
//        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
//        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//        if (intent.resolveActivity(context.getPackageManager()) != null) {
//            context.startActivity(intent);
//        }

        WebViewActivity.startActivity(context, url, title);
    }
}
