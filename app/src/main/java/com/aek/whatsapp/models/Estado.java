package com.aek.whatsapp.models;

public class Estado {

    private String uid, imgUrl, estadoId;
    private long timestamp;

    public Estado() {
    }

    public Estado(String uid, String imgUrl, String estadoId, long timestamp) {
        this.uid = uid;
        this.imgUrl = imgUrl;
        this.estadoId = estadoId;
        this.timestamp = timestamp;
    }

    public String getUid() {
        return uid;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getEstadoId() {
        return estadoId;
    }

    public long getTimestamp() {
        return timestamp;
    }

    @Override
    public String toString() {
        return "Estado{" +
                "uid='" + uid + '\'' +
                ", imgUrl='" + imgUrl + '\'' +
                ", estadoId='" + estadoId + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
