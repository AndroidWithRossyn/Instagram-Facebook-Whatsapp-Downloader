package com.allmy.allstatusdownloader.Activity;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import androidx.annotation.Keep;
import androidx.appcompat.app.AppCompatActivity;

import com.allmy.allstatusdownloader.Others.Utils;
import com.allmy.allstatusdownloader.R;
import com.google.android.gms.ads.interstitial.InterstitialAd;
@Keep

public class MainActivity extends AppCompatActivity {

    LinearLayout lInsta, lFacebook, lTwitter, lWp, lWBuiness, lGB, lRoposo, lMoj;
    ImageView ivCreation, ivSetting;
    InterstitialAd mInterstitialAd;
    FrameLayout frameBanner;
    Utils utils;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        init();


        ivCreation.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, CreationActivity.class)));

        ivSetting.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, SettingActivity.class));
        });

        lInsta.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, InstaDownloadActivity.class));
        });

        lFacebook.setOnClickListener(view -> {
            startActivity(new Intent(MainActivity.this, FBDownloadActivity.class));
        });

        lTwitter.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OtherDownloadActivity.class);
            intent.putExtra("which", "TWITTER");
            startActivity(intent);
        });

        lWp.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WhatsappActivity.class);
            intent.putExtra("which", "WA");
            startActivity(intent);
        });

        lWBuiness.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WhatsappActivity.class);
            intent.putExtra("which", "WB");
            startActivity(intent);
        });

        lGB.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, WhatsappActivity.class);
            intent.putExtra("which", "WGB");
            startActivity(intent);
        });

        lMoj.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, OtherDownloadActivity.class);
            intent.putExtra("which", "MOJ");
            startActivity(intent);
        });


    }

    public void init() {
        lInsta = findViewById(R.id.lInsta);
        lFacebook = findViewById(R.id.lFacebook);
        lTwitter = findViewById(R.id.lTwitter);
        lWp = findViewById(R.id.lWp);
        lWBuiness = findViewById(R.id.lWB);
        lGB = findViewById(R.id.lGB);
        lRoposo = findViewById(R.id.lRoposo);
        lMoj = findViewById(R.id.lMoj);
        ivCreation = findViewById(R.id.ivCreation);
        frameBanner = findViewById(R.id.frameBanner);
        ivSetting = findViewById(R.id.ivSetting);

        utils=new Utils(MainActivity.this);
        utils.loadBanner(MainActivity.this,frameBanner);
    }

}