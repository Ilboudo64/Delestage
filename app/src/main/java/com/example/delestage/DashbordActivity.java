package com.example.delestage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DashbordActivity extends BaseActivity {

    private DatabaseReference mDatabase;
    private TextView tvCountCoupures, tvCountRetours, tvTotalSignalements;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dashbord);

        // 1. Initialisation Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference();
        mDatabase.child("signalements").keepSynced(true);

        // 2. Initialisation des Vues
        tvCountCoupures = findViewById(R.id.txtCountCoupures);
        tvCountRetours = findViewById(R.id.txtCountRetours);
        tvTotalSignalements = findViewById(R.id.txtCountTotalSignalement); // Vérifie bien cet ID dans ton XML

        // 3. Bouton Profil
        CardView btnProfile = findViewById(R.id.btnProfile);
        btnProfile.setOnClickListener(v -> {
            Intent intent = new Intent(DashbordActivity.this, ProfileActivity.class);
            startActivity(intent);
        });

        // 4. Charger le cache pour un affichage immédiat au démarrage
        chargerCacheLocal();

        // 5. Navigation
        BottomNavigationView bottomNav = findViewById(R.id.nav_bar_bottom);
        if (bottomNav != null) {
            bottomNav.setItemIconTintList(null);
            setupNavigation(bottomNav, R.id.nav_accueil);
        }

        // 6. Lancer l'écoute en temps réel (C'est ici que la magie opère)
        ecouterSignalements();
    }

    private void chargerCacheLocal() {
        SharedPreferences prefs = getSharedPreferences("DashboardCache", MODE_PRIVATE);
        int savedCoupures = prefs.getInt("lastCoupures", 0);
        int savedRetours = prefs.getInt("lastRetours", 0);
        int savedTotal = prefs.getInt("lastTotal", 0);

        if (tvCountCoupures != null) tvCountCoupures.setText(String.valueOf(savedCoupures));
        if (tvCountRetours != null) tvCountRetours.setText(String.valueOf(savedRetours));
        if (tvTotalSignalements != null) tvTotalSignalements.setText(String.valueOf(savedTotal));
    }

    private void ecouterSignalements() {
        // Utilise une référence directe et propre
        DatabaseReference maReferenceRef = FirebaseDatabase.getInstance().getReference("signalements");

        maReferenceRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                int total = (int) dataSnapshot.getChildrenCount();
                int coupures = 0;
                int retours = 0;

                for (DataSnapshot snap : dataSnapshot.getChildren()) {
                    String type = snap.child("type").getValue(String.class);
                    if ("COUPURE".equalsIgnoreCase(type)) coupures++;
                    else if ("RETOUR".equalsIgnoreCase(type)) retours++;
                }

                // Mise à jour visuelle immédiate
                tvCountCoupures.setText(String.valueOf(coupures));
                tvCountRetours.setText(String.valueOf(retours));
                tvTotalSignalements.setText(String.valueOf(total));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log l'erreur pour savoir pourquoi ça bloque
                android.util.Log.e("FIREBASE_ERROR", error.getMessage());
            }
        });
    }
    private void mettreAJourUI(int coupures, int retours, int total) {
        if (tvCountCoupures != null) tvCountCoupures.setText(String.valueOf(coupures));
        if (tvCountRetours != null) tvCountRetours.setText(String.valueOf(retours));
        if (tvTotalSignalements != null) tvTotalSignalements.setText(String.valueOf(total));
    }

    private void sauvegarderEnCache(int coupures, int retours, int total) {
        SharedPreferences prefs = getSharedPreferences("DashboardCache", MODE_PRIVATE);
        prefs.edit()
                .putInt("lastCoupures", coupures)
                .putInt("lastRetours", retours)
                .putInt("lastTotal", total)
                .apply();
    }

    // Méthodes de clic pour les boutons de signalement (déclarées dans le XML : android:onClick="clicCoupure")
    public void clicCoupure(View view) {
        Intent intent = new Intent(this, SignalementActivity.class);
        intent.putExtra("TYPE", "COUPURE");
        startActivity(intent);
    }

    public void clicRetour(View view) {
        Intent intent = new Intent(this, SignalementActivity.class);
        intent.putExtra("TYPE", "RETOUR");
        startActivity(intent);
    }
}