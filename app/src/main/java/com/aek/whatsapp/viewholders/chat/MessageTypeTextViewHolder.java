package com.aek.whatsapp.viewholders.chat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.R;

public class MessageTypeTextViewHolder extends RecyclerView.ViewHolder {

    public TextView txtMensaje;
    public CardView cardView;

    public MessageTypeTextViewHolder(@NonNull View itemView) {
        super(itemView);

        this.txtMensaje = itemView.findViewById(R.id.txtMessageTimestamp);
        this.cardView = itemView.findViewById(R.id.cardViewMessage);
    }

}
