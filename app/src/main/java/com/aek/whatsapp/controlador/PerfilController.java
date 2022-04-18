package com.aek.whatsapp.controlador;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.utils.ProgressDialogUtils;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class PerfilController {

    public static void actualizarInfoUsuario(String key,
                                             String value,
                                             WeakReference<Context> contextWeakReference) {

        HashMap<String, Object> map = new HashMap<>();
        map.put(key, value);

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.USERS)
                .document(FbUser.getCurrentUserId())
                .set(map, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Context context = contextWeakReference.get();
                        if (context != null) {
                            if (task.isSuccessful()) {
                                Toast.makeText(context, "Información actualizada", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(context, "Error al intentar actualizar la información", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

    public static void actualizarImgStorage(Uri imageUriResultCrop,
                                            WeakReference<Context> contextWeakReference) {

        WeakReference<ProgressDialog> progressDialogWeakReference =
                ProgressDialogUtils.getDialogWeak(contextWeakReference.get(), null,null);
        progressDialogWeakReference.get().show();

        StorageReference filePath = FirebaseStorage.getInstance().getReference()
                .child(FirebaseConstants.STORAGE_USERS_IMG_PROFILE).child(FbUser.getCurrentUserId() + ".jpg");

        filePath.putFile(imageUriResultCrop).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                    @Override
                    public void onSuccess(Uri uri) {
                        actualizarImgDB(uri.toString(), contextWeakReference, progressDialogWeakReference);
                    }
                });

            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Context context = contextWeakReference.get();
                ProgressDialog progressDialog = progressDialogWeakReference.get();
                if (context != null && progressDialog != null) {
                    progressDialog.dismiss();
                    Toast.makeText(context, "Error al subir la imagen la storage", Toast.LENGTH_SHORT).show();
                }
            }
        }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
            @Override
            public void onProgress(@NonNull UploadTask.TaskSnapshot snapshot) {
                ProgressDialog progressDialog = progressDialogWeakReference.get();
                if (progressDialog != null) {
                    double progress = (100.0 * snapshot.getBytesTransferred() /
                            snapshot.getTotalByteCount());
                    progressDialog.setMessage("Subiende foto: " + (int) progress + "%");
                }
            }
        });
    }

    private static void actualizarImgDB(String imgPerfil, WeakReference<Context> contextWeakReference,
                                        WeakReference<ProgressDialog> progressDialogWeakReference) {

        HashMap<String, Object> mapImg = new HashMap<>();
        mapImg.put("imgUrl", imgPerfil);

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.USERS)
                .document(FbUser.getCurrentUserId())
                .update(mapImg)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {

                        ProgressDialog progressDialog = progressDialogWeakReference.get();
                        Context context = contextWeakReference.get();

                        if (context != null && progressDialog != null) {
                            progressDialog.dismiss();

                            if (!task.isSuccessful()) {
                                Toast.makeText(context, "Error al actualizar la foto en a base de datos.", Toast.LENGTH_SHORT).show();
                            }
                        }

                    }
                });

    }

    public static void deleteImgFromStorage(String imgUrl, WeakReference<Context> contextWeakReference) {
        StorageReference refImg = FirebaseStorage.getInstance().getReferenceFromUrl(imgUrl);

        refImg.delete().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    deleteImgFromDatabase(contextWeakReference);
                } else {
                    Context context = contextWeakReference.get();
                    if (context != null) {
                        Toast.makeText(context, "Error al eliminar la imagen desde storage", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    private static void deleteImgFromDatabase(WeakReference<Context> contextWeakReference) {
        HashMap<String, Object> mapImg = new HashMap<>();
        mapImg.put("imgUrl", "");

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.USERS)
                .document(FbUser.getCurrentUserId())
                .set(mapImg, SetOptions.merge())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (!task.isSuccessful()) {
                            Context context = contextWeakReference.get();
                            if (context != null) {
                                Toast.makeText(context, "Error al actualizar el link en la base de datos", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

}
