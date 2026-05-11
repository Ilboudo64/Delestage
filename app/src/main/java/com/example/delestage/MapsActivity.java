package com.example.delestage;

import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;
import androidx.fragment.app.FragmentActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptor;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;
    private DatabaseReference mDatabase;
    private TextView tvCoupure, tvRetour;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Initialisation des vues du haut
        tvCoupure = findViewById(R.id.tv_count_coupure);
        tvRetour = findViewById(R.id.tv_count_retour);

        mDatabase = FirebaseDatabase.getInstance().getReference("signalements");

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }
    }

    private BitmapDescriptor descriptorFromVector(int vecorResId) {
        android.graphics.drawable.Drawable vectorDrawable = androidx.core.content.ContextCompat.getDrawable(this, vecorResId);
        if (vectorDrawable == null) return BitmapDescriptorFactory.defaultMarker();

        // --- ICI ON DÉFINIT LA TAILLE (100x100 pixels est une bonne taille visible) ---
        int largeur = 70;
        int hauteur = 70;

        // On force la taille de l'icône
        vectorDrawable.setBounds(0, 0, largeur, hauteur);

        // On crée un bitmap à la nouvelle taille
        android.graphics.Bitmap bitmap = android.graphics.Bitmap.createBitmap(
                largeur,
                hauteur,
                android.graphics.Bitmap.Config.ARGB_8888);

        android.graphics.Canvas canvas = new android.graphics.Canvas(bitmap);
        vectorDrawable.draw(canvas);

        return BitmapDescriptorFactory.fromBitmap(bitmap);
    }
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        // On dit à la carte d'utiliser notre nouveau design
        mMap.setInfoWindowAdapter(new CustomInfoWindowAdapter(this));

        mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(com.google.android.gms.maps.model.Marker marker) {
                preparerVoteFirebase(marker);
            }
        });

        // On centre sur le Burkina Faso par défaut
        LatLng burkina = new LatLng(12.3714, -1.5197);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(burkina, 7f));

        chargerSignalements();
    }

    private void preparerVoteFirebase(com.google.android.gms.maps.model.Marker marker) {
        String quartierID = marker.getTitle();

        // On récupère le texte du snippet (qui contient "Statut: COUPURE" ou "Statut: RETABLISSEMENT")
        String statutComplet = marker.getSnippet();

        // On nettoie pour ne garder que "COUPURE" ou "RÉTABLISSEMENT"
        String statutSimple = statutComplet.replace("Statut: ", "").toLowerCase();

        new android.app.AlertDialog.Builder(this)
                .setTitle("Vérification citoyenne")
                // Le message s'adapte maintenant : "Est-il vrai qu'il y a une coupure à Saaba ?"
                // ou "Est-il vrai qu'il y a une rétablissement à Saaba ?"
                .setMessage("Est-il vrai qu'il y a un(e) " + statutSimple + " à " + quartierID + " ?")

                .setPositiveButton("C'est vrai", (dialog, which) -> {
                    enregistrerAction(quartierID, "confirmations", "Merci ! Votre confirmation pour " + quartierID + " est enregistrée.");
                })
                .setNegativeButton("C'est faux", (dialog, which) -> {
                    enregistrerAction(quartierID, "contestations", "Merci ! Votre signalement pour " + quartierID + " a été pris en compte.");
                })
                .show();
    }

    private void enregistrerAction(String ville, String typeChamp, String messageToast) {
        // 2. On cherche le signalement correspondant dans Firebase
        mDatabase.orderByChild("ville").equalTo(ville).limitToLast(1)
                .addListenerForSingleValueEvent(new com.google.firebase.database.ValueEventListener() {
                    @Override
                    public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot snapshot) {
                        for (com.google.firebase.database.DataSnapshot ds : snapshot.getChildren()) {

                            // 3. On ajoute +1 au compteur (confirmations ou contestations)
                            ds.getRef().child(typeChamp).runTransaction(new com.google.firebase.database.Transaction.Handler() {
                                @androidx.annotation.NonNull
                                @Override
                                public com.google.firebase.database.Transaction.Result doTransaction(@androidx.annotation.NonNull com.google.firebase.database.MutableData currentData) {
                                    Long valeur = currentData.getValue(Long.class);
                                    if (valeur == null) currentData.setValue(1);
                                    else currentData.setValue(valeur + 1);
                                    return com.google.firebase.database.Transaction.success(currentData);
                                }


                                @Override
                                public void onComplete(com.google.firebase.database.DatabaseError error, boolean committed, com.google.firebase.database.DataSnapshot currentData) {
                                    if (committed) {
                                        // 4. On affiche ton message de confirmation en bas
                                        android.widget.Toast.makeText(MapsActivity.this, messageToast, android.widget.Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }
                    }
                    @Override public void onCancelled(@androidx.annotation.NonNull com.google.firebase.database.DatabaseError error) {}
                });
    }




    private void chargerSignalements() {
        // --- ÉTAPE 1 : LE FILTRE DE TEMPS ---
        long seuil24h = System.currentTimeMillis() - (24 * 60 * 60 * 1000);

        // --- ÉTAPE 2 : LA REQUÊTE FIREBASE ---
        com.google.firebase.database.Query query = mDatabase.orderByChild("timestamp").startAt(seuil24h);

        query.addValueEventListener(new com.google.firebase.database.ValueEventListener() {
            @Override
            public void onDataChange(@androidx.annotation.NonNull com.google.firebase.database.DataSnapshot dataSnapshot) {
                if (mMap == null) return;

                // ON NETTOIE TOUT AU DÉBUT
                mMap.clear();
                int countCoupure = 0;
                int countRetour = 0;

                // --- ÉTAPE 3 : SÉLECTION DU PLUS RÉCENT (HashMap) ---
                // On crée une liste vide pour stocker uniquement le dernier signalement de chaque quartier
                java.util.HashMap<String, com.google.firebase.database.DataSnapshot> derniersParVille = new java.util.HashMap<>();

                for (com.google.firebase.database.DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    String ville = snapshot.child("ville").getValue(String.class);
                    if (ville != null && !ville.isEmpty()) {
                        // On écrase l'ancien par le nouveau pour n'avoir que le dernier en date
                        derniersParVille.put(ville.trim().toLowerCase(), snapshot);
                    }
                }

                // --- ÉTAPE 4 : FILTRE DE VÉRITÉ ET AFFICHAGE ---
                // Maintenant qu'on a le dernier signalement de chaque quartier, on décide de l'afficher ou non
                for (com.google.firebase.database.DataSnapshot snapshot : derniersParVille.values()) {
                    String ville = snapshot.child("ville").getValue(String.class);
                    String type = snapshot.child("type").getValue(String.class);

                    // Récupération des votes (Remplace "confirmations" par ton nom exact si besoin)
                    long conf = snapshot.hasChild("confirmations") ? snapshot.child("confirmations").getValue(Long.class) : 0;
                    long cont = snapshot.hasChild("contestations") ? snapshot.child("contestations").getValue(Long.class) : 0;

                    // LOGIQUE DE SUPPRESSION : Si trop de contestations, on saute ce quartier
                    if (cont > conf && (cont - conf) >= 3) {
                        android.util.Log.d("DEBUG_MAP", "INFO: " + ville + " retiré (Seuil de 3 atteint).");
                        continue;
                    }

                    // SI ON EST ICI, LE SIGNALEMENT EST CRÉDIBLE
                    if (ville != null && type != null) {
                        if (type.equalsIgnoreCase("COUPURE")) countCoupure++;
                        else countRetour++;

                        // Utilisation de ton dictionnaire de coordonnées
                        LatLng position = getCoordsFixes(ville);

                        int iconRes = type.equalsIgnoreCase("COUPURE") ?
                                R.drawable.ic_eclair_rouge :
                                R.drawable.ic_eclair_vert;

                        // DESSIN DU MARQUEUR
                        Marker marker = mMap.addMarker(new MarkerOptions()
                                .position(position)
                                .title(ville)
                                .snippet("Statut: " + type)
                                .icon(descriptorFromVector(iconRes))
                        );

                        if (marker != null && type.equalsIgnoreCase("COUPURE")) {
                            faireClignoter(marker);
                        }
                    }
                }

                // --- ÉTAPE 5 : MISE À JOUR DES COMPTEURS EN HAUT ---
                tvCoupure.setText("● " + countCoupure + " Coupures");
                tvRetour.setText("● " + countRetour + " Retours");
            }

            @Override public void onCancelled(com.google.firebase.database.DatabaseError error) {}
        });
    }

    // TON DICTIONNAIRE ENRICHI (Ouaga, Bobo, Koudougou)
    private LatLng getCoordsFixes(String nomZone) {
        if (nomZone == null) return new LatLng(12.3714, -1.5197);
        String z = nomZone.toLowerCase().trim();

        // --- SECTION 1 : OUAGADOUGOU (Objectif 50) ---
        // Quartiers Populaires
        if (z.contains("tanghin")) return new LatLng(12.3960, -1.5090);
        if (z.contains("pissy")) return new LatLng(12.3424, -1.5721);
        if (z.contains("karpala")) return new LatLng(12.3330, -1.4880);
        if (z.contains("kossodo")) return new LatLng(12.4281, -1.4644);
        if (z.contains("zogona")) return new LatLng(12.3780, -1.5030);
        if (z.contains("saaba")) return new LatLng(12.3800, -1.4500);
        if (z.contains("dassasgho")) return new LatLng(12.3810, -1.4780);
        if (z.contains("gounghin")) return new LatLng(12.3580, -1.5540);
        if (z.contains("tampouy")) return new LatLng(12.4020, -1.5550);
        if (z.contains("ouaga 2000")) return new LatLng(12.3010, -1.5050);
        if (z.contains("patte d'oie")) return new LatLng(12.3350, -1.5300);
        if (z.contains("1200 logements")) return new LatLng(12.3680, -1.4950);
        if (z.contains("somgandé")) return new LatLng(12.4050, -1.4920);
        if (z.contains("wayalghin")) return new LatLng(12.3850, -1.4550);
        if (z.contains("nagrin")) return new LatLng(12.3050, -1.5450);
        if (z.contains("rimkieta")) return new LatLng(12.4200, -1.5950);
        // Suite Ouagadougou
        if (z.contains("dagnoën")) return new LatLng(12.3730, -1.4820);
        if (z.contains("wemtenga")) return new LatLng(12.3640, -1.5020);
        if (z.contains("koulouba")) return new LatLng(12.3730, -1.5170);
        if (z.contains("larlé")) return new LatLng(12.3820, -1.5360);
        if (z.contains("dapoya")) return new LatLng(12.3770, -1.5230);
        if (z.contains("hamdalaye")) return new LatLng(12.3650, -1.5580);
        if (z.contains("cissin")) return new LatLng(12.3380, -1.5520);
        if (z.contains("kilwin")) return new LatLng(12.4180, -1.5680);
        if (z.contains("nonsin")) return new LatLng(12.3880, -1.5560);
        if (z.contains("nioko")) return new LatLng(12.4100, -1.4200);

        // Secteurs Ouaga (Tu peux continuer ainsi jusqu'à 50)
        if (z.contains("secteur 1")) return new LatLng(12.3710, -1.5200);
        if (z.contains("secteur 15")) return new LatLng(12.3450, -1.5750);
        if (z.contains("secteur 30")) return new LatLng(12.3300, -1.4900);
        if (z.contains("secteur 54")) return new LatLng(12.4300, -1.4600);

        // --- SECTION 2 : BOBO-DIOULASSO (Objectif 30) ---
        if (z.contains("bolomakoté")) return new LatLng(11.1850, -4.2880);
        if (z.contains("belle-ville")) return new LatLng(11.1660, -4.3160);
        if (z.contains("accart-ville")) return new LatLng(11.1780, -4.3030);
        if (z.contains("diarradougou")) return new LatLng(11.1750, -4.2850);
        if (z.contains("bindougousso")) return new LatLng(11.1950, -4.3150);
        if (z.contains("colma")) return new LatLng(11.2100, -4.3050);
        if (z.contains("sikasso-cira")) return new LatLng(11.1720, -4.2820);
        if (z.contains("kodeni")) return new LatLng(11.1350, -4.3450);

        // Secteurs Bobo
        if (z.contains("bobo secteur 1")) return new LatLng(11.1800, -4.2900);
        if (z.contains("bobo secteur 22")) return new LatLng(11.1500, -4.3200);

        // --- SECTION 3 : KOUDOUGOU (Objectif 30) ---
        if (z.contains("koudougou secteur 1")) return new LatLng(12.2530, -2.3650);
        if (z.contains("koudougou secteur 2")) return new LatLng(12.2600, -2.3600);
        if (z.contains("koudougou secteur 5")) return new LatLng(12.2450, -2.3750);
        if (z.contains("palogo")) return new LatLng(12.2300, -2.3800);
        if (z.contains("sourgou")) return new LatLng(12.2100, -2.4000);
        if (z.contains("dapoya koudougou")) return new LatLng(12.2580, -2.3680);

        // --- SECTION 4 : AUTRES VILLES ---
        if (z.contains("banfora")) return new LatLng(10.6333, -4.7500);
        if (z.contains("ouahigouya")) return new LatLng(13.5833, -2.4167);
        if (z.contains("kaya")) return new LatLng(13.0833, -1.0833);
        if (z.contains("fada")) return new LatLng(12.0667, 0.3667);

        // --- FALLBACK (Si non trouvé) ---
        // On applique un petit décalage aléatoire pour que les points inconnus
        // ne soient pas strictement les uns sur les autres au centre de Ouaga
        double random = (Math.random() - 0.5) / 100;
        return new LatLng(12.3714 + random, -1.5197 + random);
    }

    private void faireClignoter(final Marker marker) {
        final Handler handler = new Handler();
        handler.post(new Runnable() {
            boolean visible = true;
            @Override
            public void run() {
                if (marker != null) {
                    marker.setAlpha(visible ? 1.0f : 0.3f);
                    visible = !visible;
                    handler.postDelayed(this, 600);
                }
            }
        });
    }
}