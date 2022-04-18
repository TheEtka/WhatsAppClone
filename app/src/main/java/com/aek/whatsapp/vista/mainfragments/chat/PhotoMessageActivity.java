package com.aek.whatsapp.vista.mainfragments.chat;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.ChatController;
import com.aek.whatsapp.controlador.MainController;
import com.aek.whatsapp.databinding.ActivityPhotoMessageBinding;
import com.aek.whatsapp.models.Message;
import com.aek.whatsapp.models.MessageSended;
import com.aek.whatsapp.vista.ContactosActivity;
import com.bumptech.glide.Glide;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.io.File;
import java.lang.ref.WeakReference;

public class PhotoMessageActivity extends AppCompatActivity implements MessageSended {

    public static final int CONTACT_REQ_CODE = 111;
    public static final String IMG_URI = "KeyIMG";
    private String uidReceiver, imgUriString;
    //////////////////
    private ActivityPhotoMessageBinding binding;
    private ImageView imgPhoto;
    private ImageButton btnFinish;
    private FloatingActionButton fabEnviarPhotoMessage;
    private TextInputEditText txtMensaje;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPhotoMessageBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getExtras();
        init();
        setListeners();
        cambiarIconFabEnviarMessage();
        Glide.with(getBaseContext()).load(imgUriString).into(imgPhoto);
    }

    private void cambiarIconFabEnviarMessage() {
        if (uidReceiver == null) {
            MainController.cambiarFabIcon(R.drawable.ic_check, fabEnviarPhotoMessage);
        } else {
            MainController.cambiarFabIcon(R.drawable.ic_send, fabEnviarPhotoMessage);
        }
    }

    private void getExtras() {
        try {
            this.uidReceiver = getIntent().getExtras().getString(ChatActivity.UID, null);
            this.imgUriString = getIntent().getExtras().getString(PhotoMessageActivity.IMG_URI, null);
        } catch (NullPointerException e) {
            e.getMessage();
        }
    }

    private void setListeners() {
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        fabEnviarPhotoMessage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (uidReceiver == null) {
                    Intent intent = new Intent(PhotoMessageActivity.this, ContactosActivity.class);
                    intent.putExtra(ContactosActivity.TYPE_ACTION, ContactosActivity.ACTION_SEND_MESSAGE);
                    startActivityForResult(intent, PhotoMessageActivity.CONTACT_REQ_CODE);
                } else {
                    ChatController.sendFileMessage(Uri.fromFile(new File(imgUriString)), uidReceiver,
                            getMensaje(), "fotos", ".jpeg", Message.TYPE_FOTO,
                            "Compartiendo foto...",
                            new WeakReference<>(PhotoMessageActivity.this),
                            new WeakReference<>(PhotoMessageActivity.this));
                }
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case PhotoMessageActivity.CONTACT_REQ_CODE:

                if (resultCode == RESULT_OK) {
                    if (data != null) {
                        String uidReceiverResult = data.getStringExtra(ChatActivity.UID);
                        if (uidReceiverResult != null) {
                            uidReceiver = uidReceiverResult;
                            cambiarIconFabEnviarMessage();
                        }
                    }
                } else {
                    Toast.makeText(this, "No se ha seleccionado ningún contacto....", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }

    public String getMensaje() {
        return txtMensaje.getText().toString();
    }

    private void init() {
        this.imgPhoto = binding.imgPhotoToSend;
        this.btnFinish = binding.btnFinish;
        this.fabEnviarPhotoMessage = binding.fabEnviarPhotoMessage;
        this.txtMensaje = binding.txtMensajePhotoMessage;
    }

    @Override
    public void onMessageSended(boolean messageIsSended) {
        if (messageIsSended) {
            finish();
        } else {
            Toast.makeText(this, "Error al compartir la foto...", Toast.LENGTH_SHORT).show();
        }
    }
}