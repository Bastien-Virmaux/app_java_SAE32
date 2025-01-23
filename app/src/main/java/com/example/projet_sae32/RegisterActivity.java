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

// Class pour gérer l'inscription des utilisateurs
public class RegisterActivity extends AppCompatActivity {
    public static String username; // Nom d'utilisateur de la personne qui se connecte
    private EditText usernameRegister; // Champ de saisie pour le nom d'utilisateur
    private EditText emailRegister; // Champ de saisie pour l'email
    private EditText passwordRegister; // Champ de saisie pour le mot de passe
    private EditText passwordConfirmRegister; // Champ de saisie pour confirmer le mot de passe
    private Button btnRegister; // Bouton pour s'inscrire
    private Button btnRedirectLogin; // Bouton pour se connecter
    private FirebaseAuth mAuth; // Authentification Firebase
    private FirebaseFirestore db; // Firestore pour la base de données

    // Méthode pour initialiser les vues
    private void setupView(){
        usernameRegister = findViewById(R.id.usernameRegister);
        emailRegister = findViewById(R.id.emailRegister);
        passwordRegister = findViewById(R.id.passwordRegister);
        passwordConfirmRegister = findViewById(R.id.passwordConfirmRegister);
        btnRegister = findViewById(R.id.btnRegister);
        btnRedirectLogin = findViewById(R.id.btnRedirectLogin);
    }

    // Méthode pour rediriger vers l'activité de connexion
    private void redirectToLogin(){
        btnRedirectLogin.setOnClickListener(v -> {
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }

    private void redirectToMain(){
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        startActivity(intent);
        finish(); // Ferme l'activité courante
    }

    // Méthode pour gérer l'inscription des utilisateurs
    private void gestionInscription(){
        btnRegister.setOnClickListener(v -> { // Ajouter un listener au bouton d'inscription
            String username = usernameRegister.getText().toString().trim(); // Récupération du nom d'utilisateur saisi dans le champ usernameRegister
            String email = emailRegister.getText().toString().trim(); // Récupération de l'email saisi dans le champ emailRegister
            String password = passwordRegister.getText().toString().trim(); // Récupération du mot de passe saisi dans le champ passwordRegister
            String confirmPassword = passwordConfirmRegister.getText().toString().trim(); // Récupération de la confirmation du mot de passe saisi dans le champ passwordConfirmRegister

            if (username.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()) { // Vérification que les champs ne sont pas vides (renvoi une erreur indiquant de remplir tout les champs)
                Toast.makeText(RegisterActivity.this, "Veuillez remplir tous les champs", Toast.LENGTH_SHORT).show();
                return;
            }

            if (!password.equals(confirmPassword)) { // Vérification que les mots de passe correspondent (renvoi une erreur indiquant que les mots de passe ne correspondent pas)
                Toast.makeText(RegisterActivity.this, "Les mots de passe ne correspondent pas", Toast.LENGTH_SHORT).show();
                return;
            }

            // Inscription à Firebase Authentication avec l'email et le mot de passe
            mAuth.createUserWithEmailAndPassword(email, password) // Appel de la méthode createUserWithEmailAndPassword de FirebaseAuth
                    .addOnCompleteListener(this, task -> { // Appel de la méthode addOnCompleteListener pour gérer la réponse de Firebase Authentication
                        if (task.isSuccessful()) { // Vérification de l'inscription réussie

                            // Ajout du pseudo dans la BD
                            FirebaseUser currentUser = mAuth.getCurrentUser(); // Récupération de l'utilisateur actuellement connecté
                            if (currentUser != null) { // Vérification que l'utilisateur est bien connecté

                                String userId = currentUser.getUid(); // Récupération de l'ID de l'utilisateur connecté

                                // Ajout du pseudo dans Firestore
                                Map<String, String> userInfo = new HashMap<>();
                                userInfo.put("username", username);
                                userInfo.put("uuid_user", userId);

                                // Ajout du pseudo dans Firestore
                                db.collection("usernames") // Utilisation de la collection "usernames"
                                        .document(userId) // Utilisation de l'ID de l'utilisateur comme ID du document
                                        .set(userInfo) // Ajout des données du pseudo dans le document
                                        .addOnSuccessListener(documentReference -> { // Appel de la méthode addOnSuccessListener pour gérer la réponse de Firestore
                                            Log.d("Firestore", "UserInfo added successfully");
                                        })
                                        .addOnFailureListener(e -> { // Appel de la méthode addOnFailureListener pour gérer les erreurs
                                            Log.e("Firestore", "Error adding UserInfo", e);
                                        });
                            }

                            // Inscription réussie, rediriger vers l'activité principale
                            redirectToMain();
                        } else { // Si l'inscription échoue (afficher une erreur)
                            Toast.makeText(RegisterActivity.this, "Échec de l'inscription. " +
                                    task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    });
        });
    }

    // Méthode principale de la classe RegisterActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.register); // Permet de lier le fichier XML avec la classe register

        // Initialisation de Firebase Authentication et Firestore
        mAuth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // Initialisation des vues
       setupView();

       // Gestion de l'inscription des utilisateurs
        gestionInscription();

        // Redirection vers l'activité de connexion
        redirectToLogin();
    }
}