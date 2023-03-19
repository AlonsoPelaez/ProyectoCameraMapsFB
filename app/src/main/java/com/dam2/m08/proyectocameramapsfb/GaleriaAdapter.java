package com.dam2.m08.proyectocameramapsfb;

import android.content.Context;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridLayout;
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
    private final String TAG ="GOOGLE_MAPS_ADAPTER_GALERIA";

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

        ImageView imageView = new ImageView(inflater.getContext());
        imageView.setLayoutParams(new GridView.LayoutParams(340, 350));
        imageView.setRotation(-90);

        String uri = mUrls.get(position);
        Picasso.get().load(uri).resize(340,350).centerCrop().into(imageView);

        return imageView;
    }
}
