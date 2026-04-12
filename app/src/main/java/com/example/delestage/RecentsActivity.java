package com.example.delestage;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RecentsActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private SignalementAdapter adapter;
    private List<Signalement> listSignalements;
    private DatabaseReference mDatabase;
    private ValueEventListener currentListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recents);

        // 1. Initialisation UI
        recyclerView = findViewById(R.id.recyclerRecents);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        listSignalements = new ArrayList<>();
        adapter = new SignalementAdapter(listSignalements);
        recyclerView.setAdapter(adapter);

        // 2. Initialisation Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("signalements");

        // 3. Barre de navigation du bas
        BottomNavigationView bottomNav = findViewById(R.id.nav_bar_bottom);
        setupNavigation(bottomNav, R.id.nav_recents);

        // 4. Configuration des onglets AVANT de charger les données
        setupTabs();

        // 5. Charger toutes les données par défaut
        chargerDonnees("TOUS");
    }

    private void setupTabs() {
        TabLayout tabs = findViewById(R.id.tabFilter);

        // AJOUT CRUCIAL : Création des onglets si le XML est vide
        if (tabs.getTabCount() == 0) {
            tabs.addTab(tabs.newTab().setText("Tous"));
            tabs.addTab(tabs.newTab().setText("Coupures"));
            tabs.addTab(tabs.newTab().setText("Retours"));
        }

        tabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                // Utilisation de equals() sur le texte pour plus de sécurité
                String tabText = tab.getText().toString();
                if (tabText.equals("Tous")) {
                    chargerDonnees("TOUS");
                } else if (tabText.equals("Coupures")) {
                    chargerDonnees("COUPURE");
                } else if (tabText.equals("Retours")) {
                    chargerDonnees("RETOUR");
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
    }

    private void chargerDonnees(String filtre) {
        // Supprimer l'écouteur précédent sur mDatabase OU sur la Query précédente
        if (currentListener != null) {
            mDatabase.removeEventListener(currentListener);
        }

        Query query;
        if (filtre.equals("TOUS")) {
            query = mDatabase;
        } else {
            // Assure-toi que le champ "type" existe dans Firebase (COUPURE ou RETOUR)
            query = mDatabase.orderByChild("type").equalTo(filtre);
        }

        currentListener = new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                listSignalements.clear();
                for (DataSnapshot data : snapshot.getChildren()) {
                    Signalement s = data.getValue(Signalement.class);
                    if (s != null) {
                        // On ajoute au début (index 0) pour avoir le plus récent en haut
                        listSignalements.add(0, s);
                    }
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                // Log l'erreur si besoin
            }
        };

        query.addValueEventListener(currentListener);
    }
}