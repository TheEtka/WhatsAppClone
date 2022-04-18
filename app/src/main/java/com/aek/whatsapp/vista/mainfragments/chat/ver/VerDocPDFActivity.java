package com.aek.whatsapp.vista.mainfragments.chat.ver;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import com.aek.whatsapp.R;
import com.aek.whatsapp.databinding.ActivityVerDocPdfBinding;
import com.github.barteksc.pdfviewer.PDFView;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;

public class VerDocPDFActivity extends AppCompatActivity {

    private ActivityVerDocPdfBinding binding;

    public static final String PDF_URL = "PDF_URL";
    private String pdfUrl;
    private PDFView pdfView;
    private final long ONE_MEGABYTE = 1024 * 1024;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityVerDocPdfBinding.inflate(getLayoutInflater());
        View view = binding.getRoot();
        setContentView(view);

        getExtras();
        init();
        descargarDocumentoPDF();
    }

    private void descargarDocumentoPDF() {
        FirebaseStorage.getInstance().getReference().getStorage()
                .getReferenceFromUrl(pdfUrl)
                .getBytes(ONE_MEGABYTE)
                .addOnSuccessListener(new OnSuccessListener<byte[]>() {
                    @Override
                    public void onSuccess(byte[] bytes) {
                        pdfView.fromBytes(bytes).load();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(VerDocPDFActivity.this, "Error al descargar el documento PDF.", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void init() {
        this.pdfView = binding.pdfView;
    }

    private void getExtras() {
        try {
            this.pdfUrl = getIntent().getExtras().getString(VerDocPDFActivity.PDF_URL, null);
        } catch (NullPointerException e) {
            e.getMessage();
        }
    }
}