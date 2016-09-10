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

import butterknife.BindView;
import butterknife.ButterKnife;

public class BaseMainActivity extends AppCompatActivity {

    private static final String TAG = "BaseMainActivity";

    @BindView(R.id.my_toolbar)
    Toolbar mToolbar;

    @BindView(R.id.drawer_layout)
    DrawerLayout mDrawerLayout;

    @BindView(R.id.pager)
    ViewPager mViewPager;

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

        mViewPager.setOffscreenPageLimit(1);
        mViewPager.setAdapter(
                new NewsfeedViewPagerAdapter(getSupportFragmentManager()));

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
            Fragment fragment = new RedditPostsCursorFragment();
            Bundle args = new Bundle();
            args.putString(RedditPostsCursorFragment.SUBREDDIT, mList.get(i));
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
