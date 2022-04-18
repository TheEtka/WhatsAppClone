package com.aek.whatsapp.vista.mainfragments.estados.ver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.graphics.Color;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.databinding.ActivityVerListaEspectadoresEstadoBinding;
import com.aek.whatsapp.models.EspectadorEstado;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.utils.TimestampConverter;
import com.aek.whatsapp.viewholders.EstadoViewHolder;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.ref.WeakReference;

public class VerListaEspectadoresEstadoActivity extends AppCompatActivity {

    private ActivityVerListaEspectadoresEstadoBinding binding;

    public static final String ID_ESTADO = "KeyIdEstado";
    private String idEstado;
    private RecyclerView recyclerView;
    private ImageButton btnFinish;
    private TextView txtNrEspectadores;
    private FirestoreRecyclerAdapter<EspectadorEstado, EstadoViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerListaEspectadoresEstadoBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getExtras();
        init();
        setListeners();
        getListaEspectadores();
    }

    private void getListaEspectadores() {
        Query query = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.ESPECTADORES_ESTADOS)
                .document(idEstado)
                .collection(FirebaseConstants.ESPECTADORES_ESTADOS)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<EspectadorEstado> options = new FirestoreRecyclerOptions.Builder<EspectadorEstado>()
                .setQuery(query, EspectadorEstado.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<EspectadorEstado, EstadoViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull EstadoViewHolder holder, int position, @NonNull EspectadorEstado espectadorEstado) {
                holder.imgUserEstado.setBorderColor(Color.WHITE);

                holder.txtTimestamp.setText(TimestampConverter.getTimestamp(espectadorEstado.getTimestamp()));

                FbUser.getUserBasicData(espectadorEstado.getUid(),
                        new WeakReference<>(holder.imgUserEstado),
                        new WeakReference<>(holder.txtUserName),
                        new WeakReference<>(VerListaEspectadoresEstadoActivity.this));

            }

            @NonNull
            @Override
            public EstadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_estado, parent, false);
                return new EstadoViewHolder(view);
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                int nrEspectadores = getItemCount();
                txtNrEspectadores.setText("Espectadores ("+nrEspectadores+")");
            }
        };

        recyclerView.setAdapter(adapter);
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopListening();
            adapter = null;
        }
    }

    private void setListeners() {
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        this.recyclerView = binding.recyclerViewEspectadores;
        this.btnFinish = binding.btnFinish;
        this.txtNrEspectadores = binding.txtNrEspectadores;
    }

    private void getExtras() {
        try {
            this.idEstado = getIntent().getExtras()
                    .getString(VerListaEspectadoresEstadoActivity.ID_ESTADO, null);
        } catch (NullPointerException e) {
            e.getCause();
        }
    }
}