package com.aek.whatsapp.vista;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aek.whatsapp.adapters.ContactosAdapter;
import com.aek.whatsapp.controlador.ContactosController;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.databinding.ActivityContactosBinding;
import com.aek.whatsapp.models.Usuario;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QuerySnapshot;

import org.jetbrains.annotations.NotNull;

import java.lang.ref.WeakReference;
import java.util.List;

public class ContactosActivity extends AppCompatActivity {

    public static final String TYPE_ACTION = "KeyTypeAction";
    public static final String ACTION_CHAT = "CHAT";
    public static final String ACTION_CALL = "CALL";
    public static final String ACTION_SEND_MESSAGE = "SEND MESSAGE";
    /////
    private String typeAction;
    /////
    private ActivityContactosBinding binding;
    private RecyclerView recyclerViewContactos;
    private ContactosAdapter contactosAdapter;
    private TextView txtNrContactos;
    private ImageButton btnFinish;
    private DocumentSnapshot lastDocumentVisible;
    private boolean isLoading = false, isLastPage = false;
    private final int PAGE_SIZE = 15;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContactosBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getExtras();
        init();
        setListeners();
        getListaContactos();
        ContactosController.getNrContactor(new WeakReference<>(txtNrContactos),
                new WeakReference<>(getBaseContext()));
    }

    private void getExtras() {
        try {
            this.typeAction = getIntent().getExtras().getString(ContactosActivity.TYPE_ACTION, null);
            Log.e("ZZZ","TYPE ACTION CONTACTOS: " + typeAction);
        } catch (NullPointerException e) {
            e.getCause();
        }
    }

    private void setListeners() {
        btnFinish.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        recyclerViewContactos.addOnScrollListener(onScrollListener);
    }

    private void init() {
        this.txtNrContactos = binding.txtNrContactos;
        this.btnFinish = binding.btnFinish;
        this.recyclerViewContactos = binding.recyclerViewContactos;
        this.contactosAdapter = new ContactosAdapter(this, typeAction);
        this.recyclerViewContactos.setAdapter(contactosAdapter);
    }

    private void getListaContactos() {
        isLoading = true;

        Query query = FirebaseFirestore.getInstance().collection(FirebaseConstants.USERS)
                .orderBy("timestampRegistro", Query.Direction.ASCENDING).limit(PAGE_SIZE);

        if (lastDocumentVisible == null) {

            query.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    try {
                        isLoading = false;
                        if (task.isSuccessful() && task.getResult() != null) {
                            getContacto(task.getResult().getDocuments());
                        } else {
                            Toast.makeText(ContactosActivity.this, "Error " +
                                    "get contactos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        e.getCause();
                    }
                }
            });

        } else {
            query.startAfter(lastDocumentVisible).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    try {
                        isLoading = false;
                        if (task.isSuccessful() && task.getResult() != null) {
                            getContacto(task.getResult().getDocuments());
                        } else {
                            Toast.makeText(ContactosActivity.this, "Error " +
                                    "get contactos", Toast.LENGTH_SHORT).show();
                        }
                    } catch (NullPointerException e) {
                        e.getCause();
                    }
                }
            });
        }
    }

    private void getContacto(List<DocumentSnapshot> documentSnapshotList) {
        if (documentSnapshotList.size() > 0) {
            lastDocumentVisible = documentSnapshotList.get(documentSnapshotList.size() - 1);

            for (DocumentSnapshot documentSnapshot : documentSnapshotList) {
                Usuario usuario = documentSnapshot.toObject(Usuario.class);

                if (usuario != null && !usuario.getUid().equals(FbUser.getCurrentUserId())) {
                    contactosAdapter.addContacto(usuario);
                }
            }

        } else {
            isLastPage = true;
        }
    }

    private RecyclerView.OnScrollListener onScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
            super.onScrolled(recyclerView, dx, dy);

            LinearLayoutManager layoutManager = (LinearLayoutManager) recyclerView.getLayoutManager();

            if (layoutManager != null) {

                int visibleItemCount = layoutManager.getChildCount();
                int totalItemCount = layoutManager.getItemCount();
                int firstVisibleItemPosition = layoutManager.findFirstVisibleItemPosition();

                if (!isLoading && !isLastPage) {

                    if ((visibleItemCount + firstVisibleItemPosition) >= totalItemCount &&
                    firstVisibleItemPosition >= 0 && totalItemCount >= PAGE_SIZE - 1) {
                        Log.e("ZZZ","GET LIST RVIEW");
                        getListaContactos();
                    }

                }

            }

        }
    };

}