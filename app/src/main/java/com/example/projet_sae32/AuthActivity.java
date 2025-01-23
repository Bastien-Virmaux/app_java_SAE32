package com.example.projet_sae32;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

// Class pour géré le choix de l'utilisateur (Connexion ?, Inscription ?)
public class AuthActivity extends AppCompatActivity {
    private Button _login; // Bouton pour se connecter
    private Button _register; // Bouton pour s'inscrire

    // Méthode pour initialiser les vues
    private void setupViews(){
        _login = findViewById(R.id.login);
        _register = findViewById(R.id.register);
    }

    // Méthode pour rediriger vers l'activité de login
    private void redirectToLogin(){
        _login.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Ferme l'activité courante
        });
    }

    // Méthode pour rediriger vers l'activité d'inscription
    private void redirectToRegister(){
        _register.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // Ferme l'activité courante
        });
    }

    // Méthode principale de la classe AuthActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) { // Bundle => données de l'activité | saveInstanceState => données de l'activité qui sont sauvegardées
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_nexus); // Permet de lier le fichier XML avec la classe open_nexus

        // Initialisation des vues
        setupViews();

        // Redirection vers l'activité de login
        redirectToLogin();

        // Redirection vers l'activité d'inscription
        redirectToRegister();
    }
}