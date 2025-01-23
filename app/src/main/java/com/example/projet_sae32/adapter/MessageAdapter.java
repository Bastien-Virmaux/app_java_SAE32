package com.example.projet_sae32.adapter;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.projet_sae32.LoginActivity;
import com.example.projet_sae32.Message;
import com.example.projet_sae32.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

// Class pour gérer les messages (trouvé sur internet et adapter à nos besoin)
public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.setupView> { // extends RecyclerView.Adapter<MessageAdapter.setupView> => adapter pour recycler view (liste)
    private final List<Message> messages; // Liste des messages
    private Context context; // Contexte de l'application
    private FirebaseFirestore firestoreDb; // Firestore pour la base de données

    // Méthode pour créer une vue pour chaque message
    @NonNull
    @Override
    public setupView onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.message, parent, false); // Permet de lier le fichier XML avec la classe message
        return new setupView(view); // Appel du constructeur de la classe setupView
    }

    // Classe pour gérer les vues de chaque message
    static class setupView extends RecyclerView.ViewHolder {
        TextView pseudoMessage; // Pseudo du message
        TextView dateMessage; // Date du message
        TextView textMessage; // Contenu du message
        Button btnLike; // Bouton pour liker le message
        TextView likeCount; // Nombre de likes du message

        // Constructeur de la classe setupView
        setupView(View itemView) { // itemView => vue de chaque message
            super(itemView); // Appel du constructeur de la classe parent RecyclerView.ViewHolder
            pseudoMessage = itemView.findViewById(R.id.pseudoMessage);
            dateMessage = itemView.findViewById(R.id.dateMessage);
            textMessage = itemView.findViewById(R.id.textMessage);
            btnLike = itemView.findViewById(R.id.btnLike);
            likeCount = itemView.findViewById(R.id.likeCount);
        }
    }

    // Constructeur de la classe MessageAdapter
    public MessageAdapter(Context context) { // Context => contexte de l'application
        this.context = context; // Initialisation du contexte
        this.messages = new ArrayList<>(); // Initialisation de la liste des messages
        this.firestoreDb = FirebaseFirestore.getInstance(); // Initialisation de Firestore
    }

    // Méthode pour ajouter un message à la liste
    public void addMessage(Message message) { // Message => message à ajouter
        messages.add(message); // Ajout du message à la liste messages de la class MessageAdapter
    }

    // Méthode pour clear la liste des messages
    public void clearMessages() {
        messages.clear(); // Nettoyage de la liste messages de la class MessageAdapter
    }

    // Méthode pour mettre à jour l'état du bouton en fonction de si l'utilisateur a déjà liké ce message
    private void updateLikeButtonState(Button likeButton, boolean isLiked) {
        if (isLiked) { // Si l'utilisateur a déjà liké ce message
            likeButton.setBackgroundResource(R.drawable.icon_nexus_heart_like); // Changer l'image du bouton en fonction de l'état
            likeButton.setEnabled(false); // Désactiver le bouton
        }
    }

    // Méthode pour liker un message
    private void likeMessage(Message message, setupView holder) { //setupView holder => vue de chaque message
        String username = LoginActivity.username; // Récupération du nom d'utilisateur actuel de la class LoginActivity
        DocumentReference messageRef = firestoreDb.collection("messages").document(message.getId()); // Récupération de la référence du message dans Firestore

        // Récupération des données du message
        messageRef.get().addOnSuccessListener(documentSnapshot -> { // Appel de la méthode addOnSuccessListener pour gérer la réponse de Firestore
            if (documentSnapshot.exists()) { // Vérification que le message existe
                int currentLikes = documentSnapshot.getLong("likes") != null ? documentSnapshot.getLong("likes").intValue() : 0; // Récupération du nombre de likes actuel du message et conversion en entier
                ArrayList<String> likedBy = (ArrayList<String>) documentSnapshot.get("likedBy"); // Récupération de la liste des personnes qui ont liké le message

                if (!likedBy.contains(username)) { // Vérification que l'utilisateur actuel n'a pas déjà liké ce message
                    currentLikes++; // Incrémentation du nombre de likes
                    likedBy.add(username); // Ajout de l'utilisateur actuel à la liste des personnes qui ont liké le message

                    // Mise à jour des données du message
                    message.setLikes(currentLikes); // Mise à jour du nombre de likes du message
                    double newsScore = message.getNewsScore(); // Récupération du score actuel du message

                    // Mise à jour du score du message & like
                    Map<String, Object> updates = new HashMap<>();
                    updates.put("likes", currentLikes);
                    updates.put("likedBy", likedBy);
                    updates.put("newsScore", newsScore);

                    int finalCurrentLikes = currentLikes; // Correction IDE concernant la MAJ du nombre de likes du message
                    messageRef.update(updates) // Appel de la méthode update pour mettre à jour les données du message
                            .addOnSuccessListener(aVoid -> { // Appel de la méthode addOnSuccessListener pour gérer la réponse de Firestore
                                holder.likeCount.setText(String.valueOf(finalCurrentLikes)); // Mise à jour du nombre de likes du message dans la vue & score du message
                                updateLikeButtonState(holder.btnLike, true); // Mettre à jour l'état du bouton en fonction de si l'utilisateur a déjà liké ce message
                            })
                            .addOnFailureListener(e -> { // Appel de la méthode addOnFailureListener pour gérer les erreurs
                                Log.e("Firestore", "Error updating like", e);
                            });
                }
            }
        });
    }

    // Méthode pour afficher les messages
    @Override
    public void onBindViewHolder(@NonNull setupView holder, int position) { // Appelée pour chaque élément de la liste des messages (@NonNull setupView holder => vue de chaque message ; int position => position de chaque message)
        Message message = messages.get(position); // Récupération du message à afficher à partir de la liste messages de la class MessageAdapter
        String currentUsername = LoginActivity.username; // Récupération du nom d'utilisateur actuel

        // Affichage des informations du message
        holder.pseudoMessage.setText(message.getPseudo()); // Affichage du pseudo du message
        holder.dateMessage.setText(message.getDate()); // Affichage de la date du message
        holder.textMessage.setText(message.getContent()); // Affichage du contenu du message
        holder.likeCount.setText(String.valueOf(message.getLikes())); // Affichage du nombre de likes du message

        // Vérifie si l'utilisateur actuel a déjà liké ce message
        boolean isLiked = message.getLikedBy() != null && message.getLikedBy().contains(currentUsername);

        // Mettre à jour l'état du bouton en fonction de si l'utilisateur a déjà liké ce message
        updateLikeButtonState(holder.btnLike, isLiked);

        // Ajouter un listener au bouton de like
        holder.btnLike.setOnClickListener(v -> {
            if (!isLiked) {
                likeMessage(message, holder);
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

}