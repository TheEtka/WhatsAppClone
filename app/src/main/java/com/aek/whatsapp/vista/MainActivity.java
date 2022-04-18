package com.aek.whatsapp.vista;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentManager;
import androidx.viewpager.widget.ViewPager;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.SearchView;
import android.widget.Toast;

import com.aek.whatsapp.R;
import com.aek.whatsapp.adapters.ViewPagerTitleAdapter;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.controlador.MainController;
import com.aek.whatsapp.databinding.ActivityMainBinding;
import com.aek.whatsapp.utils.AnimUtils;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.vista.cuenta.LoginActivity;
import com.aek.whatsapp.vista.mainfragments.CameraFragment;
import com.aek.whatsapp.vista.mainfragments.camera.CameraActivity;
import com.aek.whatsapp.vista.mainfragments.estados.add.AddNewEstadoActivity;
import com.aek.whatsapp.vista.menu.AjustesActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.badge.BadgeDrawable;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;

import java.lang.ref.WeakReference;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity {

    public static final int WRITE_EXT_STORAGE_CAMERA_REQ_CODE = 111;
    ////////
    private ActivityMainBinding binding;
    private ViewPager viewPager;
    private ViewPagerTitleAdapter adapter;
    private AppBarLayout appBarLayout;
    private static TabLayout tabLayout;
    private FloatingActionButton fabMain, fabEditarEstadoMain;
    //////////  CAMERA
    private FrameLayout frameCamera;
    //////////  TOP MENU
    private SearchView searchView;
    private boolean searchViewIsVisible = false;
    private Menu menu;
    //////////////////  LISTENER NUEVOS ESTADOS
    private ListenerRegistration listenerNuevosEstados;

    @RequiresApi(api = Build.VERSION_CODES.M)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityMainBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        setVPAdapter();
        MainController.setCameraIconTabLayout(tabLayout);
        MainController.modifierCameraIconParams(tabLayout);
        setListeners();
        MainController.setTokenNotification();
        MainController.hideFrameLayoutCamera(frameCamera);
        comprobarPermisosCamaraAlmacenamiento();
        listenerNuevosEstados = MainController.addListenerNuevosEstado(new WeakReference<>(tabLayout));
    }

    private void setVPAdapter() {
        this.viewPager.setAdapter(adapter);
        this.viewPager.setOffscreenPageLimit(MainController.getListFragments().size());
        this.viewPager.setCurrentItem(1);
        tabLayout.setupWithViewPager(viewPager);
    }

    private void init() {
        this.viewPager = binding.viewPagerMain;
        this.appBarLayout = binding.appBarLayoutMain;
        tabLayout = binding.tabLayoutMain;
        this.fabMain = binding.fabMain;
        this.fabEditarEstadoMain = binding.fabEditarEstadoMain;
        this.searchView = binding.searchViewMain;
        this.adapter = new ViewPagerTitleAdapter(getSupportFragmentManager(),
                MainController.getListFragments(), MainController.getTitles());
        Toolbar toolbar = binding.toolbarMain;
        setSupportActionBar(toolbar);
        this.frameCamera = binding.frameCamera;
    }

    private void setListeners() {

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
                if (position == 0) {
                    fabMain.setTranslationX((1 - positionOffset) * (2 * fabMain.getHeight()));
                    fabEditarEstadoMain.setTranslationX((1 - positionOffset) * (3 * fabMain.getHeight()));
                    appBarLayout.setTranslationY((1 - positionOffset) * -appBarLayout.getHeight());
                    frameCamera.setTranslationX(-frameCamera.getWidth() * positionOffset);

                    showCameraFragment();
                } else {
                    removeCameraFragment();
                }
            }

            @Override
            public void onPageSelected(int position) {

            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                int tabPosition = tab.getPosition();

                viewPager.setCurrentItem(tabPosition);

                AnimUtils.showFabAddEstado(tabPosition, fabEditarEstadoMain, getBaseContext());

                switch (tabPosition) {
                    case 0:

                        break;

                    case 1:
                        MainController.cambiarFabIcon(R.drawable.ic_contactos, fabMain);
                        break;
                    case 2:
                        MainController.cambiarFabIcon(R.drawable.ic_camera, fabMain);
                        MainController.cambiarFabIcon(R.drawable.ic_edit, fabEditarEstadoMain);
                        MainController.marcarNotificationNuevoEstado();
                        break;
                    case 3:
                        MainController.cambiarFabIcon(R.drawable.ic_call_white, fabMain);
                        MainController.cambiarFabIcon(R.drawable.ic_video, fabEditarEstadoMain);
                        break;
                }

                MainController.ocultarOpcionesMenu(tabPosition, menu);

                if (searchViewIsVisible) {
                    searchViewIsVisible = false;
                    AnimUtils.hideSearchView(searchView, appBarLayout);
                }

                if (tabPosition == 0) {
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                        }
                    }, 200);
                } else {
                    getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
                }

            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });

        fabMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (tabLayout.getSelectedTabPosition()) {
                    case 1:
                        startActivity(new Intent(MainActivity.this, ContactosActivity.class)
                                .putExtra(ContactosActivity.TYPE_ACTION, ContactosActivity.ACTION_CHAT));
                        break;
                    case 2:
                        startActivity(new Intent(MainActivity.this, CameraActivity.class)
                                .putExtra(CameraFragment.TYPE_ACTION_CAMERA, CameraFragment.ACTION_ESTADO));
                        break;
                    case 3:
                        startActivity(new Intent(MainActivity.this, ContactosActivity.class)
                                .putExtra(ContactosActivity.TYPE_ACTION, ContactosActivity.ACTION_CALL));
                        break;
                }
            }
        });

        fabEditarEstadoMain.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                switch (tabLayout.getSelectedTabPosition()) {
                    case 2:
                        startActivity(new Intent(MainActivity.this, AddNewEstadoActivity.class));
                        break;
                    case 3:
                        Toast.makeText(MainActivity.this, "Crear sala de video", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_main, menu);
        this.menu = menu;
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {

        switch (item.getItemId()) {
            case R.id.menuBuscar:
                searchViewIsVisible = true;
                appBarLayout.setExpanded(false, true);
                AnimUtils.showSearchView(searchView);
                break;
            case R.id.menuNuevoGrupo:
                Toast.makeText(this, "NUEVO GRUPO", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuNuevaDifusion:
                Toast.makeText(this, "NUEVO DIFUSION", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuWhatsAppWeb:
                Toast.makeText(this, "WHATSAPP WEB", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuMensajesDestacados:
                Toast.makeText(this, "MENSAJES DESTACADOS", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuPrivacidadDeEstados:
                Toast.makeText(this, "PRIVACIDAD DE ESTADOS", Toast.LENGTH_SHORT).show();
                break;
            case R.id.menuAjustes:
                startActivity(new Intent(MainActivity.this, AjustesActivity.class));
                break;
            case R.id.menuSalir:

                cerrarSesion();

                break;

        }

        return super.onOptionsItemSelected(item);
    }

    private void cerrarSesion() {
        HashMap<String, Object> mapToken = new HashMap<>();
        mapToken.put("tokenNotification", "");

        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.USERS)
                .document(FbUser.getCurrentUserId())
                .update(mapToken)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            FirebaseAuth.getInstance().signOut();
                            startActivity(new Intent(MainActivity.this, LoginActivity.class));
                            finish();
                        } else {
                            Toast.makeText(MainActivity.this, "Error al eliminar el token notification", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @Override
    public void onBackPressed() {

        if (searchViewIsVisible) {
            searchViewIsVisible = false;
            AnimUtils.hideSearchView(searchView, appBarLayout);
        } else {
            if (tabLayout.getSelectedTabPosition() == 0) {
                ocultarBottomSheetGallery();
            } else if (tabLayout.getSelectedTabPosition() == 1) {
                super.onBackPressed();
            } else {
                viewPager.setCurrentItem(1);
            }
        }

    }

    private void ocultarBottomSheetGallery() {
        CameraFragment cameraFragment = (CameraFragment) getSupportFragmentManager().findFragmentByTag(CameraFragment.class.getSimpleName());
        if (cameraFragment != null) {
            BottomSheetBehavior<CoordinatorLayout> bottomSheetBehavior = cameraFragment.getBottomSheetGallery();
            if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                viewPager.setCurrentItem(1);
            }
        } else {
            viewPager.setCurrentItem(1);
        }
    }

    @RequiresApi(api = Build.VERSION_CODES.M)
    private void comprobarPermisosCamaraAlmacenamiento() {

        if (ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(getBaseContext(), Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            requestPermissions(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE}, MainActivity.WRITE_EXT_STORAGE_CAMERA_REQ_CODE);

        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case MainActivity.WRITE_EXT_STORAGE_CAMERA_REQ_CODE:

                if (grantResults.length > 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[1] == PackageManager.PERMISSION_GRANTED &&
                        grantResults[2] == PackageManager.PERMISSION_GRANTED) {
                    //Toast.makeText(this, "Hay permisos para usar la camara y el almacenamiento", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(this, "para poder tomar fotos, la app necesita los permisos de camara....", Toast.LENGTH_SHORT).show();
                    finish();
                }
                break;
        }
    }

    private void removeCameraFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(CameraFragment.class.getSimpleName()) != null) {
            fragmentManager.popBackStack(CameraFragment.class.getSimpleName(), FragmentManager.POP_BACK_STACK_INCLUSIVE);
        }
    }

    private void showCameraFragment() {
        FragmentManager fragmentManager = getSupportFragmentManager();
        if (fragmentManager.findFragmentByTag(CameraFragment.class.getSimpleName()) == null) {
            Bundle bundle = new Bundle();
            bundle.putString(CameraFragment.TYPE_ACTION_CAMERA, CameraFragment.ACTION_MESSAGE);

            CameraFragment cameraFragment = new CameraFragment();
            cameraFragment.setArguments(bundle);

            fragmentManager.beginTransaction()
                    .add(R.id.frameCamera, cameraFragment, CameraFragment.class.getSimpleName())
                    .addToBackStack(CameraFragment.class.getSimpleName())
                    .commit();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (listenerNuevosEstados != null) {
            listenerNuevosEstados.remove();
            listenerNuevosEstados = null;
        }
    }

    public static void mostrarBadgeNuevoChat(int nrNuevosChats) {
        try {
            BadgeDrawable badgeDrawable = tabLayout.getTabAt(1).getOrCreateBadge();

            if (nrNuevosChats == 0) {
                badgeDrawable.setVisible(false);
            } else {
                badgeDrawable.setVisible(true);
            }
            badgeDrawable.setNumber(nrNuevosChats);

        } catch (NullPointerException e) {
            e.getMessage();
        }
    }

}