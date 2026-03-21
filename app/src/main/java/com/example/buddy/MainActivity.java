package com.example.buddy;
import android.app.Activity;
import android.app.admin.DevicePolicyManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.BatteryManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import android.widget.Button;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends Activity {

    private TextToSpeech tts;
    private static final int REQUEST_CODE_SPEECH_INPUT = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Buddy's Voice (Indian English)
        tts = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                tts.setLanguage(new Locale("en", "IN")); 
                speak("Buddy is online. Welcome back, Master.");
            }
        });

        // The Microphone Button
        Button btnTalk = findViewById(R.id.btnTalk);
        btnTalk.setOnClickListener(v -> startVoiceRecognition());
    }

    private void startVoiceRecognition() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        
        // Listen for Hindi and Indian English
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
        intent.putExtra(RecognizerIntent.EXTRA_SUPPORTED_LANGUAGES, "hi-IN");
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Listening... बोलिए...");

        try {
            startActivityForResult(intent, REQUEST_CODE_SPEECH_INPUT);
        } catch (Exception e) {
            Toast.makeText(this, "Voice recognition not supported", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SPEECH_INPUT && resultCode == RESULT_OK && data != null) {
            ArrayList<String> result = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            if (result != null && !result.isEmpty()) {
                handleCommand(result.get(0));
            }
        }
    }

    private void handleCommand(String command) {
        String cmd = command.toLowerCase();

        // 1. GREETINGS
        if (cmd.contains("hello") || cmd.contains("namaste") || cmd.contains("नमस्ते")) {
            speak("Namaste Master! How can I help you today?");
        }

        // 2. LOCK SYSTEM
        else if (cmd.contains("lock") || cmd.contains("sleep") || cmd.contains("band kar") || cmd.contains("tala")) {
            processLockRequest();
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

        // 4. CAMERA
        else if (cmd.contains("camera") || cmd.contains("photo") || cmd.contains("camera kholo")) {
            speak("Opening camera now.");
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            startActivity(intent);
        }

        // 5. UNKNOWN COMMAND
        else {
            speak("I heard " + command + ", but I don't know that skill yet.");
        }
    }

    private void processLockRequest() {
        DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(Context.DEVICE_POLICY_SERVICE);
        ComponentName adminComponent = new ComponentName(this, AdminReceiver.class);

        if (dpm.isAdminActive(adminComponent)) {
            speak("Locking the device.");
            dpm.lockNow();
        } else {
            speak("I need admin permission to lock the screen. Please activate it.");
            Intent intent = new Intent(DevicePolicyManager.ACTION_ADD_DEVICE_ADMIN);
            intent.putExtra(DevicePolicyManager.EXTRA_DEVICE_ADMIN, adminComponent);
            startActivity(intent);
        }
    }

    private void speak(String text) {
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }

    @Override
    protected void onDestroy() {
        if (tts != null) {
            tts.stop();
            tts.shutdown();
        }
        super.onDestroy();
    }
}