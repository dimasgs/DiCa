package com.lombokapp.kasirku.api;

import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;
import com.lombokapp.kasirku.Model.Result;

public interface ApiService {
//    @FormUrlEncoded
//    @POST("regis")
//    Call<Result> createUser(
//            @Field("nama_p") String nama_p,
//            @Field("alamat") String alamat,
//            @Field("no_hp") String no_hp,
//            @Field("email") String email,
//            @Field("website") String website,
//            @Field("username") String username,
//            @Field("pass") String pass);

    @FormUrlEncoded
    @POST("login.php")
    Call<Result> userLogin(
            // keperluan login
            @Field("username") String username,
            @Field("pass") String pass,
            @Field("perusahaan") String perusahaan);

    @FormUrlEncoded
    @POST("data_perusahaan.php")
    Call<Result> updateUsaha(
            // keperluan update nama perusahaan
            @Field("nama_perusahan") String nama_perusahaan,
            @Field("alamat") String alamat,
            @Field("email") String email,
            @Field("website") String website,
            @Field("no_hp") String no_hp);
}
