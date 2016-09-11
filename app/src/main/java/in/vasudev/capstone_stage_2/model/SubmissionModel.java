package in.vasudev.capstone_stage_2.model;

import android.text.Html;

import java.util.Arrays;

/**
 * Created by vineet on 05-Sep-16.
 */
public class SubmissionModel {

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

//    public static final String BULLET_POINT = Html.fromHtml("\\u2022", ); //&#8226

    public static final String[] COLUMNS = new String[]{ID, THUMBNAIL, POSTHINT, DOMAIN, TITLE,
            SUBREDDIT_NAME, CREATED_TIME, AUTHOR, VOTE_VALUE, SCORE,
            COMMENT_COUNT, SHORT_URL};


    public static int getColumnIndex(String columnName) {
        return Arrays.asList(COLUMNS).indexOf(columnName);
    }
}
