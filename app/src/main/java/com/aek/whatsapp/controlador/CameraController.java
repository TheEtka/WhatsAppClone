package com.aek.whatsapp.controlador;

import android.widget.ImageButton;

public class CameraController {

    public static void habilitarButtonLayoutCamera(ImageButton button, boolean habilitar) {
        if (button != null)
        {
            button.setEnabled(habilitar);
            button.setFocusable(habilitar);
            button.setClickable(habilitar);
        }
    }
}
