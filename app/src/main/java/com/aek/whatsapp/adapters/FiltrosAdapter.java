package com.aek.whatsapp.adapters;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.models.Filtro;
import com.aek.whatsapp.utils.AnimUtils;
import com.aek.whatsapp.utils.OnFilterListener;
import com.aek.whatsapp.viewholders.FiltroViewHolder;
import com.aek.whatsapp.vista.mainfragments.estados.add.AddNewEstadoPhotoActivity;
import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;

import java.util.ArrayList;

public class FiltrosAdapter extends RecyclerView.Adapter<FiltroViewHolder> {

    private Activity activity;
    private ArrayList<Filtro> listaFiltros;
    private String imgUriString;
    private OnFilterListener onFilterListener;
    private FiltroViewHolder lastHolderClicked;
    private int positionFiltro = 0;

    public FiltrosAdapter(Activity activity, String imgUriString, OnFilterListener onFilterListener) {
        this.activity = activity;
        this.listaFiltros = AddNewEstadoPhotoActivity.getListaFiltros();
        this.imgUriString = imgUriString;
        this.onFilterListener = onFilterListener;
    }

    @NonNull
    @Override
    public FiltroViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.card_filtro, parent, false);
        return new FiltroViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull FiltroViewHolder holder, int position) {

        Filtro filtro = listaFiltros.get(holder.getAdapterPosition());
        holder.txtFilterName.setText(filtro.getNombre());

        if (!activity.isFinishing()) {

            if (filtro.getTransformation() == null) {
                Glide.with(activity).load(imgUriString)
                        .override(100,150).into(holder.imgFiltro);
            } else {
                Glide.with(activity).load(imgUriString)
                        .apply(RequestOptions.bitmapTransform(filtro.getTransformation()))
                        .override(100,150).into(holder.imgFiltro);
            }
        }

        if (positionFiltro == holder.getAdapterPosition()) {
            AnimUtils.scaleView(holder.imgFilterUsed, 150, 1f);
            lastHolderClicked = holder;
        } else {
            AnimUtils.scaleView(holder.imgFilterUsed, 150, 0f);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (lastHolderClicked != null) {
                    AnimUtils.scaleView(lastHolderClicked.imgFilterUsed, 150, 0f);
                }

                AnimUtils.scaleView(holder.imgFilterUsed, 150, 1f);

                positionFiltro = holder.getAdapterPosition();

                lastHolderClicked = holder;

                onFilterListener.onFilterClicked(filtro.getTransformation());

            }
        });

    }

    @Override
    public int getItemCount() {
        return listaFiltros.size();
    }

}
