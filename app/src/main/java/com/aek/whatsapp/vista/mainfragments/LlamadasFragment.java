package com.aek.whatsapp.vista.mainfragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.aek.whatsapp.R;
import com.aek.whatsapp.utils.TxtUtils;

import java.util.ArrayList;

public class LlamadasFragment extends Fragment {

    private TextView txtWSLlamadas;

    public LlamadasFragment() {

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_llamadas, container, false);
        init(view);
        setIconWSTxtInfo();
        return view;
    }

    private void setIconWSTxtInfo() {
        ArrayList<String> listChars = new ArrayList<>();
        listChars.add("$");

        ArrayList<Integer> listImages = new ArrayList<>();
        listImages.add(R.drawable.ic_call_gray);

        TxtUtils.setIconInTxtView(txtWSLlamadas, getString(R.string.txt_info_llamadas),
                listChars, listImages, requireContext());
    }

    private void init(View view) {
        this.txtWSLlamadas = view.findViewById(R.id.txtWSLlamadas);
    }
}