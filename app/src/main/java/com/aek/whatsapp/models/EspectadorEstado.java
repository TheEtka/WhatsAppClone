package com.aek.whatsapp.models;

public class EspectadorEstado {

    private String uid;
    private long timestamp;

    public EspectadorEstado() {
    }

    public EspectadorEstado(String uid, long timestamp) {
        this.uid = uid;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public long getTimestamp() {
        return timestamp;
    }
}
