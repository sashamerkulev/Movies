package ru.merkulyevsasha.movies.http;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.Query;

interface ImageInterface {

    @GET("t/p/{size}/{imagePath}")
    Call<ResponseBody> getImage(@Path("size") String size, @Path("imagePath") String path, @Query("language") String language);
}
