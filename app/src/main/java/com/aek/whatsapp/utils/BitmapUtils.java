package com.aek.whatsapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.net.Uri;
import android.view.View;

import androidx.core.content.FileProvider;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

public class BitmapUtils {

    public static Bitmap getBitmapFromView(View view) {
        Bitmap bitmap = Bitmap.createBitmap(view.getWidth(), view.getHeight(),
                Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        view.draw(canvas);
        return bitmap;
    }

    public static Uri getUri(Bitmap bitmap, Context context) {
        File cachePath = new File(context.getExternalCacheDir(), "my_images/");
        cachePath.mkdirs();

        File file = new File(cachePath, UUID.randomUUID().toString() + ".jpg");
        FileOutputStream fileOutputStream;

        try {
            fileOutputStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fileOutputStream);
            fileOutputStream.flush();
            fileOutputStream.close();
        } catch (IOException e) {
            e.getCause();
        }
        return FileProvider.getUriForFile(context, context.getPackageName() + ".provider", file);
    }

}
