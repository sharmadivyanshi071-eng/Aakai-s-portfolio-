package com.example.buddy; // Make sure this matches your project folder name

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.speech.tts.TextToSpeech;
import android.util.Log;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;

public class MainActivity extends AppCompatActivity {

    // 1. Declare variables at the top
    private TextToSpeech buddyVoice;
    private SpeechRecognizer speechRecognizer;
    private Intent speechIntent;
    private boolean isListeningActive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // 2. Initialize Voice (Indian English)
        buddyVoice = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                buddyVoice.setLanguage(new Locale("en", "IN"));
                speak("Hello, I am Buddy. I am ready.");
            }
        });

        // 3. Prepare the Speech Intent (Offline and Indian English)
        speechIntent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE, "en-IN");
        speechIntent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        speechIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);

        // 4. Button to start the loop
        findViewById(R.id.btn_listen).setOnClickListener(v -> {
            isListeningActive = true;
            startListening();
        });
    }

    private void startListening() {
        if (speechRecognizer == null) {
            speechRecognizer = SpeechRecognizer.createSpeechRecognizer(this);
        }
        
        speechRecognizer.setRecognitionListener(new RecognitionListener() {
            @Override
            public void onResults(Bundle results) {
                ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
                if (matches != null && !matches.isEmpty()) {
                    String command = matches.get(0).toLowerCase();

                    // STOP command
                    if (command.contains("stop listening") || command.contains("go to sleep")) {
                        isListeningActive = false;
                        speak("Okay, stopping now.");
                        return;
                    }

                    // WAKE WORD logic
                    if (isListeningActive && command.contains("hey buddy")) {
                        handleCommand(command);
                    }
                }
                // Restart listening automatically
                if (isListeningActive) speechRecognizer.startListening(speechIntent);
            }

            @Override
            public void onError(int error) {
                if (isListeningActive) speechRecognizer.startListening(speechIntent);
            }

            // Required empty methods
            @Override public void onReadyForSpeech(Bundle b) {}
            @Override public void onBeginningOfSpeech() {}
            @Override public void onRmsChanged(float r) {}
            @Override public void onBufferReceived(byte[] b) {}
            @Override public void onEndOfSpeech() {}
            @Override public void onPartialResults(Bundle b) {}
            @Override public void onEvent(int i, Bundle b) {}
        });
        
        speechRecognizer.startListening(speechIntent);
    }

    private void handleCommand(String command) {
        if (command.contains("open youtube")) {
     if (command.contains("pick my phone") || command.contains("answer")) {
    TelecomManager tm = (TelecomManager) getSystemService(Context.TELECOM_SERVICE);
    if (tm != null) {
        tm.acceptRingingCall(); // This answers the call
    }
}
       launchApp("com.google.android.youtube");
        } else if (command.contains("camera")) {
            speak("Opening camera.");
            startActivity(new Intent("android.media.action.IMAGE_CAPTURE"));
        } else if (command.contains("call")) {
            speak("Opening the dialer.");
            startActivity(new Intent(Intent.ACTION_DIAL));
        } else if (command.contains("portfolio")) {
            speak("Opening your website.");
            startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://sharmadivyanshi071-eng.github.io/")));
        } else if (command.contains("lock") || command.contains("home")) {
            Intent home = new Intent(Intent.ACTION_MAIN);
            home.addCategory(Intent.CATEGORY_HOME);
            home.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            startActivity(home);
        }
    }

    private void launchApp(String packageName) {
        Intent intent = getPackageManager().getLaunchIntentForPackage(packageName);
        if (intent != null) {
            startActivity(intent);
            speak("Opening it now.");
        private void openAppByName(String appName) {
    PackageManager pm = getPackageManager();
    List<ApplicationInfo> apps = pm.getInstalledApplications(0);
    for (ApplicationInfo app : apps) {
        String label = pm.getApplicationLabel(app).toString().toLowerCase();
        if (label.contains(appName.toLowerCase())) {
            Intent intent = pm.getLaunchIntentForPackage(app.packageName);
            startActivity(intent);
            return;
        
    
    private void speak(String text) {
        buddyVoice.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
