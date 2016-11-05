package ru.merkulyevsasha.movies.http;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;

import retrofit2.converter.gson.GsonConverterFactory;
import rx.schedulers.Schedulers;

public class ImageService {

    public final static String MOVIES_IMAGES_FOLDER = "MoviesImages";
    public final static String W_780 = "w780";
    public final static String W_1280 = "w1280";
    public final static String W_300 = "w300";

    private ImageInterface mInterface;

    private ImageService(){

        RxJavaCallAdapterFactory adapter = RxJavaCallAdapterFactory.createWithScheduler(Schedulers.io());

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://image.tmdb.org/")
                .addCallAdapterFactory(adapter)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        mInterface = retrofit.create(ImageInterface.class);
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

    public Call<ResponseBody> getImage(String size, String path, String language) {
        return mInterface.getImage(size, path, language);
    }

    public static boolean DownloadImage(File imageFile, ResponseBody body) {
        boolean result = true;
        try {
            InputStream in = body.byteStream();
            FileOutputStream out = new FileOutputStream(imageFile);

            byte[] buffer = new byte[1024];
            int len;
            while ((len = in.read(buffer)) != -1) {
                out.write(buffer, 0, len);
            }

            in.close();
            out.close();

        } catch (IOException e) {
            result = false;
        }
        return result;
    }


}
