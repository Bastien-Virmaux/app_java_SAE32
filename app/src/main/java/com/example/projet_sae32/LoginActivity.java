package com.example.projet_sae32;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {
    public static String username;
    private EditText emailLogin;
    private EditText passwordLogin;
    private Button btnLogin;
    private Button btnRedirectRegister;
    private FirebaseAuth mAuth;
    private FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);

        // Initialisation de Firebase Authentication
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialisation des vues
        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRedirectRegister = findViewById(R.id.btnRedirectRegister);

        // Gestion de la connexion
        btnLogin.setOnClickListener(v -> {
            String email = emailLogin.getText().toString().trim();
            String password = passwordLogin.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Connexion avec Firebase Authentication
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) {
                            //récupération du pseudo de l'utilisateur actuellement connecté
                            String userId = mAuth.getCurrentUser().getUid();

                            db.collection("usernames")  // Assurez-vous que c'est le bon nom de collection
                                    .whereEqualTo("uuid_user", userId)
                                    .get()
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                                            if (!queryDocumentSnapshots.isEmpty()) {
                                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0);
                                                username = documentSnapshot.getString("username");
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() {
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Gérez l'erreur
                                            Log.e("Firestore", "Erreur lors de la récupération du nom d'utilisateur", e);
                                        }
                                    });

                            // Connexion réussie, redirection vers l'activité principale
                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                            startActivity(intent);
                            finish(); // Ferme l'activité courante

                        } else {
                            // Échec de la connexion
                            Toast.makeText(LoginActivity.this, "Échec de la connexion. Vérifiez vos identifiants.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });

        btnRedirectRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }
}