package com.aek.whatsapp.controlador;

import android.app.Activity;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.aek.whatsapp.R;
import com.aek.whatsapp.models.Usuario;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;

public class FbUser {

    public static String getCurrentUserId() {
        return FirebaseAuth.getInstance().getCurrentUser().getUid();
    }

    public static void getUserImage(WeakReference<CircleImageView> circleImageViewWeakReference,
                                    WeakReference<Activity> activityWeakReference) {

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.USERS)
                .document(FbUser.getCurrentUserId())
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {

                    Activity activity = activityWeakReference.get();

                    if (activity != null) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            CircleImageView circleImageView = circleImageViewWeakReference.get();

                            Usuario usuario = task.getResult().toObject(Usuario.class);

                            if (circleImageView != null && !activity.isFinishing() && usuario != null) {
                                Glide.with(activity)
                                        .load(usuario.getImgUrl())
                                        .error(R.drawable.img_user_default)
                                        .into(circleImageView);
                            }

                        } else {
                            Toast.makeText(activity, "Error al obtener la imagen del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (NullPointerException e) {
                    e.getCause();
                }
            }
        });

    }

    public static void getUserBasicData(String uid, WeakReference<CircleImageView> circleImageViewWeakReference,
                                        WeakReference<TextView> textViewWeakReference,
                                        WeakReference<Activity> activityWeakReference) {

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.USERS)
                .document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                try {

                    Activity activity = activityWeakReference.get();

                    if (activity != null) {
                        if (task.isSuccessful() && task.getResult() != null) {

                            CircleImageView circleImageView = circleImageViewWeakReference.get();
                            TextView txtNombreUsuario = textViewWeakReference.get();
                            Usuario usuario = task.getResult().toObject(Usuario.class);

                            if (usuario != null) {
                                txtNombreUsuario.setText(usuario.getNombre());

                                if (circleImageView != null && !activity.isFinishing()) {
                                    Glide.with(activity)
                                            .load(usuario.getImgUrl())
                                            .error(R.drawable.img_user_default)
                                            .into(circleImageView);
                                }

                            }

                        } else {
                            Toast.makeText(activity, "Error al obtener la imagen del usuario", Toast.LENGTH_SHORT).show();
                        }
                    }

                } catch (NullPointerException e) {
                    e.getCause();
                }
            }
        });

    }

    public static void getListenerUserBasicData(String uid, WeakReference<CircleImageView> circleImageViewWeakReference,
                                                WeakReference<TextView> textViewWeakReference,
                                                WeakReference<Activity> activityWeakReference) {

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.USERS)
                .document(uid)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
                        Activity activity = activityWeakReference.get();
                        if (activity != null && documentSnapshot != null) {
                            Usuario usuario = documentSnapshot.toObject(Usuario.class);
                            if (usuario != null) {
                                CircleImageView imgUser = circleImageViewWeakReference.get();
                                TextView txtUserName = textViewWeakReference.get();
                                if (imgUser != null && txtUserName != null) {
                                    txtUserName.setText(usuario.getNombre());
                                    if (!activity.isFinishing()) {
                                        Glide.with(activity).load(usuario.getImgUrl())
                                                .error(R.drawable.img_user_default).into(imgUser);
                                    }
                                }
                            } else {
                                Toast.makeText(activity, "Error al obtener los datos del usuario...", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }
                });

    }

}
