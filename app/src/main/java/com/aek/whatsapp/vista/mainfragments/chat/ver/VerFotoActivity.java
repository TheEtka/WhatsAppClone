package com.aek.whatsapp.vista.mainfragments.chat.ver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.databinding.ActivityVerFotoBinding;
import com.bumptech.glide.Glide;

public class VerFotoActivity extends AppCompatActivity {

    private ActivityVerFotoBinding binding;

    public static final String FOTO_URL = "KeyFotoUrl";
    private String fotoUrl;
    private ImageView imgFotoChat;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerFotoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getExtras();
        init();
        Glide.with(getBaseContext()).load(fotoUrl).into(imgFotoChat);
    }

    private void init() {
        this.imgFotoChat = binding.imgFotoChat;
    }

    private void getExtras() {
        try {
            this.fotoUrl = getIntent().getExtras().getString(VerFotoActivity.FOTO_URL, null);
        } catch (NullPointerException e) {
            e.getMessage();
        }
    }
}