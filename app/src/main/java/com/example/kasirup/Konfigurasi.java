package com.example.kasirup;

public class Konfigurasi {
    // URL

    // LOGIN
    public static final String URL_LOGIN = "https://gudangaram.000webhostapp.com/api-kasir-up/admin/login.php";

    // LAPANGAN
    public static final String URL_SHOW_PRODUK = "https://gudangaram.000webhostapp.com/api-kasir-up/produk/index.php";
    public static final String URL_STORE_PRODUK = "https://gudangaram.000webhostapp.com/api-kasir-up/produk/store.php";
    public static final String URL_EDIT_PRODUK = "https://gudangaram.000webhostapp.com/api-kasir-up/produk/edit.php?id=";
    public static final String URL_UPDATE_PRODUK = "https://gudangaram.000webhostapp.com/api-kasir-up/produk/update.php";
    public static final String URL_DELETE_PRODUK = "https://gudangaram.000webhostapp.com/api-kasir-up/produk/destroy.php";

    // TRANSAKSI
    public static final String URL_SHOW_TRANSAKSI = "https://gudangaram.000webhostapp.com/api-kasir-up/transaksi/index.php";
    public static final String URL_STORE_TRANSAKSI = "https://gudangaram.000webhostapp.com/api-kasir-up/transaksi/store.php";
    public static final String URL_EDIT_TRANSAKSI = "https://gudangaram.000webhostapp.com/api-kasir-up/transaksi/edit.php?id=";
    public static final String URL_UPDATE_TRANSAKSI = "https://gudangaram.000webhostapp.com/api-kasir-up/transaksi/update.php";
    public static final String URL_DELETE_TRANSAKSI = "https://gudangaram.000webhostapp.com/api-kasir-up/transaksi/destroy.php";

    public static final String URL_EXPORT_TRANSAKSI = "https://gudangaram.000webhostapp.com/api-kasir-up/transaksi/export.php";

    // DATA

    // Lapangan
    public static final String TAG_PRODUK_ID = "id";
    public static final String TAG_PRODUK_NAMA = "nama";
    public static final String TAG_PRODUK_DESKRIPSI = "deskripsi";
    public static final String TAG_PRODUK_HARGA = "harga";
    public static final String TAG_PRODUK_STOK = "stok";
    public static final String TAG_PRODUK_CREATED_AT = "created_at";

    // Penyewaan
    public static final String TAG_TRANSAKSI_ID = "id";
    public static final String TAG_TRANSAKSI_ID_PRODUK = "id_produk";
    public static final String TAG_TRANSAKSI_NAMA_PRODUK = "nama_produk";
    public static final String TAG_TRANSAKSI_HARGA = "harga";
    public static final String TAG_TRANSAKSI_KUANTITAS = "kuantitas";
    public static final String TAG_TRANSAKSI_TOTAL_HARGA = "total_harga";
    public static final String TAG_TRANSAKSI_UANG_BAYAR = "uang_bayar";
    public static final String TAG_TRANSAKSI_UANG_KEMBALIAN = "uang_kembalian";
    public static final String TAG_TRANSAKSI_TANGGAL_PEMBELIAN = "tanggal_pembelian";
    public static final String TAG_TRANSAKSI_FORMATTED_TANGGAL_PEMBELIAN = "formatted_date";

    public static final String TAG_JSON_ARRAY = "result";

    // Login
    public static final String TAG_CREDENTIAL_EMAIL = "email";
    public static final String TAG_CREDENTIAL_PASSWORD = "password";
}