package com.example.kasirup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class TambahProduk extends AppCompatActivity {
    private EditText editTextNama, editTextDeskripsi, editTextHarga, editTextStok;

    private Button buttonBack, buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_produk);

        editTextNama = findViewById(R.id.nama);
        editTextDeskripsi = findViewById(R.id.deskripsi);
        editTextHarga = findViewById(R.id.harga);
        editTextStok = findViewById(R.id.stok);

        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TambahProduk.this, ProdukAdmin.class));
            }
        });

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addProduk();
            }
        });
    }

    private void addProduk() {

        final String nama = editTextNama.getText().toString().trim();
        final String deskripsi = editTextDeskripsi.getText().toString().trim();
        final String harga = editTextHarga.getText().toString().trim();
        final String stok = editTextStok.getText().toString().trim();

        class AddProduk extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TambahProduk.this, "Menambahkan...", "Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (s.equals("Data Produk berhasil ditambahkan")) {
                    Toast.makeText(TambahProduk.this, s, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(TambahProduk.this, ProdukAdmin.class));
                } else {
                    Toast.makeText(TambahProduk.this, s, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Konfigurasi.TAG_PRODUK_NAMA, nama);
                params.put(Konfigurasi.TAG_PRODUK_DESKRIPSI, deskripsi);
                params.put(Konfigurasi.TAG_PRODUK_HARGA, harga);
                params.put(Konfigurasi.TAG_PRODUK_STOK, stok);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Konfigurasi.URL_STORE_PRODUK, params);

                return res;
            }
        }


        // sort of simple validation

        boolean isFormFieldValid = false;

        String[] formFieldsKey = {"Nama", "Deskripsi", "Harga", "Stok"};
        String[] formFieldsValue = {nama, deskripsi, harga, stok};

        String[] numberTypeFields = {formFieldsKey[2], formFieldsKey[3]};


        for (int i = 0; i < formFieldsKey.length; i++) {
            if (formFieldsValue[i].isEmpty()) {
                Toast.makeText(TambahProduk.this, formFieldsKey[i] + " harus diisi", Toast.LENGTH_SHORT).show();
                break;
            }

            // validasi field with type number
            if (Arrays.asList(numberTypeFields).contains(formFieldsKey[i]) && !formFieldsValue[i].isEmpty()) {
                String firstChar = String.valueOf(formFieldsValue[i].charAt(0));

                if (firstChar.equals("-")) {
                    Toast.makeText(this, formFieldsKey[i] + " tidak boleh negatif", Toast.LENGTH_SHORT).show();
                    break;
                }
            }

            // kalo semua field sudah diisi maka ubah value variable isFormFieldValid menjadi true
            // agar request dijalankan
            if (i == (formFieldsKey.length - 1)) {
                isFormFieldValid = true;
            }
        }

        if (isFormFieldValid) {
            AddProduk ab = new AddProduk();
            ab.execute();
        }
    }
}