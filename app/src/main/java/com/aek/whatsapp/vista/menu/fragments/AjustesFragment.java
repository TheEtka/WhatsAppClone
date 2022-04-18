package com.aek.whatsapp.vista.menu.fragments;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.transition.TransitionInflater;
import androidx.transition.TransitionSet;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.models.Usuario;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.utils.TxtUtils;
import com.aek.whatsapp.vista.menu.AjustesActivity;
import com.bumptech.glide.Glide;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;

import de.hdodenhof.circleimageview.CircleImageView;

public class AjustesFragment extends Fragment {

    private CircleImageView imgUser;
    private TextView txtNombre, txtEstado;
    private Button btnCuenta, btnChats, btnNotificaciones, btnDatosAlmacenamiento, btnAyuda;
    private ListenerRegistration registrationUserData;

    public AjustesFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_ajustes, container, false);
        init(view);
        cambiarBtnsTxt();
        setListeners(view);
        getUserData();
        return view;
    }

    private void setListeners(View view) {
        view.findViewById(R.id.layoutDatosUsuario).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                cambiarTitulo();
                mostraPerfilFragment();
            }
        });
    }

    private void mostraPerfilFragment() {

        try {

            PerfilFragment perfilFragment = new PerfilFragment();

            perfilFragment.setSharedElementEnterTransition(getEnterTransitionPerfil());

            FragmentTransaction fragmentTransaction = getActivity().getSupportFragmentManager().beginTransaction();
            fragmentTransaction.setReorderingAllowed(true);
            fragmentTransaction.setCustomAnimations(android.R.anim.fade_in, android.R.anim.fade_out,
                    android.R.anim.fade_in, android.R.anim.fade_out);
            fragmentTransaction.addSharedElement(imgUser, imgUser.getTransitionName());
            fragmentTransaction.replace(R.id.frameAjustes, perfilFragment,
                    perfilFragment.getClass().getSimpleName());
            fragmentTransaction.addToBackStack(perfilFragment.getClass().getName());
            fragmentTransaction.commit();

        } catch (NullPointerException e) {
            e.getCause();
        }

    }

    private TransitionSet getEnterTransitionPerfil() {
        TransitionSet transitionSet = new TransitionSet();
        transitionSet.addTransition(TransitionInflater.from(requireContext()).inflateTransition(android.R.transition.move));
        transitionSet.setDuration(300);
        transitionSet.setStartDelay(150);
        return transitionSet;
    }

    private void cambiarTitulo() {
        AjustesActivity ajustesActivity = (AjustesActivity)getActivity();
        if (ajustesActivity != null) {
            ajustesActivity.cambiarTitulo("Perfil");
        }
    }

    private void cambiarBtnsTxt() {
        TxtUtils.setTitleSubtitleButton("Cuenta\n","Privacidad, seguridad, cambiar número", btnCuenta);
        TxtUtils.setTitleSubtitleButton("Chats\n","Tema, fondos de pantalla, historial de chat", btnChats);
        TxtUtils.setTitleSubtitleButton("Notificaciones\n","Tonos de mensajes, grupos y llamadas", btnNotificaciones);
        TxtUtils.setTitleSubtitleButton("Datos y almacenamiento\n","Uso de red, descarga automática", btnDatosAlmacenamiento);
        TxtUtils.setTitleSubtitleButton("Ayuda\n","Preguntas frecuentes, contáctanos, políticas", btnAyuda);
    }

    private void init(View view) {
        this.imgUser = view.findViewById(R.id.imgUser);
        this.txtNombre = view.findViewById(R.id.txtNombreUser);
        this.txtEstado = view.findViewById(R.id.txtEstadoUser);
        this.btnCuenta = view.findViewById(R.id.btnAjustesCuenta);
        this.btnChats = view.findViewById(R.id.btnAjustesChats);
        this.btnNotificaciones = view.findViewById(R.id.btnAjustesNotificaciones);
        this.btnDatosAlmacenamiento = view.findViewById(R.id.btnAjustesDatosAlmacenamiento);
        this.btnAyuda = view.findViewById(R.id.btnAjustesAyuda);
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
                        String imgUrl = usuario.getImgUrl();
                        String nombre = usuario.getNombre();
                        String estado = usuario.getEstado();

                        txtNombre.setText(nombre);

                        if (!requireActivity().isFinishing()) {
                            Glide.with(requireContext()).load(imgUrl)
                                    .placeholder(R.color.colorPrimary)
                                    .error(R.drawable.img_user_default)
                                    .into(imgUser);
                        }

                        if (estado != null && estado.length() > 0) {
                            txtEstado.setText(estado);
                            txtEstado.setVisibility(View.VISIBLE);
                        } else {
                            txtEstado.setVisibility(View.GONE);
                        }

                    }
                }

            } catch (NullPointerException | IllegalStateException e) {
                e.getCause();
            }
        }
    };

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (registrationUserData != null) {
            registrationUserData.remove();
            registrationUserData = null;
        }
    }

}