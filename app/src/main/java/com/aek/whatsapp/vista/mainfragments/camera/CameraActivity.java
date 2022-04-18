package com.aek.whatsapp.vista.mainfragments.camera;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.coordinatorlayout.widget.CoordinatorLayout;

import android.os.Bundle;
import android.view.WindowManager;

import com.aek.whatsapp.R;
import com.aek.whatsapp.vista.mainfragments.CameraFragment;
import com.aek.whatsapp.vista.mainfragments.chat.ChatActivity;
import com.google.android.material.bottomsheet.BottomSheetBehavior;

public class CameraActivity extends AppCompatActivity {

    private String typeAction, uidReceiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getExtras();

        if (savedInstanceState == null) {
            Bundle bundle = new Bundle();
            bundle.putString(CameraFragment.TYPE_ACTION_CAMERA, typeAction);
            bundle.putString(ChatActivity.UID, uidReceiver);

            CameraFragment cameraFragment = new CameraFragment();
            cameraFragment.setArguments(bundle);

            getSupportFragmentManager()
                    .beginTransaction()
                    .add(R.id.frameCamera, cameraFragment, CameraFragment.class.getSimpleName())
                    .commit();
        }

    }

    private void getExtras() {
        try {
            typeAction = getIntent().getExtras().getString(CameraFragment.TYPE_ACTION_CAMERA, null);
            uidReceiver = getIntent().getExtras().getString(ChatActivity.UID, null);
        } catch (NullPointerException e) {
            e.getCause();
        }
    }

    @Override
    public void onBackPressed() {

        CameraFragment cameraFragment = (CameraFragment)
                getSupportFragmentManager().findFragmentByTag(CameraFragment.class.getSimpleName());

        if (cameraFragment != null) {
            BottomSheetBehavior<CoordinatorLayout> bottomSheetBehavior = cameraFragment.getBottomSheetGallery();
            if (bottomSheetBehavior != null && bottomSheetBehavior.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                bottomSheetBehavior.setState(BottomSheetBehavior.STATE_COLLAPSED);
            } else {
                super.onBackPressed();
            }

        } else {
            super.onBackPressed();
        }

    }
}