package com.dange.roomly;

import android.content.Intent;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.splashscreen.SplashScreen;

public class MainActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Install splash screen BEFORE super.onCreate()

        super.onCreate(savedInstanceState);

        // Set keep condition BEFORE setContentView

        setContentView(R.layout.activity_main);

        // Start next activity after delay
        findViewById(android.R.id.content).postDelayed(() -> {
            startActivity(new Intent(this, PGListActivity.class));
            finish();
        }, 2000); // 2 seconds delay
    }
}