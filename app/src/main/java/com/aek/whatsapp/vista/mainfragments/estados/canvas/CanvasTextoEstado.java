package com.aek.whatsapp.vista.mainfragments.estados.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

import com.aek.whatsapp.utils.PixelsDPUtils;

public class CanvasTextoEstado extends View {

    private Canvas canvas;
    private Bitmap bitmap;
    private TextPaint textPaint;

    public CanvasTextoEstado(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
        init();
        configTextPaint();
    }

    private void init() {
        this.textPaint = new TextPaint();
    }

    private void configTextPaint() {
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(PixelsDPUtils.convertPixelsToDp(32, getContext()));
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setShadowLayer(1.5f, 0f, 1.5f, Color.BLACK);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap, 0, 0, null);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    public void changeTypeFace(Typeface typeface) {
        this.textPaint.setTypeface(typeface);
    }

    public void escribirTextoEnCanvas(String estado) {
        StaticLayout staticLayout = new StaticLayout(estado, textPaint, getWidth(),
                Layout.Alignment.ALIGN_CENTER, 1.0f, 0, false);

        canvas.save();

        float dy = (float) (canvas.getHeight() / 2) - (float) ((staticLayout.getHeight() / 2));

        canvas.translate(0, dy);

        staticLayout.draw(canvas);
        canvas.restore();
    }

}
