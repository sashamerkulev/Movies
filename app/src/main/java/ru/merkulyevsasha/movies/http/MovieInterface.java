package ru.merkulyevsasha.movies.http;

import retrofit.Call;
import retrofit.http.GET;
import retrofit.http.Path;
import retrofit.http.Query;

import ru.merkulyevsasha.movies.models.Details;
import ru.merkulyevsasha.movies.models.Movies;

public interface MovieInterface {

    @GET("movie/{request}")
    Call<Movies> movies(@Path("request") String request, @Query("api_key") String api, @Query("language") String language, @Query("page") int page);

    @GET("search/movie")
    Call<Movies> search(@Query("query") String query, @Query("api_key") String api, @Query("language") String language, @Query("page") int page);

    @GET("movie/{movie}")
    Call<Details> details(@Path("movie") int movie, @Query("api_key") String api, @Query("language") String language, @Query("page") int page);

}
