package com.aek.whatsapp.viewholders;

import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.R;

import de.hdodenhof.circleimageview.CircleImageView;

public class ContactoChatViewHolder extends RecyclerView.ViewHolder {

    public CircleImageView imgUser;
    public TextView txtNombreUser, txtLastMessage, txtTimestampLastMessage;
    public ImageButton imgBadgeNewMessage;
    public ImageView imgMessageSeen, imgTypeMessage, imgContactoSelected;

    public ContactoChatViewHolder(@NonNull View itemView) {
        super(itemView);

        this.imgUser = itemView.findViewById(R.id.imgUserContact);
        this.txtNombreUser = itemView.findViewById(R.id.txtNameContact);
        this.txtLastMessage = itemView.findViewById(R.id.txtLastMessageContact);
        this.txtTimestampLastMessage = itemView.findViewById(R.id.txtTimestampLastMessageContact);
        this.imgBadgeNewMessage = itemView.findViewById(R.id.imgBadgeNewMessage);
        this.imgMessageSeen = itemView.findViewById(R.id.imgMessageSeen);
        this.imgTypeMessage = itemView.findViewById(R.id.imgTypeMessage);
        this.imgContactoSelected = itemView.findViewById(R.id.imgContactoSelected);

    }

}
