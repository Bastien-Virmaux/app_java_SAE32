package com.example.projet_sae32;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class Message {
    private String id; // Ajoutez ce champ pour l'ID Firestore
    private String pseudo;
    private String content;
    private String date;
    private int likes;
    private ArrayList<String> likedBy;

    public Message(String id, String pseudo, String content, String date) {
        this.id = id;
        this.pseudo = pseudo;
        this.content = content;
        this.date = date;
        this.likes = 0;
        this.likedBy = new ArrayList<>();
    }

    // Constructeur sans ID (utilisé lors de la création d'un nouveau message)
    public Message(String pseudo, String content, String date) {
        this.pseudo = pseudo;
        this.content = content;
        this.date = date;
        this.likes = 0;
        this.likedBy = new ArrayList<>();
    }

    // Getters existants
    // Ajoutez un getter pour l'ID
    public String getId() {
        return id;
    }

    // Ajoutez un setter pour l'ID
    public void setId(String id) {
        this.id = id;
    }

    public String getPseudo() {
        return pseudo;
    }

    public String getContent() {
        return content;
    }

    public String getDate() {
        return date;
    }

    public int getLikes() {
        return likes;
    }

    // Setter pour les likes
    public void setLikes(int likes) {
        this.likes = likes;
    }

    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    // Setter pour likedBy
    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }
}