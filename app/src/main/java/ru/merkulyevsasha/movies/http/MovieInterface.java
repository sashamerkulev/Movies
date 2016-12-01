package ru.merkulyevsasha.movies.http;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import rx.Observable;

import ru.merkulyevsasha.movies.models.Details;
import ru.merkulyevsasha.movies.models.Movies;
import ru.merkulyevsasha.movies.models.Credits;

interface MovieInterface {

    @GET("search/movie")
    Observable<Movies> search(@Query("query") String query, @Query("api_key") String api, @Query("language") String language, @Query("page") int page);

    @GET("movie/{movie}")
    Observable<Details> details(@Path("movie") int movie, @Query("api_key") String api, @Query("language") String language);

    @GET("movie/{movie}/credits")
    Observable<Credits> credits(@Path("movie") int movie, @Query("api_key") String api/*, @Query("language") String language*/);

}
