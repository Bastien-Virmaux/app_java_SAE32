package com.example.projet_sae32;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_sae32.adapter.MessageAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

// Class principal du programme
public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView; // RecyclerView pour afficher les messages
    private MessageAdapter messageAdapter; // Adaptateur pour les messages
    private FirebaseFirestore db; // Firestore pour la base de données
    private ListenerRegistration messagesListener; // Listener pour les messages

    TextView _textMessage; //text du message
    TextView _dateMessage; //date du message

    Button _btnSend; //bouton pour envoyer le message
    Button _btnLike; //bouton pour liker le message
    Button _btnRefresh; //bouton pour rafraichir la page
    EditText _inpSend; //input pour écrire le message

    // Méthode pour initialiser les vues
    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        _btnSend = findViewById(R.id.btnSend);
        _btnLike = findViewById(R.id.btnLike);
        _btnRefresh = findViewById(R.id.refeshButton);
        _inpSend = findViewById(R.id.inpSend);
        _textMessage = findViewById(R.id.textMessage);
        _dateMessage = findViewById(R.id.dateMessage);
    }

    // Méthode pour configurer le RecyclerView
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this); // Utilisation d'un LinearLayoutManager pour afficher les messages dans une liste verticale
        recyclerView.setLayoutManager(layoutManager); // Réglage du layoutManager pour le RecyclerView
        messageAdapter = new MessageAdapter(this); // Création de l'adaptateur pour les messages avec le contexte de l'activité (l'adapter permet de gérer les données et les vues)
        recyclerView.setAdapter(messageAdapter); // Réglage de l'adaptateur pour le RecyclerView
    }

    // Méthode pour démarrer l'écoute des messages
    private void startListeningToMessages() {
        messagesListener = db.collection("messages") // Récupération de la collection "messages" de Firestore
                .orderBy("newsScore", Query.Direction.DESCENDING) // Tri des messages par newsScore en ordre décroissant (les plus récents en premier)
                .addSnapshotListener((value, error) -> { // Ajout d'un listener pour détecter les changements dans la collection
                    if (error != null) { // Gestion des erreurs
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value != null) { // Gestion des données récupérées (value : la liste des messages)
                        messageAdapter.clearMessages(); // Nettoyage des messages existants afin d'éviter les doublons
                        for (DocumentSnapshot doc : value.getDocuments()) { // Parcours de chaque message dans la collection "messages" dans Firestore
                            Message message = new Message( // Création d'un objet Message avec les données du message
                                    doc.getId(), // ID du message
                                    doc.getString("pseudo"), // Pseudo du messageur
                                    doc.getString("content"), // Contenu du message
                                    doc.getString("date"), // Date du message
                                    doc.getLong("creationTimestamp") // Timestamp du message
                            );

                            // Récupération du nombre de likes du message dans Firestore ("messages" -> likes)
                            Long likesCount = doc.getLong("likes");
                            message.setLikes(likesCount != null ? likesCount.intValue() : 0); // Ajouter le nombre de like dans l'objet Message

                            // Récupération de la liste des personnes qui ont liké le message dans Firestore ("messages" -> likedBy)
                            ArrayList<String> likedBy = (ArrayList<String>) doc.get("likedBy");
                            message.setLikedBy(likedBy != null ? likedBy : new ArrayList<>()); // Ajouter la liste des personnes qui ont liké dans l'objet Message

                            // Ajout du message à l'adaptateur pour l'afficher dans la liste
                            messageAdapter.addMessage(message);
                        }

                        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1); // Défilement de la liste des messages vers le bas
                    }
                });
    }

    // Méthode pour envoyer un message
    private void sendMessage() {
        String messageTexte = _inpSend.getText().toString().trim(); // Récupération du texte du message à envoyer dans le champs _inpSend

        if (!messageTexte.isEmpty()) { // Vérification que le message n'est pas vide
            // Récupération de la date et de l'heure
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String date = sdf.format(new Date()); // Formatage de la date et de l'heure

            // Récupération du pseudo de l'utilisateur via LoginActivity (pseudo de l'utilisateur connecté via firestorage)
            String username = LoginActivity.username;

            // Création d'un objet message (Map) avec les données du message
            Map<String, Object> message = new HashMap<>();
            // Ajout des données du message à l'objet message (Map)
            message.put("pseudo", username);
            message.put("content", messageTexte);
            message.put("date", date);
            message.put("likes", 0);
            message.put("likedBy", new ArrayList<String>());
            long timestamp = System.currentTimeMillis();
            message.put("creationTimestamp", timestamp);

            // Calcul du score du message
            double initialScore = 30.0;
            message.put("newsScore", initialScore);

            // Envoi du message à Firestore
            db.collection("messages") // Récupération de la collection "messages" de Firestore
                    .add(message)
                    .addOnSuccessListener(documentReference -> {
                        Log.d("Firestore", "Message added with ID: " + documentReference.getId());
                    })
                    .addOnFailureListener(e -> {
                        Log.e("Firestore", "Error adding message", e);
                    });

            // Effacement du champ _inpSend après l'envoi du message
            _inpSend.setText("");
        }
    }

    // Méthode pour rafraichir la page
    private void refresh(){
        // Supprimer les messages existants
        messageAdapter.clearMessages();

        // Démarrer à nouveau l'écoute des messages
        startListeningToMessages();

        // Afficher un message de confirmation (au niveau utilisateur)
        Toast.makeText(this, "Messages updated", Toast.LENGTH_SHORT).show();
    }

    // Méthode principale de la classe MainActivity
    @Override
    protected void onCreate(Bundle savedInstanceState) { // Bundle => données de l'activité | saveInstanceState => données de l'activité qui sont sauvegardées
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main); // Permet de lier le fichier XML avec la classe Java
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisation de Firestore (base de données)
        db = FirebaseFirestore.getInstance();

        // Setup des vues
        setupViews();

        // Configuration du RecyclerView
        setupRecyclerView();

        // Démarrer l'écoute des messages (afin de mettre à jour en temps réel l'ajout de message et de like)
        startListeningToMessages();

        // Ajouter un listener au bouton d'envoi de message
        _btnSend.setOnClickListener(v -> sendMessage());

        // Ajouter un listener au bouton de rafraichissement
        _btnRefresh.setOnClickListener(v -> refresh());
    }
}