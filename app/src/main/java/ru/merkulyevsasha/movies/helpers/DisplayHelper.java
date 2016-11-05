package ru.merkulyevsasha.movies.helpers;

import android.app.Activity;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import android.view.Display;

import ru.merkulyevsasha.movies.http.ImageService;

public class DisplayHelper {

    private static DisplayMetrics getDisplayMetrics(Activity activity){
        final Resources res = activity.getResources();
        Display display = activity.getWindowManager().getDefaultDisplay();
        DisplayMetrics dm = res.getDisplayMetrics();
        display.getMetrics(dm);
        return dm;
    }

    public static String getMainActivityImageWidth(Activity activity){
        DisplayMetrics dm = getDisplayMetrics(activity);

        String width = "";
        if (dm.widthPixels < 800)
            width = ImageService.W_300;
        else if (dm.widthPixels < 1500)
            width = ImageService.W_780;
        else
            width = ImageService.W_1280;

        return width;
    }

    public static String getDetailsActivityImageWidth(Activity activity){
        DisplayMetrics dm = getDisplayMetrics(activity);

        String width = "";
        if (dm.widthPixels < 800)
            width = ImageService.W_780;
        else
            width = ImageService.W_1280;

        return width;
    }

}
