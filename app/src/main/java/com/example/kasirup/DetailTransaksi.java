package com.example.kasirup;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ListAdapter;
import android.widget.SimpleAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

public class DetailTransaksi extends AppCompatActivity implements View.OnClickListener {
    ArrayList<String> produkList = new ArrayList<>();
    ArrayAdapter<String> produkAdapter;
    private EditText editTextKuantitas, editTextTotalHarga, editTextUangBayar;
    private TextView textViewTanggalPembelian;
    private Spinner spinnerProduk;

    private Button backBtn, updateBtn, deleteBtn;

    // untuk dikirim ke backend
    private String id, idProduk, tanggalPembelian = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_transaksi);

        Intent intent = getIntent();

        id = intent.getStringExtra(Konfigurasi.TAG_TRANSAKSI_ID);

        spinnerProduk = findViewById(R.id.spinnerProduk);

        editTextKuantitas = findViewById(R.id.kuantitas);
        editTextTotalHarga = findViewById(R.id.totalHarga);
        editTextTotalHarga.setEnabled(false);

        editTextUangBayar = findViewById(R.id.uangBayar);
        textViewTanggalPembelian = findViewById(R.id.tanggalPembelian);

        backBtn = findViewById(R.id.buttonBack);
        updateBtn = findViewById(R.id.buttonUpdate);
        deleteBtn = findViewById(R.id.buttonDelete);

        backBtn.setOnClickListener(this);
        updateBtn.setOnClickListener(this);
        deleteBtn.setOnClickListener(this);

        getTransaksi();
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
                if (spinnerProduk.getSelectedItem() != null) {
                    calculateTotalHarga();
                }
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
                        DetailTransaksi.this, new DatePickerDialog.OnDateSetListener() {
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

                            month++;
                            tanggalPembelian = year + "-" + month + "-" + day;
                        } catch (ParseException e) {
                            e.printStackTrace();
                        }

                    }
                }, year, month, day);

                if ( tanggalPembelian.contains("-") ) {
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
                loading = ProgressDialog.show(DetailTransaksi.this, "Mengambil...", "Tunggu...", false, false);
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

            int spinnerPosition = 0;

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String idProduk = jo.getString(Konfigurasi.TAG_PRODUK_ID);
                String namaProduk = jo.getString(Konfigurasi.TAG_PRODUK_NAMA);
                String harga = jo.getString(Konfigurasi.TAG_PRODUK_HARGA);


                produkList.add(idProduk + " | " + namaProduk + " | " + harga);
                produkAdapter = new ArrayAdapter<>(DetailTransaksi.this, android.R.layout.simple_spinner_dropdown_item, produkList);
                produkAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);

                spinnerProduk.setAdapter(produkAdapter);


                // set spinner position to selected value
                if (idProduk.equals(this.idProduk)) {
                    String selection = idProduk + " | " + namaProduk + " | " + harga;

                    spinnerPosition = produkAdapter.getPosition(selection);
                }
            }

            spinnerProduk.setSelection(spinnerPosition);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void getTransaksi() {
        class GetTransaksi extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailTransaksi.this, "Mengambil...", "Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                showTransaksi(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequestParam(Konfigurasi.URL_EDIT_TRANSAKSI, id);
                return s;
            }
        }

        GetTransaksi gt = new GetTransaksi();
        gt.execute();
    }

    private void showTransaksi(String json) {
        try {
            JSONObject jsonObject = new JSONObject(json);
            JSONObject result = jsonObject.getJSONObject(Konfigurasi.TAG_JSON_ARRAY);
//            JSONObject c = result.getJSONObject(0);

            this.idProduk = result.getString(Konfigurasi.TAG_TRANSAKSI_ID_PRODUK);

            String kuantitas = result.getString(Konfigurasi.TAG_TRANSAKSI_KUANTITAS);
            String totalHarga = result.getString(Konfigurasi.TAG_TRANSAKSI_TOTAL_HARGA);
            String uangBayar = result.getString(Konfigurasi.TAG_TRANSAKSI_UANG_BAYAR);
            String tanggalPembelian = result.getString(Konfigurasi.TAG_TRANSAKSI_TANGGAL_PEMBELIAN);
            String formattedTanggalPembelian = result.getString(Konfigurasi.TAG_TRANSAKSI_FORMATTED_TANGGAL_PEMBELIAN);

            editTextKuantitas.setText(kuantitas);
            editTextTotalHarga.setText(totalHarga);
            editTextUangBayar.setText(uangBayar);
            this.tanggalPembelian = tanggalPembelian;
            textViewTanggalPembelian.setText(formattedTanggalPembelian);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void updateTransaksi() {

        String produk = spinnerProduk.getSelectedItem().toString().trim();
        String[] splittedProduk = produk.split("[ ]", 10);
        final String idProduk = splittedProduk[0];
        final String kuantitas = editTextKuantitas.getText().toString().trim();
        final String uangBayar = editTextUangBayar.getText().toString().trim();

        final String tanggalPembelian = this.tanggalPembelian;


        class UpdateTransaksi extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailTransaksi.this, "Updating...", "Wait...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();


                if (s.equals("Data Transaksi berhasil diubah")) {
                    Toast.makeText(DetailTransaksi.this, s, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailTransaksi.this, Transaksi.class));
                } else {
                    Toast.makeText(DetailTransaksi.this, s, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put(Konfigurasi.TAG_TRANSAKSI_ID, id);
                hashMap.put(Konfigurasi.TAG_TRANSAKSI_ID_PRODUK, idProduk);
                hashMap.put(Konfigurasi.TAG_TRANSAKSI_KUANTITAS, kuantitas);
                hashMap.put(Konfigurasi.TAG_TRANSAKSI_UANG_BAYAR, uangBayar);
                hashMap.put(Konfigurasi.TAG_TRANSAKSI_TANGGAL_PEMBELIAN, tanggalPembelian);

                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Konfigurasi.URL_UPDATE_TRANSAKSI, hashMap);
                return s;
            }
        }


        // sort of simple validation

        boolean isFormFieldValid = false;

        String[] formFieldsKey = {"Id Produk", "Kuantitas", "Uang Bayar", "Tanggal Pembelian"};
        String[] formFieldsValue = {idProduk, kuantitas, uangBayar, tanggalPembelian};


        for (int i = 0; i < formFieldsKey.length; i++) {
            if (formFieldsValue[i].isEmpty()) {
                Toast.makeText(DetailTransaksi.this, formFieldsKey[i] + " harus diisi", Toast.LENGTH_SHORT).show();
                break;
            }

            // validasi field kuantitas
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
            UpdateTransaksi ut = new UpdateTransaksi();
            ut.execute();
        }
    }


    private void deleteTransaksi() {
        class DeleteTransaksi extends AsyncTask<Void, Void, String> {
            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(DetailTransaksi.this, "Deleting...", "Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if (s.equals("Data Transaksi berhasil dihapus")) {
                    Toast.makeText(DetailTransaksi.this, s, Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(DetailTransaksi.this, Transaksi.class));
                } else {
                    Toast.makeText(DetailTransaksi.this, s, Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Void... params) {
                HashMap<String, String> hashMap = new HashMap<>();

                hashMap.put(Konfigurasi.TAG_TRANSAKSI_ID, id);

                RequestHandler rh = new RequestHandler();

                String s = rh.sendPostRequest(Konfigurasi.URL_DELETE_TRANSAKSI, hashMap);
                return s;
            }
        }

        DeleteTransaksi dt = new DeleteTransaksi();
        dt.execute();
    }

    private void confirmDeleteTransaksi() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(this);
        alertDialogBuilder.setMessage("Apakah Kamu Yakin Ingin Menghapus Data Transaksi ini?");

        alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                deleteTransaksi();
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

    public void calculateTotalHarga() {
        String produk = this.spinnerProduk.getSelectedItem().toString();

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
                Toast.makeText(this, "Kuantitas tidak boleh negatif", Toast.LENGTH_SHORT).show();
            }
        } else {
            editTextTotalHarga.setText("Rp. 0");
        }
    }

    @Override
    public void onClick(View v) {
        if (v == updateBtn) {
            updateTransaksi();
        }

        if (v == deleteBtn) {
            confirmDeleteTransaksi();
        }

        if (v == backBtn) {
            startActivity(new Intent(DetailTransaksi.this, Transaksi.class));
        }
    }
}