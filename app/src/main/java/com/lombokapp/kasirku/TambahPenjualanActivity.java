package com.lombokapp.kasirku;

import android.app.DatePickerDialog;
import android.bluetooth.BluetoothAdapter;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.lombokapp.kasirku.Model.Perusahaan;
import com.lombokapp.kasirku.Model.User;
import com.lombokapp.kasirku.adapter.SharedPrefManager;
import com.lombokapp.kasirku.api.APIUrl;
import com.lombokapp.kasirku.api.ApiService;

import java.io.File;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TambahPenjualanActivity extends AppCompatActivity {

    public static ArrayList<TambahpenjualanModel> lsdata = new ArrayList<>();
    FloatingActionButton fbadd,fbbarcode;
    Button bbayar;
    RecyclerView rvdata;
    RecyclerView.LayoutManager layman;
    TambahpenjualanAdapter adapter;
    Dblocalhelper dbo;
    EditText ednotrans, edtanggaltrans, eddesk, edkodebarang, ednofaktur;
    ImageView bimg_barcode, bimg_tanggal;
    TextView ltotal, kembali;
    NumberFormat nf = NumberFormat.getInstance();
    String kode_transaksi = "";
    Calendar cal = Calendar.getInstance();
    SharedPreferences sp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_penjualan);
        fbadd = findViewById(R.id.fbadd);
        rvdata = findViewById(R.id.rvdata);
        ednotrans = findViewById(R.id.ednotrans);
        ednofaktur = findViewById(R.id.ednofaktur);
        edtanggaltrans = findViewById(R.id.edtanggaltrans);
        eddesk = findViewById(R.id.eddesk);
        edkodebarang = findViewById(R.id.edkodebarang);
        bimg_barcode = findViewById(R.id.bimg_barcode);
        bimg_tanggal = findViewById(R.id.bimg_tanggal);
        ltotal = findViewById(R.id.ltotal);
        bbayar = findViewById(R.id.bbayar);
        layman = new LinearLayoutManager(this);
        rvdata.setLayoutManager(layman);
        rvdata.setHasFixedSize(true);
        rvdata.setItemAnimator(new DefaultItemAnimator());
        sp=getApplicationContext().getSharedPreferences("config",0);
        adapter = new TambahpenjualanAdapter(lsdata, this);
        rvdata.setAdapter(adapter);
        ednotrans.setEnabled(false);
        edtanggaltrans.setEnabled(false);
        dbo = new Dblocalhelper(this);
        adddata();
        lsdata.clear();
        Bundle ex = getIntent().getExtras();
        if (ex != null) {
            ednotrans.setText(ex.getString("kode_penjualan_master"));
            kode_transaksi = ex.getString("kode_penjualan_master");
            loaddata(ex.getString("kode_penjualan_master"));
            SQLiteDatabase db = dbo.getReadableDatabase();
            Cursor c = db.rawQuery("SELECT tanggal_penjualan,deskripsi " +
                    "FROM penjualan_master WHERE kode_penjualan_master='" + ex.getString("kode_penjualan_master") + "' LIMIT 1", null);
            if (c.moveToFirst()) {
                edtanggaltrans.setText(c.getString(0));
                eddesk.setText(c.getString(1));
            }
            c.close();
            db.close();
        } else {
            ednotrans.setText(dbo.getkodetransaksi("PJ"));
            edtanggaltrans.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
            kode_transaksi = "";
        }
        caribarang();
        caribarcode();
        bayar();
        gettanggal();
        edtanggaltrans.requestFocus();

    }

    private void caribarcode() {
        bimg_barcode.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(TambahPenjualanActivity.this, BarcodeActivity.class);
                startActivityForResult(in, 1);
            }
        });

    }

    private void caribarang() {
        fbadd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent in = new Intent(TambahPenjualanActivity.this, CariBarangActivity.class);
                in.putExtra("tipe_transaksi", "jual");
                startActivity(in);
            }
        });
    }

    private void rawadddata(String kode_barang) {
        SQLiteDatabase db = dbo.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT kode_barang,nama_barang,satuan_barang,harga_jual,diskon,gambar_barang,harga_beli " +
                "FROM persediaan WHERE kode_barang='" + kode_barang + "' LIMIT 1", null);
        if (c.moveToFirst()) {
            if (c.getString(0).equals("") || c.getString(0) == null) {
                Toast.makeText(TambahPenjualanActivity.this, "Barang Tidak Ditemukan", Toast.LENGTH_SHORT).show();
            } else {

                int posisiindex = -1;
                for (int i = 0; i < lsdata.size(); i++) {
                    TambahpenjualanModel inmodel = lsdata.get(i);
                    if (inmodel.getKode_barang().equals(kode_barang)) {
                        posisiindex = i;
                    }
                }

                if (posisiindex < 0) {
                    double diskonpersen = c.getDouble(4);
                    double diskonnominal = c.getDouble(3) * (diskonpersen / 100);
                    lsdata.add(new TambahpenjualanModel(c.getString(0), c.getString(1),
                            c.getString(2), c.getDouble(3), 1,
                            (c.getDouble(3) - diskonnominal) * 1,
                            c.getDouble(4), c.getString(5)));
                } else {
                    double jumlahawal = lsdata.get(posisiindex).getJumlah();
                    double diskonpersen = lsdata.get(posisiindex).getDiskon();
                    double diskonnominal = lsdata.get(posisiindex).getHarga_jual() * (diskonpersen / 100);
                    double harga_jual = lsdata.get(posisiindex).getHarga_jual();
                    lsdata.get(posisiindex).setJumlah(jumlahawal + 1);
                    lsdata.get(posisiindex).setTotal((harga_jual - diskonnominal) * (jumlahawal + 1));
                }
                adapter.notifyDataSetChanged();
                double total = 0;
                for (int i = 0; i < lsdata.size(); i++) {
                    total = total + lsdata.get(i).getTotal();
                }
                ltotal.setText(nf.format(total));
            }
        } else {
            Toast.makeText(TambahPenjualanActivity.this, "Barang Tidak Ditemukan", Toast.LENGTH_SHORT).show();
        }
        c.close();
        db.close();
    }

    private void adddata() {
        edkodebarang.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE || event.getKeyCode() == KeyEvent.KEYCODE_ENTER) {
                    String kode = v.getText().toString();

                    rawadddata(kode);
                    edkodebarang.setText("");
                    edkodebarang.setFocusable(true);
                    edkodebarang.requestFocus();
                }
                return false;
            }
        });
    }

    private void loaddata(final String kode_penjualan_master) {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                SQLiteDatabase db = dbo.getReadableDatabase();
                try {

                    String query = "SELECT pd.kode_barang,nama_barang,satuan_barang,pd.harga_jual," +
                            "jumlah,(pd.harga_jual-(pd.harga_jual*(pd.diskon/100)))*jumlah AS total,pd.diskon,gambar_barang" +
                            "FROM penjualan_detail pd INNER JOIN persediaan ps ON " +
                            "pd.kode_barang=ps.kode_barang WHERE kode_penjualan_master='" + kode_penjualan_master + "'";
                    Cursor c = db.rawQuery(query, null);
                    while (c.moveToNext()) {
                        lsdata.add(new TambahpenjualanModel(c.getString(0), c.getString(1),
                                c.getString(2), c.getDouble(3), c.getDouble(4),
                                c.getDouble(5), c.getDouble(6), c.getString(7)));
                    }
                    ;

                    adapter.notifyDataSetChanged();
                    double total = 0;
                    for (int i = 0; i < lsdata.size(); i++) {
                        total = total + lsdata.get(i).getTotal();
                    }
                    ltotal.setText(nf.format(total));
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    db.close();
                }
            }
        }, 100);


    }

    private void bayar() {
        bbayar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (lsdata.size() <= 0) {
                    Toast.makeText(TambahPenjualanActivity.this, "Proses Ditolak, Anda Belum Memasukan data", Toast.LENGTH_SHORT).show();
                } else {
                    final AlertDialog.Builder adb = new AlertDialog.Builder(TambahPenjualanActivity.this);
                    View vi = getLayoutInflater().inflate(R.layout.bayar_layout, null);
                    final TextView ltotalbelanja = vi.findViewById(R.id.ltotalbelanja);
                    final TextView lkembalian = vi.findViewById(R.id.lkembalian);
                    final EditText edjumlahuang = vi.findViewById(R.id.edjumlahuang);
                    Button boke = vi.findViewById(R.id.boke);
                    Button bpas = vi.findViewById(R.id.bpass);
                    Button b5 = vi.findViewById(R.id.b5);
                    Button b10 = vi.findViewById(R.id.b10);
                    Button b20 = vi.findViewById(R.id.b20);
                    Button b50 = vi.findViewById(R.id.b50);
                    Button b100 = vi.findViewById(R.id.b100);
                    ltotalbelanja.setText(ltotal.getText().toString());
                    final double kembali = 0 - Oneforallfunc.Stringtodouble(ltotal.getText().toString().replace(".", ""));
                    lkembalian.setText(nf.format(kembali));
                    System.out.println("kembalian"+kembali);
                    adb.setTitle("Pembayaran");
                    adb.setView(vi);
                    adb.setCancelable(false);
                    adb.setNeutralButton("Tutup", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    final AlertDialog ad = adb.create();
                    boke.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            rawsavedata(1);
                            ad.dismiss();
                            lsdata.clear();
                            adapter.notifyDataSetChanged();
                            ednotrans.setText(dbo.getkodetransaksi("PJ"));
                            edtanggaltrans.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                            eddesk.setText("");
                            edkodebarang.setText("");
                            ltotal.setText("0.0");
                            edkodebarang.setFocusable(true);
                            edkodebarang.requestFocus();


                        }
                    });

                    View.OnClickListener blistener = new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            double hasil = 0.0;
                            switch (v.getId()) {
                                case R.id.bpass:
                                    try {
                                        rawsavedata(1);
                                        ad.dismiss();
                                        lsdata.clear();
                                        adapter.notifyDataSetChanged();
                                        ednotrans.setText(dbo.getkodetransaksi("PJ"));
                                        edtanggaltrans.setText(new SimpleDateFormat("yyyy-MM-dd").format(new Date()));
                                        eddesk.setText("");
                                        edkodebarang.setText("");
                                        ltotal.setText("0.0");
                                        edkodebarang.setFocusable(true);
                                        edkodebarang.requestFocus();
                                        break;
                                    } catch (Exception ex) {
                                        Toast.makeText(TambahPenjualanActivity.this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                                        ex.printStackTrace();
                                    }
                                case R.id.b5:
                                    edjumlahuang.setText("5.000");
                                    hasil = Oneforallfunc.Stringtodouble(edjumlahuang.getText().toString()) -
                                            Oneforallfunc.Stringtodouble(ltotalbelanja.getText().toString());
                                    //lkembalian.setText(nf.format(hasil));
                                    break;
                                case R.id.b10:
                                    edjumlahuang.setText("10.000");
                                    hasil = Oneforallfunc.Stringtodouble(edjumlahuang.getText().toString()) -
                                            Oneforallfunc.Stringtodouble(ltotalbelanja.getText().toString());
                                    //lkembalian.setText(nf.format(hasil));
                                    break;
                                case R.id.b20:
                                    edjumlahuang.setText("20.000");
                                    hasil = Oneforallfunc.Stringtodouble(edjumlahuang.getText().toString()) -
                                            Oneforallfunc.Stringtodouble(ltotalbelanja.getText().toString());
                                    //lkembalian.setText(nf.format(hasil));
                                    break;
                                case R.id.b50:
                                    edjumlahuang.setText("50.000");
                                    hasil = Oneforallfunc.Stringtodouble(edjumlahuang.getText().toString()) -
                                            Oneforallfunc.Stringtodouble(ltotalbelanja.getText().toString());
                                    //lkembalian.setText(nf.format(hasil));
                                    break;
                                case R.id.b100:
                                    edjumlahuang.setText("100.000");
                                    hasil = Oneforallfunc.Stringtodouble(edjumlahuang.getText().toString()) -
                                            //.replace(".", "")) -
                                            Oneforallfunc.Stringtodouble(ltotalbelanja.getText().toString());
                                                    //.replace(".", ""));
                                    //lkembalian.setText(nf.format(hasil));
                                    break;

                            }


                        }
                    };
                    bpas.setOnClickListener(blistener);
                    b5.setOnClickListener(blistener);
                    b10.setOnClickListener(blistener);
                    b20.setOnClickListener(blistener);
                    b50.setOnClickListener(blistener);
                    b100.setOnClickListener(blistener);

                    edjumlahuang.addTextChangedListener(new TextWatcher() {
                        @Override
                        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                        }

                        @Override
                        public void onTextChanged(CharSequence s, int start, int before, int count) {
                            final double jumlahuang = Oneforallfunc.Stringtodouble(String.valueOf(s).replace(".",""));
                            final double jumlahbelanja = Oneforallfunc.Stringtodouble(ltotal.getText().toString().replace(",",""));
                            double kembali = jumlahuang - jumlahbelanja;
                            lkembalian.setText(nf.format(kembali));
                            System.out.println("total "+ ltotal.getText());
                            System.out.println("jumlah uang "+jumlahuang);
                            System.out.println("jumlah belanja "+jumlahbelanja);
                            System.out.println("uang kembalian " +kembali);
                        }

                        @Override
                        public void afterTextChanged(Editable s) {

                        }
                    });
                    ad.show();
                }

            }
        });
    }

    private void rawsavedata(int status) {
        if (lsdata.size() <= 0) {
            Toast.makeText(this, "Proses Ditolak, Anda Belum Memasukan data", Toast.LENGTH_SHORT).show();
        } else {
            SQLiteDatabase db = dbo.getWritableDatabase();
            String currenttime = new SimpleDateFormat("yyyyMMddHHmmsszzz").format(new Date());
            try {
                db.beginTransaction();
                if (kode_transaksi.equals("") || kode_transaksi == null) {
                    db.execSQL("INSERT INTO penjualan_master(kode_penjualan_master,status,tanggal_penjualan,deskripsi,last_update,date_created) " +
                            "VALUES('" + ednotrans.getText().toString() + "'," + status + "," +
                            "'" + edtanggaltrans.getText().toString() + "','" + eddesk.getText().toString() + "'," +
                            "'" + currenttime + "'," +
                            "'" + currenttime + "')");
                    for (int i = 0; i < lsdata.size(); i++) {
                        db.execSQL("INSERT INTO penjualan_detail(kode_penjualan_detail,kode_penjualan_master," +
                                "kode_barang,jumlah,harga_jual,diskon) " +
                                "VALUES('" + ednotrans.getText().toString() + i + "'," +
                                "'" + ednotrans.getText().toString() + "'," +
                                "'" + lsdata.get(i).getKode_barang() + "'," +
                                "" + Oneforallfunc.validdouble(lsdata.get(i).getJumlah()) + "," +
                                "" + Oneforallfunc.validdouble(lsdata.get(i).getHarga_jual()) + "," +
                                "" + Oneforallfunc.validdouble(lsdata.get(i).getDiskon()) + ")");

                            db.execSQL("UPDATE persediaan SET jumlah_barang=jumlah_barang-" + lsdata.get(i).getJumlah() + " " +
                                    "WHERE kode_barang='" + lsdata.get(i).getKode_barang() + "' ");
                    }
                } else {

                    db.execSQL("UPDATE penjualan_master  SET kode_penjualan_master='" + ednotrans.getText().toString() + "'," +
                            "status=" + status + ",tanggal_penjualan='" + edtanggaltrans.getText().toString() + "'," +
                            "deskripsi='" + eddesk.getText().toString() + "',last_update='" + currenttime + "' " +
                            "WHERE kode_penjualan_master='" + kode_transaksi + "'");
                    Cursor c = db.rawQuery("SELECT kode_penjualan_detail,kode_barang,jumlah " +
                            "FROM penjualan_detail WHERE kode_penjualan_master='" + kode_transaksi + "'", null);
                    while (c.moveToNext()) {
                        Cursor ccek = db.rawQuery("SELECT tipe_barang FROM persediaan WHERE kode_barang='" + c.getString(1) + "'", null);
                        if (ccek.moveToFirst()) {
                            if (ccek.getInt(0) == 1) {
                                String kode_barang_racik = c.getString(1);
                                Cursor ccekup = db.rawQuery("SELECT kode_barang_isi,jumlah_isi FROM racikan WHERE kode_barang_racik='" + kode_barang_racik + "'", null);
                                while (ccekup.moveToNext()) {
                                    db.execSQL("UPDATE persediaan SET jumlah_barang=jumlah_barang+" + (ccekup.getDouble(1) * c.getDouble(2)) + " " +
                                            "WHERE kode_barang='" + ccekup.getString(0) + "' ");
                                }
                            } else {
                                db.execSQL("UPDATE persediaan SET jumlah_barang=jumlah_barang+" + c.getDouble(2) + " " +
                                        "WHERE kode_barang='" + c.getString(1) + "' ");
                            }
                        }
                        //db.execSQL("DELETE FROM penjualan_detail WHERE kode_penjualan_detail='"+c.getString(0)+"'");
                    }

                    db.execSQL("DELETE FROM penjualan_detail WHERE kode_penjualan_master='" + kode_transaksi + "'");

                    for (int i = 0; i < lsdata.size(); i++) {
                        String nodetail = ednotrans.getText().toString() + "/" + i;
                        db.execSQL("INSERT INTO penjualan_detail(kode_penjualan_detail,kode_penjualan_master," +
                                "kode_barang,jumlah,harga_jual,diskon) " +
                                "VALUES('" + nodetail + "','" + ednotrans.getText().toString() + "'," +
                                "'" + lsdata.get(i).getKode_barang() + "'," +
                                "" + Oneforallfunc.validdouble(lsdata.get(i).getJumlah()) + "," +
                                "" + Oneforallfunc.validdouble(lsdata.get(i).getHarga_jual()) + "," +
                                "" + Oneforallfunc.validdouble(lsdata.get(i).getDiskon()) + "" +
                                ")");
                            db.execSQL("UPDATE persediaan SET jumlah_barang=jumlah_barang-" + lsdata.get(i).getJumlah() + " " +
                                    "WHERE kode_barang='" + lsdata.get(i).getKode_barang() + "' ");
                    }

                }
                db.setTransactionSuccessful();
                Toast.makeText(TambahPenjualanActivity.this, "Data penjualan Berhasil Disimpan", Toast.LENGTH_SHORT).show();
            } catch (Exception ex) {
                ex.printStackTrace();
            } finally {
                db.endTransaction();
                db.close();
            }

            final StringBuilder sb = new StringBuilder();
            final String namaPerusahan = SharedPrefManager.getInstance(getApplicationContext()).getUser().getNamaPerusahaan();
            final String hp = SharedPrefManager.getInstance(getApplicationContext()).getUser().getNoHp();
            final String alamat = SharedPrefManager.getInstance(getApplicationContext()).getUser().getAlamat();
            final String nama_kasir = SharedPrefManager.getInstance(getApplicationContext()).getUser().getNama();

            sb.append(namaPerusahan);
            System.out.println("nama perusahaan " +namaPerusahan);
            sb.append("\n");
            sb.append(hp);
            System.out.println("no hp " +hp);
            sb.append("\n");
            sb.append(alamat);
            System.out.println("alamat " +alamat);
            sb.append("\n");
            sb.append("================================");
            SimpleDateFormat sdf = new SimpleDateFormat("d/M/yyyy h:m:s");
                    //.format(cal.getTime());
            String waktukini = sdf.format(cal.getTime());
            sb.append(waktukini);
            sb.append("\n");
            sb.append("Nama Kasir : "+nama_kasir);
            sb.append("\n\n");

            for (int i = 0; i < lsdata.size(); i++) {
                if (lsdata.get(i).getDiskon() > 0) {
                    sb.append(
                            i + 1 + "." + lsdata.get(i).getNama_barang()
                                    + "\nRp." + nf.format(lsdata.get(i).getHarga_jual()) + " x " + nf.format(lsdata.get(i).getJumlah()) +
                                    " (Diskon = " + nf.format(lsdata.get(i).getDiskon()) + "%)" +
                                    "\nTotal = Rp." + nf.format(lsdata.get(i).getTotal()) + "\n");
                } else {
                    sb.append(
                            i + 1 + "." + lsdata.get(i).getNama_barang()
                                    + "\nRp." + nf.format(lsdata.get(i).getHarga_jual()) + " x " + nf.format(lsdata.get(i).getJumlah()) +
                                    "\nTotal = Rp." + nf.format(lsdata.get(i).getTotal()) + "\n");
                }

            }
            System.out.println("berhasil print");
            sb.append("================================\n");
            sb.append("Total : Rp. " + ltotal.getText().toString());
            sb.append("\n");
            sb.append("\nTerima Kasih Telah Berkunjung\n");
            sb.append("================================\n");
            sb.append("\n\n");

            if (!sp.getString("default_printer","none").equals("none")) {
                try{
                    BluetoothAdapter btadapter = BluetoothAdapter.getDefaultAdapter();
                    if (btadapter.isEnabled()) {
                        Bluetoothprint bt = new Bluetoothprint(TambahPenjualanActivity.this);
                        bt.print(sb.toString());
                    } else {
                        AlertDialog.Builder adb=new AlertDialog.Builder(TambahPenjualanActivity.this);
                        adb.setTitle("Informasi");
                        adb.setMessage("Bluetooth Tidak Aktif, Aktifkan bluetooth atau masuk ke pengaturan untuk setting bluetooth printer");
                        adb.setPositiveButton("Tutup", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                    }
                }catch (Exception ex){
                    //Toast.makeText(this, ex.getMessage(), Toast.LENGTH_SHORT).show();
                }

            }
        }

    }

    private void gettanggal() {
        bimg_tanggal.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog dpd = new DatePickerDialog(TambahPenjualanActivity.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                        cal.set(Calendar.YEAR, year);
                        cal.set(Calendar.MONTH, month);
                        cal.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                        String tanggalkini = new SimpleDateFormat("yyyy-MM-dd").format(cal.getTime());
                        edtanggaltrans.setText(tanggalkini);
                    }
                }, cal.get(Calendar.YEAR), cal.get(Calendar.MONTH), cal.get(Calendar.DAY_OF_MONTH));

                dpd.show();
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == RESULT_OK) {
                String kode = data.getData().toString();
                rawadddata(kode);
            }
        } else if (requestCode == 5) {
            if (resultCode == RESULT_OK) {
                Toast.makeText(this, "Bluetooth Diaktifkan", Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        adapter.notifyDataSetChanged();
        double total = 0;

        for (int i = 0; i < lsdata.size(); i++) {
            total = total + lsdata.get(i).getTotal();
        }
        ltotal.setText(nf.format(total));
    }

    public static class TambahpenjualanModel {
        String kode_barang, nama_barang, satuan;
        double harga_jual, jumlah, total, diskon;
        String gambar_barang;

        public TambahpenjualanModel(String kode_barang, String nama_barang, String satuan, double harga_jual, double jumlah,
                                    double total, double diskon, String gambar_barang) {
            this.kode_barang = kode_barang;
            this.nama_barang = nama_barang;
            this.satuan = satuan;
            this.harga_jual = harga_jual;
            this.jumlah = jumlah;
            this.total = total;
            this.diskon = diskon;
            this.gambar_barang = gambar_barang;
        }


        public String getKode_barang() {
            return kode_barang;
        }

        public void setKode_barang(String kode_barang) {
            this.kode_barang = kode_barang;
        }

        public String getNama_barang() {
            return nama_barang;
        }

        public void setNama_barang(String nama_barang) {
            this.nama_barang = nama_barang;
        }

        public String getSatuan() {
            return satuan;
        }

        public void setSatuan(String satuan) {
            this.satuan = satuan;
        }

        public double getHarga_jual() {
            return harga_jual;
        }

        public void setHarga_jual(double harga_jual) {
            this.harga_jual = harga_jual;
        }

        public double getJumlah() {
            return jumlah;
        }

        public void setJumlah(double jumlah) {
            this.jumlah = jumlah;
        }

        public double getTotal() {
            return total;
        }

        public void setTotal(double total) {
            this.total = total;
        }

        public double getDiskon() {
            return diskon;
        }

        public void setDiskon(double diskon) {
            this.diskon = diskon;
        }

        public String getGambar_barang() {
            return gambar_barang;
        }

        public void setGambar_barang(String gambar_barang) {
            this.gambar_barang = gambar_barang;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof TambahpenjualanModel) {
                return (((TambahpenjualanModel) obj).getKode_barang().equals(this.getKode_barang()));
            }
            return false;
        }
    }

    public class TambahpenjualanAdapter extends RecyclerView.Adapter {
        ArrayList<TambahpenjualanModel> model = new ArrayList<>();
        Context ct;
            NumberFormat nf = NumberFormat.getInstance();
        int content = 0;

        public TambahpenjualanAdapter(ArrayList<TambahpenjualanModel> model, Context ct) {
            this.model = model;
            this.ct = ct;
        }

        @NonNull
        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            LayoutInflater lin = LayoutInflater.from(parent.getContext());
            View v = lin.inflate(R.layout.adapter_tambah_penjualan, parent, false);
            return new Holder(v);
        }

        @Override
        public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, final int position) {
            if (holder instanceof Holder) {
                final Holder h = (Holder) holder;
                h.lnama_barang.setText(model.get(position).getNama_barang());
                h.lkode_barang.setText(model.get(position).getKode_barang());
                h.lharga_jual.setText("Harga : " + nf.format(model.get(position).getHarga_jual()));
                h.edjumlah.setText(nf.format(model.get(position).getJumlah()));
                h.eddiskon.setText(nf.format(model.get(position).getDiskon()));
                double jumlahdiskon = model.get(position).getHarga_jual() * (model.get(position).getDiskon() / 100);
                double total_harga = (model.get(position).getHarga_jual() - jumlahdiskon) * model.get(position).getJumlah();
                h.ltotal_harga.setText(nf.format(total_harga));
                Glide.with(ct).
                        load(new File(model.get(position).getGambar_barang())).
                        placeholder(R.drawable.ic_assessment_70dp).
                        centerCrop().
                        diskCacheStrategy(DiskCacheStrategy.ALL).
                        into(h.gambar_barang);
                /*h.edjumlah.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            double jumlah=Oneforallfunc.Stringtodouble(v.getText().toString());
                            double harga=model.get(position).getHarga_jual();
                            double diskonpersen=Oneforallfunc.Stringtodouble(h.eddiskon.getText().toString());
                            double diskonnominal=harga*(diskonpersen/100);
                            double total_harga = (harga-diskonnominal)*jumlah;
                            model.get(position).setTotal(total_harga);
                            notifyItemChanged(position);
                            double total = 0;
                            for (int i = 0; i < lsdata.size(); i++) {
                                total = total + lsdata.get(i).getTotal();
                            }
                            ltotal.setText(nf.format(total));
                        }
                        return false;
                    }
                });

                h.eddiskon.setOnEditorActionListener(new TextView.OnEditorActionListener() {
                    @Override
                    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                        if (actionId == EditorInfo.IME_ACTION_DONE) {
                            double jumlah=Oneforallfunc.Stringtodouble(h.edjumlah.getText().toString());
                            double harga=model.get(position).getHarga_jual();
                            double diskonpersen=Oneforallfunc.Stringtodouble(v.getText().toString());
                            double diskonnominal=harga*(diskonpersen/100);
                            double total_harga = (harga-diskonnominal)*jumlah;
                            model.get(position).setTotal(total_harga);
                            notifyItemChanged(position);
                            double total = 0;
                            for (int i = 0; i < lsdata.size(); i++) {
                                total = total + lsdata.get(i).getTotal();
                            }
                            ltotal.setText(nf.format(total));
                        }
                        return false;
                    }
                });*/

                h.bset.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        model.get(position).setJumlah(Oneforallfunc.Stringtodouble(h.edjumlah.getText().toString()));
                        model.get(position).setDiskon(Oneforallfunc.Stringtodouble(h.eddiskon.getText().toString()));
                        double diskonnominal = model.get(position).getHarga_jual() * (model.get(position).getDiskon() / 100);
                        double total_harga = model.get(position).getJumlah() * (model.get(position).getHarga_jual() - diskonnominal);
                        model.get(position).setTotal(total_harga);
                        notifyItemChanged(position);
                        double total = 0;
                        for (int i = 0; i < lsdata.size(); i++) {
                            total = total + lsdata.get(i).getTotal();
                        }
                        ltotal.setText(nf.format(total));
                    }
                });

                h.img_hapus.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        AlertDialog.Builder adb = new AlertDialog.Builder(ct);
                        adb.setTitle("Konfirmasi");
                        adb.setMessage("Yakin ingin menghapus " + model.get(position).getNama_barang() + " ? ");
                        adb.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                try {
                                    model.remove(position);
                                    notifyDataSetChanged();
                                    double total = 0;
                                    for (int i = 0; i < lsdata.size(); i++) {
                                        total = total + lsdata.get(i).getTotal();
                                    }
                                    ltotal.setText(nf.format(total));
                                } catch (Exception ex) {
                                    ex.printStackTrace();
                                }
                            }
                        });
                        adb.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        });
                        adb.show();
                    }
                });
            }
        }

        @Override
        public int getItemCount() {
            return model.size();
        }

        @Override
        public int getItemViewType(int position) {
            return 0;
        }
    }

    public class Holder extends RecyclerView.ViewHolder {
        TextView lnama_barang, lkode_barang, lharga_jual, ltotal_harga;
        EditText edjumlah, eddiskon;
        Button bset;
        ImageView gambar_barang, img_hapus;

        public Holder(View itemView) {
            super(itemView);
            lnama_barang = itemView.findViewById(R.id.lnama_barang);
            lkode_barang = itemView.findViewById(R.id.lkode_barang);
            lharga_jual = itemView.findViewById(R.id.ljudul);
            ltotal_harga = itemView.findViewById(R.id.ltotal_harga_final);
            edjumlah = itemView.findViewById(R.id.edjumlah);
            eddiskon = itemView.findViewById(R.id.eddiskon);
            bset = itemView.findViewById(R.id.bset);
            gambar_barang = itemView.findViewById(R.id.gambar_barang);
            img_hapus = itemView.findViewById(R.id.img_hapus);
        }
    }
}
