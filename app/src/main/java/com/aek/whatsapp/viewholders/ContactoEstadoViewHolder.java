package com.aek.whatsapp.viewholders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactoEstadoViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView img;
    public TextView txtNombre, txtEstado;
    public ImageButton btnLlamada, btnVideoLlamada;

    public ContactoEstadoViewHolder(@NonNull View itemView) {
        super(itemView);
        this.img = itemView.findViewById(R.id.imgContacto);
        this.txtNombre = itemView.findViewById(R.id.txtNombreContacto);
        this.txtEstado = itemView.findViewById(R.id.txtEstadoContacto);
        this.btnLlamada = itemView.findViewById(R.id.btnLlamadaContacto);
        this.btnVideoLlamada = itemView.findViewById(R.id.btnVideoLlamadaContacto);
    }
}
