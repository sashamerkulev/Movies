package ru.merkulyevsasha.movies.adapters;

import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;

public class DownScrollListener extends RecyclerView.OnScrollListener {

    public final static int PAGE_SIZE = 20;

    private int mVisibleThreshold = 0;
    private int mLastVisibleItemPosition = 0;
    private int mScrollDirection=0;

    public boolean mLoading = false;

    public int mPage;
    public int mPageSize;
    public int mTotalPages;
    public int mTotalResults;

    public Runnable LoadMore;

    private final RecyclerView.LayoutManager mLayoutManager;

    public DownScrollListener(RecyclerView.LayoutManager layoutManager){

        mLayoutManager = layoutManager;

        mVisibleThreshold = ((GridLayoutManager)layoutManager).getSpanCount()*7;
        mLastVisibleItemPosition = 0;
        mScrollDirection=0;

    }

    @Override
    public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
        final int DOWN = 1;
        final int UP = 2;

        int visibleItemPosition = ((GridLayoutManager)mLayoutManager).findLastVisibleItemPosition();

        mScrollDirection = visibleItemPosition > mLastVisibleItemPosition
                ? DOWN
                : UP;

        //Log.d("DownScrollListener", "onScrolled: "+ visibleItemPosition + ", "+ mLastVisibleItemPosition);
        if (mLastVisibleItemPosition == visibleItemPosition){
            recyclerView.stopScroll();
        }

        mLastVisibleItemPosition = visibleItemPosition;

        if (!mLoading && mScrollDirection == DOWN && mPage < mTotalPages && (mLastVisibleItemPosition + mVisibleThreshold) > mPageSize) {
            mLoading = true;
            if (LoadMore != null){
                recyclerView.stopScroll();
                LoadMore.run();
            }
        }

    }
}
