package com.example.delestage;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
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

public class InfoSonabelActivity extends BaseActivity {

    private RecyclerView recyclerView;
    private InfoAdapter adapter;
    private List<InfoOfficielle> infoList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_sonabel);

        recyclerView = findViewById(R.id.recyclerInfos);
        infoList = new ArrayList<>();
        adapter = new InfoAdapter(infoList);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);

        // Navigation
        BottomNavigationView bottomNav = findViewById(R.id.nav_bar_bottom);
        bottomNav.setSelectedItemId(R.id.nav_info);
        // ... ajoute ton listener de navigation ici

        chargerInfosFirebase();
        setupNavigation(bottomNav, R.id.nav_info);
    }

    private void chargerInfosFirebase() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference("infos_officielles");
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                infoList.clear();
                for (DataSnapshot ds : snapshot.getChildren()) {
                    InfoOfficielle info = ds.getValue(InfoOfficielle.class);
                    if (info != null) infoList.add(0, info);
                }
                adapter.notifyDataSetChanged();
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }
}