package in.vasudev.capstone_stage_2;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;
import net.dean.jraw.models.Submission;
import net.dean.jraw.paginators.SubredditPaginator;

import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.method.ScrollingMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import in.vasudev.capstone_stage_2.utils.MyTimeUtils;

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

        new AsyncTask<RedditClient, Void, List<Submission>>() {

            @Override
            protected List<Submission> doInBackground(RedditClient... redditClients) {
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
                    List<Submission> submissions = new ArrayList<>();
                    for (Submission link : listings.next()) {
                        submissions.add(link);
                    }
                    return submissions;
                } catch (OAuthException e) {
                    e.printStackTrace();
                } catch (NetworkException e) {

                }
                return null;
            }

            @Override
            protected void onPostExecute(List<Submission> submissions) {
                super.onPostExecute(submissions);
                if (submissions != null) {
                    StringBuilder stringBuilder = new StringBuilder();
                    for (Submission submission : submissions) {
                        stringBuilder
                                .append(submission.getThumbnail())
                                .append("\n")
                                .append(submission.getPostHint())
                                .append("\n")
                                .append(submission.getDomain())
                                .append("\n")
                                .append(submission.getTitle())
                                .append("\n")
                                .append(submission.getSubredditName()).append(" ")
                                .append(MyTimeUtils.timeElapsed(submission.getCreated().getTime())).append("  ")
                                .append(submission.getAuthor())
                                .append("\n")
                                .append(submission.getVote().getValue())
                                .append(submission.getScore()).append("   ")
                                .append(submission.getCommentCount())
                                .append("   ")
                                .append("\n\n");
                    }
                    mTextView.setText(stringBuilder.toString());
                }
            }
        }.execute();
    }
}
