package com.dsapps.wallpaperapp;

import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        Handler handler=new Handler();
        handler.postDelayed(r, 1500);

    }

    Runnable r=new Runnable() {
        @Override
        public void run() {
            Intent intent=new Intent(SplashActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    };
}
