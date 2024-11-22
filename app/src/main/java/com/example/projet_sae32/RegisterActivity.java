package com.example.projet_sae32;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {
    public static String username;
    private EditText usernameRegister;
    private EditText emailRegister;
    private EditText passwordRegister;
    private EditText passwordConfirmRegister;
    private Button btnRegister;
    private Button btnRedirectLogin;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register);

        // Initialisation de Firebase Authentication
        mAuth = FirebaseAuth.getInstance();

        // Initialisation de Firestore
        db = FirebaseFirestore.getInstance();

        // Initialisation des vues
        usernameRegister = findViewById(R.id.usernameRegister);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        passwordConfirmRegister = findViewById(R.id.passwordConfirmRegister);
        btnRegister = findViewById(R.id.btnRegister);
        btnRedirectLogin = findViewById(R.id.btnRedirectLogin);

        // Gestion de l'inscription
        btnRegister.setOnClickListener(v -> {
            String email = emailRegister.getText().toString().trim();
            String password = passwordRegister.getText().toString().trim();
            String confirmPassword = passwordConfirmRegister.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) {
                Toast.makeText(RegisterActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) {
                Toast.makeText(RegisterActivity.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            // inscription avec Firebase Authentication
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            //inscription du username dans la bd FIRESTORE
                            username = usernameRegister.getText().toString().trim();

                            FirebaseUser currentUser = mAuth.getCurrentUser();
                            if (currentUser != null) {
                                String userId = currentUser.getUid(); // L'UUID exact de Firebase Auth

                                Map<String, String> userInfo = new HashMap<>();
                                userInfo.put("username", username);
                                userInfo.put("uuid_user", userId);

                                db.collection("usernames")
                                        .document(userId) // Utiliser le même UUID que Firebase Auth
                                        .set(userInfo)
                                        .addOnSuccessListener(documentReference -> {
                                            Log.d("Firestore", "UserInfo added successfully");
                                        })
                                        .addOnFailureListener(e -> {
                                            Log.e("Firestore", "Error adding UserInfo", e);
                                        });
                            }

                            // Inscription réussie, redirection vers l'activité principale
                            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                            startActivity(intent);
                            finish(); // Ferme l'activité courante
                        } else {
                            // Échec de l'inscription
                            Toast.makeText(RegisterActivity.this, "Échec de l'inscription. " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnRedirectLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}