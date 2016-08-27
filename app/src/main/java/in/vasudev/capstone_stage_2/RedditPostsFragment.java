package in.vasudev.capstone_stage_2;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Subreddit;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.UUID;

/**
 * Created by vineet on 27-Aug-16.
 */
public class RedditPostsFragment extends Fragment {

    public static final String SUBREDDIT = "subreddit_text";

    private String mSubreddit;

    private TextView mTextView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mSubreddit = args.getString(SUBREDDIT);

    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_reddit_posts, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        mTextView = (TextView) view.findViewById(R.id.textview);
        mTextView.setMovementMethod(new ScrollingMovementMethod());
        mTextView.setText(mSubreddit);

//        Credentials credentials = Credentials
//                .installedApp(RedditCredentials.CLIENT_ID, RedditCredentials.REDIRECT_URL);

        final RedditClient reddit = new RedditClient(
                UserAgent.of("android:in.vasudev.capstone_stage_2:v0.1"));
        reddit.setLoggingMode(LoggingMode.ALWAYS);

        new AsyncTask<RedditClient, Void, Subreddit>() {

            @Override
            protected Subreddit doInBackground(RedditClient... redditClients) {
                try {
                    Credentials credentials = Credentials
                            .userlessApp(RedditCredentials.CLIENT_ID, UUID
                                    .randomUUID());
                    OAuthData authData = reddit.getOAuthHelper().easyAuth(credentials);
                    reddit.authenticate(authData);
                    return reddit.getSubreddit(mSubreddit);

                } catch (OAuthException e) {
                    e.printStackTrace();
                } catch (NetworkException e) {

                }
                return null;
            }

            @Override
            protected void onPostExecute(Subreddit subreddit) {
                super.onPostExecute(subreddit);
                if (subreddit != null) {

                    mTextView.setText(subreddit.toString());
                }
            }
        }.execute();
    }
}
