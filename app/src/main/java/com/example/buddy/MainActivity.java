package com.example.buddy;

import android.Manifest;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    private TextToSpeech buddyVoice;
    private static final int VOICE_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button btnTalk = findViewById(R.id.btnTalk);

        buddyVoice = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                buddyVoice.setLanguage(new Locale("en", "IN"));
                // This runs as soon as the app starts on your phone
                speak("Buddy is online. Welcome back, Master.");
            }
        });

        btnTalk.setOnClickListener(v -> startListening());
        
        // Request permissions for Call and Camera
        checkPermissions();
    }

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Buddy is listening, Master...");
        startActivityForResult(intent, VOICE_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == VOICE_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            handleCommand(result.get(0).toLowerCase());
        }
    }

    private void handleCommand(String cmd) {
        // 1. TIME AND DATE
        if (cmd.contains("time") || cmd.contains("date")) {
            String currentTime = new SimpleDateFormat("hh:mm a, EEEE, dd MMMM", Locale.getDefault()).format(new Date());
            speak("Master, it is currently " + currentTime);
        }

        // 2. BATTERY
        else if (cmd.contains("battery") || cmd.contains("power")) {
            Intent batteryStatus = registerReceiver(null, new IntentFilter(Intent.ACTION_BATTERY_CHANGED));
            int level = batteryStatus.getIntExtra(BatteryManager.EXTRA_LEVEL, -1);
            speak("Master, your battery is at " + level + " percent.");
        }

        // 3. LOCK SCREEN
        else if (cmd.contains("lock") || cmd.contains("sleep")) {
            DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
            ComponentName admin = new ComponentName(this, AdminReceiver.class);
            if (dpm.isAdminActive(admin)) {
                speak("As you wish, Master.");
                dpm.lockNow();
            } else {
                speak("Master, please enable device admin permissions first.");
            }
        }

        // 4. CAMERA & PHOTOS
        else if (cmd.contains("camera") || cmd.contains("photo") || cmd.contains("selfie")) {
            speak("Opening the camera, Master.");
            Intent cameraIntent = new Intent(MediaStore.INTENT_ACTION_STILL_IMAGE_CAMERA);
            startActivity(cameraIntent);
        }

        // 5. CALLING (Example: "Call 12345")
        else if (cmd.contains("call")) {
            String number and name = cmd.replaceAll("[^0-9]", "any name in phone contacts");
            if(!number.isEmpty()) {
                speak("Calling " + number + ", Master.");
                Intent callIntent = new Intent(Intent.ACTION_CALL);
                callIntent.setData(Uri.parse("tel:" + number));
                startActivity(callIntent);
            } else {
                speak("Master, please tell me the phone number to call.");
            }
        }

        // 6. OPEN APPS BY NAME (Example: "Open YouTube", "Open WhatsApp")
        else if (cmd.contains("open")) {
            String appName = cmd.replace("open ", "").trim();
            speak("Opening " + appName + " for you, Master.");
            openApp(appName);
        }

        // 7. GREETING
        else if (cmd.contains("hello") || cmd.contains("hi")) {
            speak("Hello Master, I am Buddy. I am ready for your commands.");
        }
    }

    private void openApp(String name) {
        // Simple logic to find apps by name
        PackageManager pm = getPackageManager();
        for (android.content.pm.ApplicationInfo app : pm.getInstalledApplications(0)) {
            if (pm.getApplicationLabel(app).toString().toLowerCase().contains(name)) {
                Intent launchIntent = pm.getLaunchIntentForPackage(app.packageName);
                if (launchIntent != null) startActivity(launchIntent);
                return;
            }
        }
        speak("I couldn't find an app named " + name + ", Master.");
    }

    private void speak(String text) {
        buddyVoice.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    private void checkPermissions() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CALL_PHONE, Manifest.permission.CAMERA}, 1);
        }
    }
}
