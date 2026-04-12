package com.example.delestage; // Vérifie bien ton package

public class Signalement {
    // Variables (doivent correspondre aux noms dans Firebase)
    private String id;
    private String auteur;
    private String ville;
    private String dateHeure; //
    private String type;
    private int confirmations;
    private int contestations;

    // Constructeur vide (Obligatoire pour Firebase)
    public Signalement() {
    }

    // Constructeur complet
    public Signalement(String id, String auteur, String ville, String temps, String type, int confirmations, int contestations , String dateHeure) {
        this.id=id;
        this.auteur = auteur;
        this.ville = ville;
        this.dateHeure = dateHeure;
        this.type = type;
        this.confirmations = confirmations;
        this.contestations = contestations;

    }

    // --- LES GETTERS (Ceux qui enlèvent le rouge dans l'Adapter) ---
    public String getId(){
        return id;
    }


    public String getAuteur() {
        return auteur;
    }

    public String getVille() {
        return ville;
    }

    public String getDateHeure() {
        return dateHeure;
    }

    public String getType() {
        return type;
    }

    public int getConfirmations() {
        return confirmations;
    }

    public int getContestations() {
        return contestations;
    }

    // --- LES SETTERS (Utiles pour modifier les données) ---
    public void setId(String id){
        this.id=id;
    }

    public void setAuteur(String auteur) {
        this.auteur = auteur;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public void setTemps(String dateHeure) {
        this.dateHeure = dateHeure;
    }

    public void setType(String type) {
        this.type = type;
    }

    public void setConfirmations(int confirmations) {
        this.confirmations = confirmations;
    }

    public void setContestations(int contestations) {
        this.contestations = contestations;
    }


}