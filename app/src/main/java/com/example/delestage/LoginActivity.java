package com.example.delestage;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class LoginActivity extends AppCompatActivity {

    private EditText edtEmail, edtPassword;
    private ImageView imgEye;
    private Button btnLogin;
    private TextView txtForgotPassword, txtSignUp, txtTermsLink;
    private LinearLayout btnGoogle;

    // Pour gérer la visibilité du mot de passe
    private boolean isPasswordVisible = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // 1. Initialisation des Vues
        edtEmail = findViewById(R.id.edtEmail);
        edtPassword = findViewById(R.id.edtPassword);
        imgEye = findViewById(R.id.imgEye);
        btnLogin = findViewById(R.id.btnLogin);
        txtForgotPassword = findViewById(R.id.txtForgotPassword);
        btnGoogle = findViewById(R.id.btnLoginGoogle);
        txtSignUp = findViewById(R.id.txtSignUp);
        txtTermsLink = findViewById(R.id.txtTermsLink);

        // 2. Clic sur l'œil pour masquer/afficher le mot de passe
        imgEye.setOnClickListener(v -> togglePasswordVisibility());

        // 3. Clic sur le bouton de connexion
        btnLogin.setOnClickListener(v -> tenterConnexion());

        // 4. Les liens (Créer compte, etc.)
        txtSignUp.setOnClickListener(v -> startActivity(new Intent(this, SignUpActivity.class)));

        // Clics optionnels pour plus tard (Google, etc.)
        // btnGoogle.setOnClickListener(...)
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Vérifier si l'utilisateur est déjà connecté
        if (com.google.firebase.auth.FirebaseAuth.getInstance().getCurrentUser() != null) {
            // Si oui, on le redirige vers le Dashboard sans passer par le Login
            startActivity(new Intent(LoginActivity.this, DashbordActivity.class));
            finish(); // On ferme LoginActivity pour ne pas pouvoir revenir en arrière
        }
    }

    private void togglePasswordVisibility() {
        if (isPasswordVisible) {
            // Masquer
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
            imgEye.setImageResource(R.drawable.ic_eye); // Icône "œil normal"
        } else {
            // Afficher
            edtPassword.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_VISIBLE_PASSWORD);
            imgEye.setImageResource(R.drawable.ic_eye_slash); // Il te faudra une icône œil barré
        }
        isPasswordVisible = !isPasswordVisible;
        // Remettre le curseur à la fin du texte
        edtPassword.setSelection(edtPassword.getText().length());
    }

    private void tenterConnexion() {
        String email = edtEmail.getText().toString().trim();
        String pass = edtPassword.getText().toString().trim();

        // 1. Validations locales
        if (TextUtils.isEmpty(email)) {
            edtEmail.setError("Veuillez entrer votre email");
            edtEmail.requestFocus();
            return;
        }
        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            edtEmail.setError("Format d'email invalide");
            edtEmail.requestFocus();
            return;
        }
        if (TextUtils.isEmpty(pass)) {
            edtPassword.setError("Mot de passe obligatoire");
            return;
        }

        // 2. Appel à ton backend (Firebase ou autre API)
        // C'est ici que tu mettras ton code FirebaseAuth.signInWithEmailAndPassword

        // Exemple de Toast en attendant
        Toast.makeText(this, "Connexion de " + email + "...", Toast.LENGTH_SHORT).show();

        startActivity(new Intent(this, DashbordActivity.class));
        // finish();
    }
}