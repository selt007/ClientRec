package com.example.clientassistant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.widget.Button;
import android.widget.Toast;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

public class MainActivity extends AppCompatActivity {
    private static String fileName = null;
    private boolean askedPermission = false;
    private Button startButton = null;
    private final int PERMISSIONS_RECORD_AUDIO = 1;
    private final WavRecorder recorder = new WavRecorder(this);


    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(event -> startButtonOnClick());

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/temp.wav";

        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.INTERNET)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w("Warning ", "no PERMISSION INTERNET");
        }
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_NETWORK_STATE)
                != PackageManager.PERMISSION_GRANTED) {
            Log.w("Warning ", "no PERMISSION ACCESS_NETWORK_STATE");
        }
    }

    private void startButtonOnClick() {
        if(recorder.isRecording()) stopRecording();
        else startRecording();
    }

    private void startRecording() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            recorder.start(null, false);
            startButton.setText("Stop recording");
        } else {
            //When permission is not granted by user, show them message why this permission is needed.
            if (askedPermission && ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.RECORD_AUDIO)) {
                Toast.makeText(this, "Please grant permissions to record audio", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.RECORD_AUDIO},
                    PERMISSIONS_RECORD_AUDIO);
            askedPermission = true;
        } // else wait for permission result
    }

    private void stopRecording() {
        recorder.stop();
        startButton.setText("Start recording");
    }
}