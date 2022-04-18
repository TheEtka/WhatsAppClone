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

import com.aek.whatsapp.controlador.RecuperarContraseniaController;
import com.aek.whatsapp.databinding.ActivityRecuperarContraseniaBinding;
import com.aek.whatsapp.utils.EmailUtils;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;

public class RecuperarContraseniaActivity extends AppCompatActivity {

    private ActivityRecuperarContraseniaBinding binding;
    private Button btnRecuperarContrasenia;
    private TextInputEditText txtCorreo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityRecuperarContraseniaBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        setListeners();
    }

    private void setListeners() {

        txtCorreo.addTextChangedListener(textWatcher);

        btnRecuperarContrasenia.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                RecuperarContraseniaController.recuperarContrasenia(getCorreo(),
                        new WeakReference<>(RecuperarContraseniaActivity.this),
                        new WeakReference<>(txtCorreo));
            }
        });

        txtCorreo.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
                if (i == EditorInfo.IME_ACTION_DONE) {
                    if (puedoRecuperarContrasenia()) {
                        RecuperarContraseniaController.recuperarContrasenia(getCorreo(),
                                new WeakReference<>(RecuperarContraseniaActivity.this),
                                new WeakReference<>(txtCorreo));
                    }
                }
                return false;
            }
        });
    }

    private void init() {
        this.btnRecuperarContrasenia = binding.btnEnviarCorreoRecuperarContrasenia;
        this.txtCorreo = binding.txtCorreoRecuperarContrasenia;
    }

    public String getCorreo() {
        return txtCorreo.getText().toString();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

        }

        @Override
        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            puedoRecuperarContrasenia();
        }

        @Override
        public void afterTextChanged(Editable editable) {

        }
    };

    private boolean puedoRecuperarContrasenia() {
        String correo = getCorreo().trim();

        if (EmailUtils.esCorreoValido(correo)) {
            btnRecuperarContrasenia.setEnabled(true);
            return true;
        } else {
            btnRecuperarContrasenia.setEnabled(false);
            return false;
        }
    }
}