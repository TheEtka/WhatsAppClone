package com.aek.whatsapp.vista.cuenta;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.RegistroController;
import com.aek.whatsapp.databinding.ActivityRegistroBinding;
import com.aek.whatsapp.utils.EmailUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;

public class RegistroActivity extends AppCompatActivity {

    private ActivityRegistroBinding binding;
    private Button btnRegistrar;
    private TextInputEditText txtNombre, txtCorreo, txtContrasenia, txtConfContrasenia;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRegistroBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        setListeners();
    }

    private void setListeners() {

        txtNombre.addTextChangedListener(textWatcher);
        txtCorreo.addTextChangedListener(textWatcher);
        txtContrasenia.addTextChangedListener(textWatcher);
        txtConfContrasenia.addTextChangedListener(textWatcher);

        btnRegistrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RegistroController.registrarUsuario(getNombre(), getCorreo(), getContrasenia(),
                        new WeakReference<>(RegistroActivity.this));
            }
        });

        txtConfContrasenia.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (puedoRegistrarUsuario()) {
                        RegistroController.registrarUsuario(getNombre(), getCorreo(), getContrasenia(),
                                new WeakReference<>(RegistroActivity.this));
                    }
                }
                return false;
            }
        });
    }

    private void init() {
        this.btnRegistrar = binding.btnRegistrar;
        this.txtNombre = binding.txtNombreRegistro;
        this.txtCorreo = binding.txtCorreoRegistro;
        this.txtContrasenia = binding.txtContraseniaRegistro;
        this.txtConfContrasenia = binding.txtConfContraseniaRegistro;
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            puedoRegistrarUsuario();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private boolean puedoRegistrarUsuario() {
        String nombre = getNombre().trim();
        String correo = getCorreo().trim();
        String contrasenia = getContrasenia().trim();
        String confContrasenia = getConfContrasenia().trim();

        if (nombre.length() > 2 &&
                EmailUtils.esCorreoValido(correo) &&
                contrasenia.length() > 5 &&
                confContrasenia.equals(contrasenia)) {
            btnRegistrar.setEnabled(true);
            return true;
        } else {
            btnRegistrar.setEnabled(false);
            return false;
        }
    }

    public String getNombre() {
        return txtNombre.getText().toString();
    }

    public String getCorreo() {
        return txtCorreo.getText().toString();
    }

    public String getContrasenia() {
        return txtContrasenia.getText().toString();
    }

    public String getConfContrasenia() {
        return txtConfContrasenia.getText().toString();
    }
}