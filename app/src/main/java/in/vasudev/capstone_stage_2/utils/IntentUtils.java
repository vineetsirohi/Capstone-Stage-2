package in.vasudev.capstone_stage_2.utils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;

/**
 * Created by vineet on 06-Sep-16.
 */
public class IntentUtils {

    public static void openWebPage(Context context, String url) {
        Uri webpage = Uri.parse(url);
        Intent intent = new Intent(Intent.ACTION_VIEW, webpage);
        if (intent.resolveActivity(context.getPackageManager()) != null) {
            context.startActivity(intent);
        }
    }
}
