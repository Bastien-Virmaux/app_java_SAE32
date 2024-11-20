package com.example.projet_sae32;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_sae32.adapter.MessageAdapter;
import com.google.firebase.FirebaseApp;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {
    //définition des attributs
    private RecyclerView recyclerView; //ATTRIBUT : recyclerview qui va contenir les messages
    private MessageAdapter messageAdapter; //ATTRIBUT : messageAdapter qui va gérer les messages
    private FirebaseFirestore db; //ATTRIBUT : db qui va gérer la base de données
    private ListenerRegistration messagesListener; //ATTRIBUT : messagesListener qui va gérer les messages

    //définition des vues
    TextView _textMessage; //VUE : texte du message
    TextView _dateMessage; //VUE : date du message

    //définition des boutons et champs de texte
    Button _btnSend; //ACTION : bouton d'envoi de message
    EditText _inpSend; //ACTION : champ de texte d'envoi de message

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /* ===A NE PAS TOUCHER=== */
        super.onCreate(savedInstanceState);
        //====
        FirebaseApp.initializeApp(this); //initialisation de Firebase
        //====
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        /* ===FIN A NE PAS TOUCHER=== */

        // Initialisation de Firestore
        db = FirebaseFirestore.getInstance(); //récupération de la base de données

        // Setup des vues
        setupViews();

        // Configuration du RecyclerView
        setupRecyclerView();

        // Démarrer l'écoute des messages
        startListeningToMessages();

        // Ajouter un listener au bouton d'envoi de message
        _btnSend.setOnClickListener(v -> envoyerMessage()); //action du bouton d'envoi de message
    }

    /**
     * Permet d'initialiser les différentes vues
     */
    private void setupViews() {
        recyclerView = findViewById(R.id.recyclerView); //récupération du recyclerview
        _btnSend = findViewById(R.id.btnSend); //récupération du bouton d'envoi de message
        _inpSend = findViewById(R.id.inpSend); //récupération du champ de texte d'envoi de message
        _textMessage = findViewById(R.id.textMessage); //récupération du champ texte du message
        _dateMessage = findViewById(R.id.dateMessage); //récupération du champ date du message
    }

    /**
     * Permet d'initialiser le recyclerView
     */
    private void setupRecyclerView() {
        LinearLayoutManager layoutManager = new LinearLayoutManager(this); //récupération du layout manager => le layout manager est un composant qui gère la disposition des éléments dans un RecyclerView
        recyclerView.setLayoutManager(layoutManager); //on définit le layout manager du recyclerview
        messageAdapter = new MessageAdapter(this); //récupération du messageAdapter => le messageAdapter est un composant qui gère les messages
        recyclerView.setAdapter(messageAdapter); //permet d'indiquer que le récyclerView à comme adapter messageAdapter
    }

    /**
     * Permet d'enrigistrer les message dans la base de données.
     * @param mess = prend en paramétre un objet Message
     */
    private void sendMessage(Message mess) {
        //enregistrement des messages dans une MAP
        Map<String, Object> message = new HashMap<>(); //récupération de la map des messages c'est-à-dire un objet qui permet de stocker des données
        message.put("pseudo", mess.getPseudo()); //on ajoute le pseudo du message dans la map
        message.put("content", mess.getContent()); //on ajoute le contenu du message dans la map
        message.put("date", mess.getDate()); //on ajoute la date du message dans la map

        //enregistrement des messages dans Firestore
        db.collection("messages") //on récupère la collection messages
                .add(message) //on ajoute le message dans la collection messages
                .addOnSuccessListener(documentReference -> { //Permet d'indiquer que l'ajout à réussie
                    Log.d("Firestore", "Message added with ID: " + documentReference.getId());
                })
                .addOnFailureListener(e -> { //Permet d'indiquer que l'ajout à échoué
                    Log.e("Firestore", "Error adding message", e);
                });
    }

    /**
     * Permet de récupérer & afficher les messages dans le recyclerView
     */
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
                                    doc.getString("pseudo"),
                                    doc.getString("content"),
                                    doc.getString("date")
                            );
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

            Message nouveauMessage = new Message("Anonyme", messageTexte, date);
            sendMessage(nouveauMessage);
            _inpSend.setText("");
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messagesListener != null) {
            messagesListener.remove();
        }
    }
}