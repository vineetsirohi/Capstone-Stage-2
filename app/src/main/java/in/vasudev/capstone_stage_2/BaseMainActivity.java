package in.vasudev.capstone_stage_2;

import android.accounts.Account;
import android.accounts.AccountManager;
import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.GravityCompat;
import android.support.v4.view.ViewPager;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.vasudev.capstone_stage_2.appwidget.MyAppWidgetProvider;
import in.vasudev.capstone_stage_2.model.SubredditsModel;
import in.vasudev.capstone_stage_2.model.SubredditsTable;
import in.vasudev.capstone_stage_2.ui.AddSubredditDialog;
import in.vasudev.capstone_stage_2.ui.OnRVItemClickListener;
import in.vasudev.capstone_stage_2.utils.MyIntentUtils;

public class BaseMainActivity extends AppCompatActivity implements OnRVItemClickListener {

    private static final String DIALOG_FRAGMENT_TAG = "dAddSub";

    public static final String ACCOUNT = "Account";

    // Sync interval constants
    public static final long SECONDS_PER_MINUTE = 60L;

    public static final long SYNC_INTERVAL_IN_MINUTES = 60L;

    public static final long SYNC_INTERVAL = SYNC_INTERVAL_IN_MINUTES * SECONDS_PER_MINUTE;

    //    Android views
    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    //    Fields
    private SubredditsViewPagerAdapter mViewPagerAdapter;

    Account mAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setSupportActionBar(mToolbar);

        mToolbar.setNavigationIcon(R.drawable.ic_menu_white_24dp);
        mToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mDrawerLayout.openDrawer(GravityCompat.START);
            }
        });

        mViewPager.setOffscreenPageLimit(3);
        mViewPagerAdapter = new SubredditsViewPagerAdapter(getSupportFragmentManager(),
                BaseMainActivity.this);
        mViewPager.setAdapter(mViewPagerAdapter);

        TabLayout tabLayout = (TabLayout) findViewById(R.id.tabs);
        tabLayout.setupWithViewPager(mViewPager);
        tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        mViewPager.addOnPageChangeListener(new TabLayout.TabLayoutOnPageChangeListener(tabLayout));

        mAccount = createSyncAccount(this);
        if (ContentResolver.isSyncPending(mAccount, AppConstants.CONTENT_PROVIDER_AUTHORITY)  ||
                ContentResolver.isSyncActive(mAccount, AppConstants.CONTENT_PROVIDER_AUTHORITY)) {
            Log.i("ContentResolver", "SyncPending, canceling");
            ContentResolver.cancelSync(mAccount, AppConstants.CONTENT_PROVIDER_AUTHORITY);
        }
        ContentResolver
                .addPeriodicSync(mAccount, AppConstants.CONTENT_PROVIDER_AUTHORITY, Bundle.EMPTY,
                        SYNC_INTERVAL);
        ContentResolver.setSyncAutomatically(mAccount, AppConstants.CONTENT_PROVIDER_AUTHORITY, true);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MyAppWidgetProvider.updateAppWidgets(BaseMainActivity.this);
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

//        if (id == R.id.action_settings) {
//            return true;
//        }
        if (id == R.id.action_add_subreddit) {
            addSubreddit();
            return true;
        }
        if (id == R.id.action_manage_subreddits) {
            manageSubreddits();
        }
        return super.onOptionsItemSelected(item);
    }

    private void manageSubreddits() {
        ManageSubredditsActivity.startActivity(BaseMainActivity.this);
    }

    private void addSubreddit() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(DIALOG_FRAGMENT_TAG);
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        new AddSubredditDialog().show(fragmentTransaction, DIALOG_FRAGMENT_TAG);
    }

    public void reloadSubredditsList(String newSubreddit) {
        mViewPagerAdapter.addSubreddit(newSubreddit);
        mViewPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mViewPagerAdapter.getCount());
    }

    @Override
    public void onItemClicked(String title, String url) {
        MyIntentUtils.openWebPage(BaseMainActivity.this, url, title);
    }

    public static Account createSyncAccount(Context context) {
        Account newAccount = new Account(ACCOUNT, context.getString(R.string.account_type));
        AccountManager accountManager = (AccountManager) context.getSystemService(ACCOUNT_SERVICE);
        accountManager.addAccountExplicitly(newAccount, null, null);
        ContentResolver
                .setSyncAutomatically(newAccount, AppConstants.CONTENT_PROVIDER_AUTHORITY, true);
        return newAccount;
    }

    public static class SubredditsViewPagerAdapter extends FragmentStatePagerAdapter {

        private final Context mContext;

        private List<SubredditsModel> mList;

        public SubredditsViewPagerAdapter(FragmentManager fm, Context context) {
            super(fm);
            mContext = context;
            loadSubreddits();
        }

        private void loadSubreddits() {
            Cursor cursor = mContext.getContentResolver()
                    .query(SubredditsTable.CONTENT_URI, null, null, null, null);
            if (cursor.getCount() == 0) {
                insertDefaultSubreddit(0, SubredditsModel.DEFAULT_SUB_ALL);
                insertDefaultSubreddit(1, SubredditsModel.DEFAULT_SUB_FUNNY);
                insertDefaultSubreddit(2, SubredditsModel.DEFAULT_SUB_PICS);
                insertDefaultSubreddit(3, SubredditsModel.DEFAULT_SUB_GIFS);
                insertDefaultSubreddit(4, SubredditsModel.DEFAULT_SUB_VIDEOS);
                insertDefaultSubreddit(5, SubredditsModel.DEFAULT_SUB_POLITICS);
                cursor.close();
                cursor = mContext.getContentResolver()
                        .query(SubredditsTable.CONTENT_URI, null, null, null, null);
            }

            mList = SubredditsTable.getRows(cursor, false);
            cursor.close();
        }

        public void addSubreddit(String newSubreddit) {
            Cursor cursor = mContext.getContentResolver()
                    .query(SubredditsTable.CONTENT_URI, null, null, null, null);
            mContext.getContentResolver().insert(SubredditsTable.CONTENT_URI, SubredditsTable
                    .getContentValues(
                            new SubredditsModel(cursor.getCount(), newSubreddit),
                            false));
            cursor.close();

            loadSubreddits();
        }

        private void insertDefaultSubreddit(int id, String subreddit) {
            mContext.getContentResolver().insert(SubredditsTable.CONTENT_URI, SubredditsTable
                    .getContentValues(new SubredditsModel(id, subreddit), false));
        }

        @Override
        public Fragment getItem(int i) {
            Fragment fragment = new RedditPostsCursorFragment();
            Bundle args = new Bundle();
            args.putString(RedditPostsCursorFragment.SUBREDDIT, mList.get(i).mSubreddit);
            fragment.setArguments(args);
            return fragment;
        }

        @Override
        public int getCount() {
            return mList.size();
        }

        @Override
        public CharSequence getPageTitle(int position) {
            return mList.get(position).mSubreddit;
        }
    }
}
