package com.allmy.allstatusdownloader.Activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.allmy.allstatusdownloader.R;

public class SettingActivity extends AppCompatActivity {

    ImageView ivBack;
    RelativeLayout relRateUs, relShare, relPrivacy, relSuggestion, relMore;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        init();

        ivBack.setOnClickListener(view -> finish());

        relRateUs.setOnClickListener(view -> {
            try {
                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("market://details?id=" + getPackageName()));
                startActivity(intent1);
            } catch (Exception ex) {
                Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/details?id=" + getPackageName()));
                startActivity(intent1);
            }
        });
        relShare.setOnClickListener(view -> {
            Intent intentShare = new Intent(Intent.ACTION_SEND);
            intentShare.setType("text/plain");
            intentShare.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            intentShare.putExtra(Intent.EXTRA_TEXT, "https://play.google.com/store/apps/details?id=" + getPackageName());
            startActivity(intentShare);
        });

        relPrivacy.setOnClickListener(view -> {
            Intent intent1 = new Intent(Intent.ACTION_VIEW, Uri.parse("https://sites.google.com/view/all-status-story-saver-in-mob/home"));//replace with your privacy policy url
            startActivity(intent1);
        });

        relSuggestion.setOnClickListener(view -> {
            try {
                Intent intent = new Intent("android.intent.action.SEND");
                intent.putExtra("android.intent.extra.EMAIL", new String[]{"sdigvijay904@gmail.com"});
                intent.setType("text/plain");
                ResolveInfo resolveInfo = null;
                for (ResolveInfo resolveInfo2 : getPackageManager().queryIntentActivities(intent, 0)) {
                    if (resolveInfo2.activityInfo.packageName.endsWith(".gm") || resolveInfo2.activityInfo.name.toLowerCase().contains("gmail")) {
                        resolveInfo = resolveInfo2;
                    }
                }
                if (resolveInfo != null) {
                    intent.setClassName(resolveInfo.activityInfo.packageName, resolveInfo.activityInfo.name);
                }
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
            }
        });

        relMore.setOnClickListener(view -> {
            try {
                startActivity(new Intent("android.intent.action.VIEW", Uri.parse("https://play.google.com/store/apps/developer?id=Favoriteturn")));
            } catch (ActivityNotFoundException e) {
                e.printStackTrace();
            }
        });
    }
    public void init() {
        ivBack = findViewById(R.id.ivBack);
        relRateUs = findViewById(R.id.relRateUs);
        relShare = findViewById(R.id.relShare);
        relPrivacy = findViewById(R.id.relPrivacy);
        relSuggestion = findViewById(R.id.relSuggestion);
        relMore = findViewById(R.id.relMore);
    }
}