package com.example.projet_sae32;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

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

    public void likeMessage(){
        //like un message
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