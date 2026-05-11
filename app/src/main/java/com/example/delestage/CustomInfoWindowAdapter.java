package com.example.delestage;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

public class CustomInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private final View mWindow;

    // Constructeur : on prépare le design une seule fois au début
    public CustomInfoWindowAdapter(Context context) {
        mWindow = LayoutInflater.from(context).inflate(R.layout.custom_info_window, null);
    }

    // Cette méthode remplit les textes et les icônes
    private void renderWindowText(Marker marker, View view) {
        TextView tvTitle = view.findViewById(R.id.tv_info_title);
        TextView tvSnippet = view.findViewById(R.id.tv_info_snippet);
        ImageView ivIcon = view.findViewById(R.id.iv_info_icon);

        // On récupère les infos du marqueur (ex: "palogo")
        String title = marker.getTitle();
        String snippet = marker.getSnippet();

        if (title != null) tvTitle.setText(title);
        if (snippet != null) tvSnippet.setText(snippet);

        // On change l'icône selon le statut
        if (snippet != null && snippet.contains("COUPURE")) {
            ivIcon.setImageResource(R.drawable.ic_eclair_rouge);
        } else {
            ivIcon.setImageResource(R.drawable.ic_eclair_vert);
        }
    }

    @Override
    public View getInfoWindow(Marker marker) {
        // Dessine toute la fenêtre personnalisée
        renderWindowText(marker, mWindow);
        return mWindow;
    }

    @Override
    public View getInfoContents(Marker marker) {
        return null;
    }
}
