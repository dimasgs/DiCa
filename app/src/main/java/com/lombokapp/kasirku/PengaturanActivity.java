package com.lombokapp.kasirku;

import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;
import android.support.design.widget.TextInputLayout;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.lombokapp.kasirku.Model.Login;
import com.lombokapp.kasirku.Model.Logout;
import com.lombokapp.kasirku.Model.Perusahaan;
import com.lombokapp.kasirku.Model.Result;
import com.lombokapp.kasirku.Model.User;
import com.lombokapp.kasirku.adapter.SharedPrefManager;
import com.lombokapp.kasirku.api.ApiService;
import com.lombokapp.kasirku.api.APIUrl;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import okhttp3.Cache;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PengaturanActivity extends AppCompatActivity {
    public User dian = new User();
    ListView lvdata;
    Dblocalhelper dbo;
    int printcount = 1;
    SharedPreferences sp;
    SharedPreferences.Editor ed;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_pengaturan);

        lvdata = findViewById(R.id.lvdata);
        sp=getApplicationContext().getSharedPreferences("config",0);
        ed=sp.edit();
        dbo = new Dblocalhelper(this);
        loaddatalist();

    }



    private void loaddatalist() {
        List<Listviewglobaladapter.listglobalmodel> ls = new ArrayList<>();
        ls.add(new Listviewglobaladapter.listglobalmodel("0", "Profil Usaha", "Deskripsikan informasi usaha sebagai info untuk pelanggan anda"));
        ls.add(new Listviewglobaladapter.listglobalmodel("1", "Profile Pengguna", "Atur siapa saja yang boleh menggunakan aplikasi beserta hak aksesnya"));
        ls.add(new Listviewglobaladapter.listglobalmodel("2", "Atur Printer POS", "Atur printer yang anda gunakan untuk mencetak Struck, aplikasi hanya bisa menggunakan printer yang memiliki koneksi bluetooth, Printer Saat ini "+sp.getString("default_printer","none")));
        ls.add(new Listviewglobaladapter.listglobalmodel("3", "Test Koneksi Printer", "Cek apakah printer anda sudah terkoneksi dan berfungsi dengan baik"));
        ls.add(new Listviewglobaladapter.listglobalmodel("4", "Backup", "Cadangkan data anda untuk mengantisipasi kemungkinan data terhapus, default backup file ada pada folder dicabackup di internal storage anda"));
        ls.add(new Listviewglobaladapter.listglobalmodel("5", "Restore", "Pulihkan data yang sudah anda cadangkan, default restore file harus ada di dalam folder dicabackup di internal storage anda, pastikan data yang ingin anda pulihkan berada didalam folder tersebut "));
        ls.add(new Listviewglobaladapter.listglobalmodel("6", "Tentang Aplikasi", "DiCa (Digital Cashier) V1.0 \nPengembang : Digital Geek Indonesia \nWebsite : digitalgeekid.com"));
        ls.add(new Listviewglobaladapter.listglobalmodel("7", "Keluar Aplikasi",""));
        final ArrayAdapter<String> adapter = new Listviewglobaladapter(this, ls);
        lvdata.setAdapter(adapter);

        lvdata.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, final long id) {

                if (position == 0) {
                    final AlertDialog.Builder adb = new AlertDialog.Builder(PengaturanActivity.this);
                    adb.setTitle("Informasi Perusahaan");
                    adb.setCancelable(false);
                    final EditText ednama_usaha = new EditText(PengaturanActivity.this);
                    final EditText edalamat_usaha = new EditText(PengaturanActivity.this);
                    final EditText edemail_usaha = new EditText(PengaturanActivity.this);
                    final EditText edwebsite = new EditText(PengaturanActivity.this);
                    final EditText ednohp_usaha = new EditText(PengaturanActivity.this);


                    final User dian = new User();
                    final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                    final String id_perusahaan = user.getperusahaan();
                    Retrofit retrofit = new Retrofit.Builder().baseUrl(APIUrl.BASE_URL)
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();
                    ApiService service = retrofit.create(ApiService.class);
                    Call<Perusahaan> call = service.viewusaha(
                            id_perusahaan
                            );
                    call.enqueue(new Callback<Perusahaan>() {
                        @Override
                        public void onResponse(Call<Perusahaan> call, Response<Perusahaan> response) {
                            if (!response.body().getError()){
                                //set get
                                dian.setNamaPerusahaan(response.body().getNamaPerusahaan());
                                dian.setAlamat(response.body().getAlamat());
                                dian.setNoHp(response.body().getNoHp());
                                dian.setEmail(response.body().getEmail());
                                dian.setWebsite(response.body().getWebsite());
                                System.out.println("berhasil pengaturan");
                                //sout
                                System.out.println(dian.getNamaPerusahaan());
                                System.out.println(dian.getAlamat());
                                System.out.println(dian.getNoHp());
                                System.out.println(dian.getEmail());
                                System.out.println(dian.getWebsite());

                                ednama_usaha.setText(dian.getNamaPerusahaan());
                                ednama_usaha.setFocusable(false);
                                ednama_usaha.setFocusableInTouchMode(false);
                                ednama_usaha.setClickable(false);

                                edalamat_usaha.setText(dian.getAlamat());
                                edalamat_usaha.setFocusable(false);
                                edalamat_usaha.setFocusableInTouchMode(false);
                                edalamat_usaha.setClickable(false);

                                ednohp_usaha.setText(dian.getNoHp());
                                ednohp_usaha.setFocusable(false);
                                ednohp_usaha.setFocusableInTouchMode(false);
                                ednohp_usaha.setClickable(false);

                                edemail_usaha.setText(dian.getEmail());
                                edemail_usaha.setFocusable(false);
                                edemail_usaha.setFocusableInTouchMode(false);
                                edemail_usaha.setClickable(false);

                                edwebsite.setText(dian.getWebsite());
                                edwebsite.setFocusable(false);
                                edwebsite.setFocusableInTouchMode(false);
                                edwebsite.setClickable(false);
                                SharedPrefManager.getInstance(getApplicationContext()).userLogin(dian);
                                //Toast.makeText(PengaturanActivity.this, "Informasi Berhasil Diperbaharui", Toast.LENGTH_SHORT).show();
                            }else {
                                System.out.println("gagal pengaturan");
                                System.out.println(response.body().getError());
                                Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                            }
                        }

                        @Override
                        public void onFailure(Call<Perusahaan> call, Throwable t) {
                            System.out.println("failure");
                            Toast.makeText(getApplicationContext(), t.getMessage(), Toast.LENGTH_LONG).show();
                        }
                    });

                    TextInputLayout tilnama_usaha = new TextInputLayout(PengaturanActivity.this);
                    tilnama_usaha.addView(ednama_usaha);
                    tilnama_usaha.setHint("Nama Usaha");


                    TextInputLayout tilalamat_usaha = new TextInputLayout(PengaturanActivity.this);
                    tilalamat_usaha.addView(edalamat_usaha);
                    tilalamat_usaha.setHint("Alamat");


                    TextInputLayout tilnohp_usaha = new TextInputLayout(PengaturanActivity.this);
                    tilnohp_usaha.addView(ednohp_usaha);
                    tilnohp_usaha.setHint("No Handphone");


                    TextInputLayout tilweb = new TextInputLayout(PengaturanActivity.this);
                    tilweb.addView(edwebsite);
                    tilweb.setHint("Website");


                    TextInputLayout tilemail = new TextInputLayout(PengaturanActivity.this);
                    tilemail.addView(edemail_usaha);
                    tilemail.setHint("Email");

                    LinearLayout ll = new LinearLayout(PengaturanActivity.this);
                    ll.setPadding(10, 10, 10, 10);
                    ll.setOrientation(LinearLayout.VERTICAL);
                    ll.addView(tilnama_usaha);
                    ll.addView(tilalamat_usaha);
                    ll.addView(tilnohp_usaha);
                    ll.addView(tilemail);
                    ll.addView(tilweb);
                    adb.setView(ll);

                    adb.setNegativeButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    adb.show();
                } else if (position == 1) {
                        Intent in = new Intent(PengaturanActivity.this, Pengguna.class);
                        startActivity(in);
//                        System.out.println("Kamu Admin");
//                    } else {
//                    }

                } else if (position == 2) {
                    final BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
                    BluetoothDevice bdev;
                    if (btadapter.isEnabled()) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(PengaturanActivity.this);
                        adb.setTitle("Pilih Perangkat");
                        adb.setCancelable(false);

                        Set<BluetoothDevice> paireddevice = btadapter.getBondedDevices();
                        final List<String> ls = new ArrayList<>();
                        ls.add("none");
                        for (BluetoothDevice btdev : paireddevice) {
                            ls.add(btdev.getName());

                        }
                        int currentprinter = ls.indexOf(sp.getString("default_printer","none"));
                        final String[] isi = ls.toArray(new String[ls.size()]);
                        adb.setSingleChoiceItems(isi, currentprinter, new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                SQLiteDatabase db = dbo.getWritableDatabase();
                                db.beginTransaction();
                                try {
                                    db.execSQL("UPDATE pengaturan SET default_printer='" + isi[which] + "' WHERE id=1");
                                    db.setTransactionSuccessful();
                                    Toast.makeText(PengaturanActivity.this, isi[which], Toast.LENGTH_SHORT).show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                } finally {
                                    db.endTransaction();
                                    db.close();
                                }
                                ed.putString("default_printer",isi[which].split("---")[0]);
                                ed.apply();


                            }
                        });
                        adb.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        adb.show();
                    } else {
                        Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(in, 5);
                    }

                } else if (position == 3) {
                    BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
                    if(btadapter.isEnabled()){
                        if (sp.getString("default_printer","none").equals("none")) {
                            Toast.makeText(PengaturanActivity.this, "Tidak ada Printer", Toast.LENGTH_SHORT).show();
                        } else {
                            if (printcount > 1) {
                                Toast.makeText(PengaturanActivity.this, "Printer Siap Dipakai", Toast.LENGTH_SHORT).show();
                            } else {
                                printcount = 2;
                                String textprint = "Aplikasi DiCa (Digital Cashier)\nVersi 1.0\nBy Digital Geek Indonesia \n\n\n";
                                byte[] bt = textprint.getBytes();
                                Toast.makeText(PengaturanActivity.this, String.valueOf(bt.length), Toast.LENGTH_SHORT).show();
                                Bluetoothprint bprint = new Bluetoothprint(PengaturanActivity.this);
                                bprint.print(textprint);
                            }
                        }
                    }else{
                        Intent in = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                        startActivityForResult(in, 5);
                    }


                } else if (position == 4) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(PengaturanActivity.this);
                    adb.setCancelable(false);
                    adb.setTitle("Informasi");
                    adb.setMessage("Data yang dibackup akan dipindah otomatis ke dalam folder dicabackup di internal storage anda, " +
                            "ingat untuk tidak menghapus folder ini," +
                            "data backup bisa anda pindahkan ke Flashdisk, SDCARD atau sejenisnya dengan mengcopy file tersebut, " +
                            "dan untuk merestorenya anda cukup mengcopy file backupnnya ke dalam folder dicabackup ");
                    adb.setPositiveButton("Backup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            File kasiroffbackup = new File(Environment.getExternalStorageDirectory(), "dicabackup");
                            if (ActivityCompat.checkSelfPermission(PengaturanActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(PengaturanActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(PengaturanActivity.this,
                                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                android.Manifest.permission.CAMERA}, 1);
                                if (!kasiroffbackup.exists()) {
                                    kasiroffbackup.mkdirs();
                                }
                                return;
                            }

                            if (!kasiroffbackup.exists()) {
                                kasiroffbackup.mkdirs();
                            }

                            File lokasidb = getDatabasePath("dica.db");
                            File lokasibackupdb = new File(Environment.getExternalStorageDirectory(), "dicabackup/dica.db");
                            File lokasiimage = new File(getFilesDir(), "dicaimage");
                            File lokasibackupimage = new File(Environment.getExternalStorageDirectory(), "dicabackup/dicaimage");
                            try {
                                Oneforallfunc.copyfile(lokasidb, lokasibackupdb);
                                Oneforallfunc.copyfile(lokasiimage, lokasibackupimage);
                                Toast.makeText(PengaturanActivity.this, "Backup Berhasil", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        }
                    });
                    adb.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    adb.show();


                } else if (position == 5) {
                    AlertDialog.Builder adb = new AlertDialog.Builder(PengaturanActivity.this);
                    adb.setCancelable(false);
                    adb.setTitle("Informasi");
                    adb.setMessage("Pastikan folder dicabackup tidak terhapus di internal storage anda, jika tidak ada atau terhapus, " +
                            "anda bisa membuatnya dengan file explorer atau dengan melakukan backup pada pengaturan, " +
                            "dan pastikan data yang ingin anda restore berada didalam folder tersebut, jika datanya sudah dipindah ke Flashdisk, SDCARD atau sejenisnya" +
                            " maka copy dulu datanya ke dalam folder dicabackup sebelum melakukan restore");
                    adb.setPositiveButton("Restore", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if (ActivityCompat.checkSelfPermission(PengaturanActivity.this, android.Manifest.permission.READ_EXTERNAL_STORAGE)
                                    != PackageManager.PERMISSION_GRANTED &&
                                    ActivityCompat.checkSelfPermission(PengaturanActivity.this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                                            != PackageManager.PERMISSION_GRANTED) {
                                ActivityCompat.requestPermissions(PengaturanActivity.this,
                                        new String[]{android.Manifest.permission.READ_EXTERNAL_STORAGE,
                                                android.Manifest.permission.WRITE_EXTERNAL_STORAGE,
                                                android.Manifest.permission.CAMERA}, 1);
                                return;
                            }

                            File lokasidb = getDatabasePath("dica.db");
                            File lokasibackupdb = new File(Environment.getExternalStorageDirectory(), "dicabackup/dica.db");
//                            File lokasiimage = new File(getFilesDir(), "dicaimage");
//                            File lokasibackupimage = new File(Environment.getExternalStorageDirectory(), "dicabackup/dicaimage");
                            try {
                                Oneforallfunc.copyfile(lokasibackupdb, lokasidb);
                                //Oneforallfunc.copyfile(lokasibackupimage, lokasiimage);
                                Toast.makeText(PengaturanActivity.this, "Data Berhasil Di Restore", Toast.LENGTH_SHORT).show();
                            } catch (Exception e) {
                                Toast.makeText(PengaturanActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                e.printStackTrace();
                            }
                        }
                    });
                    adb.setNegativeButton("Batal", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });

                    adb.show();

                } else if (position==7){
                    final User user = SharedPrefManager.getInstance(getApplicationContext()).getUser();
                    final String iduser = user.getIduser();
                    AlertDialog.Builder adb=new AlertDialog.Builder(PengaturanActivity.this);
                    adb.setTitle("Konfirmasi");
                    adb.setMessage("Anda ingin keluar dari aplikasi ?");
                    adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl(APIUrl.BASE_URL)
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();
                            ApiService apiService = retrofit.create(ApiService.class);
                            Call<Logout> call = apiService.userLogout(
                                    iduser
                            );
                            call.enqueue(new Callback<Logout>() {
                                @Override
                                public void onResponse(Call<Logout> call, Response<Logout> response) {
                                    if (!response.body().getError()){
//                                        dian.setIduser(response.body().getId());
                                        System.out.println("ini id di logout " +iduser);
                                        System.out.println("ini id juga : " + user.getIduser());
                                        Intent in = new Intent(PengaturanActivity.this, LoginActivity.class);
                                        startActivity(in);

                                    } else {
                                        Toast.makeText(getApplicationContext(), response.body().getMessage(), Toast.LENGTH_LONG).show();
                                        System.out.println("ini error di logout "+response.body().getError());
                                        System.out.println("ini id di logout, else " +iduser);
                                    }
                                }

                                @Override
                                public void onFailure(Call<Logout> call, Throwable t) {
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
                }
            }
        });
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        if (resultCode == RESULT_OK) {
            Toast.makeText(this, "Bluetooth diaktifkan", Toast.LENGTH_SHORT).show();
        }
    }
}
