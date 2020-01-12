package com.lombokapp.kasirku.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;

import com.lombokapp.kasirku.Model.AddUser;
import com.lombokapp.kasirku.Model.DelUser;
import com.lombokapp.kasirku.Model.ListPengguna;
import com.lombokapp.kasirku.Model.Login;
import com.lombokapp.kasirku.Model.Logout;
import com.lombokapp.kasirku.Model.Perusahaan;
import com.lombokapp.kasirku.Model.Result;
import com.lombokapp.kasirku.Model.User;

import java.util.List;

public interface ApiService {
    @FormUrlEncoded
    @POST("tambahPengguna.php")
    Call<AddUser> addUser(
            @Field("username") String username,
            @Field("pass") String pass,
            @Field("id_perusahaan") String id_perusahaan);

    @FormUrlEncoded
    @POST("login.php")
    Call<Login> userLogin(
                    // keperluan login
                    @Field("username") String username,
                    @Field("pass") String pass,
                    @Field("perusahaan") String perusahaan);
    @FormUrlEncoded
    @POST("logout.php")
    Call<Logout> userLogout(
            // keperluan login
            @Field("id") String id);

    @FormUrlEncoded
    @POST("viewListPengguna.php")
    Call<ListPengguna> viewPengguna(
            @Field("id_perusahaa") String id_perusahaan);

//getuserlist() pakek id_perusahaan
    @FormUrlEncoded
    @POST("get.php")
    Call<List<ListPengguna>>getUserList(
            @Field("id_perusahaan") String id_perusahaan
    );

    @FormUrlEncoded
    @POST("hapusPengguna.php")
    Call<DelUser>deleteUser(
            @Field("id_user") String id_user
    );

    @FormUrlEncoded
    @POST("data_perusahaan.php")
    Call<Perusahaan> viewusaha(
            @Field("id_perusahaan") String id_perusahaan);
}
