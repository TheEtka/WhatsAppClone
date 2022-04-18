package com.aek.whatsapp.controlador;

import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewTreeObserver;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.aek.whatsapp.R;
import com.aek.whatsapp.models.ContactoEstado;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.vista.mainfragments.ChatsFragment;
import com.aek.whatsapp.vista.mainfragments.EstadosFragment;
import com.aek.whatsapp.vista.mainfragments.LlamadasFragment;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MainController {

    public static List<Fragment> getListFragments(){
        List<Fragment> list = new ArrayList<>();
        list.add(new Fragment());
        list.add(new ChatsFragment());
        list.add(new EstadosFragment());
        list.add(new LlamadasFragment());

        return list;
    }

    public static String[] getTitles(){
        return new String[]{"", "Chats", "Estados", "Llamadas"};
    }

    public static void modifierCameraIconParams(TabLayout tabLayout) {

        try {

            LinearLayout layout = (LinearLayout) ((LinearLayout) tabLayout.getChildAt(0)).getChildAt(0);
            LinearLayout.LayoutParams layoutParams = (LinearLayout.LayoutParams) layout.getLayoutParams();
            layoutParams.weight = 0f;
            layoutParams.width = LinearLayout.LayoutParams.WRAP_CONTENT;
            layout.setLayoutParams(layoutParams);

        } catch (NullPointerException e) {
            e.getCause();
        }
    }

    public static void setCameraIconTabLayout(TabLayout tabLayout) {
        try {
            tabLayout.getTabAt(0).setIcon(R.drawable.selector_camera);
        } catch (NullPointerException e) {
            e.getCause();
        }
    }

    public static void cambiarFabIcon(int resIcon, FloatingActionButton floatingActionButton) {
        floatingActionButton.setImageResource(resIcon);
    }

    public static void ocultarOpcionesMenu(int position, Menu menu) {
        if (menu != null) {
            switch (position) {
                case 1:
                    setMenuItemVisible(R.id.menuNuevoGrupo, true, menu);
                    setMenuItemVisible(R.id.menuNuevaDifusion, true, menu);
                    setMenuItemVisible(R.id.menuWhatsAppWeb, true, menu);
                    setMenuItemVisible(R.id.menuMensajesDestacados, true, menu);
                    setMenuItemVisible(R.id.menuPrivacidadDeEstados, false, menu);
                    break;

                case 2:
                    setMenuItemVisible(R.id.menuNuevoGrupo, false, menu);
                    setMenuItemVisible(R.id.menuNuevaDifusion, false, menu);
                    setMenuItemVisible(R.id.menuWhatsAppWeb, false, menu);
                    setMenuItemVisible(R.id.menuMensajesDestacados, false, menu);
                    setMenuItemVisible(R.id.menuPrivacidadDeEstados, true, menu);
                    break;

                case 3:
                    setMenuItemVisible(R.id.menuNuevoGrupo, false, menu);
                    setMenuItemVisible(R.id.menuNuevaDifusion, false, menu);
                    setMenuItemVisible(R.id.menuWhatsAppWeb, false, menu);
                    setMenuItemVisible(R.id.menuMensajesDestacados, false, menu);
                    setMenuItemVisible(R.id.menuPrivacidadDeEstados, false, menu);
                    break;
            }
        }
    }

    public static void setMenuItemVisible(int menuItemId, boolean visible, Menu menu) {
        if (menu != null) {
            MenuItem menuItem = menu.findItem(menuItemId);
            menuItem.setVisible(visible);
        }
    }

    public static void hideFrameLayoutCamera(FrameLayout frameLayoutCamera) {
        frameLayoutCamera.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                frameLayoutCamera.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                frameLayoutCamera.setTranslationX(-frameLayoutCamera.getWidth() * 1.0f);
            }
        });
    }

    public static ListenerRegistration addListenerNuevosEstado(WeakReference<TabLayout> tabLayoutWeakReference) {

        CollectionReference reference = FirebaseFirestore.getInstance().collection(FirebaseConstants.CONTACTOS_ESTADOS);

        return reference.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                try {

                    TabLayout tabLayout = tabLayoutWeakReference.get();

                    if (tabLayout != null && queryDocumentSnapshots != null) {

                        List<DocumentSnapshot> listContactos = queryDocumentSnapshots.getDocuments();

                        if (listContactos.size() > 0) {
                            for (DocumentSnapshot contacto : listContactos) {
                                if (!contacto.getId().equals(FbUser.getCurrentUserId())
                                && contacto.contains("espectadoresNotification")){

                                    ContactoEstado contactoEstado = contacto.toObject(ContactoEstado.class);
                                    if (contactoEstado != null) {
                                        HashMap<String, Object> mapEspectadoresNotification = contactoEstado.getEspectadoresNotification();

                                        BadgeDrawable badgeDrawable = tabLayout.getTabAt(2).getOrCreateBadge();

                                        if (mapEspectadoresNotification.containsKey(FbUser.getCurrentUserId())) {
                                            badgeDrawable.setVisible(false);
                                        } else {
                                            badgeDrawable.setVisible(true);
                                        }

                                    }

                                }
                            }
                        }

                    }

                } catch (NullPointerException e) {
                    e.getCause();
                }
            }
        });

    }

    public static void marcarNotificationNuevoEstado() {
        CollectionReference reference = FirebaseFirestore.getInstance().collection(FirebaseConstants.CONTACTOS_ESTADOS);
        reference.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                try {
                    if (task.isSuccessful() && task.getResult() != null) {
                        List<DocumentSnapshot> list = task.getResult().getDocuments();
                        if (list.size() > 0) {
                            for (DocumentSnapshot contacto : list) {
                                if (!contacto.getId().equals(FbUser.getCurrentUserId())) {
                                    ContactoEstado contactoEstado = contacto.toObject(ContactoEstado.class);
                                    if (contactoEstado != null) {
                                        HashMap<String, Object> mapEspectador = new HashMap<>();
                                        mapEspectador.put(FbUser.getCurrentUserId(), true);
                                        /////////////////
                                        HashMap<String, Object> map = new HashMap<>();
                                        map.put("espectadoresNotification", mapEspectador);
                                        /////////////////
                                        reference.document(contactoEstado.getUid()).set(map, SetOptions.merge());
                                    }
                                }
                            }
                        }
                    }
                } catch (NullPointerException e) {
                    e.getCause();
                }
            }
        });
    }

    public static void setTokenNotification() {
        FirebaseInstanceId.getInstance().getInstanceId().addOnCompleteListener(new OnCompleteListener<InstanceIdResult>() {
            @Override
            public void onComplete(@NonNull Task<InstanceIdResult> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    String tokenNotification = task.getResult().getToken();

                    HashMap<String, Object> mapToken = new HashMap<>();
                    mapToken.put("tokenNotification", tokenNotification);

                    FirebaseFirestore.getInstance()
                            .collection(FirebaseConstants.USERS)
                            .document(FbUser.getCurrentUserId())
                            .set(mapToken, SetOptions.merge());

                }
            }
        });
    }

}
