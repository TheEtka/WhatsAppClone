package com.aek.whatsapp.vista.cuenta;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.LoginController;
import com.aek.whatsapp.databinding.ActivityLoginBinding;
import com.aek.whatsapp.utils.EmailUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;

public class LoginActivity extends AppCompatActivity {

    private ActivityLoginBinding binding;
    private ImageView imgLogo;
    private Animation animationLogo;
    private Button btnRegistrar, btnEntrar;
    private TextInputEditText txtCorreo, txtContrasenia;
    private TextView btnRecuperarContrasenia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        setListeners();
    }

    private void setListeners() {
        txtCorreo.addTextChangedListener(textWatcher);
        txtContrasenia.addTextChangedListener(textWatcher);

        btnEntrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginController.iniciarSesion(getCorreo(), getContrasenia(),
                        new WeakReference<>(LoginActivity.this),
                        new WeakReference<>(txtContrasenia));
            }
        });

        txtContrasenia.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (puedoIniciarSesion()) {
                        LoginController.iniciarSesion(getCorreo(), getContrasenia(),
                                new WeakReference<>(LoginActivity.this),
                                new WeakReference<>(txtContrasenia));
                    }
                }
                return false;
            }
        });

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RegistroActivity.class));
            }
        });

        animationLogo.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        if (imgLogo != null && animationLogo != null) {
                            imgLogo.startAnimation(animationLogo);
                        }
                    }
                },2000);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        btnRecuperarContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this, RecuperarContraseniaActivity.class));
            }
        });
    }

    private void init() {
        this.imgLogo = binding.imgLogo;
        this.animationLogo = AnimationUtils.loadAnimation(getBaseContext(), R.anim.anim_logo);
        this.btnRegistrar = binding.btnRegistro;
        this.btnEntrar = binding.btnEntrarLogin;
        this.txtCorreo = binding.txtCorreoLogin;
        this.txtContrasenia = binding.txtContraseniaLogin;
        this.btnRecuperarContrasenia = binding.btnRecuperarContrasenia;
    }

    @Override
    protected void onResume() {
        super.onResume();
        imgLogo.startAnimation(animationLogo);
    }

    @Override
    protected void onPause() {
        super.onPause();

        if (animationLogo != null) {
            animationLogo.cancel();
        }
    }

    public String getCorreo() {
        return txtCorreo.getText().toString();
    }

    public String getContrasenia() {
        return txtContrasenia.getText().toString();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            puedoIniciarSesion();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private boolean puedoIniciarSesion() {
        String correo = getCorreo().trim();
        String contrasenia = getContrasenia().trim();

        if (EmailUtils.esCorreoValido(correo) && contrasenia.length() > 5) {
            btnEntrar.setEnabled(true);
            return true;
        } else {
            btnEntrar.setEnabled(false);
            return false;
        }
    }
}