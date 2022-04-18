package com.aek.whatsapp.vista.mainfragments.chat.ver;

import androidx.appcompat.app.AppCompatActivity;

import android.app.PictureInPictureParams;
import android.content.Intent;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.MediaController;
import android.widget.VideoView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.databinding.ActivityVerVideoPipmodeBinding;

public class VerVideoPIPModeActivity extends AppCompatActivity {

    public static final String VIDEO_URL = "keyVideoUrl";
    ///////////
    private String videoUrl;
    ///////////
    private ActivityVerVideoPipmodeBinding binding;
    private VideoView videoView;
    private ImageButton btnEnterPipMode;
    private MediaController mediaController;
    private PictureInPictureParams.Builder pipBuilder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerVideoPipmodeBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getExtras();
        init();
        setMController();
        setListeners();
        playVideo(videoUrl);
    }

    private void playVideo(String url) {
        videoView.setVideoPath(url);
        videoView.requestFocus();
        videoView.start();
    }

    private void setListeners() {
        btnEnterPipMode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startPIPMode();
            }
        });
    }

    private void getExtras() {
        try {
            this.videoUrl = getIntent().getExtras().getString(VerVideoPIPModeActivity.VIDEO_URL, null);
        } catch (NullPointerException e) {
            e.getMessage();
        }
    }

    private void init() {
        this.videoView = binding.videoView;
        this.btnEnterPipMode = binding.btnEntrarPIPMode;
        this.mediaController = new MediaController(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            this.pipBuilder = new PictureInPictureParams.Builder();
        }
    }

    private void startPIPMode() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            enterPictureInPictureMode(pipBuilder.build());
        }
    }

    private void setMController() {
        mediaController.setAnchorView(videoView);
        videoView.setMediaController(mediaController);
    }

    @Override
    protected void onUserLeaveHint() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            if (!isInPictureInPictureMode()) {
                startPIPMode();
            }
        }
    }

    @Override
    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
        if (isInPictureInPictureMode) {
            btnEnterPipMode.setVisibility(View.GONE);
        } else {
            btnEnterPipMode.setVisibility(View.VISIBLE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        playNextVideo(intent);
    }

    private void playNextVideo(Intent intent) {
        try {
            String nextVideoUrl = intent.getExtras().getString(VerVideoPIPModeActivity.VIDEO_URL, null);

            if (nextVideoUrl == null) {
                finish();
            } else {
                playVideo(nextVideoUrl);
            }

        } catch (NullPointerException e) {
            e.getMessage();
        }
    }
}