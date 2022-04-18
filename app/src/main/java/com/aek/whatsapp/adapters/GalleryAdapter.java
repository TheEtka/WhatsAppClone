package com.aek.whatsapp.adapters;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.utils.CropImage;
import com.aek.whatsapp.viewholders.ImageGalleryViewHolder;
import com.aek.whatsapp.vista.mainfragments.CameraFragment;
import com.aek.whatsapp.vista.mainfragments.chat.ChatActivity;
import com.aek.whatsapp.vista.mainfragments.chat.PhotoMessageActivity;
import com.aek.whatsapp.vista.mainfragments.estados.add.AddNewEstadoPhotoActivity;
import com.bumptech.glide.Glide;

import java.io.File;
import java.util.List;

public class GalleryAdapter extends RecyclerView.Adapter<ImageGalleryViewHolder> {

    private Activity activity;
    private List<String> listaImagenes;
    private int typeLayout;
    private String typeAction;
    private boolean isItemClickable;
    private String uidReceiver;
    private CameraFragment cameraFragment;

    public GalleryAdapter(Activity activity, List<String> listaImagenes, int typeLayout,
                          String typeAction, boolean isItemClickable, String uidReceiver) {
        this.activity = activity;
        this.listaImagenes = listaImagenes;
        this.typeLayout = typeLayout;
        this.typeAction = typeAction;
        this.isItemClickable = isItemClickable;
        this.uidReceiver = uidReceiver;
    }

    @NonNull
    @Override
    public ImageGalleryViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(typeLayout, parent, false);
        return new ImageGalleryViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ImageGalleryViewHolder holder, int position) {

        String imgPath = listaImagenes.get(holder.getAdapterPosition());

        if (!activity.isFinishing())
        {
            Glide.with(activity).load(imgPath).into(holder.img);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {
                if (isItemClickable)
                {
                    switch (typeAction)
                    {
                        case CameraFragment.ACTION_MESSAGE:

                            Intent intent = new Intent(activity, PhotoMessageActivity.class);
                            intent.putExtra(PhotoMessageActivity.IMG_URI, imgPath);
                            intent.putExtra(ChatActivity.UID, uidReceiver);
                            activity.startActivity(intent);

                            break;
                        case CameraFragment.ACTION_ESTADO:
                            activity.startActivity(new Intent(activity, AddNewEstadoPhotoActivity.class)
                                    .putExtra("IMG_URI", imgPath));
                            break;
                        case CameraFragment.ACTION_ACTUALIZAR_FOTO:
                            if (cameraFragment != null)
                            {
                                CropImage.cropMiniaturaFromFragment(Uri.fromFile(new File(imgPath)),
                                        activity, cameraFragment);
                            }
                            break;
                    }
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return listaImagenes.size();
    }

    public void setItemClickable(boolean itemClickable) {
        isItemClickable = itemClickable;
    }

    public void setCameraFragment(CameraFragment cameraFragment) {
        this.cameraFragment = cameraFragment;
    }
}
