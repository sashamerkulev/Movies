package ru.merkulyevsasha.movies;

import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import java.io.IOException;
import java.util.ArrayList;

import butterknife.Bind;
import ru.merkulyevsasha.movies.adapters.RecyclerViewAdapter;
import ru.merkulyevsasha.movies.http.MovieService;
import ru.merkulyevsasha.movies.models.Movie;
import ru.merkulyevsasha.movies.models.Movies;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;


public class MainActivity extends AppCompatActivity implements SearchView.OnQueryTextListener {

    private RecyclerViewAdapter mAdapter;

    private int mPage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        GridLayoutManager layoutManager = new GridLayoutManager(this, 2);
        RecyclerView mRecyclerView = (RecyclerView) findViewById(R.id.recyclerView);
        mRecyclerView.setLayoutManager(layoutManager);
        //recyclerView.setHasFixedSize(true);
        mAdapter = new RecyclerViewAdapter(this, new ArrayList<Movie>());
        mRecyclerView.setAdapter(mAdapter);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        MenuItem searchItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) MenuItemCompat.getActionView(searchItem);
        searchView.setOnQueryTextListener(this);
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

        Subscriber<Movies> moviesSubscriber = new Subscriber<Movies>() {
            @Override
            public void onCompleted() {
            }

            @Override
            public void onError(Throwable e) {
//            Snackbar.make(this.findViewById(R.id.content_main), R.string.search_nofound_message, Snackbar.LENGTH_LONG)
//                    .setAction("Action", null)
//                    .show();
            }

            @Override
            public void onNext(Movies movies) {
                if (movies.results.size() > 0) {
                    mAdapter.Items = movies.results;
                    mAdapter.notifyDataSetChanged();
                } else {
//            Snackbar.make(this.findViewById(R.id.content_main), R.string.search_nofound_message, Snackbar.LENGTH_LONG)
//                    .setAction("Action", null)
//                    .show();
                }
            }
        };

        Observable.create(new Observable.OnSubscribe<Movies>() {
            @Override
            public void call(Subscriber<? super Movies> subscriber) {
                try {
                    MovieService service = new MovieService();
                    Movies result = service.search(queryText, "ru", mPage);
                    subscriber.onNext(result);
                } catch (IOException e) {
                    e.printStackTrace();
                    Movies emptyMovies = new Movies();
                    emptyMovies.results = new ArrayList<Movie>();
                    subscriber.onNext(emptyMovies);
                } finally {
                    subscriber.onCompleted();
                }
            }
        })
                .subscribeOn(Schedulers.newThread())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(moviesSubscriber);

        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        return false;
    }
}
