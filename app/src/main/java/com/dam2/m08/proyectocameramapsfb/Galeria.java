package com.dam2.m08.proyectocameramapsfb;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;
import java.util.zip.Inflater;

public class Galeria extends AppCompatActivity {
    List<String> mUris= new ArrayList<>();
    FirebaseFirestore db = FirebaseFirestore.getInstance();
    private final String TAG="GOOGLE_MAPS_CAMERA";
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.galeria);
        cargaUris();
        GridView gridView = findViewById(R.id.gv_fotos);

        GaleriaAdapter adapter = new GaleriaAdapter(this,mUris);

        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), FotoCompleta.class);
                intent.putExtra("imagen",position);
                startActivity(intent);
            }
        });
    }

    private void cargaUris() {

        SharedPreferences prefer = getSharedPreferences(getString(R.string.prefer_file), Context.MODE_PRIVATE);
        String email = prefer.getString("email", null);
        db.collection("Usuarios").document(email).collection("misImagenes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                if (!value.isEmpty()){
                    for (DocumentSnapshot document: value) {
                        mUris.add(document.getString("uri_foto"));
                    }
                }
                else{
                    Log.d(TAG, "onEvent: error" + error.getMessage());
                }
            }
        });
    }
}
