package com.aek.whatsapp.utils;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.aek.whatsapp.R;
import com.aek.whatsapp.vista.menu.fragments.PerfilFragment;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.util.UUID;

public class CropImage {

    @RequiresApi(api = Build.VERSION_CODES.M)
    public static void cropMiniaturaFromFragment(Uri uri, Context context, Fragment fragment) {

        try {

            String fileName = UUID.randomUUID().toString() + ".jpg";

            UCrop uCrop = UCrop.of(uri, Uri.fromFile(new File(context.getCacheDir(), fileName)));

            uCrop.withAspectRatio(1,1);
            uCrop.withMaxResultSize(500,500);

            UCrop.Options options = new UCrop.Options();
            options.setCompressionQuality(90);
            options.setCompressionFormat(Bitmap.CompressFormat.JPEG);
            //options.setStatusBarColor(context.getResources().getColor(R.color.colorPrimaryDark,null));
            //options.setToolbarColor(context.getResources().getColor(R.color.colorPrimaryDark,null));
            options.setToolbarTitle("Recortar foto");

            uCrop.withOptions(options);

            uCrop.start(context, fragment, PerfilFragment.IMG_CROP_CODE);

        } catch (NullPointerException e) {
            e.getCause();
        }

    }
}
