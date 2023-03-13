package com.dam2.m08.proyectocameramapsfb;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.squareup.picasso.Picasso;


public class FotoCompleta extends AppCompatActivity {
    private GaleriaAdapter galeriaAdapter;
    private ImageView fotocompleta;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotocompleta);
        ActionBar actionBar = getSupportActionBar();
        actionBar.setTitle(" ");

        fotocompleta = findViewById(R.id.iv_fotocompleta);

        Intent intent = getIntent();

        int posicion = intent.getExtras().getInt("imagen");

        String uri = (String) galeriaAdapter.getItem(posicion);

        Picasso.get().load(uri).into(fotocompleta);

    }
}

