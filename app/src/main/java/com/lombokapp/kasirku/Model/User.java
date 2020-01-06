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

    public User(String nama_p, String perusahaan, String nama, String email) {
        this.nama_p=nama_p;
        this.perusahaan = perusahaan;
        this.nama = nama;
        this.email = email;
    }

    public User() {

    }


    public String getperusahaan() {
        return perusahaan;
    }
    public void setPerusahaan(String perusahaan){
        this.perusahaan = perusahaan;
    }

    public String getNamaPerusahaan() {
        return nama_p;
    }
    public void setNamaPerusahaan(String nama_p){
        this.nama_p = nama_p;
    }


    public String getAlamat(){
        return alamat;
    }
    public void setAlamat(){
        this.alamat=alamat;
    }


    public String getNo_hp(){
        return no_hp;
    }

    public String getWebsite(){
        return website;
    }
    public void setWebsite(){
        this.website = website;
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
    public void setEmail(String email){
        this.email = email;

    }

}
