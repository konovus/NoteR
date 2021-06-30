package com.konovus.noter.util;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.preference.PreferenceManager;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

public class StorageUtils {

    public static String saveToInternalStorage(Bitmap bitmapImage, Context context, String imageName){
        // path to /android/data/yourapp/files/images
        File directory = new File(context.getExternalFilesDir("/").getAbsolutePath()+"/images/");
        if(!directory.exists())
            directory.mkdir();
        // Create imageDir
        File mypath = new File(directory,imageName);

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(mypath);
            // Use the compress method on the BitMap object to write image to the OutputStream
            bitmapImage.compress(Bitmap.CompressFormat.JPEG, 100, fos);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                fos.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return directory.getAbsolutePath();
    }

    public static Bitmap loadImageFromStorage(String path, String imageName)
    {
        File f = new File(path, imageName + ".jpg");

        Bitmap b = BitmapFactory.decodeFile(f.getAbsolutePath());

        return b;
    }
}
