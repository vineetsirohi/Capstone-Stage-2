package in.vasudev.capstone_stage_2;

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
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.List;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.vasudev.capstone_stage_2.model.SubredditsModel;
import in.vasudev.capstone_stage_2.model.SubredditsTable;

public class BaseMainActivity extends AppCompatActivity {

    private static final String TAG = "BaseMainActivity";

    private static final String D_ADD_SUB = "dAddSub";

    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.pager)
    ViewPager mViewPager;

    private SubredditsViewPagerAdapter mViewPagerAdapter;

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
        return super.onOptionsItemSelected(item);
    }

    private void addSubreddit() {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fm.beginTransaction();
        Fragment fragment = fm.findFragmentByTag(D_ADD_SUB);
        if (fragment != null) {
            fragmentTransaction.remove(fragment);
        }
        fragmentTransaction.addToBackStack(null);
        new AddSubredditDialog().show(fragmentTransaction, D_ADD_SUB);
    }

    protected void goToNextLevel() {
//        mLevelTextView.setText(String.valueOf(++mLevel));
    }

    public void reloadSubredditsList(String newSubreddit) {
        mViewPagerAdapter.addSubreddit(newSubreddit);
        mViewPagerAdapter.notifyDataSetChanged();
        mViewPager.setCurrentItem(mViewPagerAdapter.getCount());
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
