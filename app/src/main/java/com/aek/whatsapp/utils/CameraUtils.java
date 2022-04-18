package com.aek.whatsapp.utils;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.view.WindowManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.UUID;

public class CameraUtils {

    public static File getNewFile() {

        String root = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES).toString();

        File myDir = new File(root + "/WhatsApp");

        if (!myDir.exists())
        {
            myDir.mkdirs();
        }

        return new File(myDir, UUID.randomUUID().toString() + ".jpg");

        /*File sdCard = Environment.getExternalStorageDirectory();
        File directory = new File(sdCard + "/WhatsApp");

        if (!directory.exists()) {
            directory.mkdirs();
        }

        Date date = new Date();
        String timestamp = String.valueOf(date.getTime());
        String photoFile = directory.getAbsolutePath() + "/" + timestamp + ".jpg";

        return new File(photoFile);*/
    }

    public static int getDisplayRotation(Context context) {
        return ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
    }

    public static List<String> getImagesFromGallery(Context context) {
        List<String> listaImagenes = new ArrayList<>();

        Uri uri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;

        String[] projection = {MediaStore.MediaColumns.DATA};

        Cursor cursor = context.getContentResolver().query(uri, projection, null, null,
                "datetaken DESC limit 30");

        if (cursor != null)
        {
            int columnIndexData = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);

            while (cursor.moveToNext())
            {
                String absolutePath = cursor.getString(columnIndexData);
                listaImagenes.add(absolutePath);
            }
            cursor.close();

        }

        return listaImagenes;
    }

}
