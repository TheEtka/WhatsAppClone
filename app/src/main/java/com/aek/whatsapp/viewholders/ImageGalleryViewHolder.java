package com.aek.whatsapp.viewholders;

import android.view.View;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.R;

public class ImageGalleryViewHolder extends RecyclerView.ViewHolder {

    public ImageView img;

    public ImageGalleryViewHolder(@NonNull View itemView) {
        super(itemView);
        this.img = itemView.findViewById(R.id.imgGallery);
    }
}
