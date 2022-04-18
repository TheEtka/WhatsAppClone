package com.aek.whatsapp.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aek.whatsapp.models.Usuario;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.utils.ProgressDialogUtils;
import com.aek.whatsapp.vista.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import java.lang.ref.WeakReference;

public class RegistroController {

    public static void registrarUsuario(String nombre, String correo, String contrasenia,
                                        WeakReference<Activity> activityWeakReference) {

        WeakReference<ProgressDialog> progressDialogWeakReference =
                ProgressDialogUtils.getDialogWeak(activityWeakReference.get(),
                        null,"Registrando...");

        progressDialogWeakReference.get().show();

        FirebaseAuth.getInstance().createUserWithEmailAndPassword(correo, contrasenia)
                .addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {

                        if (task.isSuccessful()) {
                            guardarUsuarioEnDatabase(nombre, correo, activityWeakReference, progressDialogWeakReference);
                        } else {
                            Activity activity = activityWeakReference.get();
                            ProgressDialog progressDialog = progressDialogWeakReference.get();
                            if (activity != null && progressDialog != null) {
                                progressDialog.dismiss();
                                Toast.makeText(activity,
                                        "Error al intentar registrar al usuario",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });
    }

    private static void guardarUsuarioEnDatabase(String nombre, String correo,
                                                 WeakReference<Activity> activityWeakReference,
                                                 WeakReference<ProgressDialog> progressDialogWeakReference) {

        try {

            FirebaseUser firebaseUser = FirebaseAuth.getInstance().getCurrentUser();

            String uid = firebaseUser.getUid();
            long timestampRegistro = firebaseUser.getMetadata().getCreationTimestamp();

            Usuario usuario = new Usuario(uid, nombre, correo, timestampRegistro);

            FirebaseFirestore.getInstance().collection(FirebaseConstants.USERS)
                    .document(uid)
                    .set(usuario, SetOptions.merge())
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            Activity activity = activityWeakReference.get();
                            ProgressDialog progressDialog = progressDialogWeakReference.get();

                            if (activity != null && progressDialog != null) {
                                progressDialog.dismiss();

                                if (task.isSuccessful()) {
                                    Intent intent = new Intent(activity, MainActivity.class);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    activity.startActivity(intent);
                                } else {
                                    Toast.makeText(activity,"Error al intentar guardar los" +
                                            " datos del usuario en db", Toast.LENGTH_SHORT).show();
                                }
                            }
                        }
                    });

        } catch (NullPointerException e) {
            e.getCause();
        }
    }
}
