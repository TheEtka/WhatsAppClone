package com.aek.whatsapp.vista.mainfragments;

import static android.app.Activity.RESULT_OK;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.camera.core.CameraSelector;
import androidx.camera.core.CameraX;
import androidx.camera.core.ImageCapture;
import androidx.camera.core.ImageCaptureException;
import androidx.camera.core.Preview;
import androidx.camera.lifecycle.ProcessCameraProvider;
import androidx.camera.view.PreviewView;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.adapters.GalleryAdapter;
import com.aek.whatsapp.controlador.CameraController;
import com.aek.whatsapp.controlador.PerfilController;
import com.aek.whatsapp.utils.AnimUtils;
import com.aek.whatsapp.utils.CameraUtils;
import com.aek.whatsapp.utils.CropImage;
import com.aek.whatsapp.vista.mainfragments.chat.ChatActivity;
import com.aek.whatsapp.vista.mainfragments.chat.PhotoMessageActivity;
import com.aek.whatsapp.vista.mainfragments.estados.add.AddNewEstadoPhotoActivity;
import com.aek.whatsapp.vista.menu.fragments.PerfilFragment;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.common.util.concurrent.ListenableFuture;
import com.yalantis.ucrop.UCrop;

import java.io.File;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.concurrent.ExecutionException;

public class CameraFragment extends Fragment {

    public static final String TYPE_ACTION_CAMERA = "typeActionCamera";
    public static final String ACTION_MESSAGE = "actionMessage";
    public static final String ACTION_ESTADO = "actionEstado";
    public static final String ACTION_ACTUALIZAR_FOTO = "actionActualizarFoto";
    //////////////  EXTRAS
    private String typeAction, uidReceiver;
    //////////////  CAMERA
    private PreviewView cameraPreview;
    private ProcessCameraProvider cameraProvider;
    private ImageCapture imageCapture;
    private int lensFacingType = CameraSelector.LENS_FACING_BACK, flashMode = ImageCapture.FLASH_MODE_OFF;
    //////////////  LAYOUT CAMERA
    private ImageButton btnTakePhoto, btnSwitchCamera, btnSwitchFlashCamera;
    private RelativeLayout layoutCamera;
    //////////////  BOTTOM SHEET GALLERY
    private BottomSheetBehavior<CoordinatorLayout> bottomSheetGallery;
    private RelativeLayout toolbarGaleria;
    private RecyclerView recyclerViewHorizontal, recyclerViewGrid;
    private TextView txtNrFotos;
    private ImageButton btnOcultarBottomSheet;
    private GalleryAdapter adapterHorizontal, adapterGrid;

