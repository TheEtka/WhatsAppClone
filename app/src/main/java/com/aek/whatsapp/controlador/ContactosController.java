package com.aek.whatsapp.controlador;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aek.whatsapp.utils.FirebaseConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;

public class ContactosController {

    public static void getNrContactor(WeakReference<TextView> txtNrContactosWeakReference,
                                      WeakReference<Context> contextWeakReference) {

        FirebaseFirestore.getInstance().collection(FirebaseConstants.USERS)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                try {

                    if (task.isSuccessful() && task.getResult() != null) {
                        int nrContacots = task.getResult().size() - 1;

                        TextView txtNrContactos = txtNrContactosWeakReference.get();
                        if (txtNrContactos != null) {
                            txtNrContactos.setText(nrContacots + " contactos");
                        }
                    } else {
                        Context context = contextWeakReference.get();
                        if (context != null) {
                            Toast.makeText(context, "Error al intentar obtener el" +
                                    "nr de usuarios", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (NullPointerException e) {
                    e.getCause();
                }

            }
        });
    }
}
