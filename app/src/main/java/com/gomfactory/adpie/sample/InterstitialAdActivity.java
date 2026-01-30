/*******************************************************************************
 * Copyright (c) 2016 GomFactory, Inc. All Rights Reserved.
 ******************************************************************************/

package com.gomfactory.adpie.sample;

import android.content.Context;
import android.os.Bundle;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.gomfactory.adpie.sdk.AdPieError;
import com.gomfactory.adpie.sdk.AdPieSDK;
import com.gomfactory.adpie.sdk.InterstitialAd;
import com.gomfactory.adpie.sdk.videoads.FinishState;
import com.gomfactory.adpie.sdk.videoads.VideoAdPlaybackListener;
import com.gomfactory.adpiex.sample.R;

public class InterstitialAdActivity extends AppCompatActivity
        implements InterstitialAd.InterstitialAdListener {

    public static final String TAG = InterstitialAdActivity.class.getSimpleName();
    private static final String NO_SLOT_ID = "";

    private InterstitialAd interstitialAd;

    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_interstitial);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Insert your AdPie-Slot-ID
        String mSlotId = getIntent().getStringExtra("key");

        TextView tvName = (TextView) findViewById(R.id.text_app_name);
        tvName.setText(getString(R.string.app_name));

        TextView tvVersion = (TextView) findViewById(R.id.text_version);
        tvVersion.setText("AdPie SDK Version : " + AdPieSDK.getInstance().getVersion());

        TextView tvMediaId = (TextView) findViewById(R.id.text_media_id);
        tvMediaId.setText("Media ID : " + getString(R.string.mid));

        TextView tvSlotId = (TextView) findViewById(R.id.text_slot);
        tvSlotId.setText("Slot ID : " + mSlotId);

        interstitialAd = new InterstitialAd(this, mSlotId);
        interstitialAd.setAdListener(this);

        button = (Button) findViewById(R.id.button_interstitial_load);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interstitialAd.load();
            }
        });

        findViewById(R.id.button_interstitial_show).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                interstitialAd.show();
            }
        });
    }

    @Override
    protected void onDestroy() {

        if (interstitialAd != null) {
            interstitialAd.destroy();
            interstitialAd = null;
        }

        super.onDestroy();
    }

    @Override
    public void onAdLoaded() {
        printMessage(InterstitialAdActivity.this, "Interstitial onAdLoaded");
    }

    @Override
    public void onAdFailedToLoad(int errorCode) {
        printMessage(InterstitialAdActivity.this, "Interstitial onAdFailedToLoad "
                + AdPieError.getMessage(errorCode));
    }

    @Override
    public void onAdShown() {
        printMessage(InterstitialAdActivity.this, "Interstitial onAdShown");
    }

    @Override
    public void onAdClicked() {
        printMessage(InterstitialAdActivity.this, "Interstitial onAdClicked");
    }

    @Override
    public void onAdDismissed() {
        printMessage(InterstitialAdActivity.this, "Interstitial onAdDismissed");
    }

    @Override
    public void onAdFailedToShow() {
        printMessage(InterstitialAdActivity.this, "Interstitial onAdFailedToShow");
    }


    public void printMessage(Context context, String message) {
        Log.d(TAG, message);

        if (context != null) {
            Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
        }
    }
}
