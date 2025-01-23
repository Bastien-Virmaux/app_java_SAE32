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

public class MainActivity extends AppCompatActivity {
    private RecyclerView recyclerView;
    private MessageAdapter messageAdapter;
    private FirebaseFirestore db;
    private ListenerRegistration messagesListener;

    TextView _textMessage; //text du message
    TextView _dateMessage; //date du message
    TextView _likeCount; //like du message

    Button _btnSend; //bouton pour envoyer le message
    EditText _inpSend; //input pour écrire le message
    Button _btnLike; //bouton pour liker un message

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FirebaseApp.initializeApp(this);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Initialisation de Firestore
        db = FirebaseFirestore.getInstance();

        // Setup des vues
        setupViews();

        // Configuration du RecyclerView
        setupRecyclerView();

        // Démarrer l'écoute des messages
        startListeningToMessages();

        // Ajouter un listener au bouton d'envoi de message
        _btnSend.setOnClickListener(v -> envoyerMessage());
    }

    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView);
        _btnSend = findViewById(R.id.btnSend);
        _inpSend = findViewById(R.id.inpSend);
        _textMessage = findViewById(R.id.textMessage);
        _dateMessage = findViewById(R.id.dateMessage);
    }

    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);
        messageAdapter = new MessageAdapter(this);
        recyclerView.setAdapter(messageAdapter);
    }

    private void sendMessage(Message mess) {
        Map<String, Object> message = new HashMap<>();
        message.put("pseudo", mess.getPseudo());
        message.put("content", mess.getContent());
        message.put("date", mess.getDate());
        message.put("likes", 0);  // Initialiser les likes à 0
        message.put("likedBy", new ArrayList<String>()); // Ajouté ici

        db.collection("messages")
                .add(message)
                .addOnSuccessListener(documentReference -> {
                    Log.d("Firestore", "Message added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> {
                    Log.e("Firestore", "Error adding message", e);
                });
    }

    private void startListeningToMessages() {
        messagesListener = db.collection("messages")
                .orderBy("date", Query.Direction.ASCENDING)
                .addSnapshotListener((value, error) -> {
                    if (error != null) {
                        Log.e("Firestore", "Listen failed.", error);
                        return;
                    }

                    if (value != null) {
                        messageAdapter.clearMessages();
                        for (DocumentSnapshot doc : value.getDocuments()) {
                            Message message = new Message(
                                    doc.getId(), // Utilisez l'ID du document
                                    doc.getString("pseudo"),
                                    doc.getString("content"),
                                    doc.getString("date")
                            );
                            message.setLikes(doc.getLong("likes").intValue());
                            message.setLikedBy((ArrayList<String>) doc.get("likedBy"));
                            messageAdapter.addMessage(message);
                        }

                        recyclerView.scrollToPosition(messageAdapter.getItemCount() - 1);
                    }
                });
    }

    private void envoyerMessage() {
        String messageTexte = _inpSend.getText().toString().trim();

        if (!messageTexte.isEmpty()) {
            // Obtenir l'heure actuelle
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault());
            String date = sdf.format(new Date());

            //récupération du pseudo de l'utilisateur actuellement connecté
            String username = LoginActivity.username;

            Message nouveauMessage = new Message(username, messageTexte, date);
            sendMessage(nouveauMessage);
            _inpSend.setText("");
        }
    }

    private void likedMessage(String messageId) {
        DocumentReference messageRef = db.collection("messages").document(messageId);

        messageRef.get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.exists()) {
                // Récupérer les valeurs actuelles
                int currentLikes = documentSnapshot.getLong("likes") != null ?
                        documentSnapshot.getLong("likes").intValue() : 0;
                ArrayList<String> likedBy = (ArrayList<String>) documentSnapshot.get("likedBy");

                if (likedBy == null) {
                    likedBy = new ArrayList<>();
                }

                // Vérifier si l'utilisateur n'a pas déjà liké
                if (!likedBy.contains(LoginActivity.username)) {
                    // Incrémenter le compteur de likes
                    currentLikes++;
                    // Ajouter l'utilisateur à la liste des personnes ayant liké
                    likedBy.add(LoginActivity.username);

                    // Mettre à jour le document
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("likes", currentLikes);
                    updates.put("likedBy", likedBy);

                    messageRef.update(updates)
                            .addOnSuccessListener(aVoid -> {
                                Log.d("Firestore", "Like successfully updated");
                            })
                            .addOnFailureListener(e -> {
                                Log.e("Firestore", "Error updating like", e);
                            });
                }else{
                    Log.d("Firestore", "User already liked this message");
                    // Afficher un message indiquant que l'utilisateur a déjà liké
                    Toast.makeText(this, "Vous avez déjà liké ce message", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesListener != null) {
            messagesListener.remove();
        }
    }
}