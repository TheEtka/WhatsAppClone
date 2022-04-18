package com.aek.whatsapp.viewholders;

import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.R;

public class FiltroViewHolder extends RecyclerView.ViewHolder {

    public ImageView imgFiltro, imgFilterUsed;
    public TextView txtFilterName;

    public FiltroViewHolder(@NonNull View itemView) {
        super(itemView);
        this.imgFiltro = itemView.findViewById(R.id.imgFiltro);
        this.imgFilterUsed = itemView.findViewById(R.id.imgFilterUser);
        this.txtFilterName = itemView.findViewById(R.id.txtFilterName);
    }
}
