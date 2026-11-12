package com.gomfactory.adpie.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import androidx.appcompat.app.AppCompatActivity;

import com.gomfactory.adpiex.sample.R;

public class SplashLoadingActivity extends AppCompatActivity {

    private static final long LOADING_DURATION_MS = 3000L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable openMain = () -> {
        if (isFinishing()) {
            return;
        }
        startActivity(new Intent(this, MainActivity.class));
        finish();
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash_loading);
    }

    @Override
    protected void onResume() {
        super.onResume();
        handler.postDelayed(openMain, LOADING_DURATION_MS);
    }

    @Override
    protected void onPause() {
        handler.removeCallbacks(openMain);
        super.onPause();
    }
}
