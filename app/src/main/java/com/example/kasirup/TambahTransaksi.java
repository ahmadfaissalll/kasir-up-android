package com.example.kasirup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.math.BigInteger;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class TambahTransaksi extends AppCompatActivity {
    EditText editTextKuantitas, editTextTotalHarga, editTextUangBayar;

    TextView textViewTanggalPembelian;

    ArrayList<String> produkList = new ArrayList<>();
    ArrayAdapter<String> produkAdapter;

    Spinner spinnerProduk;

    Button buttonBack, buttonAdd;

    // untuk dikirim ke backend
    String idProduk, tanggalPembelian = "";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tambah_transaksi);

        spinnerProduk = findViewById(R.id.spinnerProduk);

        editTextKuantitas = findViewById(R.id.kuantitas);
        editTextTotalHarga = findViewById(R.id.totalHarga);
        editTextTotalHarga.setEnabled(false);

        editTextUangBayar = findViewById(R.id.uangBayar);

        textViewTanggalPembelian = findViewById(R.id.tanggalPembelian);


        buttonBack = findViewById(R.id.buttonBack);
        buttonBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(TambahTransaksi.this, Transaksi.class));
            }
        });

        buttonAdd = findViewById(R.id.buttonAdd);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addTransaksi();
            }
        });

        populateProdukSpinner();

        spinnerProduk.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parentView, View selectedItemView, int position, long id) {
                if (spinnerProduk.getSelectedItem() != null) {
                    calculateTotalHarga();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parentView) {
            }

        });

        editTextKuantitas.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                calculateTotalHarga();
            }
        });

        Calendar calendar = Calendar.getInstance();
        final int year = calendar.get(Calendar.YEAR);
        final int month = calendar.get(Calendar.MONTH);
        final int day = calendar.get(Calendar.DAY_OF_MONTH);


        textViewTanggalPembelian.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog = new DatePickerDialog(
                        TambahTransaksi.this, new DatePickerDialog.OnDateSetListener() {
                    @Override
                    public void onDateSet(DatePicker view, int year, int month, int day) {
                        String[] monthNames = {"Januari", "Februari",
                                "Maret", "April", "Mei", "Juni",
                                "Juli", "Agustus", "September",
                                "Oktober", "November", "Desember"};

                        SimpleDateFormat inFormat = new SimpleDateFormat("dd-MM-yyyy");

                        try {
                            int date = day;
                            Date myDate = inFormat.parse(date + "-" + month + "-" + year);
                            SimpleDateFormat simpleDateFormat = new SimpleDateFormat("EEEE");

                            String dayName = simpleDateFormat.format(myDate);

                            String tanggalBeli = dayName + ", " + date + " " + monthNames[month] + " " + year;
                            textViewTanggalPembelian.setText(tanggalBeli);

                            month += 1;
                            tanggalPembelian = year + "-" + month + "-" + day;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, year, month, day);

                if ( !tanggalPembelian.isEmpty() ) {
                    String[] splittedtanggalPembelian = tanggalPembelian.split("[-]");
                    int year = Integer.parseInt(splittedtanggalPembelian[0]);
                    int month = Integer.parseInt(splittedtanggalPembelian[1]) - 1;
                    int day = Integer.parseInt(splittedtanggalPembelian[2]);

                    datePickerDialog.updateDate(year, month, day);
                }

                datePickerDialog.show();
            }
        });

    }

    private void populateProdukSpinner() {
        class GetProduk extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TambahTransaksi.this, "Mengambil...", "Tunggu...", false, false);
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
                String s = rh.sendGetRequest(Konfigurasi.URL_SHOW_PRODUK);
                return s;
            }
        }

        GetProduk gp = new GetProduk();
        gp.execute();
    }

    private void showProduk(String json) {
        JSONObject jsonObject = null;

        try {
            jsonObject = new JSONObject(json);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);

            produkList.add("Pilih Produk");
            produkAdapter = new ArrayAdapter<>(TambahTransaksi.this, android.R.layout.simple_spinner_dropdown_item, produkList);
            produkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

            spinnerProduk.setAdapter(produkAdapter);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String idProduk = jo.getString(Konfigurasi.TAG_PRODUK_ID);
                String namaProduk = jo.getString(Konfigurasi.TAG_PRODUK_NAMA);
                String harga = jo.getString(Konfigurasi.TAG_PRODUK_HARGA);

                produkList.add(idProduk + " | " + namaProduk + " | " + harga);
                produkAdapter = new ArrayAdapter<>(TambahTransaksi.this, android.R.layout.simple_spinner_dropdown_item, produkList);
                produkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerProduk.setAdapter(produkAdapter);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void addTransaksi() {

        String produk = spinnerProduk.getSelectedItem().toString().trim();

        if (!produk.equals("Pilih Produk")) {
            String[] splittedProduk = produk.split("[ ]", 10);

            idProduk = splittedProduk[0];
        } else {
            idProduk = "Pilih Produk";
        }


        final String kuantitas = editTextKuantitas.getText().toString().trim();
        final String uangBayar = editTextUangBayar.getText().toString().trim();

        class AddTransaksi extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(TambahTransaksi.this, "Menambahkan...", "Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (s.equals("Data Transaksi berhasil ditambahkan")) {
                    Toast.makeText(TambahTransaksi.this, s, Toast.LENGTH_LONG).show();
                    startActivity(new Intent(TambahTransaksi.this, Transaksi.class));
                } else {
                    Toast.makeText(TambahTransaksi.this, s, Toast.LENGTH_LONG).show();
                }
            }

            @Override
            protected String doInBackground(Void... v) {
                HashMap<String, String> params = new HashMap<>();

                params.put(Konfigurasi.TAG_TRANSAKSI_ID_PRODUK, idProduk);
                params.put(Konfigurasi.TAG_TRANSAKSI_KUANTITAS, kuantitas);
                params.put(Konfigurasi.TAG_TRANSAKSI_UANG_BAYAR, uangBayar);
                params.put(Konfigurasi.TAG_TRANSAKSI_TANGGAL_PEMBELIAN, tanggalPembelian);

                RequestHandler rh = new RequestHandler();
                String res = rh.sendPostRequest(Konfigurasi.URL_STORE_TRANSAKSI, params);

                return res;
            }
        }

        // sort of simple validation

        boolean isFormFieldValid = false;

        String[] formFieldsKey = {"Id Produk", "Kuantitas", "Uang Bayar", "Tanggal Pembelian"};
        String[] formFieldsValue = {idProduk, kuantitas, uangBayar, tanggalPembelian};

        for (int i = 0; i < formFieldsKey.length; i++) {
            if (formFieldsValue[i].isEmpty() && !formFieldsKey[i].equals("Id Produk")) {
                Toast.makeText(TambahTransaksi.this, formFieldsKey[i] + " harus diisi", Toast.LENGTH_SHORT).show();
                break;
            }

            if (formFieldsKey[i].equals("Id Produk") && formFieldsValue[i].equals("Pilih Produk")) {
                Toast.makeText(TambahTransaksi.this, "Pilih Produk", Toast.LENGTH_SHORT).show();
                break;
            }

            if (formFieldsKey[i].equals("Kuantitas") && !formFieldsValue[i].isEmpty()) {
                String firstChar = String.valueOf(formFieldsValue[i].charAt(0));

                if (firstChar.equals("-") || firstChar.equals("0")) {
                    Toast.makeText(this, formFieldsKey[i] + " harus lebih dari 0", Toast.LENGTH_SHORT).show();
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
            AddTransaksi ab = new AddTransaksi();
            ab.execute();
        }

    }

    public void calculateTotalHarga() {
        String produk = spinnerProduk.getSelectedItem().toString();

        if (produk.equals("Pilih Produk")) {
            return;
        }

        String[] splittedProduk = produk.split("[ ]", 10);

        String[] dirtyHarga = splittedProduk[splittedProduk.length - 1].split("[,]", 10);

        String hargaConcat = "";

        for (int i = 0; i < dirtyHarga.length; i++) {
            hargaConcat += dirtyHarga[i];
        }

        int harga = Integer.parseInt(hargaConcat);

        String kuantitasInput = editTextKuantitas.getText().toString();

        if (kuantitasInput.length() > 0) {
            if (!String.valueOf(kuantitasInput.charAt(0)).equals("-")) {

                int kuantitas = Integer.parseInt(kuantitasInput);

                double totalHarga = kuantitas * harga;
                editTextTotalHarga.setText("Rp. " + String.format("%, .0f", totalHarga));
            } else {
                Toast.makeText(this, "Kuantitas harus lebih dari 0", Toast.LENGTH_SHORT).show();
            }
        } else {
            editTextTotalHarga.setText("Rp. 0");
        }
    }
}