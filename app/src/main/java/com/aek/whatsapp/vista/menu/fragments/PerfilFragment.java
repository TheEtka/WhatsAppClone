package com.aek.whatsapp.vista.menu.fragments;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.controlador.PerfilController;
import com.aek.whatsapp.models.Usuario;
import com.aek.whatsapp.utils.AnimUtils;
import com.aek.whatsapp.utils.CropImage;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.vista.mainfragments.CameraFragment;
import com.aek.whatsapp.vista.mainfragments.camera.CameraActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.yalantis.ucrop.UCrop;

import java.lang.ref.WeakReference;

import de.hdodenhof.circleimageview.CircleImageView;

import static android.app.Activity.RESULT_OK;

public class PerfilFragment extends Fragment {

    public static final int IMG_GALERIA_CODE = 111;
    public static final int IMG_CROP_CODE = 222;
    ////////////////
    private CircleImageView imgUser;
    private TextView txtNombre, txtEstado;
    private FloatingActionButton fabCambiarFotoPerfil;
    private ImageButton btnActualizarNombre, btnActualizarEstado;
    private ListenerRegistration registrationUserData;
    private String imgUrl;

    public PerfilFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_perfil, container, false);
        init(view);
        setListeners();
        AnimUtils.scaleFab(fabCambiarFotoPerfil);
        getUserData();
        return view;
    }

    private void setListeners() {
        btnActualizarNombre.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (getNombre().trim().length() > 0) {
                    PerfilController.actualizarInfoUsuario("nombre", getNombre(),
                            new WeakReference<>(requireContext()));
                } else {
                    Toast.makeText(requireContext(), "Escribe nombre o apodo", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnActualizarEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PerfilController.actualizarInfoUsuario("estado", getEstado(),
                        new WeakReference<>(requireContext()));
            }
        });

        fabCambiarFotoPerfil.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mostrarBottomSheetOpcionesFoto();
            }
        });
    }

    private void init(View view) {
        this.imgUser = view.findViewById(R.id.imgUser);
        this.txtNombre = view.findViewById(R.id.txtNombreUsuarioPerfil);
        this.txtEstado = view.findViewById(R.id.txtEstadoUsuarioPerfil);
        this.btnActualizarNombre = view.findViewById(R.id.btnActualizarNombre);
        this.btnActualizarEstado = view.findViewById(R.id.btnActualizarEstado);
        this.fabCambiarFotoPerfil = view.findViewById(R.id.fabCambiarFotoPerfil);
    }

    public FloatingActionButton getFabCambiarFotoPerfil() {
        return fabCambiarFotoPerfil;
    }

    private void mostrarBottomSheetOpcionesFoto() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(requireContext());
        View layoutBottomSheet = getLayoutInflater().inflate(R.layout.layout_editar_foto_perfil, null);
        setButtonBottomSheetListeners(layoutBottomSheet, bottomSheetDialog);
        bottomSheetDialog.setContentView(layoutBottomSheet);
        bottomSheetDialog.show();
    }

    private void setButtonBottomSheetListeners(View layoutBottomSheet,
                                               BottomSheetDialog bottomSheetDialog) {

        layoutBottomSheet.findViewById(R.id.fabEliminarFotoPerfil)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (imgUrl != null && imgUrl.length() > 0) {
                            PerfilController.deleteImgFromStorage(imgUrl, new WeakReference<>(requireContext()));
                        } else {
                            Toast.makeText(requireContext(), "No hay foto", Toast.LENGTH_SHORT).show();
                        }
                        bottomSheetDialog.dismiss();
                    }
                });

        layoutBottomSheet.findViewById(R.id.fabFotoGaleriaPerfil)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivityForResult(new Intent().setAction(Intent.ACTION_GET_CONTENT)
                        .setType("image/*"), PerfilFragment.IMG_GALERIA_CODE);
                        bottomSheetDialog.dismiss();
                    }
                });

        layoutBottomSheet.findViewById(R.id.fabFotoCamaraPerfil)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        startActivity(new Intent(requireContext(), CameraActivity.class)
                        .putExtra(CameraFragment.TYPE_ACTION_CAMERA, CameraFragment.ACTION_ACTUALIZAR_FOTO));
                        bottomSheetDialog.dismiss();
                    }
                });
    }

    private void getUserData() {
        DocumentReference documentReference = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.USERS)
                .document(FbUser.getCurrentUserId());

        registrationUserData = documentReference.addSnapshotListener(userDataListener);
    }

    private EventListener<DocumentSnapshot> userDataListener = new EventListener<DocumentSnapshot>() {
        @Override
        public void onEvent(@Nullable DocumentSnapshot documentSnapshot, @Nullable FirebaseFirestoreException error) {
            try {

                if (documentSnapshot != null) {
                    Usuario usuario = documentSnapshot.toObject(Usuario.class);

                    if (usuario != null) {
                        imgUrl = usuario.getImgUrl();
                        String nombre = usuario.getNombre();
                        String estado = usuario.getEstado();

                        txtNombre.setText(nombre);
                        txtEstado.setText(estado);

                        if (!requireActivity().isFinishing()) {
                            Glide.with(requireContext()).load(imgUrl)
                                    .placeholder(R.color.colorPrimary)
                                    .error(R.drawable.img_user_default)
                                    .into(imgUser);
                        }

                    }
                }

            } catch (NullPointerException | IllegalStateException e) {
                e.getCause();
            }
        }
    };

    @Override
    public void onDetach() {
        super.onDetach();
        if (registrationUserData != null) {
            registrationUserData.remove();
            registrationUserData = null;
        }
    }

    public String getNombre() {
        return txtNombre.getText().toString();
    }

    public String getEstado() {
        return txtEstado.getText().toString();
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PerfilFragment.IMG_GALERIA_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri imgGaleria = data.getData();
                        if (imgGaleria != null) {
                            Log.e("ZZZ", "URI IMG GALERIA: " + imgGaleria.toString());
                            CropImage.cropMiniaturaFromFragment(imgGaleria, requireContext(), this);
                        }
                    } catch (NullPointerException e) {
                        e.getCause();
                    }
                }
                break;
            case PerfilFragment.IMG_CROP_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri imgCrop = UCrop.getOutput(data);
                        if (imgCrop != null) {
                            Log.e("ZZZ", "IMG RECORDATA: " + imgCrop.toString());
                            PerfilController.actualizarImgStorage(imgCrop,
                                    new WeakReference<>(requireContext()));
                        }
                    } catch (NullPointerException e) {
                        e.getCause();
                    }
                }
                break;
        }

    }
}