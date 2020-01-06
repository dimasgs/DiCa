package com.lombokapp.kasirku.Model;

public class User {
    private String perusahaan;
    private String nama_p;
    private String alamat;
    private String no_hp;
    private String email;
    private String website;
    private String nama;
    private String pass;

    public User (String nama_p, String alamat, String email, String website, String no_hp){
        this.nama_p = nama_p;
        this.alamat = alamat;
        this.email = email;
        this.website = website;
        this.no_hp = no_hp;
    }

    public User(String perusahaan, String nama, String email) {
        this.perusahaan = perusahaan;
        this.nama = nama;
        this.email = email;
    }

    public User() {

    }

//
//    public  User (int id, String nama, String email, String password){
//        this.id = id;
//        this.nama = nama;
//        this.email = email;
//        this.password = password;
//    }

    public String getperusahaan() {
        return perusahaan;
    }

    public String getNama_p() {
        return nama_p;
    }
    public void setNama_p(String nama_p){
        this.nama_p = nama_p;
    }


    public String getAlamat(){
        return alamat;
    }

    public String getNo_hp(){
        return no_hp;
    }

    public String getWebsite(){
        return website;
    }

    public String getNama(){
        return nama;
    }

    public void setNama(String nama){
        this.nama = nama;
    }


    public String getEmail() {
        return email;
    }

}
