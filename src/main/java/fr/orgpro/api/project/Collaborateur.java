package fr.orgpro.api.project;

public class Collaborateur implements CollaborateurInterface {

    private String nom;

    public Collaborateur() {
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getNom() {
        return nom;
    }
}
