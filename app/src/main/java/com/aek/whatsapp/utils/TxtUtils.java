package com.aek.whatsapp.utils;

import android.content.Context;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.style.ForegroundColorSpan;
import android.text.style.ImageSpan;
import android.text.style.RelativeSizeSpan;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class TxtUtils {

    public static void setTitleSubtitleButton(String titulo, String subtitulo, Button button) {
        String txtFinal = titulo + subtitulo;
        SpannableString spannableString = SpannableString.valueOf(txtFinal);
        spannableString.setSpan(new ForegroundColorSpan(Color.GRAY),
                titulo.length(), txtFinal.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        spannableString.setSpan(new RelativeSizeSpan(0.8f), titulo.length(),
                txtFinal.length(), Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
        button.setText(spannableString);
    }

    public static void setIconInTxtView(TextView txtView, String texto,
                                        ArrayList<String> listChars,
                                        ArrayList<Integer> listImagenes, Context context) {

        if (listChars.size() == listImagenes.size()) {

            Spannable spannable = Spannable.Factory.getInstance().newSpannable(texto);

            for (int i=0; i < listChars.size(); i++) {
                int index = texto.indexOf(listChars.get(i));
                spannable.setSpan(new ImageSpan(context, listImagenes.get(i)), index, index + 1,
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            txtView.setText(spannable);

        } else {
            Toast.makeText(context, "Debe pasar el mismo nr de chars y de imagenes", Toast.LENGTH_SHORT).show();
        }
    }
}
