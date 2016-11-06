# Movies

Простое приложение поиска/просмотра фильмов (The Movie Database API).

В приложение используются следующие фреймворки:
- RxJava
- Retrofit2

Подключен Firebase Crash Reporting service (файл с настройками google-services.json, я не выкладывал :)).

# Приложение содержит две Activity:

- Первая Activity отображает список найденных фильмов (android.support.v7.widget.RecyclerView и android.support.v7.widget.CardView).
- Вторая Activity отображает более подробную информацию о фильме.

Для загрузки списка фильмов и детальной информации о фильме используется возможности поддержки Retrofit2-ом RxJava:

```java

interface MovieInterface {
    @GET("search/movie")
    Observable<Movies> search(@Query("query") String query, @Query("api_key") String api, @Query("language") String language, @Query("page") int page);
    @GET("movie/{movie}")
    Observable<Details> details(@Path("movie") int movie, @Query("api_key") String api, @Query("language") String language);
}

```

В свзяи с этим работа по загрузке данных становится совсем простой:
```java

    MovieService service = MovieService.getInstance();
        mSubscription = service.search(queryText, mLocale, mPage)
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe(getSubscriber());

```

Для загрузки изображений, тоже используется Retrofit2, но другим способом:

```java

interface ImageInterface {
    @GET("t/p/{size}/{imagePath}")
    Call<ResponseBody> getImage(@Path("size") String size, @Path("imagePath") String path, @Query("language") String language);
}

    ImageService service = ImageService.getInstance();
    service.getImage(mImageWidth, mBackdropPath, mLocale)
        .enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    if (ImageService.DownloadImage(imageFile, response.body())) {
                        setImageBitmap(imageFile, image);
                    }
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                FirebaseCrash.report(t);
            }
        });
        
```

# Подключение Retrofit2 и RxJava

```
    compile 'com.squareup.retrofit2:retrofit:2.1.0'
    compile 'com.squareup.retrofit2:adapter-rxjava:2.1.0'
    compile 'com.squareup.retrofit2:converter-gson:2.1.0'
    compile 'com.squareup.okhttp:logging-interceptor:2.7.0'
    compile 'io.reactivex:rxjava:1.1.6'
    compile 'io.reactivex:rxandroid:1.2.1'
```
