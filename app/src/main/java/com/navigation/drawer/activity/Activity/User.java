package com.navigation.drawer.activity.Activity;

/**
 * Created by amine on 4/16/2017.
 */

public class User {
    String id,password,nom,prenom,sexe,email,tel,num_permi,date_permi;

    public User(String id, String password, String nom, String prenom, String sexe, String email, String tel, String num_permi, String date_permi) {
        this.id = id;
        this.password = password;
        this.nom = nom;
        this.prenom = prenom;
        this.sexe = sexe;
        this.email = email;
        this.tel = tel;
        this.num_permi = num_permi;
        this.date_permi = date_permi;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getNom() {
        return nom;
    }

    public void setNom(String nom) {
        this.nom = nom;
    }

    public String getPrenom() {
        return prenom;
    }

    public void setPrenom(String prenom) {
        this.prenom = prenom;
    }

    public String getSexe() {
        return sexe;
    }

    public void setSexe(String sexe) {
        this.sexe = sexe;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getTel() {
        return tel;
    }

    public void setTel(String tel) {
        this.tel = tel;
    }

    public String getNum_permi() {
        return num_permi;
    }

    public void setNum_permi(String num_permi) {
        this.num_permi = num_permi;
    }

    public String getDate_permi() {
        return date_permi;
    }

    public void setDate_permi(String date_permi) {
        this.date_permi = date_permi;
    }

    public User() {

    }
}
