package com.gomfactory.adpie.sample;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.gomfactory.adpie.sdk.AdPieSDK;
import com.gomfactory.adpie.sdk.AppOpenAd;
import com.gomfactory.adpie.sdk.AppOpenAdService;
import com.gomfactory.adpie.sdk.util.AdPieLog;
import com.gomfactory.adpiex.sample.R;

public class SplashActivity extends AppCompatActivity {

    private static final String TAG = SplashActivity.class.getSimpleName();

    private static final long AD_START_TIMEOUT_MS = 5000L;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable openLoading = this::requestSplashLoadingActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (!isTaskRoot()
                && Intent.ACTION_MAIN.equals(intent.getAction())
                && intent.hasCategory(Intent.CATEGORY_LAUNCHER)) {
            finish();
            return;
        }

        setContentView(R.layout.activity_splash);

        // Insert your AdPie-Media-ID
        AdPieLog.setLogEnable(true);
        AdPieLog.setLogLevel(Log.DEBUG);
        AdPieSDK.getInstance().initialize(getApplicationContext(), getString(R.string.mid));

        handler.postDelayed(openLoading, AD_START_TIMEOUT_MS);

        AppOpenAdService.getInstance().show(this, getString(R.string.app_open_sid), new AppOpenAdService.OnAppOpenAdListener() {
            @Override
            public void onAdShown() {
                Log.d(TAG, "AppOpenAd onAdShown");
                handler.removeCallbacks(openLoading);
            }

            @Override
            public void onAdClicked() {
                Log.d(TAG, "AppOpenAd onAdClicked");
            }

            @Override
            public void onAdDismissed() {
                Log.d(TAG, "AppOpenAd onAdDismissed");
                runOnUiThread(openLoading);
            }

            @Override
            public void onAdFailedToShow() {
                Log.d(TAG, "AppOpenAd onAdFailedToShow");
                runOnUiThread(openLoading);
            }
        }, new AppOpenAd.OnPaidEventListener() {
            @Override
            public void onPaidEvent(double ecpm) {
                Log.d(TAG, "AppOpenAd onPaidEvent : " + ecpm);
            }
        });
    }

    private void requestSplashLoadingActivity() {
        handler.removeCallbacks(openLoading);
        if (isFinishing() || isDestroyed()) {
            return;
        }
        startActivity(new Intent(this, SplashLoadingActivity.class));
        finish();
    }

    @Override
    protected void onDestroy() {
        handler.removeCallbacks(openLoading);
        super.onDestroy();
    }
}
