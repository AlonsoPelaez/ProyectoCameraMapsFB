package com.dam2.m08.proyectocameramapsfb;

import android.graphics.Bitmap;

public class Foto {
    private Bitmap bitmapOriginal;
    private Bitmap bitmapMiniatura;
    private String ubicacion;


    public Foto(Bitmap bitmapOriginal, Bitmap bitmapMiniatura, String ubicacion) {
        this.bitmapOriginal = bitmapOriginal;
        this.bitmapMiniatura = bitmapMiniatura;
        this.ubicacion = ubicacion;
    }

    public Foto(Bitmap bitmapOriginal, Bitmap bitmapMiniatura) {
        this.bitmapOriginal = bitmapOriginal;
        this.bitmapMiniatura = bitmapMiniatura;
        this.ubicacion = "";
    }

    public Bitmap getBitmapOriginal() {
        return bitmapOriginal;
    }

    public void setBitmapOriginal(Bitmap bitmapOriginal) {
        this.bitmapOriginal = bitmapOriginal;
    }

    public Bitmap getBitmapMiniatura() {
        return bitmapMiniatura;
    }

    public void setBitmapMiniatura(Bitmap bitmapMiniatura) {
        this.bitmapMiniatura = bitmapMiniatura;
    }

    public String getUbicacion() {
        return ubicacion;
    }

    public void setUbicacion(String ubicacion) {
        this.ubicacion = ubicacion;
    }



    @Override
    public String toString() {
        return "Foto{" +
                "bitmapOriginal='" + bitmapOriginal + '\'' +
                ", bitmapMiniatura='" + bitmapMiniatura + '\'' +
                ", ubicacion='" + ubicacion + '\'' +
                '}';
    }
}
