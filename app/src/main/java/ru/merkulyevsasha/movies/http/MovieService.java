package ru.merkulyevsasha.movies.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import rx.Observable;
import rx.Subscription;
import rx.schedulers.Schedulers;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import ru.merkulyevsasha.movies.models.Details;
import ru.merkulyevsasha.movies.models.Movies;

public class MovieService {

    private final String API_KEY = "cb3669160734c367b5275ff3ea5ae417";

    private MovieInterface movieInterface;

    private MovieService(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        RxJavaCallAdapterFactory adapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(adapter)
                .build();

        movieInterface = retrofit.create(MovieInterface.class);
    }

    // https://habrahabr.ru/post/27108/
    private static volatile MovieService mInstance;
    public static MovieService getInstance() {
        if (mInstance == null) {
            synchronized (MovieService.class) {
                if (mInstance == null) {
                    mInstance = new MovieService();
                }
            }
        }
        return mInstance;
    }

    public Observable<Movies> search(String query, String language, int page) {
        return movieInterface.search(query, API_KEY, language, page);
    }

    public Observable<Details> details(int movie, String language) {
        return movieInterface.details(movie, API_KEY, language);
    }

    public static void unsubscribe(Subscription subscription){
        if (subscription != null && !subscription.isUnsubscribed()){
            subscription.unsubscribe();
        }
    }

}
