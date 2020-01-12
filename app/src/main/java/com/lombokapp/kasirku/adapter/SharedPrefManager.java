package com.lombokapp.kasirku.adapter;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.lombokapp.kasirku.LoginActivity;
import com.lombokapp.kasirku.Model.Login;
import com.lombokapp.kasirku.Model.Logout;
import com.lombokapp.kasirku.Model.User;

public class SharedPrefManager {

    private static SharedPrefManager mInstance;
    private static Context mCtx;

    private static final String SHARED_PREF_NAME = "kasirku";

    private static final String KEY_USER_ID_USER = "keyuseriduser";
    private static final String KEY_USER_ID_PERUSAHAAN = "keyuseridperusahaan";
    private static final String KEY_USER_NAME_PERUSAHAAN = "keynamaperusahaan";
    private static final String KEY_USER_EMAIL = "keyuseremail";
    private static final String KEY_USER_HP = "keyuserhp";
    private static final String KEY_USER_ALAMAT = "keyuseralamat";
    private static final String KEY_USER_WEBSITE = "keyuserwebsite";
    private static final String KEY_USER_NAME ="keyusername" ;
    private static final String KEY_USER_PASS = "keyuserpass";
    private static final String KEY_USER_STATUS = "keyuserstatus";

    SharedPreferences sp;

    private SharedPrefManager(Context context) {
        mCtx = context;
    }

    public static synchronized SharedPrefManager getInstance(Context context) {
        if (mInstance == null) {
            mInstance = new SharedPrefManager(context);
        }
        return mInstance;
    }

    public void userLogin(User user) {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_ID_PERUSAHAAN, user.getperusahaan());
        editor.putString(KEY_USER_NAME, user.getNama());
        editor.putString(KEY_USER_ID_USER, user.getIduser());
        editor.putString(KEY_USER_STATUS,user.getStatus());
        editor.apply();
    }

    public User getUser() {
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        return new User(
                sharedPreferences.getString(KEY_USER_ID_USER,null),
                sharedPreferences.getString(KEY_USER_ID_PERUSAHAAN,null),
                sharedPreferences.getString(KEY_USER_NAME_PERUSAHAAN,null),
                sharedPreferences.getString(KEY_USER_ALAMAT,null),
                sharedPreferences.getString(KEY_USER_HP,null),
                sharedPreferences.getString(KEY_USER_EMAIL,null),
                sharedPreferences.getString(KEY_USER_WEBSITE,null),
                sharedPreferences.getString(KEY_USER_NAME,null),
                sharedPreferences.getString(KEY_USER_STATUS,null)
        );
    }
    public void dataperusahaan(User user){
        SharedPreferences sharedPreferences = mCtx.getSharedPreferences(SHARED_PREF_NAME, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.putString(KEY_USER_NAME_PERUSAHAAN, user.getNamaPerusahaan());
        editor.putString(KEY_USER_ALAMAT, user.getAlamat());
        editor.putString(KEY_USER_EMAIL, user.getEmail());
        editor.putString(KEY_USER_WEBSITE, user.getWebsite());
        editor.putString(KEY_USER_HP, user.getNoHp());
        editor.putString(KEY_USER_NAME_PERUSAHAAN, user.getNamaPerusahaan());
        editor.apply();
    }
}
