package ru.merkulyevsasha.movies.adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;

public class DownScrollListener extends RecyclerView.OnScrollListener {

    public final static int PAGE_SIZE = 20;

    private final int DOWN = 1;
    private final int UP = 1;

    private int mVisibleThreshold = 0;
    private int mLastVisibleItemPosition = 0;
    private int mScrollDirection=0;

    public int mPage;
    public int mPageSize;
    public int mTotalPages;
    public int mTotalResults;

    public Runnable LoadMore;

    RecyclerView.LayoutManager mLayoutManager;

    public DownScrollListener(RecyclerView.LayoutManager layoutManager){

        mLayoutManager = layoutManager;

        mVisibleThreshold = ((GridLayoutManager)layoutManager).getSpanCount()*5;
        mLastVisibleItemPosition = 0;
        mScrollDirection=0;

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        int visibleItemPosition = ((GridLayoutManager)mLayoutManager).findLastVisibleItemPosition();

        mScrollDirection = visibleItemPosition > mLastVisibleItemPosition
                ? DOWN
                : UP;

        mLastVisibleItemPosition = visibleItemPosition;

        if (mScrollDirection == DOWN && mPage < mTotalPages && (mLastVisibleItemPosition + mVisibleThreshold) > mPageSize) {
            mPage++;
            if (LoadMore != null){
                LoadMore.run();
            }
        }

    }
}
