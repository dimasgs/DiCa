package com.lombokapp.kasirku.Model;

public class User {
    private String iduser;
    private String perusahaan;
    private String nama_p;
    private String alamat;
    private String no_hp;
    private String email;
    private String website;
    private String nama;
    private String status;

//    public User (String nama_p, String alamat, String email, String website, String no_hp){
//        this.nama_p = nama_p;
//        this.alamat = alamat;
//        this.email = email;
//        this.website = website;
//        this.no_hp = no_hp;
//    }

    public User(String iduser, String perusahaan, String nama_p, String alamat, String no_hp,  String email, String website, String nama, String status) {
        this.iduser=iduser;
        this.perusahaan = perusahaan;
        this.nama_p=nama_p;
        this.alamat=alamat;
        this.no_hp=no_hp;
        this.email = email;
        this.website = website;
        this.nama = nama;
        this.status = status;
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
    public void setAlamat(String alamat){
        this.alamat= alamat;
    }


    public String getNoHp(){
        return no_hp;
    }
    public void setNoHp(String noHp) {
        this.no_hp=noHp;
    }

    public String getWebsite(){
        return website;
    }
    public void setWebsite(String website){
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

    public String getIduser(){
        return iduser;
    }

    public void setIduser(String iduser){
        this.iduser = iduser;
    }

    public String getStatus(){
        return status;
    }

    public void setStatus(String status){
        this.status = status;
    }


}
