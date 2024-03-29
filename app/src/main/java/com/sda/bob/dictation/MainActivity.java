package com.sda.bob.dictation;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.StrictMode;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;

import java.util.Objects;

@SuppressWarnings("CommentedOutCode")
public class MainActivity extends AppCompatActivity {
    Button addButton;
    Button mulButton;
    Button divButton;
    Button alphButton;
    ImageButton appInfoButton;

//    AdView homepageBannerAd;

//    static int sumCount = 0;
//    static int adTrigger;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_main);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowCustomEnabled(true);
        getSupportActionBar().setCustomView(R.layout.custom_action_bar);

        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);

        addButton = findViewById(R.id.addButton);
        mulButton = findViewById(R.id.mulButton);
        divButton = findViewById(R.id.divButton);
        alphButton = findViewById(R.id.alphButton);
        appInfoButton = findViewById(R.id.appInfoButton);
//        homepageBannerAd = findViewById(R.id.homepageBannerAd);

//        if (!isNetworkConnected()) {
//            Toast.makeText(getApplicationContext(), "Please Turn on Internet to use the app!", Toast.LENGTH_SHORT).show();
//            onBackPressed();
//        }

//        adTrigger = new Random().nextInt(3) + 5;

        /*RequestConfiguration configuration = new RequestConfiguration.Builder().setTestDeviceIds(Arrays.asList("E31DD0DF9B5CFE51E75ABA11B514B326")).build();
        MobileAds.setRequestConfiguration(configuration);
        MobileAds.initialize(this, initializationStatus -> {});*/

//        AdRequest adRequest = new AdRequest.Builder().build();
//        homepageBannerAd.loadAd(adRequest);

        addButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AddActivity.class)));
        mulButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, MulActivity.class)));
        alphButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, AlphActivity.class)));
        divButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, DivActivity.class)));

//        appInfoButton.setOnClickListener(view -> startActivity(new Intent(MainActivity.this, InfoActivity.class)));
        appInfoButton.setOnClickListener(view -> {
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("market://dev?id=5751796953368988342")));
            } catch (ActivityNotFoundException activityNotFoundException) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://play.google.com/store/apps/dev?id=5751796953368988342")));
            }
        });
    }

//    private boolean isNetworkConnected() {
//        try {
//            URL googlePing = new URL("http://www.google.com");
//            HttpURLConnection pingCheck = (HttpURLConnection) googlePing.openConnection();
//            pingCheck.setRequestMethod("GET");
//            pingCheck.setConnectTimeout(2000);
//            pingCheck.disconnect();
//
//            return pingCheck.getResponseCode() == 200;
//        } catch (IOException e) {
//            e.printStackTrace();
//            return false;
//        }
//    }
}