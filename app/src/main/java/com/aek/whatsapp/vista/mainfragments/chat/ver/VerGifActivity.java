package com.aek.whatsapp.vista.mainfragments.chat.ver;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;

import com.aek.whatsapp.R;
import com.aek.whatsapp.databinding.ActivityVerGifBinding;
import com.giphy.sdk.core.models.enums.RenditionType;
import com.giphy.sdk.ui.views.GPHMediaView;

public class VerGifActivity extends AppCompatActivity {

    private ActivityVerGifBinding binding;

    public static final String GIF_ID = "KeyGIFId";
    private String mediaId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerGifBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getExtras();

        GPHMediaView gphMediaView = binding.gphMediaView;
        gphMediaView.setMediaWithId(mediaId, RenditionType.original, null, null);
    }

    private void getExtras() {
        try {
            this.mediaId = getIntent().getExtras().getString(VerGifActivity.GIF_ID, null);
        } catch (NullPointerException e) {
            e.getMessage();
        }
    }
}