package com.aek.whatsapp.viewholders.chat;

import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.R;
import com.giphy.sdk.ui.views.GPHMediaView;

public class MessageTypeGIFViewHolder extends RecyclerView.ViewHolder {

    public TextView txtMensaje;
    public GPHMediaView gphMediaView;
    public CardView cardView;

    public MessageTypeGIFViewHolder(@NonNull View itemView) {
        super(itemView);

        this.txtMensaje = itemView.findViewById(R.id.txtMessageTimestamp);
        this.cardView = itemView.findViewById(R.id.cardViewMessage);
        this.gphMediaView = itemView.findViewById(R.id.gphMediaView);
    }

}
