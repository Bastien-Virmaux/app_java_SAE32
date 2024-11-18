package com.example.projet_sae32;

public class Message {
    private String pseudo;
    private String content;
    private String date;
    private int likes;

    public Message(String pseudo, String content, String date) {
        this.pseudo = pseudo;
        this.content = content;
        this.date = date;
        this.likes = 0;
    }

    // Getters
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
}