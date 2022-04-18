package com.aek.whatsapp.vista;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;

import com.aek.whatsapp.vista.cuenta.LoginActivity;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SplashScreenActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

        if (user == null)
        {
            startNewAct(LoginActivity.class);
        }
        else
        {
            startNewAct(MainActivity.class);
        }
    }

    private void startNewAct(Class clase)
    {
        startActivity(new Intent(SplashScreenActivity.this, clase));
        finish();
    }
}