package com.example.kasirup;

import static com.example.kasirup.Konfigurasi.URL_EXPORT_TRANSAKSI;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class Transaksi extends AppCompatActivity implements ListView.OnItemClickListener {
    private SimpleAdapter simpleAdapter;
    private ListView listView;

    private Button backToDashboard, buttonAdd, exportDataButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transaksi);

        backToDashboard = findViewById(R.id.backToDashboard);
        backToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Transaksi.this, DashboardAdmin.class));
            }
        });

        buttonAdd = findViewById(R.id.tambahData);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Transaksi.this, TambahTransaksi.class));
            }
        });

        exportDataButton = findViewById(R.id.exportDataButton);
        exportDataButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(URL_EXPORT_TRANSAKSI)));
            }
        });

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        getJSON();
    }

    private void showDaftarTransaksi(String s) {
        JSONObject jsonObject = null;

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        try {
            jsonObject = new JSONObject(s);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String id = jo.getString(Konfigurasi.TAG_TRANSAKSI_ID);
                String idProduk = jo.getString(Konfigurasi.TAG_TRANSAKSI_ID_PRODUK);
                String namaProduk = jo.getString(Konfigurasi.TAG_TRANSAKSI_NAMA_PRODUK);
                String harga = jo.getString(Konfigurasi.TAG_TRANSAKSI_HARGA);
                String kuantitas = jo.getString(Konfigurasi.TAG_TRANSAKSI_KUANTITAS);
                String totalHarga = jo.getString(Konfigurasi.TAG_TRANSAKSI_TOTAL_HARGA);
                String uangBayar = jo.getString(Konfigurasi.TAG_TRANSAKSI_UANG_BAYAR);
                String uangKembalian = jo.getString(Konfigurasi.TAG_TRANSAKSI_UANG_KEMBALIAN);
                String tanggalPembelian = jo.getString(Konfigurasi.TAG_TRANSAKSI_TANGGAL_PEMBELIAN);

                HashMap<String, String> daftarTransaksi = new HashMap<>();
                daftarTransaksi.put("no", Integer.toString(i+1));
                daftarTransaksi.put(Konfigurasi.TAG_TRANSAKSI_ID, id);
                daftarTransaksi.put(Konfigurasi.TAG_TRANSAKSI_ID_PRODUK, idProduk);
                daftarTransaksi.put(Konfigurasi.TAG_TRANSAKSI_NAMA_PRODUK, namaProduk);
                daftarTransaksi.put(Konfigurasi.TAG_TRANSAKSI_HARGA, harga);
                daftarTransaksi.put(Konfigurasi.TAG_TRANSAKSI_KUANTITAS, kuantitas);
                daftarTransaksi.put(Konfigurasi.TAG_TRANSAKSI_TOTAL_HARGA, totalHarga);
                daftarTransaksi.put(Konfigurasi.TAG_TRANSAKSI_UANG_BAYAR, uangBayar);
                daftarTransaksi.put(Konfigurasi.TAG_TRANSAKSI_UANG_KEMBALIAN, uangKembalian);
                daftarTransaksi.put(Konfigurasi.TAG_TRANSAKSI_TANGGAL_PEMBELIAN, tanggalPembelian);
                list.add(daftarTransaksi);
            }

        } catch (JSONException e) {
            e.printStackTrace();
//            Toast.makeText(this, e.toString(), Toast.LENGTH_SHORT).show();
        }

        simpleAdapter = new SimpleAdapter(
                Transaksi.this, list, R.layout.list_item_transaksi,
                new String[]{"no", Konfigurasi.TAG_TRANSAKSI_ID_PRODUK, Konfigurasi.TAG_TRANSAKSI_NAMA_PRODUK, Konfigurasi.TAG_TRANSAKSI_HARGA, Konfigurasi.TAG_TRANSAKSI_KUANTITAS, Konfigurasi.TAG_TRANSAKSI_TOTAL_HARGA, Konfigurasi.TAG_TRANSAKSI_UANG_BAYAR, Konfigurasi.TAG_TRANSAKSI_UANG_KEMBALIAN, Konfigurasi.TAG_TRANSAKSI_TANGGAL_PEMBELIAN},
                new int[]{R.id.no, R.id.idProduk, R.id.namaProduk, R.id.harga, R.id.kuantitas, R.id.totalHarga, R.id.uangBayar, R.id.uangKembalian, R.id.tanggalPembelian});
        listView.setAdapter(simpleAdapter);
    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Transaksi.this, "Mengambil Data", "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                showDaftarTransaksi(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();

                String s = rh.sendGetRequest(Konfigurasi.URL_SHOW_TRANSAKSI);
                return s;
            }
        }

        GetJSON gj = new GetJSON();
        gj.execute();
    }

    // search feature
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.menu, menu);

        MenuItem menuItem = menu.findItem(R.id.action_search);
        SearchView searchView = (SearchView) menuItem.getActionView();
        searchView.setQueryHint("Ketik di sini untuk mencari transaksi");

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                simpleAdapter.getFilter().filter(newText);

                return false;
            }
        });


        return super.onCreateOptionsMenu(menu);
    }

    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Intent intent = new Intent(this, DetailTransaksi.class);

        HashMap<String, String> map =(HashMap) parent.getItemAtPosition(position);
        String transaksiId = map.get(Konfigurasi.TAG_TRANSAKSI_ID).toString();

        intent.putExtra(Konfigurasi.TAG_TRANSAKSI_ID, transaksiId);
        startActivity(intent);
    }
}