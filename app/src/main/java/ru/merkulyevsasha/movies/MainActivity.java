package ru.merkulyevsasha.movies;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ProgressBar;

import com.google.firebase.crash.FirebaseCrash;

import java.io.File;
import java.util.ArrayList;
import java.util.Locale;

import butterknife.Bind;
import butterknife.ButterKnife;

import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

import ru.merkulyevsasha.movies.adapters.RecyclerViewAdapter;
import ru.merkulyevsasha.movies.adapters.DownScrollListener;
import ru.merkulyevsasha.movies.http.ImageService;
import ru.merkulyevsasha.movies.http.MovieService;
import ru.merkulyevsasha.movies.models.Movie;
import ru.merkulyevsasha.movies.models.Movies;

public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerViewAdapter mAdapter;

    private int mPage;
    private String mQueryText;
    private String mNewQueryText;

    private String mLocale;

    @Bind(R.id.content_main)
    public View mRootView;

    @Bind(R.id.action_search)
    public SearchView mSearchView;

    @Bind(R.id.recyclerView)
    public RecyclerView mRecyclerView;

    @Bind(R.id.progressBar)
    public ProgressBar mProgressBar;

    private Subscription mSubscription;
    private DownScrollListener mDownScrollListener;
    private GridLayoutManager mLayoutManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        toolbar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideSearchBar(false);
            }
        });

        mSearchView.setOnQueryTextListener(this);

        hideSearchBar(false);

        mLocale = Locale.getDefault().getLanguage();

        File mImageFolder = new File(this.getFilesDir(), ImageService.MOVIES_IMAGES_FOLDER);
        mImageFolder.mkdirs();

        mLayoutManager = new GridLayoutManager(this, 2);
        mRecyclerView.setLayoutManager(mLayoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerViewAdapter(this, new ArrayList<Movie>());
        mRecyclerView.setAdapter(mAdapter);

        mDownScrollListener = new DownScrollListener(mLayoutManager);

        mDownScrollListener.LoadMore = new Runnable() {
            @Override
            public void run() {
                mPage++;
                search(mQueryText);
            }
        };

        mRecyclerView.addOnScrollListener(mDownScrollListener);

        if (savedInstanceState != null){
            mQueryText = savedInstanceState.getString("mQueryText");
            if (mQueryText != null && !mQueryText.isEmpty()) {
                setTitle(getActivityTitle());
            }

            mNewQueryText = savedInstanceState.getString("mNewQueryText");
            if (mNewQueryText != null && !mNewQueryText.isEmpty()){
                hideSearchBar(false);
                mSearchView.setQuery(mNewQueryText, false);
            }

            mPage = savedInstanceState.getInt("mPage");
            mDownScrollListener.mTotalPages = savedInstanceState.getInt("mTotalPages");
            mDownScrollListener.mTotalResults = savedInstanceState.getInt("mTotalResults");
            mDownScrollListener.mPageSize = savedInstanceState.getInt("mPageSize");
            mDownScrollListener.mPage = mPage;

            int visibleItemPosition = savedInstanceState.getInt("visibleItemPosition");
            ArrayList movies = savedInstanceState.getParcelableArrayList("movies");
            if (movies != null && movies.size() > 0){
                mAdapter.Items = movies;
                mAdapter.notifyDataSetChanged();
                mRecyclerView.scrollToPosition(visibleItemPosition);
            }
        }

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("mQueryText", mQueryText);
        savedInstanceState.putString("mNewQueryText", mNewQueryText);
        savedInstanceState.putInt("mPage", mPage);

        savedInstanceState.putInt("mTotalPages", mDownScrollListener.mTotalPages);
        savedInstanceState.putInt("mTotalResults", mDownScrollListener.mTotalResults);
        savedInstanceState.putInt("mPageSize", mDownScrollListener.mPageSize);

        savedInstanceState.putInt("visibleItemPosition", mLayoutManager.findFirstCompletelyVisibleItemPosition());

        savedInstanceState.putParcelableArrayList("movies", (ArrayList)mAdapter.Items);

    }

    @Override
    public boolean onQueryTextSubmit(final String queryText) {
        if (queryText.length() < 3) {
            Snackbar.make(this.findViewById(R.id.content_main), R.string.search_validation_message, Snackbar.LENGTH_LONG)
                    .show();
            return false;
        }
        mPage = 1;
        mNewQueryText = "";
        mQueryText = queryText;

        hideSearchBar(true);

        search(mQueryText);
        return false;
    }

    private void hideSearchBar(boolean hide){
        mSearchView.setIconified(hide);
        if (hide){
            mSearchView.onActionViewCollapsed();
        } else {
            mSearchView.onActionViewExpanded();
        }
    }

    private String getActivityTitle(){
        return getString(R.string.app_name) +  ": \""+mQueryText+"\"";
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        mNewQueryText = newText;
        return false;
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        MovieService.unsubscribe(mSubscription);
    }

    private void search(String queryText){
        MovieService service = MovieService.getInstance();
        MovieService.unsubscribe(mSubscription);
        mSubscription = service.search(queryText, mLocale, mPage)
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(getSubscriber());
    }

    private Subscriber<Movies> getSubscriber(){
        mProgressBar.setVisibility(View.VISIBLE);
        return new Subscriber<Movies>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                mProgressBar.setVisibility(View.GONE);
                mDownScrollListener.mLoading = false;
                FirebaseCrash.report(e);
                Snackbar.make(mRootView.findViewById(R.id.content_main), R.string.search_nofound_message, Snackbar.LENGTH_LONG)
                        .show();
            }

            @Override
            public void onNext(Movies movies) {
                mProgressBar.setVisibility(View.GONE);
                mDownScrollListener.mLoading = false;
                if (movies.results.size() > 0) {

                    mDownScrollListener.mPage = mPage;
                    mDownScrollListener.mTotalPages = movies.totalPages;
                    mDownScrollListener.mTotalResults = movies.totalResults;
                    mDownScrollListener.mPageSize = movies.totalResults >  DownScrollListener.PAGE_SIZE
                    ? DownScrollListener.PAGE_SIZE
                    : movies.totalResults;

                    if (mPage > 1) {
                        mAdapter.Items.addAll(movies.results);
                        mDownScrollListener.mPageSize = mAdapter.Items.size();
                    } else{
                        mAdapter.Items = movies.results;
                        mRecyclerView.scrollToPosition(0);
                    }
                    mAdapter.notifyDataSetChanged();

                    setTitle(getActivityTitle());

                } else {
                    Snackbar.make(mRootView.findViewById(R.id.content_main), R.string.search_nofound_message, Snackbar.LENGTH_LONG)
                            .show();
                }
            }
        };
    }

}
