package com.dam2.m08.proyectocameramapsfb;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;


import java.util.List;

public class GaleriaAdapter extends BaseAdapter {

    private List<String> mUrls;
    private LayoutInflater inflater;

    public GaleriaAdapter(Context mContext, List<String> mUrls) {
        this.mUrls = mUrls;
        this.inflater = LayoutInflater.from(mContext);
    }

    @Override
    public int getCount() {
        return mUrls.size();
    }

    @Override
    public Object getItem(int position) {
        return mUrls.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = convertView;

        if (view == null) {
            view = inflater.inflate(R.layout.galeria,parent,false);
        }

        ImageView imageView = view.findViewById(R.id.gv_fotos);
        Log.d("asda", "getView: "+imageView);

        String uri = mUrls.get(position);
        Log.d("asda", "getView: "+uri);

        Picasso.get().load(uri).into(imageView);
        Log.d("asda", "getView: "+view);

        return view;
    }
}
