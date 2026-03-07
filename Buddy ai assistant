import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.speech.RecognizerIntent;
import android.speech.tts.TextToSpeech;
import androidx.appcompat.app.AppCompatActivity;
import java.util.ArrayList;
import java.util.Locale;

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
