package com.sda.bob.dictation;

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
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

//import com.google.android.gms.ads.AdRequest;
//import com.google.android.gms.ads.AdView;
//import com.google.android.gms.ads.LoadAdError;
//import com.google.android.gms.ads.interstitial.InterstitialAd;
//import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;

import java.util.Locale;
import java.util.Objects;
import java.util.Random;

public class AlphActivity extends AppCompatActivity {
    Spinner countSpinner;
    Button startButton;

    LinearLayout sumLayout1;
    LinearLayout sumLayout2;
    LinearLayout sumLayout3;

    TextView answerText;
    long startTime = 0;
    long endTime = 0;
    int correctAnswer = 0;
    TextView helpText;

    Random random;

//    AdView homepageBannerAd;
//    InterstitialAd nSumAd;

    int status = 0;
    int answer = 0;

    @SuppressLint("SetTextI18n")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        setContentView(R.layout.activity_alph);
        Objects.requireNonNull(getSupportActionBar()).hide();

        helpText = findViewById(R.id.helpText);
        countSpinner = findViewById(R.id.countSpinner);
        startButton = findViewById(R.id.startButton);
        sumLayout1 = findViewById(R.id.sumLayout1);
        sumLayout2 = findViewById(R.id.sumLayout2);
        sumLayout3 = findViewById(R.id.sumLayout3);
        answerText = findViewById(R.id.answerText);
//        homepageBannerAd = findViewById(R.id.homepageBannerAd);

//        AdRequest adRequest = new AdRequest.Builder().build();
//        homepageBannerAd.loadAd(adRequest);

        random = new Random();
        
        helpText.setOnClickListener(view -> {
            Dialog helpDialog = new Dialog(AlphActivity.this);
            helpDialog.setContentView(R.layout.dialog_instructions);

            LinearLayout instructionLayout = helpDialog.findViewById(R.id.instructionLayout);

            TextView step1Text = new TextView(instructionLayout.getContext());
            step1Text.setText("Step 1");
            step1Text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            step1Text.setTextSize(TypedValue.COMPLEX_UNIT_SP, 17);
            step1Text.setTextAppearance(Typeface.BOLD);
            instructionLayout.addView(step1Text);

            TextView step1 = new TextView(instructionLayout.getContext());
            step1.setText("Select the Count / Length of the Sum\n\nThe Alphabet Values are as follows:");
            step1.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
            instructionLayout.addView(step1);

            TableLayout alphabetMapping = new TableLayout(instructionLayout.getContext());
            for (int i = 0 ; i <= 5 ; i++) {
                TableRow newRow = new TableRow(alphabetMapping.getContext());
                for (int a = 1 ; a <= 2 ; a++) {
                    int n = 2 * i + a;
                    if (n <= 9) {
                        TextView nText = new TextView(newRow.getContext());
                        nText.setText(String.format(Locale.ENGLISH, "%c = %d", n + 64, n));
                        nText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        nText.setPadding(0, 0, 48, 0);
                        newRow.addView(nText);
                    } else {
                        TextView spacerText = new TextView(newRow.getContext());
                        spacerText.setText("");
                        spacerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 13);
                        spacerText.setPadding(0, 0, 32, 0);
                        newRow.addView(spacerText);
                    }
                }
                alphabetMapping.addView(newRow);
            }
            instructionLayout.addView(alphabetMapping);

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
//                    if (MainActivity.sumCount == MainActivity.adTrigger - 1)
//                        prepareAd(adRequest);
                    break;

                case 1:
                    clearSum();
//                    if (MainActivity.sumCount >= MainActivity.adTrigger) {
//                        if (nSumAd != null) {
//                            nSumAd.show(AlphActivity.this);
//                            MainActivity.sumCount = 0;
//                            nSumAd = null;
//                        } else
//                            MainActivity.sumCount--;
//                    }
                    break;
            }
        });

        answerText.setOnClickListener(view -> {
            answerText.setTextColor(getColor(R.color.answer));
            answerText.setText(String.valueOf(answer));
            answerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 50);

            if (endTime == 0)
                endTime = System.currentTimeMillis();

            AlertDialog.Builder timeDialog = new AlertDialog.Builder(AlphActivity.this);
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

                    MediaPlayer effectPlayer = MediaPlayer.create(AlphActivity.this, R.raw.effect_positive);
                    Dialog positiveDialog = new Dialog(AlphActivity.this);
                    positiveDialog.setContentView(R.layout.dialog_positive);
                    positiveDialog.show();
                    effectPlayer.start();

                    new Handler().postDelayed(positiveDialog::dismiss, 1500);
                });
                timeDialog.setNegativeButton("No", ((dialogInterface, i) -> {
                    correctAnswer = -1;
                    Toast.makeText(AlphActivity.this, "Better Luck Next Time!", Toast.LENGTH_SHORT).show();
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

    int genRand(int currentSum) {
        int val = random.nextInt(9) + 1;

        if (random.nextBoolean())
            return val;
        else {
            if (currentSum - val > 0)
                return -val;
            else
                return genRand(currentSum);
        }
    }

    @SuppressLint("SetTextI18n")
    void clearSum() {
        helpText.setVisibility(View.VISIBLE);
        startButton.setText("Start");
        sumLayout1.removeAllViews();
        sumLayout2.removeAllViews();
        sumLayout3.removeAllViews();
        answerText.setTextColor(getColor(R.color.black));
        answerText.setText("View Answer");
        answerText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 36);
        answer = correctAnswer = 0;
        answerText.setVisibility(View.GONE);
        startTime = endTime = 0;
        status = 0;
    }

    @SuppressLint({"DefaultLocale", "SetTextI18n"})
    void createSum() {
        int count = countSpinner.getSelectedItemPosition();

        if (count == 0)
            Toast.makeText(getApplicationContext(), "Please select the number of rows!", Toast.LENGTH_SHORT).show();
        else {
//            MainActivity.sumCount++;
            clearSum();
            helpText.setVisibility(View.GONE);

            int sum = 0;
            for (int i = 0; i < Integer.parseInt(String.valueOf(countSpinner.getItemAtPosition(count))); i++) {
                int digit = genRand(sum);
                sum += digit;

                TextView digitText = new TextView(getApplicationContext());

                if (digit > 0) {
                    digitText.setText(String.format("%c", digit + 64));
                    digitText.setTextColor(getColor(R.color.positive));
                } else {
                    digitText.setText(String.format("-%c", -digit + 64));
                    digitText.setTextColor(getColor(R.color.negative));
                }
                digitText.setTextSize(TypedValue.COMPLEX_UNIT_SP, 24);
                digitText.setTextAlignment(View.TEXT_ALIGNMENT_TEXT_END);

                if (i < 10)
                    sumLayout1.addView(digitText);
                else if (i < 20)
                    sumLayout2.addView(digitText);
                else
                    sumLayout3.addView(digitText);
            }

            if (sum < 0)
                createSum();
            else {
                answer = sum;
                answerText.setVisibility(View.VISIBLE);
                startButton.setText("Reset");
            }

            status = 1;
            startTime = System.currentTimeMillis();
        }
    }

//    void prepareAd(AdRequest adRequest) {
//        InterstitialAd.load(getApplicationContext(), "ca-app-pub-8122105205019110/1975225139", adRequest, new InterstitialAdLoadCallback() {
//            @Override
//            public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
//                super.onAdLoaded(interstitialAd);
//
//                nSumAd = interstitialAd;
//            }
//
//            @Override
//            public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
//                super.onAdFailedToLoad(loadAdError);
//
//                nSumAd = null;
//            }
//        });
//    }
}