package com.aek.whatsapp.models;

import com.aek.whatsapp.controlador.FbUser;

import java.util.HashMap;

public class ContactoEstado {

    private String uid;
    private long timestampLastEstado;
    private HashMap<String, Object> espectadoresNotification;

    public ContactoEstado() {
    }

    public ContactoEstado(long timestampLastEstado) {
        this.uid = FbUser.getCurrentUserId();
        this.timestampLastEstado = timestampLastEstado;
        this.espectadoresNotification = new HashMap<>();
    }

    public HashMap<String, Object> getEspectadoresNotification() {
        return espectadoresNotification;
    }

    public String getUid() {
        return uid;
    }

    public long getTimestampLastEstado() {
        return timestampLastEstado;
    }

}
