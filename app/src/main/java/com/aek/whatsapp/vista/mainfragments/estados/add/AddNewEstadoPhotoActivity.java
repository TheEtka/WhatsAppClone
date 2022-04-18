package com.aek.whatsapp.vista.mainfragments.estados.add;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.net.Uri;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aek.whatsapp.R;
import com.aek.whatsapp.adapters.FiltrosAdapter;
import com.aek.whatsapp.controlador.EstadosController;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.databinding.ActivityAddNewEstadoPhotoBinding;
import com.aek.whatsapp.models.Filtro;
import com.aek.whatsapp.utils.BitmapUtils;
import com.aek.whatsapp.utils.OnFilterListener;
import com.aek.whatsapp.vista.mainfragments.estados.canvas.CanvasImagenEstado;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.Transformation;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.material.bottomsheet.BottomSheetBehavior;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textfield.TextInputEditText;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.wasabeef.glide.transformations.BlurTransformation;
import jp.wasabeef.glide.transformations.ColorFilterTransformation;
import jp.wasabeef.glide.transformations.GrayscaleTransformation;
import jp.wasabeef.glide.transformations.gpu.InvertFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.PixelationFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SepiaFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.SketchFilterTransformation;
import jp.wasabeef.glide.transformations.gpu.VignetteFilterTransformation;

public class AddNewEstadoPhotoActivity extends AppCompatActivity implements OnFilterListener {

