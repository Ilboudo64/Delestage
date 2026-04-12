package com.example.delestage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.bottomnavigation.BottomNavigationView;

public class ProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // --- 1. NAVIGATION FONCTIONNELLE ---
        BottomNavigationView navBar = findViewById(R.id.nav_bar_bottom);

        // On marque "Infos" comme l'onglet actif
        navBar.setSelectedItemId(R.id.nav_info);

        navBar.setOnItemSelectedListener(item -> {
            int id = item.getItemId();

            if (id == R.id.nav_accueil) {
                startActivity(new Intent(this, DashbordActivity.class));
                overridePendingTransition(0, 0); // Transition fluide
                finish(); // On ferme le profil pour ne pas accumuler les pages
                return true;
            }
            else if (id == R.id.nav_recents) {
                startActivity(new Intent(this, RecentsActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (id == R.id.nav_historique) {
                startActivity(new Intent(this, HistoryActivity.class));
                overridePendingTransition(0, 0);
                finish();
                return true;
            }
            else if (id == R.id.nav_info) {
                return true; // Déjà sur cette activité
            }
            return false;
        });

        // --- 2. CONFIGURATION DES PARAMÈTRES (Remplissage des infos) ---
        setupParameterRow(R.id.itemNotifications, "Notifications", "Gérer mes préférences", R.drawable.ic_bell);
        setupParameterRow(R.id.itemZone, "Ma zone", "Ouagadougou - Secteur 15", R.drawable.ic_location_circle);
        setupParameterRow(R.id.itemPrivacy, "Confidentialité", "Paramètres de sécurité", R.drawable.ic_edit_circle);

        // --- 3. BOUTON DÉCONNEXION ---
        Button btnLogout = findViewById(R.id.btnLogout);
        if (btnLogout != null) {
            btnLogout.setOnClickListener(v -> {
                Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                finish();
            });
        }
    }

    // Méthode utilitaire pour éviter la répétition de code
    private void setupParameterRow(int viewId, String title, String subTitle, int iconRes) {
        View row = findViewById(viewId);
        if (row != null) {
            ((TextView) row.findViewById(R.id.txtTitle)).setText(title);
            ((TextView) row.findViewById(R.id.txtSub)).setText(subTitle);
            ((ImageView) row.findViewById(R.id.imgIcon)).setImageResource(iconRes);
        }
    }

    // Optionnel : Gestion du bouton "Retour" du téléphone
    @Override
    public void onBackPressed() {
        super.onBackPressed();
        startActivity(new Intent(this, DashbordActivity.class));
        finish();
    }
}