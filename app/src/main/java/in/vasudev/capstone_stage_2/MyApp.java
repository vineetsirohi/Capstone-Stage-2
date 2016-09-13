package in.vasudev.capstone_stage_2;

import net.dean.jraw.RedditClient;
import net.dean.jraw.http.LoggingMode;
import net.dean.jraw.http.NetworkException;
import net.dean.jraw.http.UserAgent;
import net.dean.jraw.http.oauth.Credentials;
import net.dean.jraw.http.oauth.OAuthData;
import net.dean.jraw.http.oauth.OAuthException;

import android.app.Application;
import android.util.Log;

import java.util.UUID;

/**
 * Created by vineet on 25-Aug-16.
 */
public class MyApp extends Application {

    private static RedditClient mReddit;

    @Override
    public void onCreate() {
        super.onCreate();

        new Thread(new Runnable() {
            @Override
            public void run() {
                getRedditClient();
            }
        }).start();
    }


    public synchronized static RedditClient getRedditClient() {
        if (mReddit != null && mReddit.isAuthenticated()) {
            Log.d(AppConstants.LOG_TAG, "in.vasudev.capstone_stage_2.MyApp.getRedditClient" + " reddit client authenticated");
            return mReddit;
        }

        Log.d(AppConstants.LOG_TAG, "in.vasudev.capstone_stage_2.MyApp.getRedditClient" + " authenticating reddit client");

        //        Credentials credentials = Credentials
//                .installedApp(RedditCredentials.CLIENT_ID, RedditCredentials.REDIRECT_URL);
        mReddit = new RedditClient(
                UserAgent.of("android:in.vasudev.capstone_stage_2:v0.1"));
        if (BuildConfig.DEBUG) {
            mReddit.setLoggingMode(LoggingMode.ALWAYS);
        }

        try {
            Credentials credentials = Credentials
                    .userlessApp(RedditCredentials.CLIENT_ID, UUID
                            .randomUUID());
            OAuthData authData = mReddit.getOAuthHelper().easyAuth(credentials);
            mReddit.authenticate(authData);
            return mReddit;
        } catch (OAuthException e) {
            e.printStackTrace();
        } catch (NetworkException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return mReddit;
    }

}
