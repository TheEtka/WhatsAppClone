package com.aek.whatsapp.models;

import com.bumptech.glide.load.Transformation;

public class Filtro {

    private Transformation transformation;
    private String nombre;

    public Filtro(Transformation transformation, String nombre) {
        this.transformation = transformation;
        this.nombre = nombre;
    }

    public Transformation getTransformation() {
        return transformation;
    }

    public String getNombre() {
        return nombre;
    }
}
