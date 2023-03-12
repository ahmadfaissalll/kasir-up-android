package com.example.kasirup;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

public class DashboardAdmin extends AppCompatActivity {
    CardView produk, dataTransaksi, logout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard_admin);

        produk = findViewById(R.id.produk);
        produk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdmin.this, ProdukAdmin.class));
            }
        });

        dataTransaksi = findViewById(R.id.dataTransaksi);
        dataTransaksi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(DashboardAdmin.this, Transaksi.class));
            }
        });

        logout = findViewById(R.id.logout);
        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                confirmLogout();
            }
        });
    }

    private void confirmLogout() {
        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(DashboardAdmin.this);
        alertDialogBuilder.setMessage("Apakah Kamu Yakin Ingin Logout?");

        alertDialogBuilder.setPositiveButton("Ya", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface arg0, int arg1) {
                Toast.makeText(DashboardAdmin.this, "Logout berhasil", Toast.LENGTH_SHORT).show();
                startActivity(new Intent(DashboardAdmin.this, Pilihan.class));
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
}