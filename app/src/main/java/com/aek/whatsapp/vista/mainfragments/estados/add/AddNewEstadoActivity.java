package com.aek.whatsapp.vista.mainfragments.estados.add;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.EstadosController;
import com.aek.whatsapp.databinding.ActivityAddNewEstadoBinding;
import com.aek.whatsapp.utils.BitmapUtils;
import com.aek.whatsapp.vista.mainfragments.estados.canvas.CanvasTextoEstado;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;

public class AddNewEstadoActivity extends AppCompatActivity {

    private ActivityAddNewEstadoBinding binding;
    private ImageButton btnChangeBGColor;
    private TextView btnChangeFont;
    private CanvasTextoEstado canvasTextoEstado;
    private TextInputEditText txtEstado;
    private FloatingActionButton fabAddNewEstado;
    private int positionTypeFace = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewEstadoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        setListeners();
        changeBGColor();
        fabAddNewEstado.hide();

    }

    private void setListeners() {
        txtEstado.addTextChangedListener(textWatcher);

        fabAddNewEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasTextoEstado.escribirTextoEnCanvas(getEstado());
                txtEstado.setText("");
                txtEstado.setHint("");

                publicarNuevoEstado();
            }
        });

        btnChangeFont.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeTypeFace();
            }
        });

        btnChangeBGColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeBGColor();
            }
        });
    }

    private void publicarNuevoEstado() {
        Uri estadoUri = BitmapUtils.getUri(BitmapUtils.getBitmapFromView(canvasTextoEstado), getBaseContext());

        if (estadoUri == null) {
            Toast.makeText(this, "Error al guardar el estado en cache...", Toast.LENGTH_SHORT).show();
        } else {
            EstadosController.publicarEstadoEnStorage(estadoUri, new WeakReference<>(this));
        }
    }

    private void changeBGColor() {
        canvasTextoEstado.setBackgroundColor(EstadosController.getRandomColor());
    }

    private void changeTypeFace() {
        Typeface typeface = EstadosController.getTypeFace(positionTypeFace, getAssets());

        btnChangeFont.setTypeface(typeface);
        txtEstado.setTypeface(typeface);
        canvasTextoEstado.changeTypeFace(typeface);

        if (positionTypeFace == 2) {
            positionTypeFace = 0;
        } else {
            positionTypeFace++;
        }

    }

    private void init() {
        this.fabAddNewEstado = binding.fabAddNewEstado;
        this.btnChangeBGColor = binding.btnChangeBackgroundColor;
        this.canvasTextoEstado = binding.lienzoEstado;
        this.txtEstado = binding.txtEstado;
        this.btnChangeFont = binding.btnChangeFont;
    }

    public String getEstado() {
        return txtEstado.getText().toString();
    }

    private TextWatcher textWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
            String estado = getEstado().trim();

            if (estado.length() > 0) {
                fabAddNewEstado.show();
            } else {
                fabAddNewEstado.hide();
            }
        }

        @Override
        public void afterTextChanged(Editable s) {

        }
    };

}