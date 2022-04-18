package com.aek.whatsapp.controlador;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.res.AssetManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aek.whatsapp.models.ContactoEstado;
import com.aek.whatsapp.models.EspectadorEstado;
import com.aek.whatsapp.models.Estado;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.utils.ProgressDialogUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Random;
import java.util.UUID;

public class EstadosController {

    public static int getRandomColor() {
        Random random = new Random();
        return Color.argb(255,
                random.nextInt(256),
                random.nextInt(256),
                random.nextInt(256));
    }

    public static Typeface getTypeFace(int positionTypeFace, AssetManager assetManager) {
        switch (positionTypeFace) {
            case 1:
                return createFromAsset("jacksilver", assetManager);
            case 2:
                return createFromAsset("retrowmentho", assetManager);
            case 0:
            default:
                return Typeface.SANS_SERIF;
        }
    }

    private static Typeface createFromAsset(String nameTypeface, AssetManager assetManager) {
        return Typeface.createFromAsset(assetManager, "fonts/" + nameTypeface + ".ttf");
    }

    public static void publicarEstadoEnStorage(Uri uriEstado, WeakReference<Activity> activityWeakReference) {

        WeakReference<ProgressDialog> progressDialogWeakReference =
                ProgressDialogUtils.getDialogWeak(activityWeakReference.get(), null, null);

        progressDialogWeakReference.get().show();

        StorageReference filePath = FirebaseStorage.getInstance().getReference()
                .child(FirebaseConstants.ESTADOS)
                .child(FbUser.getCurrentUserId())
                .child(UUID.randomUUID().toString()+".jpg");

        filePath.putFile(uriEstado).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        publicarEstadoEnDB(uri.toString(), activityWeakReference, progressDialogWeakReference);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Activity activity = activityWeakReference.get();
                ProgressDialog progressDialog = progressDialogWeakReference.get();
                if (activity != null && progressDialog != null) {
                    progressDialog.dismiss();
                    Toast.makeText(activity, "Error al subir el estado al storage", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                ProgressDialog progressDialog = progressDialogWeakReference.get();
                if (progressDialog != null) {
                    double progress = (100.0 * snapshot.getBytesTransferred()) /
                            snapshot.getTotalByteCount();

                    progressDialog.setMessage("Publicando estado: " + (int) progress + "%");
                }
            }
        });

    }

    private static void publicarEstadoEnDB(String imgEstado,
                                           WeakReference<Activity> activityWeakReference,
                                           WeakReference<ProgressDialog> progressDialogWeakReference) {

        String estadoId = FirebaseFirestore.getInstance().collection(FirebaseConstants.ESTADOS).document().getId();
        long timestamp = System.currentTimeMillis();
        ContactoEstado contactoEstado = new ContactoEstado(timestamp);
        Estado estado = new Estado(FbUser.getCurrentUserId(), imgEstado, estadoId, timestamp);
        //////////////////
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        WriteBatch writeBatch = db.batch();
        //////////////////
        DocumentReference documentEstado = db.collection(FirebaseConstants.ESTADOS)
                .document(FbUser.getCurrentUserId())
                .collection(FirebaseConstants.ESTADOS)
                .document(estadoId);

        DocumentReference documentContactoEstado = db.collection(FirebaseConstants.CONTACTOS_ESTADOS)
                .document(FbUser.getCurrentUserId());
        ////////////////
        writeBatch.set(documentEstado, estado);
        writeBatch.set(documentContactoEstado, contactoEstado);
        ///////////////
        writeBatch.commit().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                ProgressDialog progressDialog = progressDialogWeakReference.get();
                Activity activity = activityWeakReference.get();
                if (progressDialog != null && activity != null) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        activity.finish();
                    } else {
                        Toast.makeText(activity, "Error al publicar el estado en la base de datos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    public static void addVisita(String estadoId) {
        EspectadorEstado espectadorEstado = new EspectadorEstado(FbUser.getCurrentUserId(),
                System.currentTimeMillis());

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.ESPECTADORES_ESTADOS)
                .document(estadoId)
                .collection(FirebaseConstants.ESPECTADORES_ESTADOS)
                .document(FbUser.getCurrentUserId())
                .set(espectadorEstado, SetOptions.merge());
    }

    public static void getNrEspectadores(String estadoId,
                                         WeakReference<LinearLayout> linearLayoutWeakReference,
                                         WeakReference<TextView> textViewWeakReference) {

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.ESPECTADORES_ESTADOS)
                .document(estadoId)
                .collection(FirebaseConstants.ESPECTADORES_ESTADOS)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {

                try {

                    if (task.isSuccessful() && task.getResult() != null) {
                        LinearLayout layout = linearLayoutWeakReference.get();
                        TextView txtNrEspectadores = textViewWeakReference.get();
                        if (layout != null && txtNrEspectadores != null) {
                            List<DocumentSnapshot> list = task.getResult().getDocuments();
                            int nrEspectadores = list.size();

                            if (nrEspectadores > 0) {
                                layout.setVisibility(View.VISIBLE);
                                txtNrEspectadores.setText(String.valueOf(nrEspectadores));
                            } else {
                                layout.setVisibility(View.GONE);
                            }
                        }
                    }

                } catch (NullPointerException e) {
                    e.getCause();
                }

            }
        });

    }

    public static void eliminarEstadoFromStorage(String estadoUrl, String estadoId,
                                                 WeakReference<Activity> activityWeakReference) {

        WeakReference<ProgressDialog> progressDialogWeakReference =
                ProgressDialogUtils.getDialogWeak(activityWeakReference.get(), null,
                        "Eliminando estado...");
        progressDialogWeakReference.get().show();

        FirebaseStorage.getInstance()
                .getReferenceFromUrl(estadoUrl)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    eliminarEstadoFromDatabase(estadoId, activityWeakReference, progressDialogWeakReference);
                } else {
                    Activity activity = activityWeakReference.get();
                    if (activity != null) {
                        Toast.makeText(activity, "Error al eliminar el estado desde storage", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

    private static void eliminarEstadoFromDatabase(String estadoId, WeakReference<Activity> activityWeakReference,
                                                   WeakReference<ProgressDialog> progressDialogWeakReference) {

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.ESTADOS)
                .document(FbUser.getCurrentUserId())
                .collection(FirebaseConstants.ESTADOS)
                .document(estadoId)
                .delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                Activity activity = activityWeakReference.get();
                ProgressDialog progressDialog = progressDialogWeakReference.get();

                if (activity != null && progressDialog != null) {
                    progressDialog.dismiss();
                    if (task.isSuccessful()) {
                        activity.finish();
                    } else {
                        Toast.makeText(activity, "Error al eliminar el estado desde la base de datos", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });

    }

}
