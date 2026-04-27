package com.example.delestage;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.material.navigation.NavigationBarView;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public abstract class BaseActivity extends AppCompatActivity {

    protected void setupNavigation(BottomNavigationView bottomNav, int currentId) {

        // 1. Sélectionner visuellement l'icône de la page actuelle
        bottomNav.setSelectedItemId(currentId);

        // 2. Écouter les clics sur la barre
        bottomNav.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            // Si on clique sur l'icône de la page où on est déjà, on ne fait rien
            if (id == currentId) {
                return true;
            }

            Intent intent = null;

            if (id == R.id.nav_accueil) {
                intent = new Intent(this, DashbordActivity.class);
            } else if (id == R.id.nav_recents) {
                intent = new Intent(this, RecentsActivity.class);
            } else if (id == R.id.nav_historique) {
                // MODIFICATION : On ouvre TA page de statistiques !
                intent = new Intent(this, StatisticsActivity.class);
            } else if (id == R.id.nav_info) {
                intent = new Intent(this, InfoSonabelActivity.class);
            }

            if (intent != null) {
                startActivity(intent);
                // Animation fluide pour éviter le clignotement
                overridePendingTransition(0, 0);
                finish();
                return true;
            }

            return false;
        });
    }
}