package ru.merkulyevsasha.movies.http;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

import rx.Observable;
import rx.schedulers.Schedulers;

public class ImageService {

    private ImageService mInterface;

    private ImageService(){

        RxJavaCallAdapterFactory adapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://image.tmdb.org/")
                .addCallAdapterFactory(adapter)
                .build();

        mInterface = retrofit.create(ImageService.class);
    }

    // https://habrahabr.ru/post/27108/
    private static volatile ImageService mInstance;
    public static ImageService getInstance() {
        if (mInstance == null) {
            synchronized (ImageService.class) {
                if (mInstance == null) {
                    mInstance = new ImageService();
                }
            }
        }
        return mInstance;
    }

    public Observable<byte[]> getImage(String size, String path, String language) {
        return mInterface.getImage(size, path, language);
    }

}
