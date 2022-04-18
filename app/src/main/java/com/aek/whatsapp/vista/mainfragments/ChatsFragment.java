package com.aek.whatsapp.vista.mainfragments;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.ChatController;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.models.ContactoChat;
import com.aek.whatsapp.utils.AnimUtils;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.utils.TxtUtils;
import com.aek.whatsapp.viewholders.ContactoChatViewHolder;
import com.aek.whatsapp.vista.MainActivity;
import com.aek.whatsapp.vista.mainfragments.chat.ChatActivity;
import com.aek.whatsapp.vista.mainfragments.chat.archivados.ListaChatsArchivadosActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.HashMap;

public class ChatsFragment extends Fragment {

    private RecyclerView recyclerViewContactos;
    private TextView txtWsNoContactos, txtChatsArchivados;
    private FirestoreRecyclerAdapter<ContactoChat, ContactoChatViewHolder> adapter;
    private HashMap<String, Boolean> mapNrChatsSinLeer;
    /////////
    private androidx.appcompat.view.ActionMode actionMode;
    private HashMap<String, ContactoChatViewHolder> mapActionMode;

    public ChatsFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_chats, container, false);
        init(view);
        getContactos();
        getNrContactosArchivados();
        setIconWSTxtInfo();
        return view;
    }

    private void getNrContactosArchivados() {
        Query query = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CONTACTOS_CHAT)
                .document(FbUser.getCurrentUserId())
                .collection(FirebaseConstants.CONTACTOS_ARCHIVADOS);

        query.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot queryDocumentSnapshots, @Nullable FirebaseFirestoreException error) {
                if (queryDocumentSnapshots != null) {
                    int nrChatsArchivados = queryDocumentSnapshots.getDocuments().size();
                    if (nrChatsArchivados == 0) {
                        txtChatsArchivados.setVisibility(View.GONE);
                    } else {
                        txtChatsArchivados.setText("Archivados (" + nrChatsArchivados + ")");
                        txtChatsArchivados.setVisibility(View.VISIBLE);

                        txtChatsArchivados.setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                startActivity(new Intent(requireContext(), ListaChatsArchivadosActivity.class));
                            }
                        });

                    }
                }
            }
        });
    }

    private void setIconWSTxtInfo() {
        ArrayList<String> listChars = new ArrayList<>();
        listChars.add("$");

        ArrayList<Integer> listImages = new ArrayList<>();
        listImages.add(R.drawable.ic_contactos_gray);

        TxtUtils.setIconInTxtView(txtWsNoContactos,
                getString(R.string.txt_info_chats),
                listChars, listImages, requireContext());

    }

    private void init(View view) {
        this.recyclerViewContactos = view.findViewById(R.id.recyclerViewContactosChat);
        this.txtWsNoContactos = view.findViewById(R.id.txtWsNoContactos);
        this.txtChatsArchivados = view.findViewById(R.id.txtChatArchivados);
        this.mapNrChatsSinLeer = new HashMap<>();
        this.mapActionMode = new HashMap<>();
    }

    @Override
    public void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        if (adapter != null) {
            adapter.stopListening();
        }
    }

    private void getContactos() {
        Query query = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CONTACTOS_CHAT)
                .document(FbUser.getCurrentUserId())
                .collection(FirebaseConstants.CONTACTOS_ACTIVOS)
                .orderBy("timestamp", Query.Direction.DESCENDING);
        ///////////
        FirestoreRecyclerOptions<ContactoChat> options = new FirestoreRecyclerOptions.Builder<ContactoChat>()
                .setQuery(query, ContactoChat.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ContactoChat, ContactoChatViewHolder>(options) {
            @Override
            protected void onBindViewHolder(@NonNull ContactoChatViewHolder holder, int position, @NonNull ContactoChat contacto) {

                String contactoChatId = getSnapshots().getSnapshot(holder.getAdapterPosition()).getId();

                FbUser.getListenerUserBasicData(contactoChatId,
                        new WeakReference<>(holder.imgUser),
                        new WeakReference<>(holder.txtNombreUser),
                        new WeakReference<>(requireActivity()));

                ChatController.showDoubleCheckMessageSeen(contactoChatId,
                        new WeakReference<>(holder.imgMessageSeen),
                        new WeakReference<>(requireContext()));

                ChatController.getLastMessage(contactoChatId,
                        new WeakReference<>(holder.txtLastMessage),
                        new WeakReference<>(holder.txtTimestampLastMessage),
                        new WeakReference<>(holder.imgTypeMessage),
                        new WeakReference<>(requireContext()));

                if (contacto.isChatVisto) {
                    holder.imgBadgeNewMessage.setVisibility(View.GONE);
                    mapNrChatsSinLeer.remove(contactoChatId);
                } else {
                    holder.imgBadgeNewMessage.setVisibility(View.VISIBLE);
                    mapNrChatsSinLeer.put(contactoChatId, true);
                }

                MainActivity.mostrarBadgeNuevoChat(mapNrChatsSinLeer.size());

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        if (actionMode == null) {
                            Intent intent = new Intent(requireActivity(), ChatActivity.class);
                            intent.putExtra(ChatActivity.UID, contactoChatId);
                            startActivity(intent);
                        } else {
                            onItemSelect(contactoChatId, holder, false);
                        }

                    }
                });

                holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        onItemSelect(contactoChatId, holder, false);
                        return true;
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

            @Override
            public void onDataChanged() {
                super.onDataChanged();

                int nrContactos = getItemCount();

                if (nrContactos == 0) {
                    txtWsNoContactos.setVisibility(View.VISIBLE);
                } else {
                    txtWsNoContactos.setVisibility(View.GONE);
                }
            }
        };

        recyclerViewContactos.setAdapter(adapter);

    }

    private void onItemSelect(String contactoChatId, ContactoChatViewHolder holder, boolean selectAllItems) {
        toggleSelection(contactoChatId, holder, selectAllItems);

        boolean hasItemsChecked = mapActionMode.size() > 0;

        if (hasItemsChecked && actionMode == null) {
            try {
                if (getActivity() != null) {
                    actionMode = ((AppCompatActivity) getActivity()).startSupportActionMode((androidx.appcompat.view.ActionMode.Callback) actionModeCallback);
                }
            } catch (NullPointerException e) {
                e.getMessage();
            }

        } else if (!hasItemsChecked && actionMode != null) {
            actionMode.finish();
            hideAllContactsSelected();
        }
        mostrarNrItemsSeleccionados();
        mostrarMenuItem();
    }

    private void mostrarMenuItem() {
        if (actionMode != null) {
            if (mapActionMode.size() > 1) {
                actionMode.getMenu().getItem(0).setVisible(false);
            } else {
                actionMode.getMenu().getItem(0).setVisible(true);
            }
        }
    }

    private void mostrarNrItemsSeleccionados() {
        if (actionMode != null) {
            actionMode.setTitle(String.valueOf(mapActionMode.size()));
        }
    }

    private void hideAllContactsSelected() {
        for (String uid : mapActionMode.keySet()) {
            ContactoChatViewHolder contactoChatViewHolder = mapActionMode.get(uid);
            if (contactoChatViewHolder != null) {
                AnimUtils.scaleView(contactoChatViewHolder.imgContactoSelected, 150, 0f);
            }
        }
        mapActionMode.clear();
    }

    private void toggleSelection(String contactoChatId, ContactoChatViewHolder holder, boolean selectAllItems) {
        if (selectAllItems) {
            AnimUtils.scaleView(holder.imgContactoSelected, 150, 1f);
            mapActionMode.put(contactoChatId, holder);
        } else {
            if (mapActionMode.containsKey(contactoChatId)) {
                AnimUtils.scaleView(holder.imgContactoSelected, 150, 0f);
                mapActionMode.remove(contactoChatId);
            } else {
                AnimUtils.scaleView(holder.imgContactoSelected, 150, 1f);
                mapActionMode.put(contactoChatId, holder);
            }
        }
    }

    private ActionMode.Callback actionModeCallback = new ActionMode.Callback() {
        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            mode.getMenuInflater().inflate(R.menu.menu_action_mode_chat, menu);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
            switch (item.getItemId()) {

                case R.id.menuAMDelete:
                    Toast.makeText(getContext(), "ELIMINAR CHAT: " + mapActionMode.keySet(), Toast.LENGTH_SHORT).show();
                    mode.finish();
                    return true;

                case R.id.menuAMArchivar:

                    if (mapActionMode.size() > 0) {
                        for (String uid : mapActionMode.keySet()) {
                            ChatController.archivarChat(uid, new WeakReference<>(requireContext()));
                        }
                    }
                    mode.finish();
                    return true;
                case R.id.menuAMSelectAll:
                    if (adapter != null) {
                        int itemsCount = adapter.getItemCount();
                        for (int i = 0; i < itemsCount; i++) {
                            String uid = adapter.getSnapshots().getSnapshot(i).getId();
                            ContactoChatViewHolder holder = (ContactoChatViewHolder)
                                    recyclerViewContactos.findViewHolderForAdapterPosition(i);
                            if (holder != null) {
                                onItemSelect(uid, holder, true);
                            }
                        }
                    }
                    return true;
                default:
                    return false;
            }
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            if (actionMode != null) {
                actionMode = null;
                hideAllContactsSelected();
            }
        }
    };

}