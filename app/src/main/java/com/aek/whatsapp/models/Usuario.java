package com.aek.whatsapp.models;

public class Usuario {

    private String uid, nombre, correo, imgUrl, estado;
    private long timestampRegistro;

    public Usuario() {
    }

    public Usuario(String uid, String nombre, String correo, long timestampRegistro) {
        this.uid = uid;
        this.nombre = nombre;
        this.correo = correo;
        this.imgUrl = "";
        this.estado = "";
        this.timestampRegistro = timestampRegistro;
    }

    public String getUid() {
        return uid;
    }

    public String getNombre() {
        return nombre;
    }

    public String getCorreo() {
        return correo;
    }

    public String getImgUrl() {
        return imgUrl;
    }

    public String getEstado() {
        return estado;
    }

    public long getTimestampRegistro() {
        return timestampRegistro;
    }

    @Override
    public String toString() {
        return "Usuario{" +
                "uid='" + uid + '\'' +
                ", nombre='" + nombre + '\'' +
                ", timestampRegistro=" + timestampRegistro +
                '}';
    }
}
