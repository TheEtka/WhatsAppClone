package com.aek.whatsapp.vista.mainfragments.estados.canvas;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.util.AttributeSet;
import android.view.MotionEvent;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatImageView;

import com.aek.whatsapp.controlador.EstadosController;
import com.aek.whatsapp.utils.PixelsDPUtils;

public class CanvasImagenEstado extends AppCompatImageView {

    private Path path;
    private Paint paint;
    private TextPaint textPaint;
    private Canvas canvas;
    private Bitmap bitmap;
    private boolean puedoPintar = false;

    public CanvasImagenEstado(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
        configTextPaint();
        configPaint();
    }

    private void configPaint() {
        this.paint.setColor(Color.WHITE);
        this.paint.setAntiAlias(true);
        this.paint.setStrokeWidth(20f);
        this.paint.setStyle(Paint.Style.STROKE);
        this.paint.setStrokeJoin(Paint.Join.ROUND);
        this.paint.setStrokeCap(Paint.Cap.ROUND);
    }

    private void configTextPaint() {
        this.textPaint.setColor(Color.WHITE);
        this.textPaint.setTextSize(PixelsDPUtils.convertPixelsToDp(32, getContext()));
        this.textPaint.setStyle(Paint.Style.FILL);
        this.textPaint.setShadowLayer(1.5f,0f,1.5f,Color.BLACK);
    }

    private void init() {
        this.textPaint = new TextPaint();
        this.paint = new Paint();
        this.path = new Path();
    }

    public void setRandomColorPaint() {
        this.paint.setColor(EstadosController.getRandomColor());
    }

    public void enablePintar() {
        if (puedoPintar) {
            puedoPintar = false;
        } else {
            puedoPintar = true;
        }
    }

    public void disablePintar() {
        this.puedoPintar = false;
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

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawBitmap(bitmap,0,0, paint);
        if (puedoPintar) {
            canvas.drawPath(path,paint);
        }
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);

        bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        canvas = new Canvas(bitmap);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (puedoPintar) {

            float touchX = event.getX();
            float touchY = event.getY();

            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    path.moveTo(touchX, touchY);
                    break;

                case MotionEvent.ACTION_MOVE:
                    path.lineTo(touchX, touchY);
                    break;

                case MotionEvent.ACTION_UP:
                    path.lineTo(touchX, touchY);
                    canvas.drawPath(path, paint);
                    path.reset();
                    break;
                default:
                    return false;
            }
            invalidate();
            return true;
        } else {
            return false;
        }

    }
}
