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

// Class pour gérer la connexion des utilisateurs
public class LoginActivity extends AppCompatActivity {
    private EditText emailLogin; // Champ de saisie pour l'email
    private EditText passwordLogin; // Champ de saisie pour le mot de passe
    private Button btnLogin; // Bouton pour connecter
    private Button btnRedirectRegister; // Bouton pour s'inscrire
    private FirebaseAuth mAuth; // Authentification Firebase
    private FirebaseFirestore db; // Firestore pour la base de données

    // Définition des attributs
    public static String username; // Nom d'utilisateur de la personne qui se connecte

    // Méthode pour initialiser les vues
    private void setupView(){
        emailLogin = findViewById(R.id.emailLogin);
        passwordLogin = findViewById(R.id.passwordLogin);
        btnLogin = findViewById(R.id.btnLogin);
        btnRedirectRegister = findViewById(R.id.btnRedirectRegister);
    }

    // Méthode pour rediriger vers l'activité d'inscription
    private void redirectToRegister(){
        btnRedirectRegister.setOnClickListener(v -> {
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
            finish();
        });
    }

    // Méthode pour rediriger vers l'activité principale
    private void redirectToMain(){
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        startActivity(intent);
        finish(); // Ferme l'activité courante
    }

    // Méthode pour gérer la connexion des utilisateurs
    private void gestionConnexion(){
        btnLogin.setOnClickListener(v -> { // Ajouter un listener au bouton de connexion
            String email = emailLogin.getText().toString().trim(); // Récupération de l'email saisi dans le champ emailLogin
            String password = passwordLogin.getText().toString().trim(); // Récupération du mot de passe saisi dans le champ passwordLogin

            if (email.isEmpty() || password.isEmpty()) { // Vérification que les champs ne sont pas vides (renvoi une erreur indiquant de remplir tout les champs)
                Toast.makeText(LoginActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            // Connexion à Firebase Authentication avec l'email et le mot de passe
            mAuth.signInWithEmailAndPassword(email, password) // Appel de la méthode signInWithEmailAndPassword de FirebaseAuth
                    .addOnCompleteListener(this, task -> {
                        if (task.isSuccessful()) { // Vérification de la connexion réussie
                            String userId = mAuth.getCurrentUser().getUid(); // Récupération de l'ID de l'utilisateur connecté

                            // Récupération du nom d'utilisateur depuis Firestore
                            db.collection("usernames")
                                    .whereEqualTo("uuid_user", userId) // Recherche de l'utilisateur par son ID
                                    .get() // Appel de la méthode get de Firestore pour récupérer les données de l'utilisateur
                                    .addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() { // Appel de la méthode addOnSuccessListener pour gérer la réponse de Firestore
                                        public void onSuccess(QuerySnapshot queryDocumentSnapshots) { // Gestion de la réponse de Firestore
                                            if (!queryDocumentSnapshots.isEmpty()) { // Vérification que des données ont été trouvées
                                                DocumentSnapshot documentSnapshot = queryDocumentSnapshots.getDocuments().get(0); // Récupération de la première occurrence trouvée
                                                username = documentSnapshot.getString("username"); // Récupération du nom d'utilisateur en fonction de l'ID de l'utilisateur
                                            }
                                        }
                                    })
                                    .addOnFailureListener(new OnFailureListener() { // Appel de la méthode addOnFailureListener pour gérer les erreurs
                                        @Override
                                        public void onFailure(@NonNull Exception e) {
                                            // Gérez l'erreur
                                            Log.e("Firestore", "Erreur lors de la récupération du nom d'utilisateur", e);
                                        }
                                    });

                            // Connexion réussie, rediriger vers l'activité principale
                            redirectToMain();

                        } else { // Si la connexion échoue (afficher une erreur)
                            Toast.makeText(LoginActivity.this, "Échec de la connexion. Vérifiez vos identifiants.",
                                    Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // Méthode principale de la classe LoginActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) { // Bundle => données de l'activité | saveInstanceState => données de l'activité qui sont sauvegardées
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login); // Permet de lier le fichier XML avec la classe Java

        // Initialisation de Firebase Authentication et Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Setup des vues
        setupView();

        // Gestion de la connexion des utilisateurs
        gestionConnexion();

        // Redirection vers l'activité d'inscription
        redirectToRegister();
    }
}