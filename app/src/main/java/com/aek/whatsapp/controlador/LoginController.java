package com.aek.whatsapp.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aek.whatsapp.utils.ProgressDialogUtils;
import com.aek.whatsapp.vista.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

import java.lang.ref.WeakReference;

public class LoginController {

    public static void iniciarSesion(String correo, String contrasenia,
                                     WeakReference<Activity> activityWeakReference,
                                     WeakReference<TextInputEditText> textContraseniaWeakReference) {

        WeakReference<ProgressDialog> progressDialogWeakReference = ProgressDialogUtils.getDialogWeak(
                activityWeakReference.get(),null,"Iniciando sesión...");

        progressDialogWeakReference.get().show();

        FirebaseAuth.getInstance().signInWithEmailAndPassword(correo, contrasenia)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        Activity activity = activityWeakReference.get();
                        ProgressDialog progressDialog = progressDialogWeakReference.get();

                        if (activity != null && progressDialog != null) {
                            progressDialog.dismiss();
                            if (task.isSuccessful()) {

                                activity.startActivity(new Intent(activity, MainActivity.class));
                                activity.finish();

                            } else {
                                TextInputEditText txtContrasenia = textContraseniaWeakReference.get();

                                if (txtContrasenia != null) {
                                    txtContrasenia.setText("");
                                }

                                Toast.makeText(activity,
                                        "Se ha producido un error al intentar iniciar sesión",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });
    }
}
