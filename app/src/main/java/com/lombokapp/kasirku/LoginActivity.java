package com.lombokapp.kasirku;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.lombokapp.kasirku.Model.Login;
import com.lombokapp.kasirku.Model.Result;
import com.lombokapp.kasirku.Model.User;
import com.lombokapp.kasirku.adapter.SharedPrefManager;
import com.lombokapp.kasirku.api.ApiService;
import com.lombokapp.kasirku.api.APIUrl;

import java.io.File;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends AppCompatActivity {
    public User dian = new User();
    EditText edusername,edpassword,edid_perusahaan;
    Button bmasuk;
    Context mContext;
    Dblocalhelper dbo;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        dbo=new Dblocalhelper(this);
        edid_perusahaan=findViewById(R.id.edid_perusahaan);
        edusername=findViewById(R.id.edusername);
        edpassword=findViewById(R.id.edpassword);
        bmasuk=findViewById(R.id.bmasuk);
        loadpermission();
        login();
        checkConnection();
        mContext = this;
        SQLiteDatabase db = dbo.getWritableDatabase();
        db.beginTransaction();
        db.endTransaction();
        db.close();

    }

    protected boolean isOnline() {
        ConnectivityManager cm = (ConnectivityManager)getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo netInfo = cm.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnectedOrConnecting()) {
            return true;
        } else {
            return false;
        }
    }
    public void checkConnection(){
        if(!isOnline()){
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setMessage("Ponsel tidak terhubung internet")
                    .setCancelable(false)
                    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int id) {
                            finish();
                        }
                    });
            AlertDialog alert = builder.create();
            alert.show();
        }
    }
    private void loadpermission(){
        if (
                        ActivityCompat.checkSelfPermission(this, android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED  ||
                        ActivityCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                        ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                            android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            android.Manifest.permission.CAMERA,
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
            File kasiroffbackup=new File(Environment.getExternalStorageDirectory(),"kasirkubackup");
            if(!kasiroffbackup.exists()){
                kasiroffbackup.mkdirs();
            }
            return;
        }

    }



    private void login() {
        final ProgressDialog progressDialog = new ProgressDialog(this);
        progressDialog.setMessage("Mohon Tunggu...");


        bmasuk.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                final String username = edusername.getText().toString().trim();
                final String pass = edpassword.getText().toString().trim();
                final String perusahaan = edid_perusahaan.getText().toString().trim();


                progressDialog.show();

                Retrofit retrofit = new Retrofit.Builder()
                        .baseUrl(APIUrl.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiService apiService = retrofit.create(ApiService.class);

                Call<Login> call = apiService.userLogin(
                        username,
                        pass,
                        perusahaan
                );
                call.enqueue(new Callback<Login>() {
                    @Override
                    public void onResponse(Call<Login> call, Response<Login> response) {
                        System.out.println(response.body());
                        if (!response.body().getError() && response.body().getSuccess()==1) {
                            progressDialog.dismiss();
                            finish();
                            System.out.println("Berhasil");
                            dian.setNama(username);
                            dian.setPerusahaan(perusahaan);
                            dian.setStatus(response.body().getStatus());
                            dian.setIduser(response.body().getId());
                            System.out.println("ini status " + response.body().getStatus());
                            System.out.println("ini id dari response " + response.body().getId());
                            System.out.println("ini id dari user.java :" + dian.getIduser());
                            System.out.println(perusahaan);
                            SharedPrefManager.getInstance(getApplicationContext()).userLogin(dian);
                            startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        } else if (response.body().getSuccess()==2){
                            progressDialog.dismiss();
                            Toast.makeText(getApplicationContext(), "Anda login di device lain, silahkan logout dulu di device tersebut", Toast.LENGTH_LONG).show();
                            System.out.println("response skses 2 "+response.body().getMessage());
                        } else {
                            progressDialog.dismiss();
                            System.out.println("error");
                            Toast.makeText(getApplicationContext(), "Email Atau Kata Sandi Salah", Toast.LENGTH_LONG).show();
                        }
                    }

                    @Override
                    public void onFailure(Call<Login> call, Throwable t) {
                        progressDialog.dismiss();
                        System.out.println("ini failure");
                        System.out.println(pass);
                        //Toast.makeText(getApplicationContext(), "Gagal Login", Toast.LENGTH_LONG).show();
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });

            }
        });
    }
}
