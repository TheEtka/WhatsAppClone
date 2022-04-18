package com.aek.whatsapp.viewholders;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class EstadoViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView imgUserEstado;
    public TextView txtUserName, txtTimestamp;

    public EstadoViewHolder(@NonNull View itemView) {
        super(itemView);
        this.imgUserEstado = itemView.findViewById(R.id.imgUserEstado);
        this.txtUserName = itemView.findViewById(R.id.txtUserNameEstado);
        this.txtTimestamp = itemView.findViewById(R.id.txtTimestampEstado);
    }

}
