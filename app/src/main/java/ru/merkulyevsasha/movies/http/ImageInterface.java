package ru.merkulyevsasha.movies.http;

import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

import rx.Observable;

import ru.merkulyevsasha.movies.models.Movies;

public interface ImageInterface {
    @GET("t/{size}/{path}")
    Observable<byte[]> image(@Path("size") String size, @Path("path") String path, @Query("language") String language);
}
