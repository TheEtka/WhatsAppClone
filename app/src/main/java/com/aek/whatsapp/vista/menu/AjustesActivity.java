package com.aek.whatsapp.vista.menu;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.databinding.ActivityAjustesBinding;
import com.aek.whatsapp.vista.menu.fragments.AjustesFragment;
import com.aek.whatsapp.vista.menu.fragments.PerfilFragment;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

public class AjustesActivity extends AppCompatActivity {

    private ActivityAjustesBinding binding;
    private ImageButton btnFinish;
    private TextView txtTitulo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAjustesBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        setListeners();
        mostrarAjustesFragment(savedInstanceState);
    }

    private void mostrarAjustesFragment(Bundle savedInstanceState) {
        if (savedInstanceState == null) {
            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameAjustes, new AjustesFragment(), AjustesFragment.class.getSimpleName())
                    .commit();
        }
    }

    private void init() {
        this.btnFinish = binding.btnFinish;
        this.txtTitulo = binding.txtTituloAjustes;
    }

    private void setListeners() {
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });
    }

    public void cambiarTitulo(String titulo) {
        txtTitulo.animate().alpha(0f).setDuration(150).withEndAction(new Runnable() {
            @Override
            public void run() {
                txtTitulo.setText(titulo);
                txtTitulo.animate().alpha(1f).setDuration(150);
            }
        });
    }

    @Override
    public void onBackPressed() {

        if (getSupportFragmentManager().getBackStackEntryCount() == 0) {
            super.onBackPressed();
        } else {
            cambiarTitulo("Ajustes");

            PerfilFragment perfilFragment = (PerfilFragment) getSupportFragmentManager()
                    .findFragmentByTag(PerfilFragment.class.getSimpleName());

            if (perfilFragment != null) {
                FloatingActionButton floatingActionButton = perfilFragment.getFabCambiarFotoPerfil();
                if (floatingActionButton != null) {
                    floatingActionButton.animate().scaleX(0f).scaleY(0f).setDuration(100)
                            .withEndAction(new Runnable() {
                                @Override
                                public void run() {
                                    getSupportFragmentManager().popBackStack();
                                }
                            });
                }
            }
        }

    }
}