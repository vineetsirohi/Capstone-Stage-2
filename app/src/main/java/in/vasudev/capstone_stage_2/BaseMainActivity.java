package in.vasudev.capstone_stage_2;

import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

public class BaseMainActivity extends AppCompatActivity {

    private static final String TAG = "BaseMainActivity";

//    private int mLevel = 1;

//    protected Button mNextLevelButton;

//    private TextView mLevelTextView;

    private Toolbar mToolbar;

    private DrawerLayout mDrawerLayout;

    private ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mToolbar = (Toolbar) findViewById(R.id.my_toolbar);
        setSupportActionBar(mToolbar);

        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);

        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(
                new NewsfeedViewPagerAdapter(getSupportFragmentManager()));

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

//        // Create the next level button, which tries to show an interstitial when clicked.
//        mNextLevelButton = ((Button) findViewById(R.id.next_level_button));
//
//        // Create the text view to show the level number.
//        mLevelTextView = (TextView) findViewById(R.id.level);

//        Credentials credentials = Credentials
//                .installedApp(RedditCredentials.CLIENT_ID, RedditCredentials.REDIRECT_URL);
//
//        final RedditClient reddit = new RedditClient(
//                UserAgent.of("android:in.vasudev.capstone_stage_2:v0.1"));
//        reddit.setLoggingMode(LoggingMode.ALWAYS);
//
//        new AsyncTask<RedditClient, Void, Void>() {
//
//            @Override
//            protected Void doInBackground(RedditClient... redditClients) {
//                try {
//                    Credentials credentials = Credentials.userlessApp(RedditCredentials.CLIENT_ID, UUID.randomUUID());
//                    OAuthData authData = reddit.getOAuthHelper().easyAuth(credentials);
//                    reddit.authenticate(authData);
//                    List<String> me = reddit.getTrendingSubreddits();
//                    Log.d(TAG, "in.vasudev.capstone_stage_2.BaseMainActivity.doInBackground" + ": me: " + me);
//
//                } catch (OAuthException e) {
//                    e.printStackTrace();
//                }
//                return null;
//            }
//        }.execute();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    protected void goToNextLevel() {
//        mLevelTextView.setText(String.valueOf(++mLevel));
    }

    public static class NewsfeedViewPagerAdapter extends FragmentStatePagerAdapter {

        private List<String> mList;

        public NewsfeedViewPagerAdapter(FragmentManager fm) {
            super(fm);

            mList = new ArrayList();
            mList.add("All");
            mList.add("Politics");
            mList.add("Funny");
            mList.add("Pics");
            mList.add("Gifs");
            mList.add("Videos");
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new RedditPostsFragment();
            Bundle args = new Bundle();
            args.putString(RedditPostsFragment.SUBREDDIT, mList.get(i));
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mList.get(position);
        }

    }
}