    private ActivityAddNewEstadoPhotoBinding binding;
    private String imgUriString;
    //////////
    private CanvasImagenEstado canvasImagenEstado;
    private FloatingActionButton fabAddNewEstado;
    private ImageButton btnFinish, btnChangePaintColor, btnHabilitarPainter;
    private CircleImageView imgUser;
    private TextInputEditText txtEstado;
    private TextView btnAddText;
    //////////  FILTROS
    private RecyclerView recyclerViewFiltros;
    private ImageView imgArrow;
    private BottomSheetBehavior<LinearLayout> bottomSheetFiltros;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAddNewEstadoPhotoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getExtras();
        init();
        setAdapterFiltros();
        setListeners();
        Glide.with(getBaseContext()).load(imgUriString).into(canvasImagenEstado);
        FbUser.getUserImage(new WeakReference<>(imgUser),
                new WeakReference<>(this));
    }

    private void setAdapterFiltros() {
        this.recyclerViewFiltros.setAdapter(new FiltrosAdapter(this, imgUriString, this));
    }

    private void getExtras() {
        try {
            this.imgUriString = getIntent().getExtras().getString("IMG_URI", null);
        } catch (NullPointerException e) {
            e.getCause();
        }
    }

    private void setListeners() {
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onBackPressed();
            }
        });

        btnAddText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                txtEstado.setVisibility(View.VISIBLE);
            }
        });

        btnChangePaintColor.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasImagenEstado.setRandomColorPaint();
            }
        });

        fabAddNewEstado.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri uri = BitmapUtils.getUri(BitmapUtils.getBitmapFromView(canvasImagenEstado), getBaseContext());

                if (uri == null) {
                    Toast.makeText(AddNewEstadoPhotoActivity.this, "Error al intentar obtener la uri del estado", Toast.LENGTH_SHORT).show();
                } else {
                    EstadosController.publicarEstadoEnStorage(uri, new WeakReference<>(AddNewEstadoPhotoActivity.this));
                }
            }
        });

        txtEstado.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    String estado = getEstado().trim();
                    if (estado.length() > 0) {
                        canvasImagenEstado.escribirTextoEnCanvas(estado);
                        txtEstado.setText("");
                        txtEstado.setVisibility(View.GONE);
                    }
                }

                return false;
            }
        });

        btnHabilitarPainter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                canvasImagenEstado.enablePintar();
            }
        });

        bottomSheetFiltros.addBottomSheetCallback(new BottomSheetBehavior.BottomSheetCallback() {
            @Override
            public void onStateChanged(@NonNull View bottomSheet, int newState) {

                switch (newState) {
                    case BottomSheetBehavior.STATE_COLLAPSED:
                        enableButtons(true);
                        break;
                    case BottomSheetBehavior.STATE_EXPANDED:
                        enableButtons(false);
                        canvasImagenEstado.disablePintar();
                        break;
                }
            }

            @Override
            public void onSlide(@NonNull View bottomSheet, float slideOffset) {

                imgArrow.setRotation(slideOffset * 180);

                canvasImagenEstado.setScaleX(1f - (0.20f * slideOffset));
                canvasImagenEstado.setScaleY(1f - (0.20f * slideOffset));

                fabAddNewEstado.setAlpha(1-slideOffset);
                btnHabilitarPainter.setAlpha(1-slideOffset);
                btnChangePaintColor.setAlpha(1-slideOffset);
                btnAddText.setAlpha(1-slideOffset);

            }
        });

    }

    public String getEstado() {
        return txtEstado.getText().toString();
    }

    private void init() {
        this.canvasImagenEstado = binding.imgNewEstado;
        this.fabAddNewEstado = binding.fabAddNewEstadoPhoto;
        this.btnFinish = binding.btnFinish;
        this.imgUser = binding.imgUserEstado;
        this.txtEstado = binding.txtEstado;
        this.btnAddText = binding.btnAddText;
        this.btnChangePaintColor = binding.btnChangePintColor;
        this.btnHabilitarPainter = binding.btnHabilitarPintar;
        //////////////
        LinearLayout layoutFiltros = findViewById(R.id.bottomSheetFiltros);
        this.bottomSheetFiltros = BottomSheetBehavior.from(layoutFiltros);
        this.recyclerViewFiltros = findViewById(R.id.recyclerViewFiltros);
        this.imgArrow = findViewById(R.id.imgArrow);
    }

    public static ArrayList<Filtro> getListaFiltros() {
        ArrayList<Filtro> arrayList = new ArrayList<>();
        arrayList.add(new Filtro(null, "Ninguno"));
        arrayList.add(new Filtro(new BlurTransformation(10,3),"Blur"));
        arrayList.add(new Filtro(new GrayscaleTransformation(),"Gray"));
        arrayList.add(new Filtro(new ColorFilterTransformation(0x50FF0000),"Red"));
        arrayList.add(new Filtro(new VignetteFilterTransformation(),"Vignette"));
        arrayList.add(new Filtro(new InvertFilterTransformation(),"Invert"));
        arrayList.add(new Filtro(new PixelationFilterTransformation(),"Pixelation"));
        arrayList.add(new Filtro(new SepiaFilterTransformation(),"Sepia"));
        arrayList.add(new Filtro(new SketchFilterTransformation(),"Sketch"));
        return arrayList;
    }

    @Override
    public void onFilterClicked(Transformation filtro) {
        if (filtro == null) {
            Glide.with(getBaseContext()).load(imgUriString)
                    .into(canvasImagenEstado);
        } else {
            Glide.with(getBaseContext()).load(imgUriString)
                    .apply(RequestOptions.bitmapTransform(filtro))
                    .into(canvasImagenEstado);
        }
    }

    private void enableButtons(boolean enabled) {
        fabAddNewEstado.setEnabled(enabled);
        btnHabilitarPainter.setEnabled(enabled);
        btnChangePaintColor.setEnabled(enabled);
        btnAddText.setEnabled(enabled);
    }

    @Override
    public void onBackPressed() {

        if (bottomSheetFiltros.getState() == BottomSheetBehavior.STATE_EXPANDED) {
            bottomSheetFiltros.setState(BottomSheetBehavior.STATE_COLLAPSED);
        } else if (txtEstado.getVisibility() == View.VISIBLE) {
            txtEstado.setVisibility(View.GONE);
        } else {
            super.onBackPressed();
        }
    }

}