package com.example.kasirup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SearchView;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.Filterable;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;

public class ProdukAdmin extends AppCompatActivity implements ListView.OnItemClickListener {
    private SimpleAdapter simpleAdapter;
    private ListView listView;

    private Button backToDashboard, buttonAdd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_produk_admin);

        backToDashboard = findViewById(R.id.backToDashboard);
        backToDashboard.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProdukAdmin.this, DashboardAdmin.class));
            }
        });

        buttonAdd = findViewById(R.id.tambahData);
        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(ProdukAdmin.this, TambahProduk.class));
            }
        });

        listView = findViewById(R.id.listView);
        listView.setOnItemClickListener(this);

        getJSON();
    }

    private void showDaftarProduk(String s) {
        JSONObject jsonObject = null;

        ArrayList<HashMap<String, String>> list = new ArrayList<HashMap<String, String>>();
//        Toast.makeText(this, s, Toast.LENGTH_SHORT).show();
        try {
            jsonObject = new JSONObject(s);
            JSONArray result = jsonObject.getJSONArray(Konfigurasi.TAG_JSON_ARRAY);

            for (int i = 0; i < result.length(); i++) {
                JSONObject jo = result.getJSONObject(i);

                String id = jo.getString(Konfigurasi.TAG_PRODUK_ID);
                String nama = jo.getString(Konfigurasi.TAG_PRODUK_NAMA);
                String deskripsi = jo.getString(Konfigurasi.TAG_PRODUK_DESKRIPSI);
                String harga = jo.getString(Konfigurasi.TAG_PRODUK_HARGA);
                String stok = jo.getString(Konfigurasi.TAG_PRODUK_STOK);
                String created_at = jo.getString(Konfigurasi.TAG_PRODUK_CREATED_AT);

                HashMap<String, String> daftarProduk = new HashMap<>();
                daftarProduk.put("no", Integer.toString(i+1));
                daftarProduk.put(Konfigurasi.TAG_PRODUK_ID, id);
                daftarProduk.put(Konfigurasi.TAG_PRODUK_NAMA, nama);
                daftarProduk.put(Konfigurasi.TAG_PRODUK_DESKRIPSI, deskripsi);
                daftarProduk.put(Konfigurasi.TAG_PRODUK_HARGA, harga);
                daftarProduk.put(Konfigurasi.TAG_PRODUK_STOK, stok);
                daftarProduk.put(Konfigurasi.TAG_PRODUK_CREATED_AT, created_at);
                list.add(daftarProduk);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }

        simpleAdapter = new SimpleAdapter(
                ProdukAdmin.this, list, R.layout.list_item_produk_admin,
                new String[]{"no", Konfigurasi.TAG_PRODUK_NAMA, Konfigurasi.TAG_PRODUK_DESKRIPSI, Konfigurasi.TAG_PRODUK_HARGA, Konfigurasi.TAG_PRODUK_STOK, Konfigurasi.TAG_PRODUK_CREATED_AT},
                new int[]{R.id.no, R.id.nama, R.id.deskripsi, R.id.harga, R.id.stok, R.id.createdAt});
        listView.setAdapter(simpleAdapter);
    }

    private void getJSON() {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(ProdukAdmin.this, "Mengambil Data", "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                showDaftarProduk(s);
            }

            @Override
            protected String doInBackground(Void... params) {
                RequestHandler rh = new RequestHandler();
                String s = rh.sendGetRequest(Konfigurasi.URL_SHOW_PRODUK);
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
        searchView.setQueryHint("Ketik di sini untuk mencari produk");

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
        Intent intent = new Intent(this, DetailProduk.class);

        HashMap<String, String> map =(HashMap) parent.getItemAtPosition(position);
        String produkId = map.get(Konfigurasi.TAG_PRODUK_ID);

        intent.putExtra(Konfigurasi.TAG_PRODUK_ID, produkId);
        startActivity(intent);
    }
}