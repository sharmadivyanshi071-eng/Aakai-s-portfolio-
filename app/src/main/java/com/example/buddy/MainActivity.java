package com.example.buddy;

import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.BatteryManager;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.telecom.TelecomManager;
import android.widget.Button;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {
    private SpeechRecognizer speechRecognizer;
    private TextToSpeech tts;
    private TextView welcomeText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        welcomeText = findViewById(R.id.welcomeText);
        Button actionButton = findViewById(R.id.actionButton);

        // 1. Initialize Text-to-Speech (Buddy speaks back)
        tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) tts.setLanguage(new Locale("en", "IN"));
        });

        // 2. Initialize Voice Recognition (Buddy listens)
        speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        final Intent speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");

        actionButton.setOnClickListener(v -> {
            welcomeText.setText("Listening...");
            speechRecognizer.startListening(speechIntent);
        });

        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> data = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (data != null) {
                    handleCommand(data.get(0).toLowerCase());
                }
            }
            @Override public void onReadyForSpeech(Bundle b) {} @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float r) {} @Override public void onBufferReceived(byte[] b) {}
            @Override public void onEndOfSpeech() {} @Override public void onError(int e) { welcomeText.setText("Try again..."); }
            @Override public void onPartialResults(Bundle p) {} @Override public void onEvent(int t, Bundle b) {}
        });
    }

    private void handleCommand(String cmd) {
        if (cmd.contains("battery")) {
            speak("Your battery is at " + getBatteryLevel());
        } else if (cmd.contains("lock my phone")) {
            lockPhone();
        } else if (cmd.contains("open")) {
            String appToOpen = cmd.replace("open ", "").trim();
            openApp(appToOpen);
        } else if (cmd.contains("pick my phone") || cmd.contains("answer")) {
            answerCall();
        } else {
            speak("I heard " + cmd + ". What else can I do?");
        }
    }

    private String getBatteryLevel() {
        Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
        int level = batteryStatus != null ? batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1) : -1;
        return level + " percent";
    }

    private void lockPhone() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName admin = new ComponentName(this, DeviceAdmin.class);
        if (dpm != null && dpm.isAdminActive(admin)) {
            dpm.lockNow();
        } else {
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, admin);
            startActivity(intent);
        }
    }

    private void openApp(String name) {
        PackageManager pm = getPackageManager();
        List<ApplicationInfo> apps = pm.getInstalledApplications(0);
        for (ApplicationInfo app : apps) {
            if (pm.getApplicationLabel(app).toString().toLowerCase().contains(name)) {
                Intent launchIntent = pm.getLaunchIntentForPackage(app.packageName);
                if (launchIntent != null) startActivity(launchIntent);
                return;
            }
        }
        speak("I couldn't find an app named " + name);
    }

    private void answerCall() {
        TelecomManager tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
        try { 
            if (tm != null) tm.acceptRingingCall(); 
        } catch (SecurityException e) { 
            speak("Permission denied for calls."); 
        }
    }

    private void speak(String text) {
        welcomeText.setText(text);
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}

        
        
