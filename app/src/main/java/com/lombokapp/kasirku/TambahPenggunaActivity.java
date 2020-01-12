package com.lombokapp.kasirku;

import android.content.DialogInterface;
import android.content.SearchRecentSuggestionsProvider;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.lombokapp.kasirku.Model.AddUser;
import com.lombokapp.kasirku.Model.DelUser;
import com.lombokapp.kasirku.Model.ListPengguna;
import com.lombokapp.kasirku.Model.User;
import com.lombokapp.kasirku.adapter.SharedPrefManager;
import com.lombokapp.kasirku.api.APIUrl;
import com.lombokapp.kasirku.api.ApiService;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class TambahPenggunaActivity extends AppCompatActivity {
    ImageView breload;
    FloatingActionButton fbtambahpengguna;
    Dblocalhelper dbo;
    String passlama = "";
    ListView listView;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_pengguna);
        fbtambahpengguna = findViewById(R.id.fbtambahpengguna);
        listView = findViewById(R.id.lv);
//        breload = findViewById(R.id.breload);
//        dbo = new Dblocalhelper(this);
//        loaddata();
        adduser();
        getUserList();
//        reload();
    }
//    private void reload() {
//        breload.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                getUserList();
//            }
//        });
//    }
//getUserList() pakek id_perusahaan
    private void getUserList(){

        final User dian = new User();
        final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        final String id_perusahaan = user.getperusahaan();
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(APIUrl.BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        ApiService apiService = retrofit.create(ApiService.class);

        Call<List<ListPengguna>> call = apiService.getUserList(
                id_perusahaan
        );
        call.enqueue(new Callback<List<ListPengguna>>() {
            @Override
            public void onResponse(Call<List<ListPengguna>> call, Response<List<ListPengguna>> response) {
                final List<ListPengguna> listuser = response.body();
                final String[] user = new String[listuser.size()];
                for (int i = 0; i < listuser.size(); i++) {
                    if (String.valueOf(listuser.get(i).getError()).equals(false) && listuser.get(i).getSuccess() == 2) {
                        Toast.makeText(getApplicationContext(), listuser.get(i).getMessage(), Toast.LENGTH_LONG).show();
                        System.out.println("sout nya" + listuser.get(i).getMessage());
                    } else if (String.valueOf(listuser.get(i).getError()).equals(false) && listuser.get(i).getSuccess() == 1) {
                        user[i] = listuser.get(i).getUsername();
                        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

                            @Override
                            public void onItemClick(AdapterView<?> parent, View view, final int position, long id) {
                                listuser.get(position).getIdUser();
                                AlertDialog.Builder adb = new AlertDialog.Builder(TambahPenggunaActivity.this);
                                adb.setTitle("Konfirmasi");
                                adb.setMessage("Yakin Ingin Menghapus " + listuser.get(position).getUsername());
                                adb.setPositiveButton("Hapus", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        final String id_user = listuser.get(position).getIdUser();
                                        Retrofit retrofit = new Retrofit.Builder().baseUrl(APIUrl.BASE_URL)
                                                .addConverterFactory(GsonConverterFactory.create())
                                                .build();
                                        ApiService service = retrofit.create(ApiService.class);
                                        Call<DelUser> call = service.deleteUser(
                                                id_user
                                        );
                                        call.enqueue(new Callback<DelUser>() {
                                            @Override
                                            public void onResponse(Call<DelUser> call, Response<DelUser> response) {

                                                if (!response.body().getError()) {
                                                    System.out.println("id user di hapus user " + id_user);
                                                    System.out.println("berhasil hapus pengguna");
                                                    Toast.makeText(TambahPenggunaActivity.this, "Berhasil Menghapus Pengguna", Toast.LENGTH_SHORT).show();
                                                } else {
                                                    System.out.println("gagal hapus pengguna");
                                                    Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                                }
                                            }

                                            @Override
                                            public void onFailure(Call<DelUser> call, Throwable t) {
                                                System.out.println("failure");
                                                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                                            }
                                        });
                                    }
                                });

                                adb.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                    }
                                });
                                adb.show();
                                getUserList();
                            }
                        });

                    } else{

                }
                }

                listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, user));
            }

            @Override
            public void onFailure(Call<List<ListPengguna>> call, Throwable t) {
                Toast.makeText(getApplicationContext(),"Gagal Memuat Data", Toast.LENGTH_SHORT).show();
            }
        });
        //hapus user
    }
    private void adduser() {
        fbtambahpengguna.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                tambahpengguna();
                getUserList();
            }
        });

    }

    private void tambahpengguna() {
        final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
        final AlertDialog.Builder adb = new AlertDialog.Builder(TambahPenggunaActivity.this);
        adb.setTitle("Tambah Pengguna");
        adb.setCancelable(false);

        final EditText edusername = new EditText(TambahPenggunaActivity.this);

        final EditText edpasswords = new EditText(TambahPenggunaActivity.this);

        final EditText edidperusahaan = new EditText(TambahPenggunaActivity.this);
        edidperusahaan.setText(user.getperusahaan());
        edidperusahaan.setClickable(false);
        edidperusahaan.setFocusable(false);
        edidperusahaan.setFocusableInTouchMode(false);


        TextInputLayout tilusername = new TextInputLayout(TambahPenggunaActivity.this);
        tilusername.addView(edusername);
        tilusername.setHint("Username");

        TextInputLayout tilpassword = new TextInputLayout(TambahPenggunaActivity.this);
        tilpassword.addView(edpasswords);
        tilpassword.setHint("Password");

        TextInputLayout tilidperusahaan = new TextInputLayout(TambahPenggunaActivity.this);
        tilidperusahaan.addView(edidperusahaan);
        tilidperusahaan.setHint("Id Perusahaan");

        LinearLayout ll = new LinearLayout(TambahPenggunaActivity.this);
        ll.setPadding(10, 10, 10, 10);
        ll.setOrientation(LinearLayout.VERTICAL);
        ll.addView(tilusername);
        ll.addView(tilpassword);
        ll.addView(tilidperusahaan);
        adb.setView(ll);

        adb.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which ) {
                final String username = edusername.getText().toString().trim();
                final String pass=edpasswords.getText().toString().trim();
                final String id_perusahaan = edidperusahaan.getText().toString().trim();
                Retrofit retrofit = new  Retrofit.Builder().baseUrl(APIUrl.BASE_URL)
                        .addConverterFactory(GsonConverterFactory.create())
                        .build();
                ApiService service = retrofit.create(ApiService.class);
                Call<AddUser> call = service.addUser(
                        username,
                        pass,
                        id_perusahaan
                );
                call.enqueue(new Callback<AddUser>() {
                    @Override
                    public void onResponse(Call<AddUser> call, Response<AddUser> response) {
                        if(!response.body().getError()){
                            System.out.println("berhasil tambah pengguna");
                            Toast.makeText(TambahPenggunaActivity.this, "Berhasil Menambahkan Pengguna", Toast.LENGTH_SHORT).show();
                            System.out.println("ini username "+username);
                            System.out.println("ini password "+pass);
                            System.out.println("ini id perusahaan "+id_perusahaan);
                        } else {
                            System.out.println("gagal tambah pengguna");
                            System.out.println(response.body().getError());
                            Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<AddUser> call, Throwable t) {
                        System.out.println("failure");
                        Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                    }
                });
            }

        });
        adb.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        adb.show();
    }



