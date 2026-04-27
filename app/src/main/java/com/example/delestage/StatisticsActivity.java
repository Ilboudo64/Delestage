package com.example.delestage;

import android.graphics.Color;
import android.os.Bundle;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.github.mikephil.charting.animation.Easing;
import com.github.mikephil.charting.charts.HorizontalBarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class StatisticsActivity extends BaseActivity {

    private TextView tvTotal7J, tvMoyenne7J, tvMax7J;
    private LineChart lineChart;
    private DatabaseReference mDatabase;
    private HorizontalBarChart barChartZones;
    private SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM", Locale.getDefault());

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        // 1. Initialisation des vues
        tvTotal7J = findViewById(R.id.tvTotal7J);
        tvMoyenne7J = findViewById(R.id.tvMoyenne7J);
        tvMax7J = findViewById(R.id.tvMax7J);
        lineChart = findViewById(R.id.lineChart);
        barChartZones = findViewById(R.id.barChartZones);
        setupBarChartDesign();

        // 2. Configuration Navigation
        BottomNavigationView bottomNav = findViewById(R.id.bottom_navigation);
        if (bottomNav != null) {
            setupNavigation(bottomNav, R.id.nav_historique);
        }

        // 3. Configuration initiale du graphique (Style vide)
        setupChartDesign();

        // 4. Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("signalements");
        getDataFromFirebase();
    }

    private void setupChartDesign() {
        lineChart.getDescription().setEnabled(false); // Pas de description
        lineChart.setTouchEnabled(true); // INTERACTIF
        lineChart.setDragEnabled(true);
        lineChart.setScaleEnabled(true);
        lineChart.setPinchZoom(true);
        lineChart.setDrawGridBackground(false);

        // Personnalisation de l'Axe X (le bas)
        XAxis xAxis = lineChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawGridLines(false); // Pas de grille verticale
        xAxis.setTextColor(Color.GRAY);
        xAxis.setTextSize(10f);
        xAxis.setGranularity(1f); // Un label par jour

        // Personnalisation de l'Axe Y (la gauche)
        YAxis leftAxis = lineChart.getAxisLeft();
        leftAxis.setTextColor(Color.GRAY);
        leftAxis.setTextSize(10f);
        leftAxis.setDrawGridLines(true); // Grille horizontale discrète
        leftAxis.setGridColor(Color.LTGRAY);
        leftAxis.setAxisMinimum(0f); // Commence à 0

        lineChart.getAxisRight().setEnabled(false); // Désactiver l'axe droit
        lineChart.getLegend().setEnabled(false); // Pas de légende
    }

    private void setupBarChartDesign() {
        barChartZones.getDescription().setEnabled(false);
        barChartZones.getLegend().setEnabled(false);
        barChartZones.setDrawGridBackground(false);

        XAxis xAxis = barChartZones.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM); // Sur un HorizontalBarChart, BOTTOM = côté gauche
        xAxis.setDrawGridLines(false);
        xAxis.setGranularity(1f);
        xAxis.setTextColor(Color.DKGRAY);
        xAxis.setTextSize(10f);

        // On cache les lignes de fond pour un look "Dashboard"
        barChartZones.getAxisLeft().setEnabled(false);
        barChartZones.getAxisRight().setEnabled(false);

        // On empêche le zoom pour garder le design propre
        barChartZones.setScaleEnabled(false);
    }

    private void getDataFromFirebase() {
        mDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                // Préparation des structures de données
                ArrayList<Entry> entries = new ArrayList<>();
                final ArrayList<String> datesLabel = new ArrayList<>();
                Map<String, Integer> dailyCounts = new HashMap<>();

                // Calculer les 7 derniers jours (pour créer des boîtes vides)
                Calendar cal = Calendar.getInstance();
                cal.add(Calendar.DAY_OF_YEAR, -6); // On recule de 6 jours + aujourd'hui = 7 jours

                for (int i = 0; i < 7; i++) {
                    String dateKey = dateFormat.format(cal.getTime());
                    dailyCounts.put(dateKey, 0); // Initialiser à 0
                    datesLabel.add(dateKey); // Enregistrer les labels pour l'axe X
                    cal.add(Calendar.DAY_OF_YEAR, 1);
                }

                // Parcourir tes signalements Firebase
                int totalCoupures = 0;
                for (DataSnapshot ds : snapshot.getChildren()) {

                    // Récupérer la date (supposée être un Long timestamp)
                    Long timestamp = ds.child("timestamp").getValue(Long.class);
                    if (timestamp != null) {
                        String dateCoupure = dateFormat.format(new Date(timestamp));

                        // Si cette date est dans nos 7 derniers jours, on l'incrémente
                        if (dailyCounts.containsKey(dateCoupure)) {
                            dailyCounts.put(dateCoupure, dailyCounts.get(dateCoupure) + 1);
                            totalCoupures++;
                        }
                    }
                }

                // Convertir les comptes quotidiens en Entries pour le graphique
                int maxCoupures = 0;
                for (int i = 0; i < 7; i++) {
                    int count = dailyCounts.get(datesLabel.get(i));
                    entries.add(new Entry(i, count));
                    if (count > maxCoupures) maxCoupures = count;
                }

                // Mettre à jour les compteurs de l'interface
                tvTotal7J.setText(String.valueOf(totalCoupures));
                float moyenne = (float) totalCoupures / 7;
                tvMoyenne7J.setText(String.format(Locale.getDefault(), "%.1f", moyenne));
                tvMax7J.setText(String.valueOf(maxCoupures));

                // Configurer les labels de l'axe X
                XAxis xAxis = lineChart.getXAxis();
                xAxis.setValueFormatter(new ValueFormatter() {
                    @Override
                    public String getFormattedValue(float value) {
                        int index = (int) value;
                        if (index >= 0 && index < datesLabel.size()) {
                            return datesLabel.get(index);
                        }
                        return "";
                    }
                });

                // Tracer et Animer le graphique
                setChartDataAndAnimate(entries);

                // Dans onDataChange de Firebase...
                Map<String, Integer> villeCounts = new HashMap<>();

                for (DataSnapshot ds : snapshot.getChildren()) {
                    // On récupère le nom de la zone (ex: "Secteur 15")
                    String ville = ds.child("ville").getValue(String.class);
                    if (ville != null) {
                        villeCounts.put(ville, villeCounts.getOrDefault(ville, 0) + 1);
                    }
                }

                // On transforme la Map en liste pour pouvoir la trier
                List<ZoneStats> sortedZones = new ArrayList<>();
                for (Map.Entry<String, Integer> entry : villeCounts.entrySet()) {
                    sortedZones.add(new ZoneStats(entry.getKey(), entry.getValue()));
                }

                // 1. On trie du plus grand au plus petit
                Collections.sort(sortedZones, (z1, z2) -> Integer.compare(z2.getNombreCoupures(), z1.getNombreCoupures()));

                // 2. On garde les 5 premiers seulement si la liste est longue
                List<ZoneStats> top5Zones = new ArrayList<>();
                int limit = Math.min(sortedZones.size(), 5);
                for (int i = 0; i < limit; i++) {
                    top5Zones.add(sortedZones.get(i));
                }

                // 3. On envoie la liste triée normalement au graphique
                displayBarChart(top5Zones);

                // 1. On trie du PLUS GRAND au PLUS PETIT pour la liste
                Collections.sort(sortedZones, (z1, z2) -> Integer.compare(z2.getNombreCoupures(), z1.getNombreCoupures()));

                // 2. On configure le RecyclerView
                RecyclerView recyclerView = findViewById(R.id.rvDetailsZones);
                recyclerView.setLayoutManager(new LinearLayoutManager(StatisticsActivity.this));
               // 3. On branche l'Adapter
                ZoneAdapter adapter = new ZoneAdapter(sortedZones);
                recyclerView.setAdapter(adapter);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });
    }

    private void setChartDataAndAnimate(ArrayList<Entry> entries) {
        LineDataSet dataSet = new LineDataSet(entries, "Coupures");

        // --- STYLE DE LA COURBE (comme ton image) ---
        dataSet.setColor(Color.parseColor("#E53935")); // Rouge SONABEL
        dataSet.setLineWidth(3f); // Ligne épaisse

        // LISSAGE (C'est ça qui rend la courbe "douce" et "bouge")
        dataSet.setMode(LineDataSet.Mode.CUBIC_BEZIER);

        // Personnalisation des points
        dataSet.setDrawCircles(true);
        dataSet.setCircleColor(Color.parseColor("#E53935"));
        dataSet.setCircleRadius(5f);
        dataSet.setDrawCircleHole(true);
        dataSet.setCircleHoleColor(Color.WHITE);
        dataSet.setCircleHoleRadius(2.5f);

        // Remplissage en dessous
        dataSet.setDrawFilled(true);
        dataSet.setFillColor(Color.parseColor("#E53935"));
        dataSet.setFillAlpha(30); // Très transparent

        // Masquer les valeurs au-dessus des points
        dataSet.setDrawValues(false);

        // Créer les données finales
        LineData lineData = new LineData(dataSet);
        lineChart.setData(lineData);

        // --- ANIMATION (C'est ça qui la fait bouger à l'ouverture) ---
        lineChart.animateY(1500, Easing.EaseInOutCubic);
        lineChart.invalidate(); // Rafraîchir
    }

    private void displayBarChart(List<ZoneStats> sortedZones) {
        // 1. On s'assure que la liste est triée : PETIT en premier, GRAND en dernier
        // Pourquoi ? Parce que le graphique dessine de bas en haut.
        // Le dernier de la liste sera donc tout en haut du graphique.
        Collections.sort(sortedZones, (z1, z2) -> Integer.compare(z1.getNombreCoupures(), z2.getNombreCoupures()));

        ArrayList<BarEntry> barEntries = new ArrayList<>();
        final ArrayList<String> labels = new ArrayList<>();

        for (int i = 0; i < sortedZones.size(); i++) {
            // i = index (0, 1, 2...), la valeur = nombre de coupures
            barEntries.add(new BarEntry(i, (float) sortedZones.get(i).getNombreCoupures()));
            labels.add(sortedZones.get(i).getNomZone());
        }

        BarDataSet dataSet = new BarDataSet(barEntries, "Villes");
        // --- INSERTION DU DEGRADÉ DE ROUGE ---
        List<Integer> colors = new ArrayList<>();
        int rougePale   = Color.parseColor("#FFCDD2"); // Pour les petits chiffres (bas)
        int rougeClair  = Color.parseColor("#EF9A9A");
        int rougeMoyen  = Color.parseColor("#E53935");
        int rougeVif    = Color.parseColor("#D32F2F");
        int rougeSombre = Color.parseColor("#B71C1C"); // Pour le plus grand (haut)

        int[] palette = {rougePale, rougeClair, rougeMoyen, rougeVif, rougeSombre};

        for (int i = 0; i < sortedZones.size(); i++) {
            // On choisit la couleur selon l'index (i)
            int colorIndex = i;
            if (colorIndex >= palette.length) colorIndex = palette.length - 1; // Sécurité
            colors.add(palette[colorIndex]);
        }
        dataSet.setColors(colors); // On applique la liste de couleurs
        dataSet.setValueTextSize(12f);
        dataSet.setDrawValues(true); // Affiche le chiffre sur la barre

        BarData data = new BarData(dataSet);
        data.setBarWidth(0.8f); // Ajuste l'épaisseur des barres

        // On applique les données
        barChartZones.setData(data);

        // 1. On récupère notre NestedScrollView par son ID
        androidx.core.widget.NestedScrollView scrollView = findViewById(R.id.main_scroll_view);

        if (scrollView != null) {
            scrollView.setOnScrollChangeListener(new androidx.core.widget.NestedScrollView.OnScrollChangeListener() {
                private boolean isBarChartVisible = false;
                private boolean isLineChartVisible = false;

                @Override
                public void onScrollChange(androidx.core.widget.NestedScrollView v, int scrollX, int scrollY, int oldScrollX, int oldScrollY) {
                    int screenHeight = v.getHeight();

                    // --- Gestion pour le BarChart (Graphique à barres) ---
                    int[] barLocation = new int[2];
                    barChartZones.getLocationOnScreen(barLocation);
                    boolean isNowVisibleBar = (barLocation[1] > 0 && barLocation[1] < screenHeight);

                    if (isNowVisibleBar && !isBarChartVisible) {
                        barChartZones.animateY(1000); // Rejoue l'animation
                        isBarChartVisible = true;
                    } else if (!isNowVisibleBar) {
                        isBarChartVisible = false; // Reset pour la prochaine fois
                    }

                    // --- Gestion pour le LineChart (Graphique courbe) ---
                    int[] lineLocation = new int[2];
                    lineChart.getLocationOnScreen(lineLocation);
                    boolean isNowVisibleLine = (lineLocation[1] > 0 && lineLocation[1] < screenHeight);

                    if (isNowVisibleLine && !isLineChartVisible) {
                        lineChart.animateY(1000); // Rejoue l'animation
                        isLineChartVisible = true;
                    } else if (!isNowVisibleLine) {
                        isLineChartVisible = false;
                    }
                }
            });
        }

        // 1. Définition du rayon
        //float radius = 20f;

       // 2. Activation du rendu arrondi (on utilise barChartZones ici)
        //barChartZones.setRenderer(new MyRoundedHorizontalBarRenderer(barChartZones, barChartZones.getAnimator(), barChartZones.getViewPortHandler(), radius));

       // 3. Rafraîchissement
        barChartZones.invalidate();


        // 2. Réglage de l'Axe X (les noms des villes)
        XAxis xAxis = barChartZones.getXAxis();
        xAxis.setValueFormatter(new ValueFormatter() {
            @Override
            public String getFormattedValue(float value) {
                int index = (int) value;
                if (index >= 0 && index < labels.size()) {
                    return labels.get(index);
                }
                return "";
            }
        });

        // --- IMPORTANT : REGLAGES POUR ALIGNER LES BARRES ---
        barChartZones.setFitBars(true); // Aligne les barres proprement
        barChartZones.getAxisLeft().setAxisMinimum(0f); // Force le départ à zéro

        barChartZones.animateY(1000);

        // On ajoute une marge à gauche pour que les noms des villes ne soient pas coupés
        // Plus le chiffre est grand, plus la marge est large
        barChartZones.setExtraLeftOffset(80f);

        // On force l'affichage de tous les labels
        xAxis.setLabelCount(sortedZones.size());

        xAxis.setLabelCount(sortedZones.size());

        barChartZones.invalidate();
    }
}