package com.aek.whatsapp.vista.mainfragments;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.aek.whatsapp.R;
import com.aek.whatsapp.controlador.FbUser;
import com.aek.whatsapp.models.ContactoEstado;
import com.aek.whatsapp.utils.FirebaseConstants;
import com.aek.whatsapp.utils.TimestampConverter;
import com.aek.whatsapp.utils.TxtUtils;
import com.aek.whatsapp.viewholders.EstadoViewHolder;
import com.aek.whatsapp.vista.mainfragments.estados.ver.VerEstadoActivity;
import com.firebase.ui.firestore.FirestoreRecyclerAdapter;
import com.firebase.ui.firestore.FirestoreRecyclerOptions;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;

import java.lang.ref.WeakReference;
import java.util.ArrayList;

public class EstadosFragment extends Fragment {

    private TextView txtWsNoEstados;
    private RecyclerView recyclerViewEstados;
    private FirestoreRecyclerAdapter<ContactoEstado, EstadoViewHolder> adapter;

    public EstadosFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_estados, container, false);
        init(view);
        setIconsWSTxtInfo();
        getContactosEstados();
        return view;
    }

    private void setIconsWSTxtInfo() {
        ArrayList<String> listChars = new ArrayList<>();
        listChars.add("$");
        listChars.add("%");

        ArrayList<Integer> listImages = new ArrayList<>();
        listImages.add(R.drawable.ic_edit_gray);
        listImages.add(R.drawable.ic_camera);

        TxtUtils.setIconInTxtView(txtWsNoEstados, getString(R.string.txt_info_estado),
                listChars, listImages, requireContext());
    }

    private void init(View view) {
        this.txtWsNoEstados = view.findViewById(R.id.txtWsNoEstados);
        this.recyclerViewEstados = view.findViewById(R.id.recyclerViewEstados);
    }

    private void getContactosEstados() {
        Query query = FirebaseFirestore.getInstance()
                .collection(FirebaseConstants.CONTACTOS_ESTADOS)
                .orderBy("timestampLastEstado", Query.Direction.DESCENDING);

        FirestoreRecyclerOptions<ContactoEstado> options = new FirestoreRecyclerOptions.Builder<ContactoEstado>()
                .setQuery(query, ContactoEstado.class)
                .build();

        adapter = new FirestoreRecyclerAdapter<ContactoEstado, EstadoViewHolder>(options) {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            protected void onBindViewHolder(@NonNull EstadoViewHolder holder, int position, @NonNull ContactoEstado contactoEstado) {

                FbUser.getListenerUserBasicData(contactoEstado.getUid(),
                        new WeakReference<>(holder.imgUserEstado),
                        new WeakReference<>(holder.txtUserName),
                        new WeakReference<>(requireActivity()));

                holder.txtTimestamp.setText(TimestampConverter.getTimestamp(contactoEstado.getTimestampLastEstado()));

                if (contactoEstado.getUid().equals(FbUser.getCurrentUserId())) {
                    holder.imgUserEstado.setBorderColor(getResources().getColor(android.R.color.darker_gray, null));
                } else {
                    holder.imgUserEstado.setBorderColor(getResources().getColor(R.color.colorAccent, null));
                }

                holder.itemView.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(getContext(), VerEstadoActivity.class);
                        intent.putExtra("UID", contactoEstado.getUid());
                        startActivity(intent);
                    }
                });

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

                int nrEstados = adapter.getItemCount();

                if (nrEstados == 0) {
                    txtWsNoEstados.setVisibility(View.VISIBLE);
                } else {
                    txtWsNoEstados.setVisibility(View.GONE);
                }
            }
        };

        recyclerViewEstados.setAdapter(adapter);
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
}