    public CameraFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
        getExtras();
        init(view);
        setListeners();
        startPreviewCamera();
        getImages();
        setFragmentCameraInAdapters();
        return view;
    }

    private void init(View view) {
        this.cameraPreview = view.findViewById(R.id.cameraPreview);
        this.btnTakePhoto = view.findViewById(R.id.btnTakePhoto);
        this.btnSwitchCamera = view.findViewById(R.id.btnSwitchCamera);
        this.btnSwitchFlashCamera = view.findViewById(R.id.btnSwitchFlashCamera);
        this.layoutCamera = view.findViewById(R.id.layoutCamera);
        //////////////  BOTTOM SHEET GALLERY
        CoordinatorLayout layoutBottomSheetGallery = view.findViewById(R.id.bottomSheetGallery);
        this.bottomSheetGallery = BottomSheetBehavior.from(layoutBottomSheetGallery);
        this.recyclerViewHorizontal = view.findViewById(R.id.recyclerViewGalleryHorizontal);
        this.recyclerViewGrid = view.findViewById(R.id.recyclerViewGalleryGrid);
        this.toolbarGaleria = view.findViewById(R.id.toolbarGaleria);
        this.txtNrFotos = view.findViewById(R.id.txtNrFotosGaleria);
        this.btnOcultarBottomSheet = view.findViewById(R.id.btnOcultarBottomSheet);
    }

    private void getExtras() {
        try {
            this.typeAction = getArguments().getString(CameraFragment.TYPE_ACTION_CAMERA, null);
            this.uidReceiver = getArguments().getString(ChatActivity.UID,null);
        } catch (NullPointerException e) {
            e.getCause();
        }
    }

    private void setListeners() {
        btnTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnTakePhoto.setEnabled(false);
                AnimUtils.animateBtnTakePhoto(btnTakePhoto);

                File file = CameraUtils.getNewFile();

                ImageCapture.OutputFileOptions outputFileOptions =
                        new ImageCapture.OutputFileOptions.Builder(file).build();

                imageCapture.setTargetRotation(CameraUtils.getDisplayRotation(requireContext()));
                imageCapture.setFlashMode(flashMode);
                imageCapture.takePicture(outputFileOptions, ContextCompat.getMainExecutor(requireContext()),
                        new ImageCapture.OnImageSavedCallback() {
                            @RequiresApi(api = Build.VERSION_CODES.M)
                            @Override
                            public void onImageSaved(@NonNull ImageCapture.OutputFileResults outputFileResults) {

                                MediaScannerConnection.scanFile(requireContext(),
                                        new String[]{file.toString()},null,null);

                                btnTakePhoto.setEnabled(true);

                                switch (typeAction) {
                                    case CameraFragment.ACTION_MESSAGE:
                                        Intent intent = new Intent(requireContext(), PhotoMessageActivity.class);
                                        intent.putExtra(PhotoMessageActivity.IMG_URI, file.getPath());
                                        intent.putExtra(ChatActivity.UID, uidReceiver);
                                        startActivity(intent);
                                        break;

                                    case CameraFragment.ACTION_ESTADO:
                                        startActivity(new Intent(requireContext(), AddNewEstadoPhotoActivity.class)
                                        .putExtra("IMG_URI", file.getPath()));
                                        break;
                                    case CameraFragment.ACTION_ACTUALIZAR_FOTO:
                                        CropImage.cropMiniaturaFromFragment(Uri.fromFile(new File(file.getPath())),
                                                requireContext(), CameraFragment.this);
                                        break;
                                }

                            }

                            @Override
                            public void onError(@NonNull ImageCaptureException exception) {
                                btnTakePhoto.setEnabled(true);
                                Toast.makeText(requireContext(), "Error al tomar la foto: " + exception.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });

            }
        });

        btnSwitchCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lensFacingType == CameraSelector.LENS_FACING_BACK) {
                    lensFacingType = CameraSelector.LENS_FACING_FRONT;
                } else {
                    lensFacingType = CameraSelector.LENS_FACING_BACK;
                }
                startPreviewCamera();
                AnimUtils.rotateSwitchCameraButton(btnSwitchCamera);
            }
        });

        btnSwitchFlashCamera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                switch (flashMode) {
                    case ImageCapture.FLASH_MODE_OFF:
                        flashMode = ImageCapture.FLASH_MODE_ON;
                        AnimUtils.changeSwitchFlashButtonIcon(btnSwitchFlashCamera, R.drawable.ic_flash_on, requireContext());
                        break;
                    case ImageCapture.FLASH_MODE_ON:
                        flashMode = ImageCapture.FLASH_MODE_AUTO;
                        AnimUtils.changeSwitchFlashButtonIcon(btnSwitchFlashCamera, R.drawable.ic_flash_auto, requireContext());
                        break;
                    case ImageCapture.FLASH_MODE_AUTO:
                        flashMode = ImageCapture.FLASH_MODE_OFF;
                        AnimUtils.changeSwitchFlashButtonIcon(btnSwitchFlashCamera, R.drawable.ic_flash_off, requireContext());
                        break;
                }
            }
        });

        btnOcultarBottomSheet.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bottomSheetGallery.getState() == BottomSheetBehavior.STATE_EXPANDED) {
                    bottomSheetGallery.setState(BottomSheetBehavior.STATE_COLLAPSED);
                }
            }
        });

        bottomSheetGallery.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState)
                {
                    case BottomSheetBehavior.STATE_COLLAPSED:

                        adapterHorizontal.setItemClickable(true);
                        toolbarGaleria.setFocusableInTouchMode(false);
                        adapterGrid.setItemClickable(false);

                        CameraController.habilitarButtonLayoutCamera(btnTakePhoto, true);
                        CameraController.habilitarButtonLayoutCamera(btnSwitchCamera, true);
                        CameraController.habilitarButtonLayoutCamera(btnSwitchFlashCamera, true);

                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:

                        adapterHorizontal.setItemClickable(false);
                        toolbarGaleria.setFocusableInTouchMode(true);
                        adapterGrid.setItemClickable(true);

                        CameraController.habilitarButtonLayoutCamera(btnTakePhoto, false);
                        CameraController.habilitarButtonLayoutCamera(btnSwitchCamera, false);
                        CameraController.habilitarButtonLayoutCamera(btnSwitchFlashCamera, false);

                        break;
                }

            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {
                recyclerViewHorizontal.setAlpha(1-slideOffset);
                layoutCamera.setAlpha(1-slideOffset);

                recyclerViewGrid.setAlpha(slideOffset);
                toolbarGaleria.setAlpha(slideOffset);
            }
        });

    }

    private void startPreviewCamera() {
        ListenableFuture<ProcessCameraProvider> cameraProvideFuture = ProcessCameraProvider.getInstance(requireContext());

        cameraProvideFuture.addListener(() -> {

            try {

                cameraProvider = cameraProvideFuture.get();
                cameraProvider.unbindAll();

                Preview preview = new Preview.Builder().build();

                CameraSelector cameraSelector = new CameraSelector.Builder().requireLensFacing(lensFacingType).build();

                imageCapture = new ImageCapture.Builder().build();

                cameraProvider.bindToLifecycle(CameraFragment.this, cameraSelector, imageCapture, preview);

                // preview.setSurfaceProvider(cameraPreview.createSurfaceProvider());
                preview.setSurfaceProvider(cameraPreview.getSurfaceProvider());

            } catch (ExecutionException | InterruptedException | NullPointerException | IllegalStateException e) {
                e.getCause();
            }

        }, ContextCompat.getMainExecutor(requireContext()));
    }

    @SuppressLint("RestrictedApi")
    private void stopPreviewCamera() {
        if (CameraX.isInitialized()) {
            // CameraX.unbindAll();
            CameraX.shutdown();
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        stopPreviewCamera();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        stopPreviewCamera();
    }

    private void getImages() {
        List<String> listImages = CameraUtils.getImagesFromGallery(requireContext());

        adapterHorizontal = new GalleryAdapter(requireActivity(), listImages, R.layout.card_gallery_small,
                typeAction, true, uidReceiver);
        adapterGrid = new GalleryAdapter(requireActivity(), listImages, R.layout.card_gallery,
                typeAction, false, uidReceiver);

        recyclerViewHorizontal.setAdapter(adapterHorizontal);
        recyclerViewGrid.setAdapter(adapterGrid);
        txtNrFotos.setText(listImages.size() + " fotos");
    }

    public BottomSheetBehavior<CoordinatorLayout> getBottomSheetGallery() {
        return bottomSheetGallery;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode) {
            case PerfilFragment.IMG_CROP_CODE:
                if (resultCode == RESULT_OK) {
                    try {
                        Uri imgCrop = UCrop.getOutput(data);
                        if (imgCrop != null) {
                            PerfilController.actualizarImgStorage(imgCrop,
                                    new WeakReference<>(requireContext()));
                        }
                    } catch (NullPointerException e) {
                        e.getCause();
                    }
                }
                break;
        }
    }

    private void setFragmentCameraInAdapters() {
        adapterGrid.setCameraFragment(this);
        adapterHorizontal.setCameraFragment(this);
    }

}