/*******************************************************************************
 * Copyright (c) 2017 GomFactory, Inc. All Rights Reserved.
 ******************************************************************************/

package com.gomfactory.adpie.sample;

import android.content.Context;
import android.content.res.Configuration;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.core.view.WindowInsetsControllerCompat;

import com.gomfactory.adpie.sdk.AdPieError;
import com.gomfactory.adpie.sdk.AdPieSDK;
import com.gomfactory.adpie.sdk.PrerollVideoAd;
import com.gomfactory.adpie.sdk.videoads.FinishState;
import com.gomfactory.adpie.sdk.videoads.VideoAdPlaybackListener;
import com.gomfactory.adpie.sdk.videoads.VideoAdView;
import com.gomfactory.adpiex.sample.R;

public class PrerollVideoAdActivity extends AppCompatActivity {

   public static final String TAG = PrerollVideoAdActivity.class.getSimpleName();

   private PrerollVideoAd prerollVideoAd;
   private VideoAdView videoAdView;
   private VideoView videoView;
   private View contentContainer;
   private View prerollRoot;
   private View prerollContentRoot;
   private View textAppName;
   private View textVersion;
   private View textMediaId;
   private View textSlot;
   private ImageButton buttonPrerollPlay;

   @Override
   protected void onCreate(Bundle savedInstanceState) {
      super.onCreate(savedInstanceState);

      EdgeToEdge.enable(this);
      setContentView(R.layout.activity_preroll_video);
      ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
         Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
         boolean isLandscape =
                 getResources().getConfiguration().orientation == Configuration.ORIENTATION_LANDSCAPE;
         if (isLandscape) {
            v.setPadding(0, 0, 0, 0);
         } else {
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
         }
         return insets;
      });

      TextView tvName = (TextView) findViewById(R.id.text_app_name);
      tvName.setText(getString(R.string.app_name));
      textAppName = tvName;

      TextView tvVersion = (TextView) findViewById(R.id.text_version);
      tvVersion.setText("AdPie SDK Version : " + AdPieSDK.getInstance().getVersion());
      textVersion = tvVersion;

      TextView tvMediaId = (TextView) findViewById(R.id.text_media_id);
      tvMediaId.setText("Media ID : " + getString(R.string.mid));
      textMediaId = tvMediaId;

      TextView tvPrerollSlotId = (TextView) findViewById(R.id.text_slot);
      tvPrerollSlotId.setText("Slot ID : " + getString(R.string.preroll_video_sid));
      textSlot = tvPrerollSlotId;

      contentContainer = findViewById(R.id.content_container);
      prerollRoot = findViewById(R.id.preroll_root);
      prerollContentRoot = findViewById(R.id.preroll_content_root);

      videoView = (VideoView) findViewById(R.id.video_view);
      videoView.animate().alpha(1);
      videoView.setOnCompletionListener(mediaPlayer -> showPlayButton());

      videoAdView = (VideoAdView) findViewById(R.id.video_adview);

      prerollVideoAd = new PrerollVideoAd(this, getString(R.string.preroll_video_sid), videoAdView);
      prerollVideoAd.setAdListener(new PrerollVideoAd.AdListener() {
         @Override
         public void onAdLoaded() {
            if (prerollVideoAd.isLoaded()) {
               hidePlayButton();
               videoView.setVisibility(View.GONE);
               prerollVideoAd.show();
            }
         }

         @Override
         public void onAdFailedToLoad(int errorCode) {

            printMessage(PrerollVideoAdActivity.this, "Preroll onAdFailedToLoad "
                    + AdPieError.getMessage(errorCode));

            playCustomVideo();
         }

         @Override
         public void onAdClicked() {
            printMessage(PrerollVideoAdActivity.this, "Preroll onAdClicked");
         }
      });

      prerollVideoAd.setVideoAdPlaybackListener(new VideoAdPlaybackListener() {
         @Override
         public void onVideoAdStarted() {
            printMessage(PrerollVideoAdActivity.this, "Preroll onVideoAdStarted");
            hidePlayButton();

            // 필요한 경우 비디오 음소거 처리 (음소거 토글 기능에 대한 UI 개별 구현 필요)
            // videoAdView.mute();
         }

         @Override
         public void onVideoFinished(FinishState finishState) {
            printMessage(PrerollVideoAdActivity.this, "Preroll onVideoFinished : " + finishState);

            switch (finishState) {
               case ERROR:
               case SKIPPED:
               case COMPLETED:
                  playCustomVideo();
                  break;
            }
         }
      });

      buttonPrerollPlay = (ImageButton) findViewById(R.id.button_preroll_play);
      buttonPrerollPlay.setOnClickListener(new View.OnClickListener() {

         @Override
         public void onClick(View view) {
            if (prerollVideoAd != null) {
               if (videoView.isPlaying() || prerollVideoAd.isPlaying()) {
                  Toast.makeText(PrerollVideoAdActivity.this, "Wait until video is finished.", Toast.LENGTH_SHORT).show();
                  return;
               }

               hidePlayButton();
               prerollVideoAd.load();
            } else {
               playCustomVideo();
            }
         }
      });

      applyOrientationLayout(getResources().getConfiguration().orientation);
   }

   public void playCustomVideo() {
      hidePlayButton();
      videoView.setVisibility(View.VISIBLE);

      String path = "android.resource://" + getPackageName() + "/" + R.raw.sample;
      Uri uri = Uri.parse(path);
      videoView.setVideoURI(uri);

      videoView.start();
   }

   public void printMessage(Context context, String message) {
      Log.d(TAG, message);

      if (context != null) {
         Toast.makeText(context, message, Toast.LENGTH_SHORT).show();
      }
   }

   @Override
   public void onConfigurationChanged(Configuration newConfig) {
      super.onConfigurationChanged(newConfig);
      applyOrientationLayout(newConfig.orientation);
   }

   private void applyOrientationLayout(int orientation) {
      boolean isLandscape = orientation == Configuration.ORIENTATION_LANDSCAPE;

      textAppName.setVisibility(isLandscape ? View.GONE : View.VISIBLE);
      textVersion.setVisibility(isLandscape ? View.GONE : View.VISIBLE);
      textMediaId.setVisibility(isLandscape ? View.GONE : View.VISIBLE);
      textSlot.setVisibility(isLandscape ? View.GONE : View.VISIBLE);

      updateContentContainerLayout(isLandscape);
      updateVideoContainerLayout(isLandscape);
      updateSystemBars(isLandscape);
      syncPlayButtonVisibility();
   }

   private void updateContentContainerLayout(boolean isLandscape) {
      RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) contentContainer.getLayoutParams();

      if (isLandscape) {
         params.addRule(RelativeLayout.BELOW, 0);
         params.addRule(RelativeLayout.ALIGN_PARENT_TOP, RelativeLayout.TRUE);
         params.setMargins(0, 0, 0, 0);
      } else {
         params.addRule(RelativeLayout.ALIGN_PARENT_TOP, 0);
         params.addRule(RelativeLayout.BELOW, R.id.text_slot);
         params.setMargins(0, dpToPx(10), 0, 0);
      }

      contentContainer.setLayoutParams(params);
      contentContainer.setPadding(0, 0, 0, isLandscape ? 0 : dpToPx(10));
   }

   private void updateVideoContainerLayout(boolean isLandscape) {
      View videoContainer = (View) videoAdView.getParent();
      ConstraintLayout.LayoutParams params =
              (ConstraintLayout.LayoutParams) videoContainer.getLayoutParams();

      if (isLandscape) {
         params.width = 0;
         params.height = 0;
         params.dimensionRatio = null;
         params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
         params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
         params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
         params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
         params.verticalBias = 0.5f;
      } else {
         params.width = 0;
         params.height = 0;
         params.dimensionRatio = "1280:720";
         params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID;
         params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID;
         params.startToStart = ConstraintLayout.LayoutParams.PARENT_ID;
         params.endToEnd = ConstraintLayout.LayoutParams.PARENT_ID;
         params.verticalBias = 0.3f;
      }

      videoContainer.setLayoutParams(params);
   }

   private void updateSystemBars(boolean isLandscape) {
      setRootPadding(isLandscape);
      ViewCompat.requestApplyInsets(findViewById(R.id.main));

      WindowInsetsControllerCompat controller =
              ViewCompat.getWindowInsetsController(findViewById(R.id.main));
      if (controller == null) {
         return;
      }

      if (isLandscape) {
         controller.hide(WindowInsetsCompat.Type.systemBars());
         controller.setSystemBarsBehavior(
                 WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE);
      } else {
         controller.show(WindowInsetsCompat.Type.systemBars());
      }
   }

   private void setRootPadding(boolean isLandscape) {
      int horizontalPadding = isLandscape ? 0 : dpToPx(16);
      int verticalPadding = isLandscape ? 0 : dpToPx(16);
      prerollRoot.setPadding(horizontalPadding, verticalPadding, horizontalPadding, verticalPadding);
      prerollContentRoot.setPadding(0, 0, 0, 0);
   }

   private int dpToPx(int dp) {
      return Math.round(dp * getResources().getDisplayMetrics().density);
   }

   private void hidePlayButton() {
      buttonPrerollPlay.setVisibility(View.GONE);
   }

   private void showPlayButton() {
      buttonPrerollPlay.setVisibility(View.VISIBLE);
   }

   private void syncPlayButtonVisibility() {
      if (videoView.isPlaying() || (prerollVideoAd != null && prerollVideoAd.isPlaying())) {
         hidePlayButton();
      } else {
         showPlayButton();
      }
   }

   @Override
   protected void onDestroy() {
      if (prerollVideoAd != null) {
         prerollVideoAd.destroy();
         prerollVideoAd = null;
      }

      super.onDestroy();
   }
}
