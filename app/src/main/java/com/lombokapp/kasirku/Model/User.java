package com.lombokapp.kasirku.Model;

public class User {
    private String perusahaan;
    private String nama_p;
    private String alamat;
    private int no_hp;
    private String email;
    private String website;
    private String nama;
   // private String pass;

    public User (String nama_p, String alamat, int no_hp,String website){
        this.nama_p = nama_p;
        this.alamat = alamat;
        this.no_hp = no_hp;
        this.website = website;
        //this.pass = pass;
    }

    public User(String perusahaan, String nama, String email) {
        this.perusahaan = perusahaan;
        this.nama = nama;
        this.email = email;
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


    public String getAlamat(){
        return alamat;
    }

    public int getNo_hp(){
        return no_hp;
    }

    public String getWebsite(){
        return website;
    }

    public String getNama(){
        return nama;
    }

    public String getEmail() {
        return email;
    }



//    public String getPass() {
//        return pass;
//    }

}
