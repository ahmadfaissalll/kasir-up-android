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

import java.util.HashMap;

public class Login extends AppCompatActivity {

    EditText emailInput, passwordInput;

    private String email, password;

    Button btnLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        emailInput = findViewById(R.id.email);
        passwordInput = findViewById(R.id.password);

        btnLogin = findViewById(R.id.button);

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                email = emailInput.getText().toString().trim();
                password = passwordInput.getText().toString().trim();

                if ( !email.equals("") && !password.equals("") ) {
                    login(email, password);
                } else {
                    Toast.makeText(Login.this, "Email dan password harus diisi",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login(String email, String password) {
        class GetJSON extends AsyncTask<Void, Void, String> {

            ProgressDialog loading;

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loading = ProgressDialog.show(Login.this, "Memeriksa kredensial", "Mohon Tunggu...", false, false);
            }

            @Override
            protected void onPostExecute(String s) {
                super.onPostExecute(s);
                loading.dismiss();

                if ( s.equals("true") ) {
                    Toast.makeText(Login.this, "Login berhasil", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(Login.this, DashboardAdmin.class));
                } else {
                    Toast.makeText(Login.this, "Email atau Password salah", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            protected String doInBackground(Void... v) {

                HashMap<String, String> params = new HashMap<>();

                params.put(Konfigurasi.TAG_CREDENTIAL_EMAIL, email);
                params.put(Konfigurasi.TAG_CREDENTIAL_PASSWORD, password);

                RequestHandler rh = new RequestHandler();

                String response = rh.sendPostRequest(Konfigurasi.URL_LOGIN, params);

                return response;
            }
        }

        GetJSON gj = new GetJSON();
        gj.execute();
    }
}