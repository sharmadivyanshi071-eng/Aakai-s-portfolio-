import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;
import android.speech.RecognitionListener;
import android.speech.SpeechRecognizer;
import android.net.Uri;
EXTRA_PREFER_OFFLINE, true);

public class MainActivity extends AppCompatActivity {

    private TextToSpeech buddyVoice;
    private static final int SPEECH_REQUEST_CODE = 100;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialize Buddy's Voice
        buddyVoice = new TextToSpeech(this, status -> {
            if (status != TextToSpeech.ERROR) {
                buddyVoice.setLanguage(Locale.US);
                speak("Hello, I am Buddy. Tap the screen to give me a command.");
            }
        });

        // Trigger voice recognition (e.g., on a button click)
        findViewById(R.id.btn_listen).setOnClickListener(v -> startListening());
    }

findViewById(R.id.btn_listen).setOnClickListener(v -> {
    isListeningActive = true; // Reactivate Buddy if he was stopped
    startListening(); 
});

    private void startListening() {
        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Buddy is listening...");
        startActivityForResult(intent, SPEECH_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SPEECH_REQUEST_CODE && resultCode == RESULT_OK && data != null) {
            ArrayList<String> results = data.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS);
            String command = results.get(0).toLowerCase();
            handleCommand(command);
        }
    }
@Override
public void onResults(Bundle results) {
    ArrayList<String> matches = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);
    if (matches != null && !matches.isEmpty()) {
        String command = matches.get(0).toLowerCase();

        // Check for the Stop Command
        if (command.contains("stop listening") || command.contains("go to sleep")) {
            isListeningActive = false;
            buddyVoice.speak("Okay, I will stop listening now. Tap the button to wake me up later.", TextToSpeech.QUEUE_FLUSH, null, null);
            speechRecognizer.stopListening();
            return; // Exit the method so it doesn't restart
        }speechIntent.putExtra(RecognizerIntent.EXTRA_PREFER_OFFLINE, true);
buddyVoice = new TextToSpeech(this, status -> {
    if (status != TextToSpeech.ERROR) {
        // Change Locale.US to new Locale("en", "IN")
        int result = buddyVoice.setLanguage(new Locale("en", "IN"));
        
        if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
            Log.e("TTS", "Indian English is not supported on this device");
        } else {
            buddyVoice.speak("Hello, I am Buddy. I am ready.", TextToSpeech.QUEUE_FLUSH, null, null);
        }
    }
});
else if (command.contains("lock") || command.contains("home")) {
    Intent startMain = new Intent(Intent.ACTION_MAIN);
    startMain.addCategory(Intent.CATEGORY_HOME);
    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(startMain);
}

        // Existing Wake Word Logic
        if (isListeningActive && command.contains("hey buddy")) {
            // ... (Your portfolio and YouTube logic here)
        }
    }

    // Only restart if the stop command wasn't triggered
    if (isListeningActive) {
        speechRecognizer.startListening(speechIntent);else if (command.contains("lock") || command.contains("home")) {
    Intent startMain = new Intent(Intent.ACTION_MAIN);
    startMain.addCategory(Intent.CATEGORY_HOME);
    startMain.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
    startActivity(startMain);
}
else if (command.contains("open camera")) {
    buddyVoice.speak("Opening camera.", TextToSpeech.QUEUE_FLUSH, null, null);
    Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
    startActivity(intent);
}
else if (command.contains("call")) {
    buddyVoice.speak("Calling now.", TextToSpeech.QUEUE_FLUSH, null, null);
    Intent intent = new Intent(Intent.ACTION_DIAL); // DIAL allows you to see the number before it calls
    // To call directly, use ACTION_CALL (requires more permissions)
    startActivity(intent);
}

    }
}

    private void handleCommand(String command) {
        if (command.contains("open youtube")) {
            launchApp("com.google.android.youtube");
        } else if (command.contains("open camera")) {
            Intent intent = new Intent("android.media.action.IMAGE_CAPTURE");
            startActivity(intent);
        } else if (command.contains("hello")) {
            speak("Hi there! How can I help you today?");
        } else {
            speak("I heard " + command + ", but I don't know how to do that yet.");
        }
    }

    private void launchApp(String packageName) {
        PackageManager pm = getPackageManager();
        Intent intent = pm.getLaunchIntentForPackage(packageName);
        if (intent != null) {
            startActivity(intent);
            speak("Opening it now.");
        } else {
            speak("I couldn't find that app on your phone.");
        }
    }

    private void speak(String text) {
        buddyVoice.speak(text, TextToSpeech.QUEUE_FLUSH, null, null);
    }
}
