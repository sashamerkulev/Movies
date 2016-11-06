package ru.merkulyevsasha.movies;

import android.os.Bundle;
import android.os.Parcelable;
import android.os.PersistableBundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

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

    private String mLocale;

    @Bind(R.id.content_main)
    public View mRootView;

    private SearchView mSearchView;

    private Subscription mSubscription;

    private File mImageFolder;

    private DownScrollListener mDownScrollListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mLocale = Locale.getDefault().getLanguage();

        mImageFolder = new File(this.getFilesDir(), ImageService.MOVIES_IMAGES_FOLDER);
        mImageFolder.mkdirs();

        final GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        mRecyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerViewAdapter(this, new ArrayList<Movie>());
        mRecyclerView.setAdapter(mAdapter);

        mDownScrollListener = new DownScrollListener(layoutManager);

        mDownScrollListener.LoadMore = new Runnable() {
            @Override
            public void run() {
                mPage++;
                search(mQueryText);
            }
        };

        mRecyclerView.addOnScrollListener(mDownScrollListener);

    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);

        savedInstanceState.putString("mQueryText", mQueryText);
        savedInstanceState.putInt("mPage", mPage);

        //savedInstanceState.putParcelableArray("contact", mAdapter.Items);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        mSearchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        mSearchView.setOnQueryTextListener(this);

        hideSearchBar(false);

        return true;
    }

    @Override
    public boolean onQueryTextSubmit(final String queryText) {
        if (queryText.length() < 5) {
            Snackbar.make(this.findViewById(R.id.content_main), R.string.search_validation_message, Snackbar.LENGTH_LONG)
                    .setAction("Action", null)
                    .show();
            return false;
        }

        mPage = 1;
        mQueryText = queryText;

        hideSearchBar(true);

        setTitle(getActivityTitle());

        search(mQueryText);
        return false;
    }

    private void hideSearchBar(boolean hide){
        mSearchView.setIconified(hide);
        if (hide){
            mSearchView.onActionViewCollapsed();
        }
    }

    private String getActivityTitle(){
        return getString(R.string.app_name) +  ": \""+mQueryText+"\"";
    }

    @Override
    public boolean onQueryTextChange(String newText) {
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
        return new Subscriber<Movies>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
                FirebaseCrash.report(e);
                Snackbar.make(mRootView.findViewById(R.id.content_main), R.string.search_nofound_message, Snackbar.LENGTH_LONG)
                        .setAction("Action", null)
                        .show();
            }

            @Override
            public void onNext(Movies movies) {
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
                    }
                    mAdapter.notifyDataSetChanged();
                } else {
                    Snackbar.make(mRootView.findViewById(R.id.content_main), R.string.search_nofound_message, Snackbar.LENGTH_LONG)
                            .setAction("Action", null)
                            .show();
                }
            }
        };
    }

}
