package com.example.delestage;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class SignUpActivity extends AppCompatActivity {

    private EditText edtName, edtEmail, edtZone, edtPass, edtConfirmPass;
    private CheckBox checkTerms;
    private AppCompatButton btnRegister;
    private TextView txtLoginLink;
    private FirebaseAuth mAuth;
    private DatabaseReference mDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        // Initialisation Firebase
        mAuth = FirebaseAuth.getInstance();
        mDatabase = FirebaseDatabase.getInstance().getReference();

        // 1. Récupère les références des images d'œil (vérifie les IDs dans ton XML)
        ImageView imgEyePass = findViewById(R.id.imgEyePass); // L'ID de l'œil pour le premier MDP
        ImageView imgEyeConfirm = findViewById(R.id.imgEyeConfirm); // L'ID pour la confirmation

        // 2. APPELLE tes méthodes ici pour activer le clic
        configurerVisibiliteMdp(edtPass, imgEyePass);
        configurerVisibiliteMdp(edtConfirmPass, imgEyeConfirm);

        // Liaisons des vues
        edtName = findViewById(R.id.edtFullName);
        edtEmail = findViewById(R.id.edtEmail);
        edtZone = findViewById(R.id.edtZone);
        edtPass = findViewById(R.id.edtPassword);
        edtConfirmPass = findViewById(R.id.edtConfirmPassword);
        checkTerms = findViewById(R.id.checkTerms);
        btnRegister = findViewById(R.id.btnRegister);
        txtLoginLink = findViewById(R.id.txtBackToLogin);

        // Gestion du choix de la zone (Pop-up de sélection)
        edtZone.setOnClickListener(v -> afficherDialogueZones());

        // Bouton S'inscrire
        btnRegister.setOnClickListener(v -> inscrireUtilisateur());

        // Retour à la connexion
        txtLoginLink.setOnClickListener(v -> finish());
    }

    private void afficherDialogueZones() {
        // Liste des quartiers (à adapter selon tes besoins)
        String[] zones = {"Pissy", "Saaba", "Benogo", "Somgande", "Wemtenga", "Gounghin", "Patte d'Oie"};

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Sélectionnez votre zone");
        builder.setItems(zones, (dialog, which) -> {
            edtZone.setText(zones[which]);
        });
        builder.show();
    }

    private void inscrireUtilisateur() {
        String name = edtName.getText().toString().trim();
        String email = edtEmail.getText().toString().trim();
        String zone = edtZone.getText().toString().trim();
        String pass = edtPass.getText().toString().trim();
        String confirm = edtConfirmPass.getText().toString().trim();

        // VALIDATIONS
        if (TextUtils.isEmpty(name)) { edtName.setError("Nom requis"); return; }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) { edtEmail.setError("Email invalide"); return; }
        if (TextUtils.isEmpty(zone)) { Toast.makeText(this, "Sélectionnez une zone", Toast.LENGTH_SHORT).show(); return; }
        if (pass.length() < 6) { edtPass.setError("6 caractères minimum"); return; }
        if (!pass.equals(confirm)) { edtConfirmPass.setError("Les mots de passe ne correspondent pas"); return; }
        if (!checkTerms.isChecked()) {
            Toast.makeText(this, "Acceptez les conditions d'utilisation", Toast.LENGTH_SHORT).show();
            return;
        }

        // CRÉATION DU COMPTE FIREBASE
        mAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String userId = mAuth.getCurrentUser().getUid();

                // Sauvegarde des infos supplémentaires dans Realtime Database
                Map<String, Object> userMap = new HashMap<>();
                userMap.put("nom", name);
                userMap.put("email", email);
                userMap.put("zone", zone);

                mDatabase.child("users").child(userId).setValue(userMap).addOnCompleteListener(dbTask -> {
                    if (dbTask.isSuccessful()) {
                        Toast.makeText(SignUpActivity.this, "Compte créé !", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(SignUpActivity.this, DashbordActivity.class));
                        finish();
                    }
                });
            } else {
                Toast.makeText(SignUpActivity.this, "Erreur : " + task.getException().getMessage(), Toast.LENGTH_LONG).show();
            }
        });
    }
    private void configurerVisibiliteMdp(EditText edt, ImageView img) {
        img.setOnClickListener(v -> {
            // Sauvegarde de la police actuelle
            android.graphics.Typeface typeface = edt.getTypeface();

            if (edt.getInputType() == (InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD)) {
                edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
                img.setImageResource(R.drawable.ic_eye_slash);
            } else {
                edt.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
                img.setImageResource(R.drawable.ic_eye);
            }

            // Réapplique la police et remet le curseur à la fin
            edt.setTypeface(typeface);
            edt.setSelection(edt.getText().length());
        });
    }
}