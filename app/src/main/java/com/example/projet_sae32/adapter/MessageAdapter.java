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

public class MessageAdapter extends RecyclerView.Adapter<MessageAdapter.MessageViewHolder> {
    private final List<Message> messages;
    private Context context;
    private FirebaseFirestore firestoreDb;
    private String currentUsername;

    public MessageAdapter(Context context) {
        this.context = context;
        this.messages = new ArrayList<>();
        this.firestoreDb = FirebaseFirestore.getInstance();
    }
    public void addMessage(Message message) {
        messages.add(message);
        notifyItemInserted(messages.size() - 1);
    }
    public void clearMessages() {
        messages.clear();
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public MessageViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.message, parent, false);
        return new MessageViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MessageViewHolder holder, int position) {
        Message message = messages.get(position);
        // Obtenir le username actuel à chaque affichage
        String currentUsername = LoginActivity.username;

        holder.pseudoMessage.setText(message.getPseudo());
        holder.dateMessage.setText(message.getDate());
        holder.textMessage.setText(message.getContent());
        holder.likeCount.setText(String.valueOf(message.getLikes()));

        // Initialisation de likedBy si nécessaire
        if (message.getLikedBy() == null) {
            message.setLikedBy(new ArrayList<>());
        }

        // Mettre à jour l'apparence du bouton selon si l'utilisateur a déjà liké
        boolean hasLiked = message.getLikedBy().contains(currentUsername);

        if(hasLiked){
            holder.btnLike.setBackgroundColor(R.drawable.icon_nexus_heart_like);
        }

        // Ajouter le listener pour le bouton like
        holder.btnLike.setOnClickListener(v -> {
            // Réobtenir le username actuel au moment du clic
            String username = LoginActivity.username;
            if (!message.getLikedBy().contains(username)) {
                DocumentReference messageRef = firestoreDb.collection("messages").document(message.getId());

                messageRef.get().addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        int currentLikes = documentSnapshot.getLong("likes") != null ?
                                documentSnapshot.getLong("likes").intValue() : 0;
                        ArrayList<String> likedBy = (ArrayList<String>) documentSnapshot.get("likedBy");

                        if (likedBy == null) {
                            likedBy = new ArrayList<>();
                        }

                        if (!likedBy.contains(username)) {
                            currentLikes++;
                            likedBy.add(username);

                            Map<String, Object> updates = new HashMap<>();
                            updates.put("likes", currentLikes);
                            updates.put("likedBy", likedBy);

                            int finalCurrentLikes = currentLikes;
                            messageRef.update(updates)
                                    .addOnSuccessListener(aVoid -> {
                                        holder.likeCount.setText(String.valueOf(finalCurrentLikes));
                                        holder.btnLike.setEnabled(false);
                                    })
                                    .addOnFailureListener(e -> {
                                        Log.e("Firestore", "Error updating like", e);
                                    });
                        }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    static class MessageViewHolder extends RecyclerView.ViewHolder {
        TextView pseudoMessage;
        TextView dateMessage;
        TextView textMessage;
        Button btnLike;
        TextView likeCount;

        MessageViewHolder(View itemView) {
            super(itemView);
            pseudoMessage = itemView.findViewById(R.id.pseudoMessage);
            dateMessage = itemView.findViewById(R.id.dateMessage);
            textMessage = itemView.findViewById(R.id.textMessage);
            btnLike = itemView.findViewById(R.id.btnLike);
            likeCount = itemView.findViewById(R.id.likeCount);
        }
    }
}