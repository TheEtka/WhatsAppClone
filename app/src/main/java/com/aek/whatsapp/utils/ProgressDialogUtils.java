package com.aek.whatsapp.utils;

import android.app.ProgressDialog;
import android.content.Context;

import java.lang.ref.WeakReference;

public class ProgressDialogUtils {

    public static WeakReference<ProgressDialog> getDialogWeak(Context context, String title, String message) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setCancelable(false);
        if (title != null) {
            progressDialog.setTitle(title);
        }
        if (message != null) {
            progressDialog.setMessage(message);
        }

        return new WeakReference<>(progressDialog);
    }
}
