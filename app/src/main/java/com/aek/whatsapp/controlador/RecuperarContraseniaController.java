package com.aek.whatsapp.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aek.whatsapp.utils.ProgressDialogUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.ref.WeakReference;

public class RecuperarContraseniaController {

    public static void recuperarContrasenia(String correo,
                                            WeakReference<Activity> activityWeakReference,
                                            WeakReference<TextInputEditText> textCorreoWeakReference) {

        WeakReference<ProgressDialog> progressDialogWeakReference = ProgressDialogUtils.getDialogWeak(
                activityWeakReference.get(),null,"Recuperando Contraseña...");

        progressDialogWeakReference.get().show();

        FirebaseAuth.getInstance()
                .sendPasswordResetEmail(correo)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Activity activity = activityWeakReference.get();
                TextInputEditText txtCorreo = textCorreoWeakReference.get();
                ProgressDialog progressDialog = progressDialogWeakReference.get();

                if (activity != null && txtCorreo != null && progressDialog != null) {
                    progressDialog.dismiss();
                    txtCorreo.setText("");
                    if (task.isSuccessful()) {
                        Toast.makeText(activity,
                                "Se ha enviado un correo para poder cambiar la contraseña",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(activity,
                                "Se ha producido un error al intentar recuperar la contraseña",
                                Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }
}
