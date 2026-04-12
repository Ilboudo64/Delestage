package com.example.delestage;

import android.os.Bundle;
import android.provider.Settings;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.util.ArrayList;
import java.util.List;

public class HistoryActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private HistoryAdapter adapter;
    private List<Signalement> userList;
    private TextView tvCoupures, tvRetours; // Déclarés ici pour éviter les recherches inutiles
    private String androidId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);

        // Navigation
        BottomNavigationView bottomNav = findViewById(R.id.nav_bar_bottom);
        setupNavigation(bottomNav, R.id.nav_historique);
        bottomNav.setItemIconTintList(null); // Cela désactive le filtre de couleur automatique pour voir si les icônes originales reviennent

        // ID unique du téléphone
        androidId = Settings.Secure.getString(getContentResolver(), Settings.Secure.ANDROID_ID);

        // Initialisation des vues
        tvCoupures = findViewById(R.id.txtCountCoupures);
        tvRetours = findViewById(R.id.txtCountRetours);
        recyclerView = findViewById(R.id.recyclerHistory);

        userList = new ArrayList<>();
        // IMPORTANT : On passe "this" (le context) à l'adapter pour l'AlertDialog
        adapter = new HistoryAdapter(userList, this);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        chargerHistorique();
    }

    private void chargerHistorique() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("signalements");

        // NOTE : Pour l'instant on récupère TOUT pour tester l'affichage,
        // car le champ "userId" n'est peut-être pas encore dans ta base.
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                int cCount = 0;
                int rCount = 0;

                for (DataSnapshot ds : snapshot.getChildren()) {
                    Signalement s = ds.getValue(Signalement.class);
                    if (s != null) {
                        // On force l'ID de Firebase dans l'objet pour pouvoir supprimer après
                        s.setId(ds.getKey());

                        userList.add(0, s); // Le plus récent en haut

                        if ("COUPURE".equalsIgnoreCase(s.getType())) {
                            cCount++;
                        } else if ("RETOUR".equalsIgnoreCase(s.getType())) {
                            rCount++;
                        }
                    }
                }

                // Mise à jour des compteurs du Header
                if (tvCoupures != null) tvCoupures.setText(String.valueOf(cCount));
                if (tvRetours != null) tvRetours.setText(String.valueOf(rCount));

                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}