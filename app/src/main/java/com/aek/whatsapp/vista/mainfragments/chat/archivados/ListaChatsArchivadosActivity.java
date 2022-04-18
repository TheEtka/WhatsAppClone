package com.aek.whatsapp.vista.mainfragments.chat.archivados;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.ChatController;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.databinding.ActivityListaChatsArchivadosBinding;
import com.aek.whatsapp.models.ContactoChat;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.viewholders.ContactoChatViewHolder;
import com.aek.whatsapp.vista.MainActivity;
import com.aek.whatsapp.vista.mainfragments.chat.ChatActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.ref.WeakReference;

public class ListaChatsArchivadosActivity extends AppCompatActivity {

    private ActivityListaChatsArchivadosBinding binding;
    private RecyclerView recyclerViewChatsArchivados;
    private ImageButton btnFinish;
    private FirestoreRecyclerAdapter<ContactoChat, ContactoChatViewHolder> adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityListaChatsArchivadosBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        init();
        setListeners();
        getContactosArchivados();
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
        this.btnFinish = binding.btnFinish;
        this.recyclerViewChatsArchivados = binding.recyclerViewChatsArchivados;
    }

    private void getContactosArchivados() {
        Query query = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CONTACTOS_CHAT)
                .document(FbUser.getCurrentUserId())
                .collection(FirebaseConstants.CONTACTOS_ARCHIVADOS)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        ///////////
        FirestoreRecyclerOptions<ContactoChat> options = new FirestoreRecyclerOptions.Builder<ContactoChat>()
                .setLifecycleOwner(this)
                .setQuery(query, ContactoChat.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ContactoChat, ContactoChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactoChatViewHolder holder, int position, @NonNull ContactoChat contacto) {

                String contactoChatId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();

                FbUser.getListenerUserBasicData(contactoChatId,
                        new WeakReference<>(holder.imgUser),
                        new WeakReference<>(holder.txtNombreUser),
                        new WeakReference<>(ListaChatsArchivadosActivity.this));

                ChatController.showDoubleCheckMessageSeen(contactoChatId,
                        new WeakReference<>(holder.imgMessageSeen),
                        new WeakReference<>(ListaChatsArchivadosActivity.this));

                ChatController.getLastMessage(contactoChatId,
                        new WeakReference<>(holder.txtLastMessage),
                        new WeakReference<>(holder.txtTimestampLastMessage),
                        new WeakReference<>(holder.imgTypeMessage),
                        new WeakReference<>(ListaChatsArchivadosActivity.this));

                if (contacto.isChatVisto) {
                    holder.imgBadgeNewMessage.setVisibility(View.GONE);
                } else {
                    holder.imgBadgeNewMessage.setVisibility(View.VISIBLE);
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(ListaChatsArchivadosActivity.this, ChatActivity.class);
                        intent.putExtra(ChatActivity.UID, contactoChatId);
                        startActivity(intent);
                    }
                });

            }

            @NonNull
            @Override
            public ContactoChatViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_contacto_chat,
                        parent, false);
                return new ContactoChatViewHolder(view);
            }

        };

        recyclerViewChatsArchivados.setAdapter(adapter);

    }

}