package com.example.projet_sae32;

import java.util.ArrayList;

public class Utilisateur {
    private String nom;
    private String prenom;
    private String email;
    private ArrayList<String> utilisateurs;
    public ArrayList<ArrayList<String>> utilisateursTab;

    //constructeur de la classe Utilisateur
    Utilisateur(String nom, String prenom, String email){
        this.nom = nom;
        this.prenom = prenom;
        this.email = email;
    }

    //creation des utilisateur
    public void createUser(){
        System.out.println(nom);
        System.out.println(prenom);
        System.out.println(email);
    }
}
