package com.example.projet_sae32;

import android.app.ActivityManager;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

// Class Message pour gérer les messages
public class Message {
    private String id; // Identifiant unique du message
    private String pseudo; // Pseudo de l'auteur du message
    private String content; // Contenu du message
    private String date; // Date du message
    private int likes; // Nombre de likes du message
    private ArrayList<String> likedBy; // Liste des personnes qui ont liké le message
    private long creationTimestamp; // Timestamp de création du message
    private double newsScore; // Score de news du message

    // Défintion du constructeur pour créer un message avec ou sans ID
    public Message(String id, String pseudo, String content, String date, long timestamp) {
        this.id = id;
        this.pseudo = pseudo;
        this.content = content;
        this.date = date;
        this.likes = 0;
        this.likedBy = new ArrayList<>();
        this.creationTimestamp = timestamp;
        calculateNewsScore();
    }

    // Méthode pour calculer le score des Messages
    public void calculateNewsScore() {
        long currentTime = System.currentTimeMillis(); // On récupère le temps actuel en millisecondes
        long ageInDays = (currentTime - creationTimestamp) / (24 * 60 * 60 * 1000L); // On calcule l'âge du message en jours (1000L => 1 seconde)

        // Si l'âge du message est supérieur à 30 jours, on met le score à 0
        if (ageInDays > 30) {
            this.newsScore = 0;
            return;
        }

        // Calculer le score avec la formule : ω = 30 - δ × α sachant que :δ  est l'age du message pas jour et α est le nombre de likes
        this.newsScore = 30 - ageInDays * likes;

        // On limite le score à 0 si il est négatif
        this.newsScore = Math.max(0, this.newsScore);
    }

    // GETTER

    // Ajoutez un getter pour le score de Message (retourne un double)
    public double getNewsScore() {
        return newsScore;
    }

    // Ajoutez un getter pour l'ID du Message
    public String getId() {
        return id;
    }

    // Ajoutez un getter pour le pseudo du Message
    public String getPseudo() {
        return pseudo;
    }

    // Ajoutez un getter pour le contenu du Message
    public String getContent() {
        return content;
    }

    // Ajoutez un getter pour la date du Message
    public String getDate() {
        return date;
    }

    // Ajoutez un getter pour le nombre de likes du Message
    public int getLikes() {
        return likes;
    }

    // Getter pour likedBy (liste des personnes qui ont liké le Message)
    public ArrayList<String> getLikedBy() {
        return likedBy;
    }

    // SETTER

    // Ajoutez un setter pour l'ID du Message
    public void setId(String id) {
        this.id = id;
    }

    // Setter pour les likes du Message
    public void setLikes(int likes) {
        this.likes = likes;
        calculateNewsScore();
    }

    // Setter pour likedBy (liste des personnes qui ont liké le Message)
    public void setLikedBy(ArrayList<String> likedBy) {
        this.likedBy = likedBy;
    }
}