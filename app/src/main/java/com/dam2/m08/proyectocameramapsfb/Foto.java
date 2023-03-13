package com.dam2.m08.proyectocameramapsfb;

import android.graphics.Bitmap;
import android.widget.ImageView;

import com.google.android.gms.maps.model.LatLng;

public class Foto {
    private ImageView fotoOriginal;
    private ImageView fotoMiniatura;
    private LatLng ubicacion;

    public Foto(ImageView fotoOriginal, ImageView fotoMiniatura, LatLng ubicacion) {
        this.fotoOriginal = fotoOriginal;
        this.fotoMiniatura = fotoMiniatura;
        this.ubicacion = ubicacion;
    }

    public ImageView getFotoOriginal() {
        return fotoOriginal;
    }

    public ImageView getFotoMiniatura() {
        return fotoMiniatura;
    }

    public LatLng getUbicacion() {
        return ubicacion;
    }

    @Override
    public String toString() {
        return "Foto{" +
                "fotoOriginal=" + fotoOriginal +
                ", fotoMiniatura=" + fotoMiniatura +
                ", ubicacion=" + ubicacion +
                '}';
    }
}
