package com.aek.whatsapp.vista.perfil;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.databinding.ActivityPerfilUsuarioBinding;
import com.aek.whatsapp.models.Message;
import com.aek.whatsapp.models.Usuario;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.viewholders.ImageGalleryViewHolder;
import com.aek.whatsapp.vista.mainfragments.chat.ChatActivity;
import com.aek.whatsapp.vista.mainfragments.chat.ver.VerFotoActivity;
import com.bumptech.glide.Glide;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

public class PerfilUsuarioActivity extends AppCompatActivity {

    private String uid;
    ////////////
    private ActivityPerfilUsuarioBinding binding;
    private CollapsingToolbarLayout collapsingToolbar;
    private Toolbar toolbar;
    private ImageView imgUser;
    private TextView txtEstado, txtNrFotos;
    private LinearLayout layoutFotos;
    private RecyclerView recyclerViewFotos;
    private FirestoreRecyclerAdapter<Message, ImageGalleryViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityPerfilUsuarioBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getExtras();
        init();
        setSupportActionBar(toolbar);
        enableBackButton();
        getUserData();

        getFotosChat();
    }

    private void getFotosChat() {
        Query query = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CANALES_CHATS)
                .document(FbUser.getCurrentUserId())
                .collection(FirebaseConstants.CHATS)
                .document(uid)
                .collection(FirebaseConstants.MESSAGES)
                .whereEqualTo("type", Message.TYPE_FOTO)
                .orderBy("timestamp", Query.Direction.DESCENDING);

        ////////////////////////
        FirestoreRecyclerOptions<Message> options = new FirestoreRecyclerOptions.Builder<Message>()
                .setLifecycleOwner(this)
                .setQuery(query, Message.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<Message, ImageGalleryViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ImageGalleryViewHolder holder, int position, @NonNull Message message) {

                ImageView imagen = holder.img;

                Glide.with(getBaseContext()).load(message.getDataUrl()).into(imagen);

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(PerfilUsuarioActivity.this, VerFotoActivity.class);
                        intent.putExtra(VerFotoActivity.FOTO_URL, message.getDataUrl());
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ImageGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_gallery_small, parent, false);
                return new ImageGalleryViewHolder(view);
            }

            @Override
            public int getItemViewType(int position) {
                return getItem(position).getType();
            }

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                int nrFotos = getItemCount();

                if (nrFotos == 0) {
                    layoutFotos.setVisibility(View.GONE);
                } else {
                    layoutFotos.setVisibility(View.VISIBLE);
                    txtNrFotos.setText(String.valueOf(nrFotos));
                }
            }
        };

        recyclerViewFotos.setAdapter(adapter);

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
        }
    }

    private void getUserData() {
        FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.USERS)
                .document(uid)
                .get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful() && task.getResult() != null) {
                    Usuario usuario = task.getResult().toObject(Usuario.class);
                    if (usuario != null) {
                        Glide.with(getBaseContext()).load(usuario.getImgUrl())
                                .error(R.drawable.img_user_default)
                                .into(imgUser);
                        collapsingToolbar.setTitle(usuario.getNombre());
                        String estado = usuario.getEstado();
                        if (estado != null && estado.length() > 0) {
                            txtEstado.setText(estado);
                        } else {
                            txtEstado.setText("Sin estado");
                        }
                    }
                } else {
                    Toast.makeText(PerfilUsuarioActivity.this, "Error al obtener los datos del usuario", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void init() {
        this.imgUser = binding.imgPerfilUsuario;
        this.collapsingToolbar = binding.collapsingToolbarPerfilUsuario;
        this.toolbar = binding.toolbarPerfilUsuario;
        this.recyclerViewFotos = binding.recyclerViewFotosChat;
        this.txtEstado = binding.txtEstadoPerfil;
        this.txtNrFotos = binding.txtNrFotosChat;
        this.layoutFotos = binding.layoutFotosChat;
    }

    private void getExtras() {
        try {
            this.uid = getIntent().getExtras().getString(ChatActivity.UID, null);
        } catch (NullPointerException e) {
            e.getMessage();
        }
    }

    private void enableBackButton() {
        try {
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        } catch (NullPointerException e) {
            e.getMessage();
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        finish();
        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.menu_perfil_usuario, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menuPerfilUsuarioCompartir:
                Toast.makeText(this, "Compartir", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuPerfilUsuarioEditar:
                Toast.makeText(this, "Editar", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuPerfilUsuarioVerEnLibreta:
                Toast.makeText(this, "VerEnLibreta", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.menuPerfilUsuarioConfirmarCodigoSeguridad:
                Toast.makeText(this, "Confirmar codigo de sequridad", Toast.LENGTH_SHORT).show();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

}