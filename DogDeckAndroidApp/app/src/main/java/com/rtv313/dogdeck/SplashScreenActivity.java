package com.rtv313.dogdeck;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import DataBase.DBManager;


public class SplashScreenActivity extends AppCompatActivity {

    // Splash screen timer
    private static int SPLASH_TIME_OUT = 2000;
    private DBManager dbManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_screen);

        // Create or Update Database
        dbManager = new DBManager(this,this);
        dbManager.open();
        dbManager.close();

        new Handler().postDelayed(new Runnable() {

            @Override
            public void run() {
                Intent i = new Intent(SplashScreenActivity.this, MyDogsActivity.class);
                i.putExtra("ComeFromAllMatches",false);
                startActivity(i);
                finish();
            }
        }, SPLASH_TIME_OUT);
    }
}
