package ru.merkulyevsasha.movies.http;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;

import retrofit.Call;
import retrofit.GsonConverterFactory;
import retrofit.Retrofit;
import ru.merkulyevsasha.movies.models.Details;
import ru.merkulyevsasha.movies.models.Movies;

public class MovieService {

    private final String API_KEY = "cb3669160734c367b5275ff3ea5ae417";

    private MovieInterface movieInterface;

    public MovieService(){
        Gson gson = new GsonBuilder()
                .setDateFormat("yyyy-MM-dd")
                .create();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.themoviedb.org/3/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        movieInterface = retrofit.create(MovieInterface.class);
    }

    public Movies latest(String language, int page) throws IOException {
        Call<Movies> movies = movieInterface.movies("latest", API_KEY, language, page);
        return movies.execute().body();
    }

    public Movies popular(String language, int page) throws IOException {
        Call<Movies> movies = movieInterface.movies("popular", API_KEY, language, page);
        return movies.execute().body();
    }

    public Movies search(String query, String language, int page) throws IOException {
        Call<Movies> movies = movieInterface.search(query, API_KEY, language, page);
        return movies.execute().body();
    }

    public Details details(int movie, String language, int page) throws IOException {
        Call<Details> movies = movieInterface.details(movie, API_KEY, language, page);
        return movies.execute().body();
    }

}
