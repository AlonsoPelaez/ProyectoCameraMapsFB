package com.dam2.m08.proyectocameramapsfb;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.squareup.picasso.Picasso;


public class FotoCompleta extends AppCompatActivity {
    private GaleriaAdapter galeriaAdapter;
    private ImageView fotocompleta;
    private final String TAG="GOOGLE_MAPS_GALERIA_FOTOCOMPLETA";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fotocompleta);



        fotocompleta = findViewById(R.id.iv_fotocompleta);
        fotocompleta.setRotation(-90);

        Intent intent = getIntent();
        String uri= intent.getExtras().getString("imagen");

        Picasso.get().load(uri).resize(720,640).centerCrop().into(fotocompleta);

        BottomNavigationView bottomNavigationView = findViewById(R.id.menu_horizontal);

        bottomNavigationView.setSelectedItemId(R.id.galeria);

        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch (item.getItemId()) {

                    case R.id.maps:
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.camera:
                        startActivity(new Intent(getApplicationContext(), Camera.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.galeria:
                        startActivity(new Intent(getApplicationContext(), Galeria.class));
                        finish();
                        overridePendingTransition(0, 0);
                        return true;
                    case R.id.configuracion:
                        startActivity(new Intent(getApplicationContext(), Configuracion.class));
                        overridePendingTransition(0, 0);
                        return true;
                    default:
                        return false;
                }
            }
        });

    }
}