//
//    private void loaddata() {
//
//        SQLiteDatabase db = dbo.getReadableDatabase();
//        Cursor c = db.rawQuery("SELECT kode_user,email,username,password,read_persediaan,write_persediaan, " +
//                "read_pembelian,write_pembelian,read_penjualan,write_penjualan,read_laporan,read_user FROM pengguna", null);
//
//        while (c.moveToNext()) {
//            ls.add(new Listviewglobaladapter.listglobalmodel(c.getString(0), c.getString(2), c.getString(1)));
//        }
//        ArrayAdapter<String> adapter = new Listviewglobaladapter(this, ls);
//        lvdata.setAdapter(adapter);
//        c.close();
//        db.close();
//
//
//

//getuserLIst() ga pakek id_perusahaan
//
//    private void getUserList(){
//
//        Retrofit retrofit = new Retrofit.Builder()
//                .baseUrl(APIUrl.BASE_URL)
//                .addConverterFactory(GsonConverterFactory.create())
//                .build();
//        ApiService apiService = retrofit.create(ApiService.class);
//
//        Call<List<ListPengguna>> call = apiService.getUserList();
//        call.enqueue(new Callback<List<ListPengguna>>() {
//            @Override
//            public void onResponse(Call<List<ListPengguna>> call, Response<List<ListPengguna>> response) {
//                List<ListPengguna> listuser = response.body();
//
//                String[] user = new String[listuser.size()];
//                System.out.println(response.body());
//
//                for (int i = 0; i < listuser.size(); i++) {
//                    user[i] = listuser.get(i).getUsername();
//                }
//
//                listView.setAdapter(new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_list_item_1, user));
//            }
//
//            @Override
//            public void onFailure(Call<List<ListPengguna>> call, Throwable t) {
//                Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_SHORT).show();
//            }
//        });
//    }


