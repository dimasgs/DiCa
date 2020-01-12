package com.lombokapp.kasirku;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.lombokapp.kasirku.Model.User;
import com.lombokapp.kasirku.adapter.SharedPrefManager;

public class Pengguna extends AppCompatActivity {

    LinearLayout btnLinkToListUser;
    TextView profile_nama;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengguna);

        profile_nama = findViewById(R.id.profile_nama);
        btnLinkToListUser =findViewById(R.id.btnLinkToListUser);

        User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();

        profile_nama.setText(user.getNama());


        btnLinkToListUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                final String status = SharedPrefManager.getInstance(getApplicationContext()).getUser().getStatus();
                if (status.equals("1")) {
                    System.out.println("Kamu Admin");
                    startActivity(new Intent(getApplicationContext(), TambahPenggunaActivity.class));
                } else {
                    Toast.makeText(Pengguna.this, "Proses Ditolak, Anda tidak memiliki akses", Toast.LENGTH_SHORT).show();
                    System.out.println("Kamu Kasir");
                }
            }
            });

    }
}
