package com.dam2.m08.proyectocameramapsfb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class Galeria extends AppCompatActivity {
    public List<String> mUris;
    private FirebaseFirestore db = FirebaseFirestore.getInstance();
    private GridView gv_fotos;
    private GaleriaAdapter galeriaAdapter;
    private final String TAG="GOOGLE_MAPS_GALERIA";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galeria);

        mUris = new ArrayList<>();
        gv_fotos = findViewById(R.id.gv_fotos);
        cargaUris();

        gv_fotos.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FotoCompleta.class);
                intent.putExtra("imagen",mUris.get(position));
                startActivity(intent);
            }
        });

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

    private void cargaUris() {

        SharedPreferences prefer = getSharedPreferences(getString(R.string.prefer_file), Context.MODE_PRIVATE);
        String email = prefer.getString("email", null);

        db.collection("Usuarios").document(email).collection("misImagenes").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                for (DocumentSnapshot document: queryDocumentSnapshots) {

                    mUris.add(document.getString("uri_foto"));
                }

                if (mUris.size() > 0) {
                    galeriaAdapter = new GaleriaAdapter(getApplicationContext(), mUris);
                    gv_fotos.setAdapter(galeriaAdapter);
                }
            }
        });
    }
}