//    private void userop(final String kode_user) {
//        AlertDialog.Builder adb = new AlertDialog.Builder(TambahPenggunaActivity.this);
//        adb.setTitle("Tambah Pengguna");
//        adb.setCancelable(false);
//        final EditText edemail = new EditText(TambahPenggunaActivity.this);
//        edemail.setInputType(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
//        final EditText edusername = new EditText(TambahPenggunaActivity.this);
//        edemail.setInputType(TextView.AUTO_SIZE_TEXT_TYPE_UNIFORM);
//        final EditText edoldpasswords = new EditText(TambahPenggunaActivity.this);
//        edoldpasswords.setTransformationMethod(PasswordTransformationMethod.getInstance());
//        final EditText edpasswords = new EditText(TambahPenggunaActivity.this);
//        edpasswords.setTransformationMethod(PasswordTransformationMethod.getInstance());
//        final EditText edrepasswords = new EditText(TambahPenggunaActivity.this);
//        edrepasswords.setTransformationMethod(PasswordTransformationMethod.getInstance());
//
//
//        TextInputLayout tilemail = new TextInputLayout(TambahPenggunaActivity.this);
//        tilemail.addView(edemail);
//        tilemail.setHint("Email");
//
//        TextInputLayout tilusername = new TextInputLayout(TambahPenggunaActivity.this);
//        tilusername.addView(edusername);
//        tilusername.setHint("Username");
//
//        TextInputLayout tiloldpassword = new TextInputLayout(TambahPenggunaActivity.this);
//        tiloldpassword.addView(edoldpasswords);
//        tiloldpassword.setHint("Password Lama");
//
//        TextInputLayout tilpassword = new TextInputLayout(TambahPenggunaActivity.this);
//        tilpassword.addView(edpasswords);
//        if (kode_user.equals("")) {
//            tilpassword.setHint("Password");
//        } else {
//            tilpassword.setHint("Password baru");
//        }
//
//
//        TextInputLayout tilrepassword = new TextInputLayout(TambahPenggunaActivity.this);
//        tilrepassword.addView(edrepasswords);
//        tilrepassword.setHint("Konfirmasi Password");
//
//
//        final CheckBox ckread_persediaan = new CheckBox(TambahPenggunaActivity.this);
//        ckread_persediaan.setText("Lihat Persediaan");
//
//        final CheckBox ckwrite_persediaan = new CheckBox(TambahPenggunaActivity.this);
//        ckwrite_persediaan.setText("Tambah Persediaan");
//
//        final CheckBox ckread_pembelian = new CheckBox(TambahPenggunaActivity.this);
//        ckread_pembelian.setText("Lihat Pembelian");
//
//        final CheckBox ckwrite_pembelian = new CheckBox(TambahPenggunaActivity.this);
//        ckwrite_pembelian.setText("Tambah Pembelian");
//
//        final CheckBox ckread_penjualan = new CheckBox(TambahPenggunaActivity.this);
//        ckread_penjualan.setText("Lihat Penjualan");
//
//        final CheckBox ckwrite_penjualan = new CheckBox(TambahPenggunaActivity.this);
//        ckwrite_penjualan.setText("Tambah Penjualan");
//
//        final CheckBox ckread_laporan = new CheckBox(TambahPenggunaActivity.this);
//        ckread_laporan.setText("Lihat Laporan");
//
//        final CheckBox ckread_pengguna = new CheckBox(TambahPenggunaActivity.this);
//        ckread_pengguna.setText("Lihat Pengguna");
//
//
//        LinearLayout ll = new LinearLayout(TambahPenggunaActivity.this);
//        ll.setPadding(10, 10, 10, 10);
//        ll.setOrientation(LinearLayout.VERTICAL);
//        ll.addView(tilemail);
//        ll.addView(tilusername);
//        if (!kode_user.equals("")) {
//            ll.addView(tiloldpassword);
//        }
//        ll.addView(tilpassword);
//        ll.addView(tilrepassword);
//        ll.addView(ckread_persediaan);
//        ll.addView(ckwrite_persediaan);
//        ll.addView(ckread_pembelian);
//        ll.addView(ckwrite_pembelian);
//        ll.addView(ckread_penjualan);
//        ll.addView(ckwrite_penjualan);
//        ll.addView(ckread_laporan);
//        ll.addView(ckread_pengguna);
//        adb.setView(ll);
//
//        if (!kode_user.equals("")) {
//            SQLiteDatabase db = dbo.getReadableDatabase();
//            Cursor c = db.rawQuery("SELECT kode_user,email,username,password,read_persediaan,write_persediaan, " +
//                    "read_pembelian,write_pembelian,read_penjualan,write_penjualan,read_laporan,read_user FROM pengguna " +
//                    "WHERE kode_user='" + kode_user + "'", null);
//            if (c.moveToFirst()) {
//                edemail.setText(c.getString(1));
//                edusername.setText(c.getString(2));
//                passlama = c.getString(3);
//                c.getString(3);
//                if (c.getInt(4) == 1) {
//                   // ckread_persediaan.setChecked(true);
//                }
//
//                if (c.getInt(5) == 1) {
//                    ckwrite_persediaan.setChecked(true);
//                    ckread_persediaan.setChecked(true);
//                    ckread_persediaan.setEnabled(false);
//                }
//
//                if (c.getInt(6) == 1) {
//                    //ckread_pembelian.setChecked(true);
//                }
//
//                if (c.getInt(7) == 1) {
//                    ckwrite_pembelian.setChecked(true);
//                    ckread_pembelian.setChecked(true);
//                    ckread_pembelian.setEnabled(false);
//                }
//
//                if (c.getInt(8) == 1) {
//                    ckread_penjualan.setChecked(true);
//                }
//
//                if (c.getInt(9) == 1) {
//                    ckwrite_penjualan.setChecked(true);
//                    ckread_penjualan.setChecked(true);
//                    ckread_penjualan.setEnabled(false);
//                }
//
//                if (c.getInt(10) == 1) {
//                    //ckread_laporan.setChecked(true);
//                }
//
//                if (c.getInt(11) == 1) {
//                    //ckread_pengguna.setChecked(true);
//                }
//
//
//            }
//            c.close();
//            db.close();
//        }
//
//        ckwrite_persediaan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked == true) {
//                    ckread_persediaan.setChecked(true);
//                    ckread_persediaan.setEnabled(false);
//                } else {
//                    ckread_persediaan.setChecked(false);
//                    ckread_persediaan.setEnabled(true);
//                }
//            }
//        });
//
//        ckwrite_pembelian.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked == true) {
//                    ckread_pembelian.setChecked(true);
//                    ckread_pembelian.setEnabled(false);
//                } else {
//                    ckread_pembelian.setChecked(false);
//                    ckread_pembelian.setEnabled(true);
//                }
//            }
//        });
//
//        ckwrite_penjualan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
//            @Override
//            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
//                if (isChecked == true) {
//                    ckread_penjualan.setChecked(true);
//                    ckread_penjualan.setEnabled(false);
//                } else {
//                    ckread_penjualan.setChecked(false);
//                    ckread_penjualan.setEnabled(true);
//                }
//            }
//        });
//
//
//        adb.setPositiveButton("Simpan", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                if (edemail.getText().toString().equals("") ||
//                        edusername.getText().toString().equals("") ||
//                        edpasswords.getText().toString().equals("") ||
//                        edrepasswords.getText().toString().equals("")) {
//
//                    Toast.makeText(TambahPenggunaActivity.this, "Operasi Gagal, Inputan tidak boleh kosong", Toast.LENGTH_SHORT).show();
//
//                } else {
//                    if (edpasswords.getText().toString().equals(edrepasswords.getText().toString())) {
//                        SQLiteDatabase db = dbo.getWritableDatabase();
//                        try {
//                            db.beginTransaction();
//
//                            int iread_persediaan = 0, iwrite_persediaan = 0, iread_pembelian = 0, iwrite_pembelian = 0,
//                                    iread_penjualan = 0, iwrite_penjualan = 0, iread_laporan = 0, iread_pengguna = 0;
//
//
//                            if (ckread_persediaan.isChecked()) {
//                                iread_persediaan = 1;
//                            } else {
//                                iread_persediaan = 0;
//                            }
//
//                            if (ckwrite_persediaan.isChecked()) {
//                                iwrite_persediaan = 1;
//                            } else {
//                                iwrite_persediaan = 0;
//                            }
//
//                            if (ckread_pembelian.isChecked()) {
//                                iread_pembelian = 1;
//                            } else {
//                                iread_pembelian = 0;
//                            }
//
//                            if (ckwrite_pembelian.isChecked()) {
//                                iwrite_pembelian = 1;
//                            } else {
//                                iwrite_pembelian = 0;
//                            }
//
//                            if (ckread_penjualan.isChecked()) {
//                                iread_penjualan = 1;
//                            } else {
//                                iread_penjualan = 0;
//                            }
//
//                            if (ckwrite_penjualan.isChecked()) {
//                                iwrite_penjualan = 1;
//                            } else {
//                                iwrite_penjualan = 0;
//                            }
//
//                            if (ckread_laporan.isChecked()) {
//                                iread_laporan = 1;
//                            } else {
//                                iread_laporan = 0;
//                            }
//
//                            if (ckread_pengguna.isChecked()) {
//                                iread_pengguna = 1;
//                            } else {
//                                iread_pengguna = 0;
//                            }
//
//
//                            if (kode_user.equals("")) {
//                                String query = "INSERT INTO pengguna(email,username,password,read_persediaan,write_persediaan," +
//                                        "read_pembelian,write_pembelian,read_penjualan,write_penjualan," +
//                                        "read_laporan,read_user)" +
//                                        "VALUES('" + edemail.getText().toString() + "'," +
//                                        "'" + edusername.getText().toString() + "'," +
//                                        "'" + edpasswords.getText().toString() + "'," + iread_persediaan + "," +
//                                        "" + iwrite_persediaan + "," + iread_pembelian + "," + iwrite_pembelian + "," +
//                                        "" + iread_penjualan + "," + iwrite_penjualan + "," + iread_laporan + "," + iread_pengguna + ")";
//                                db.execSQL(query);
//                                Toast.makeText(TambahPenggunaActivity.this, "Data Berhasil Disimpan", Toast.LENGTH_SHORT).show();
//                            } else {
//                                if (passlama.equals(edoldpasswords.getText().toString())) {
//                                    String query = "UPDATE pengguna  SET email='" + edemail.getText().toString() + "'," +
//                                            "username='" + edusername.getText().toString() + "'," +
//                                            "password='" + edpasswords.getText().toString() + "',read_persediaan=" + iread_persediaan + "," +
//                                            "write_persediaan=" + iwrite_persediaan + ",read_pembelian=" + iread_pembelian + ",write_pembelian=" + iwrite_pembelian + "," +
//                                            "read_penjualan=" + iread_penjualan + ",write_penjualan=" + iwrite_penjualan + ",read_laporan=" + iread_laporan + ",read_user=" + iread_pengguna + " " +
//                                            "WHERE kode_user='" + kode_user + "'";
//                                    db.execSQL(query);
//                                    Toast.makeText(TambahPenggunaActivity.this, "Data Berhasil Disimpan", Toast.LENGTH_SHORT).show();
//                                } else {
//                                    Toast.makeText(TambahPenggunaActivity.this, "Data Gagal Disimpan,Password Lama Tidak Cocok", Toast.LENGTH_SHORT).show();
//                                }
//
//                            }
//                            db.setTransactionSuccessful();
//
//                        } catch (Exception ex) {
//                            ex.printStackTrace();
//                        } finally {
//                            db.endTransaction();
//                            db.close();
//                        }
////                        loaddata();
//                    } else {
//                        Toast.makeText(TambahPenggunaActivity.this, "Data Gagal Disimpan, Konfirmasi Password Tidak Cocok", Toast.LENGTH_SHORT).show();
//                    }
//                }
//            }
//        });
//
//        adb.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.dismiss();
//            }
//        });
//
//        adb.show();
//    }
//

}
