package in.vasudev.capstone_stage_2;

import com.devbrackets.android.recyclerext.adapter.RecyclerCursorAdapter;
import com.squareup.picasso.Picasso;

import android.app.Activity;
import android.database.Cursor;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.Loader;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.StaggeredGridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import butterknife.BindView;
import butterknife.ButterKnife;
import in.vasudev.capstone_stage_2.utils.IntentUtils;

/**
 * Created by vineet on 27-Aug-16.
 */
public class RedditPostsCursorFragment extends Fragment
        implements LoaderManager.LoaderCallbacks<Cursor> {

    public static final String SUBREDDIT = "subreddit_text";

    private String mSubreddit;

    @BindView(R.id.recycler_view)
    RecyclerView mRecyclerView;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle args = getArguments();
        mSubreddit = args.getString(SUBREDDIT);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(0, null, this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater,
            ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_reddit_posts, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new SubmissionsCursorLoader(getActivity(), mSubreddit);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        if (data != null) {
            RecyclerView.Adapter adapter = new MyAdapter(data, getActivity());
            adapter.setHasStableIds(true);
            mRecyclerView.setAdapter(adapter);
            int columnCount = getResources().getInteger(R.integer.list_column_count);
            StaggeredGridLayoutManager sglm =
                    new StaggeredGridLayoutManager(columnCount,
                            StaggeredGridLayoutManager.VERTICAL);
            mRecyclerView.setLayoutManager(sglm);
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mRecyclerView.setAdapter(null);
    }

    private class MyAdapter extends RecyclerCursorAdapter<ViewHolder> {

        private Activity mContext;

        public MyAdapter(Cursor cursor, Activity context) {
            super(cursor);
            mContext = context;
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = mContext.getLayoutInflater()
                    .inflate(R.layout.list_item_submission, parent, false);
            view.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    getCursor().moveToPosition((Integer) view.getTag());
                    IntentUtils.openWebPage(mContext, getCursor().getString(11));
                }
            });
            final ViewHolder vh = new ViewHolder(view);
            return vh;
        }

        @Override
        public void onBindViewHolder(final ViewHolder holder, Cursor cursor,
                int position) {
            holder.itemView.setTag(position);
            holder.titleView.setText(cursor.getString(4));
            holder.subredditNameView.setText(cursor.getString(5));

            holder.thumbnailView.setVisibility(View.VISIBLE);
            String url = cursor.getString(1);
            if (url != null) {
                Picasso.with(mContext)
                        .load(url)
                        .into(holder.thumbnailView);
            } else {
                holder.thumbnailView.setVisibility(View.GONE);
            }
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public ImageView thumbnailView;

        public TextView titleView;

        public TextView subredditNameView;

        public ViewHolder(View view) {
            super(view);
            thumbnailView = (ImageView) view.findViewById(R.id.thumbnail);
            titleView = (TextView) view.findViewById(R.id.title);
            subredditNameView = (TextView) view.findViewById(R.id.subreddit_name);
        }
    }
}
