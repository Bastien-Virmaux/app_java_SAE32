package com.example.projet_sae32;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

public class AuthActivity extends AppCompatActivity {

    private Button _login;
    private Button _register;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.open_nexus);

        // Initialisation des boutons
        _login = findViewById(R.id.login);
        _register = findViewById(R.id.register);

        // Redirection vers l'activité de login
        _login.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, LoginActivity.class);
            startActivity(intent);
            finish(); // Ferme l'activité courante
        });

        // Redirection vers l'activité d'inscription
        _register.setOnClickListener(v -> {
            Intent intent = new Intent(AuthActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish(); // Ferme l'activité courante
        });
    }
}