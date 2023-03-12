package com.example.kasirup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Objects;

public class DetailProduk extends AppCompatActivity implements View.OnClickListener {
    private EditText editTextNama, editTextDeskripsi, editTextHarga, editTextStok;

    private Button buttonBack, buttonUpdate, buttonDelete;

    private String id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_produk);

        Intent intent = getIntent();

        id = intent.getStringExtra(Konfigurasi.TAG_PRODUK_ID);

        editTextNama = (EditText) findViewById(R.id.nama);
        editTextDeskripsi = (EditText) findViewById(R.id.deskripsi);
        editTextHarga = (EditText) findViewById(R.id.harga);
        editTextStok = (EditText) findViewById(R.id.stok);

        buttonBack = (Button) findViewById(R.id.buttonBack);
        buttonUpdate = (Button) findViewById(R.id.buttonUpdate);
        buttonDelete = (Button) findViewById(R.id.buttonDelete);

        buttonBack.setOnClickListener(this);
        buttonUpdate.setOnClickListener(this);
        buttonDelete.setOnClickListener(this);

        getProduk();
    }

    private void getProduk() {
        class GetProduk extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailProduk.this, "Mengambil...", "Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                showProduk(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Konfigurasi.URL_EDIT_PRODUK, id);
                return s;
            }
        }

        GetProduk gp = new GetProduk();
        gp.execute();
    }

    private void showProduk(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject result = jsonObject.getJSONObject(Konfigurasi.TAG_JSON_ARRAY);
//            JSONObject c = result.getJSONObject(0);

            String nama = result.getString(Konfigurasi.TAG_PRODUK_NAMA);
            String deskripsi = result.getString(Konfigurasi.TAG_PRODUK_DESKRIPSI);
            String harga = result.getString(Konfigurasi.TAG_PRODUK_HARGA);
            String stok = result.getString(Konfigurasi.TAG_PRODUK_STOK);


            editTextNama.setText(nama);
            editTextDeskripsi.setText(deskripsi);
            editTextHarga.setText(harga);
            editTextStok.setText(stok);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateProduk() {

        final String nama = editTextNama.getText().toString().trim();
        final String deskripsi = editTextDeskripsi.getText().toString().trim();
        final String harga = editTextHarga.getText().toString().trim();
        final String stok = editTextStok.getText().toString().trim();

        class UpdateProduk extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailProduk.this, "Updating...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (s.equals("Data Produk berhasil diubah")) {
                    Toast.makeText(DetailProduk.this, s, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailProduk.this, ProdukAdmin.class));
                } else {
                    Toast.makeText(DetailProduk.this, s, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put(Konfigurasi.TAG_PRODUK_ID, id);
                hashMap.put(Konfigurasi.TAG_PRODUK_NAMA, nama);
                hashMap.put(Konfigurasi.TAG_PRODUK_DESKRIPSI, deskripsi);
                hashMap.put(Konfigurasi.TAG_PRODUK_HARGA, harga);
                hashMap.put(Konfigurasi.TAG_PRODUK_STOK, stok);

                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Konfigurasi.URL_UPDATE_PRODUK, hashMap);
                return s;
            }
        }

        // sort of simple validation

        boolean isFormFieldValid = false;

        String[] formFieldsKey = {"Nama", "Deskripsi", "Harga", "Stok"};
        String[] formFieldsValue = {nama, deskripsi, harga, stok};

        String[] numberTypeFields = {formFieldsKey[2], formFieldsKey[3]};


        for (int i = 0; i < formFieldsKey.length; i++) {
            if (formFieldsValue[i].isEmpty()) {
                Toast.makeText(DetailProduk.this, formFieldsKey[i] + " harus diisi", Toast.LENGTH_SHORT).show();
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
            UpdateProduk up = new UpdateProduk();
            up.execute();
        }
    }


    private void deleteProduk() {
        class DeleteProduk extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailProduk.this, "Deleting...", "Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (s.equals("Data Produk berhasil dihapus")) {
                    Toast.makeText(DetailProduk.this, s, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailProduk.this, ProdukAdmin.class));
                } else {
                    Toast.makeText(DetailProduk.this, s, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put(Konfigurasi.TAG_PRODUK_ID, id);

                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Konfigurasi.URL_DELETE_PRODUK, hashMap);
                return s;
            }
        }

        DeleteProduk dp = new DeleteProduk();
        dp.execute();
    }

    private void confirmDeleteProduk() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Apakah Kamu Yakin Ingin Menghapus Produk ini?");

        alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                deleteProduk();
            }
        });

        alertDialogBuilder.setNegativeButton("Tidak", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {

            }
        });

        AlertDialog alertDialog = alertDialogBuilder.create();
        alertDialog.show();
    }

    @Override
    public void onClick(View v) {
        if (v == buttonUpdate) {
            updateProduk();
        }

        if (v == buttonDelete) {
            confirmDeleteProduk();
        }
        if (v == buttonBack) {
            startActivity(new Intent(DetailProduk.this, ProdukAdmin.class));
        }
    }
}