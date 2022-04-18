package com.aek.whatsapp.viewholders.chat;

import android.view.View;
import android.widget.ImageButton;
import android.widget.SeekBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.R;

public class MessageTypeAudioViewHolder extends RecyclerView.ViewHolder {

    public TextView txtMensaje;
    public CardView cardView;
    public ImageButton btnPlay;
    public SeekBar seekBar;

    public MessageTypeAudioViewHolder(@NonNull View itemView) {
        super(itemView);

        this.txtMensaje = itemView.findViewById(R.id.txtMessageTimestamp);
        this.cardView = itemView.findViewById(R.id.cardViewMessage);
        this.btnPlay = itemView.findViewById(R.id.btnPlayPauseAUDIO);
        this.seekBar = itemView.findViewById(R.id.seekBarAUDIO);
    }

}
