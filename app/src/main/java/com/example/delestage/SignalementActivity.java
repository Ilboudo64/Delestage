package com.example.delestage;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Vibrator;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.card.MaterialCardView;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

public class SignalementActivity extends AppCompatActivity {

    private static final String TAG = "SignalementActivity"; // Pour le debug
    private EditText etQuartier;
    private Button btnSignaler;
    private MaterialCardView cardCoupure, cardRetour;
    private String typeRecu = "COUPURE";
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Assure-toi que le nom du fichier est bien 'signalement.xml' dans res/layout
        setContentView(R.layout.signalement);

        // 1. Initialisation
        etQuartier = findViewById(R.id.etQuartier);
        btnSignaler = findViewById(R.id.btnSignaler);
        cardCoupure = findViewById(R.id.cardCoupure);
        cardRetour = findViewById(R.id.cardRetour);

        // Initialisation Firebase
        mDatabase = FirebaseDatabase.getInstance().getReference("signalements");

        // Gestion du titre
        @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView txtTitre = findViewById(R.id.txtTitreInfo);
        if (txtTitre != null) {
            txtTitre.setText("Signaler un événement");
        }

        // 2. Mode initial
        String typeIntention = getIntent().getStringExtra("TYPE");
        if ("RETOUR".equals(typeIntention)) {
            activerModeRetour();
        } else {
            activerModeCoupure();
        }

        // 3. Listeners
        if (cardCoupure != null) cardCoupure.setOnClickListener(v -> activerModeCoupure());
        if (cardRetour != null) cardRetour.setOnClickListener(v -> activerModeRetour());

        // 4. Action du bouton (Utilisation du OnClickListener du bouton)
        btnSignaler.setOnClickListener(v -> {
            Log.d("TEST_CLIC", "LE BOUTON A ETE CLIQUE !");
            Toast.makeText(this, "Clic détecté !", Toast.LENGTH_SHORT).show();
            // Appelle la suite
            envoyerSignalementVersFirebase(etQuartier.getText().toString(), typeRecu);
        });
    }

    private void activerModeCoupure() {
        typeRecu = "COUPURE";
        if (cardCoupure != null) {
            cardCoupure.setStrokeColor(Color.parseColor("#E53935"));
            cardCoupure.setStrokeWidth(6);
        }
        if (cardRetour != null) cardRetour.setStrokeWidth(0);

        btnSignaler.setText("Signaler la coupure");
        btnSignaler.setBackgroundColor(Color.parseColor("#E53935"));
    }

    private void activerModeRetour() {
        typeRecu = "RETOUR";
        if (cardRetour != null) {
            cardRetour.setStrokeColor(Color.parseColor("#10B981"));
            cardRetour.setStrokeWidth(6);
        }
        if (cardCoupure != null) cardCoupure.setStrokeWidth(0);

        btnSignaler.setText("Signaler le retour courant");
        btnSignaler.setBackgroundColor(Color.parseColor("#10B981"));
    }

    private void envoyerSignalementVersFirebase(String quartierSaisi, String typeChoisi) {
        btnSignaler.setEnabled(false); // Désactive pour éviter le multi-clic

        String idSignalement = mDatabase.push().getKey();
        String heureSimple = new SimpleDateFormat("HH:mm", Locale.getDefault()).format(new Date());

        HashMap<String, Object> map = new HashMap<>();
        map.put("id", idSignalement);
        map.put("auteur", "Utilisateur");
        map.put("ville", quartierSaisi);
        map.put("type", typeChoisi);
        map.put("dateHeure", "Aujourd'hui à " + heureSimple);
        map.put("confirmations", 0);
        map.put("contestations", 0);

        if (idSignalement != null) {
            mDatabase.child(idSignalement).setValue(map)
                    .addOnCompleteListener(task -> { // On utilise OnCompleteListener pour être plus sûr
                        if (task.isSuccessful()) {
                            Log.d("FIREBASE", "Envoi réussi");

                            // Vibration
                            Vibrator v = (Vibrator) getSystemService(VIBRATOR_SERVICE);
                            if (v != null) v.vibrate(100);

                            // Affichage de l'alerte
                            AlertDialog.Builder builder = new AlertDialog.Builder(SignalementActivity.this);
                            builder.setTitle("Signalement pris en compte");
                            builder.setMessage("Merci ! Votre signalement est maintenant visible par la communauté.");
                            builder.setCancelable(false);
                            builder.setPositiveButton("OK", (dialog, which) -> {
                                finish(); // Ferme l'activité et retourne à l'écran précédent (Récents)
                            });

                            AlertDialog alert = builder.create();
                            alert.show();
                        } else {
                            btnSignaler.setEnabled(true);
                            Toast.makeText(this, "Erreur de connexion", Toast.LENGTH_SHORT).show();
                        }
                    });
        }
    }

    public void recupererPosition(View view) {
        etQuartier.setText("Ouagadougou - Secteur 15");
    }
}