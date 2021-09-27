package com.sda.bob.dictation;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Typeface;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.util.TypedValue;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class MulActivity extends AppCompatActivity {
    Spinner modeSpinner;
    Button startButton;
    TextView mulText;
    TextView helpText;

    TextView answerText;
    long startTime = 0;
    long endTime = 0;
    int correctAnswer = 0;

    int status = 0;
    int answer = 0;

    Random random;

    AdView homepageBannerAd;
    InterstitialAd nSumAd;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_mul);
        Objects.requireNonNull(getSupportActionBar()).hide();

        random = new Random();

        helpText = findViewById(R.id.helpText);
        modeSpinner = findViewById(R.id.modeSpinner);
        startButton = findViewById(R.id.startButton);
        mulText = findViewById(R.id.mulText);
        answerText = findViewById(R.id.answerText);
        homepageBannerAd = findViewById(R.id.homepageBannerAd);

        AdRequest adRequest = new AdRequest.Builder().build();
        homepageBannerAd.loadAd(adRequest);

        helpText.setOnClickListener(view -> {
            Dialog helpDialog = new Dialog(MulActivity.this);
            helpDialog.setContentView(R.layout.dialog_instructions);

            LinearLayout instructionLayout = helpDialog.findViewById(R.id.instructionLayout);

            TextView step1Text = new TextView(instructionLayout.getContext());
            step1Text.setText("Step 1");
            step1Text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            step1Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            step1Text.setTextAppearance(Typeface.BOLD);
            instructionLayout.addView(step1Text);

            TextView step1 = new TextView(instructionLayout.getContext());
            step1.setText("Select the Type of Sum / Problem\n\n1D : Single Digit Numbers\n2D : Double Digit Numbers\n3D : Triple Digit Numbers\n4D : Four Digit Number\n\n");
            step1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            instructionLayout.addView(step1);

            TextView step2Text = new TextView(instructionLayout.getContext());
            step2Text.setText("Step 2");
            step2Text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            step2Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            step2Text.setTextAppearance(Typeface.BOLD);
            instructionLayout.addView(step2Text);

            TextView step2 = new TextView(instructionLayout.getContext());
            step2.setText("Hit START to generate the sum\n\n");
            step2.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            instructionLayout.addView(step2);

            TextView step3Text = new TextView(instructionLayout.getContext());
            step3Text.setText("Step 3");
            step3Text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            step3Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            step3Text.setTextAppearance(Typeface.BOLD);
            instructionLayout.addView(step3Text);

            TextView step3 = new TextView(instructionLayout.getContext());
            //step3.setText("Type in the Answer and hit SUBMIT\n\nOn submitting a Correct answer, a Dialog will show the time taken to solve that question!");
            step3.setText("Tap on View Answer to reveal the right answer\n\nThe Time Taken will Pop-Up on your screen");
            step3.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            instructionLayout.addView(step3);

            helpDialog.show();
        });

        startButton.setOnClickListener(view -> {
            switch (status) {
                case 0:
                    createSum();
                    if (MainActivity.sumCount == MainActivity.adTrigger - 1)
                        prepareAd(adRequest);
                    break;

                case 1:
                    clearAns();
                    if (MainActivity.sumCount >= MainActivity.adTrigger) {
                        if (nSumAd != null) {
                            nSumAd.show(MulActivity.this);
                            MainActivity.sumCount = 0;
                            nSumAd = null;
                        } else
                            MainActivity.sumCount--;
                    }
                    break;
            }
        });

        answerText.setOnClickListener(view -> {
            answerText.setTextColor(getColor(R.color.answer));
            answerText.setText(String.valueOf(answer));
            answerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);

            if (endTime == 0)
                endTime = System.currentTimeMillis();

            AlertDialog.Builder timeDialog = new AlertDialog.Builder(MulActivity.this);
            timeDialog.setTitle("Answer is " + answer);

            if (correctAnswer == 0) {
                double timeTakenS = (float) ((endTime - startTime)) / 1000.0;

                if (timeTakenS < 60)
                    timeDialog.setMessage(String.format(Locale.ENGLISH, "\nTime Taken = %.3f s!\n\nDid you get it right?", timeTakenS));
                else {
                    int timeTakenM = (int)(timeTakenS / 60);
                    timeTakenS -= (timeTakenM * 60);
                    timeDialog.setMessage(String.format(Locale.ENGLISH, "\nTime Taken = %d m %.3f s!\n\nDid you get it right?", timeTakenM, timeTakenS));
                }

                timeDialog.setPositiveButton("Yeah!", (dialogInterface, i) -> {
                    correctAnswer = 1;

                    MediaPlayer effectPlayer = MediaPlayer.create(MulActivity.this, R.raw.effect_positive);
                    Dialog positiveDialog = new Dialog(MulActivity.this);
                    positiveDialog.setContentView(R.layout.dialog_positive);
                    positiveDialog.show();
                    effectPlayer.start();

                    new Handler().postDelayed(positiveDialog::dismiss, 1500);
                });
                timeDialog.setNegativeButton("No", ((dialogInterface, i) -> {
                    correctAnswer = -1;
                    Toast.makeText(MulActivity.this, "Better Luck Next Time!", Toast.LENGTH_SHORT).show();
                }));
            } else {
                if (correctAnswer == 1)
                    timeDialog.setMessage(String.format(Locale.ENGLISH, "\nTime Taken = %.3f s!\n\nGood Job!", (endTime - startTime) / 1000.0));
                else
                    timeDialog.setMessage(String.format(Locale.ENGLISH, "\nTime Taken = %.3f s!\n\nBetter Luck Next Time!", (endTime - startTime) / 1000.0));
            }

            timeDialog.show();
        });
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    void createSum() {
        int mode = modeSpinner.getSelectedItemPosition();

        if (mode == 0)
            Toast.makeText(getApplicationContext(), "Please Select the right type of sum!", Toast.LENGTH_SHORT).show();
        else {
            MainActivity.sumCount++;
            clearAns();
            helpText.setVisibility(View.GONE);

            int a, b;

            switch (mode) {
                case 1:
                    a = random.nextInt(8) + 2;
                    b = random.nextInt(8) + 2;
                    break;
                case 2:
                    a = random.nextInt(89) + 11;
                    b = random.nextInt(8) + 2;
                    break;
                case 3:
                    a = random.nextInt(899) + 101;
                    b = random.nextInt(8) + 2;
                    break;
                case 4:
                    a = random.nextInt(8999) + 1001;
                    b = random.nextInt(8) + 2;
                    break;
                case 5:
                    a = random.nextInt(89) + 11;
                    b = random.nextInt(89) + 11;
                    break;
                case 6:
                    a = random.nextInt(899) + 101;
                    b = random.nextInt(89) + 11;
                    break;
                case 7:
                    a = random.nextInt(8999) + 1001;
                    b = random.nextInt(89) + 11;
                    break;
                default:
                    a = b = 1;
            }

            mulText.setText(String.format("%d X %d =", a, b));
            answer = a * b;

            answerText.setVisibility(View.VISIBLE);
            startButton.setText("Reset");

            status = 1;
            startTime = System.currentTimeMillis();
        }
    }

    @SuppressLint("SetTextI18n")
    void clearAns() {
        helpText.setVisibility(View.VISIBLE);
        startButton.setText("Start");
        mulText.setText("");
        answerText.setTextColor(getColor(R.color.black));
        answerText.setText("View Answer");
        answerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
        answer = correctAnswer = 0;
        answerText.setVisibility(View.GONE);
        startTime = endTime = 0;
        status = 0;
    }

    void prepareAd(AdRequest adRequest) {
        InterstitialAd.load(getApplicationContext(), "ca-app-pub-8122105205019110/1975225139", adRequest, new InterstitialAdLoadCallback() {
            @Override
            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                super.onAdLoaded(interstitialAd);

                nSumAd = interstitialAd;
            }

            @Override
            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                super.onAdFailedToLoad(loadAdError);

                nSumAd = null;
            }
        });
    }
}