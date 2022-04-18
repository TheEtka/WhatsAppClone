package com.aek.whatsapp.vista.mainfragments.estados.ver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.EstadosController;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.databinding.ActivityVerEstadoBinding;
import com.aek.whatsapp.models.Estado;
import com.aek.whatsapp.utils.AnimUtils;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.utils.TimestampConverter;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import jp.shts.android.storiesprogressview.StoriesProgressView;

public class VerEstadoActivity extends AppCompatActivity implements StoriesProgressView.StoriesListener{

    private ActivityVerEstadoBinding binding;
    private String uid;
    /////////////////////
    private StoriesProgressView storiesProgress;
    private View btnNext, btnReverse;
    private ImageView imgEstado;
    private ImageButton btnFinish, btnOpciones;
    private CircleImageView imgUser;
    private TextView txtNombre, txtTimestamp, txtNrEspectadores;
    private RelativeLayout topBar;
    private LinearLayout layoutEspectadores;
    private List<DocumentSnapshot> list;
    private long pressTime = 0, limit = 500;
    private int counterPosition = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerEstadoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);

        getExtras();
        init();
        setListeners();
        comprobarAuthorEstado();
        getListaEstados();
        FbUser.getUserBasicData(uid, new WeakReference<>(imgUser),
                new WeakReference<>(txtNombre), new WeakReference<>(this));
    }

    private void comprobarAuthorEstado() {
        if (uid.equals(FbUser.getCurrentUserId())) {
            btnOpciones.setVisibility(View.VISIBLE);
            btnOpciones.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mostrarBottomSheetOpcionesEstado();
                }
            });

            layoutEspectadores.setVisibility(View.VISIBLE);
            layoutEspectadores.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Estado estado = list.get(counterPosition).toObject(Estado.class);
                    if (estado != null) {
                        Intent intent = new Intent(getBaseContext(), VerListaEspectadoresEstadoActivity.class);
                        intent.putExtra(VerListaEspectadoresEstadoActivity.ID_ESTADO, estado.getEstadoId());
                        startActivity(intent);
                    }
                }
            });
        } else {
            btnOpciones.setVisibility(View.GONE);
            layoutEspectadores.setVisibility(View.GONE);
        }
    }

    private void setListeners() {
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        btnNext.setOnTouchListener(onTouchListener);
        btnNext.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgress.skip();
            }
        });

        btnReverse.setOnTouchListener(onTouchListener);
        btnReverse.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                storiesProgress.reverse();
            }
        });
    }

    private View.OnTouchListener onTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    pressTime = System.currentTimeMillis();
                    storiesProgress.pause();
                    AnimUtils.showView(topBar, 350, 0);
                    AnimUtils.showView(storiesProgress, 350, 0);
                    if (uid.equals(FbUser.getCurrentUserId())) {
                        AnimUtils.showView(layoutEspectadores, 350, 0);
                    }
                    return false;

                case MotionEvent.ACTION_UP:
                    long now = System.currentTimeMillis();
                    storiesProgress.resume();
                    AnimUtils.showView(topBar, 150, 1);
                    AnimUtils.showView(storiesProgress, 150, 1);
                    if (uid.equals(FbUser.getCurrentUserId())) {
                        AnimUtils.showView(layoutEspectadores, 150, 1);
                    }
                    return limit < now - pressTime;
            }
            return false;
        }
    };

    private void init() {
        this.storiesProgress = binding.storiesProgress;
        this.btnNext = binding.btnNext;
        this.btnReverse = binding.btnReverse;
        this.imgEstado = binding.imgEstado;
        this.btnFinish = binding.btnFinish;
        this.btnOpciones = binding.btnOpcionesEstado;
        this.imgUser = binding.imgUserEstado;
        this.txtNombre = binding.txtUserNameEstado;
        this.txtTimestamp = binding.txtTimestampEstado;
        this.topBar = binding.topBar;
        this.txtNrEspectadores = binding.txtNrEspectadoresEstado;
        this.layoutEspectadores = binding.layoutEspectadores;
    }

    private void getExtras() {
        try {
            this.uid = getIntent().getExtras().getString("UID", null);
        } catch (NullPointerException e) {
            e.getCause();
        }
    }

    private void getListaEstados() {
        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.ESTADOS)
                .document(uid)
                .collection(FirebaseConstants.ESTADOS)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    list = task.getResult().getDocuments();

                    if (list.size() > 0) {

                        getEstado(counterPosition);
                        configStoriesProgress();
                        startStories();

                    } else {
                        Toast.makeText(VerEstadoActivity.this, "Sin estados...", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(VerEstadoActivity.this, "Error al obtener la lista de estados", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void startStories() {
        storiesProgress.startStories();
    }

    private void configStoriesProgress() {
        storiesProgress.setStoriesCount(list.size());
        storiesProgress.setStoryDuration(3000);
        storiesProgress.setStoriesListener(VerEstadoActivity.this);
    }

    private void getEstado(int index) {
        try {
            Estado estado = list.get(index).toObject(Estado.class);
            if (estado != null) {
                if (!isFinishing()) {
                    txtTimestamp.setText(TimestampConverter.getTimestamp(estado.getTimestamp()));
                    Glide.with(getBaseContext()).load(estado.getImgUrl())
                            .transition(new DrawableTransitionOptions().crossFade()).into(imgEstado);
                }

                if (!estado.getUid().equals(FbUser.getCurrentUserId())) {
                    EstadosController.addVisita(estado.getEstadoId());
                }

                if (estado.getUid().equals(FbUser.getCurrentUserId())) {
                    EstadosController.getNrEspectadores(estado.getEstadoId(),
                            new WeakReference<>(layoutEspectadores),
                            new WeakReference<>(txtNrEspectadores));
                }

            }
        } catch (IndexOutOfBoundsException e) {
            e.getCause();
        }
    }

    @Override
    public void onNext() {
        getEstado(++counterPosition);
    }

    @Override
    public void onPrev() {
        if (counterPosition > 0) {
            getEstado(--counterPosition);
        }
    }

    @Override
    public void onComplete() {
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        storiesProgress.destroy();
    }

    @Override
    protected void onPause() {
        super.onPause();
        storiesProgress.pause();
    }

    @Override
    protected void onResume() {
        super.onResume();

        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                storiesProgress.resume();
            }
        },500);
    }

    private void mostrarBottomSheetOpcionesEstado() {
        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
        View layoutBottomSheet = getLayoutInflater().inflate(R.layout.layout_opciones_estado, null);
        setButtonBottomSheetListeners(layoutBottomSheet, bottomSheetDialog);
        bottomSheetDialog.setContentView(layoutBottomSheet);
        bottomSheetDialog.show();
        storiesProgress.pause();
    }

    private void setButtonBottomSheetListeners(View layoutBottomSheet, BottomSheetDialog bottomSheetDialog) {
        layoutBottomSheet.findViewById(R.id.fabEliminarEstado).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Estado estado = list.get(counterPosition).toObject(Estado.class);
                if (estado != null) {
                    EstadosController.eliminarEstadoFromStorage(estado.getImgUrl(),
                            estado.getEstadoId(), new WeakReference<>(VerEstadoActivity.this));
                }
                bottomSheetDialog.dismiss();
            }
        });

        bottomSheetDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                storiesProgress.resume();
            }
        });
    }

}