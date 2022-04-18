package com.aek.whatsapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.models.Usuario;
import com.aek.whatsapp.viewholders.ContactoEstadoViewHolder;
import com.aek.whatsapp.vista.ContactosActivity;
import com.aek.whatsapp.vista.mainfragments.chat.ChatActivity;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class ContactosAdapter extends RecyclerView.Adapter<ContactoEstadoViewHolder> {

    private Activity activity;
    private List<Usuario> list;
    private String typeAction;

    public ContactosAdapter(Activity activity, String typeAction) {
        this.activity = activity;
        this.list = new ArrayList<>();
        this.typeAction = typeAction;
    }

    @NonNull
    @Override
    public ContactoEstadoViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_contacto_estado, parent, false);
        return new ContactoEstadoViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactoEstadoViewHolder holder, int position) {

        Usuario usuario = list.get(holder.getAdapterPosition());

        if (usuario != null) {

            holder.txtNombre.setText(usuario.getNombre());

            String estado = usuario.getEstado();

            if (estado != null && estado.length() > 0) {
                holder.txtEstado.setText(estado);
                holder.txtEstado.setVisibility(View.VISIBLE);
            } else {
                holder.txtEstado.setVisibility(View.GONE);
            }

            if (!activity.isFinishing()) {
                Glide.with(activity).load(usuario.getImgUrl()).error(R.drawable.img_user_default).into(holder.img);
            }

            switch (typeAction) {
                case ContactosActivity.ACTION_CHAT:

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent(activity, ChatActivity.class);
                            intent.putExtra(ChatActivity.UID, usuario.getUid());
                            activity.startActivity(intent);
                            activity.finish();
                        }
                    });

                    break;

                case ContactosActivity.ACTION_CALL:

                    holder.btnLlamada.setVisibility(View.VISIBLE);
                    holder.btnVideoLlamada.setVisibility(View.VISIBLE);

                    holder.btnLlamada.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(activity, "Llamada a: " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    holder.btnVideoLlamada.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            Toast.makeText(activity, "Video llamada a: " + usuario.getNombre(), Toast.LENGTH_SHORT).show();
                        }
                    });

                    break;

                case ContactosActivity.ACTION_SEND_MESSAGE:

                    holder.itemView.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            Intent intent = new Intent();
                            intent.putExtra(ChatActivity.UID, usuario.getUid());
                            activity.setResult(Activity.RESULT_OK, intent);
                            activity.finish();
                        }
                    });

                    break;
            }

        }

    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void addContacto(Usuario usuario) {
        list.add(usuario);
        notifyDataSetChanged();
    }
}
