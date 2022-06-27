package com.sashantgroup.clientassistant;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
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
    private EditText edTxt = null;
    private final int PERMISSIONS_RECORD_AUDIO = 1;
    private final WavRecorder recorder = new WavRecorder(this);
    public static String ip;
    SharedPreferences prefs;

    @SuppressLint("ClickableViewAccessibility")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        startButton = findViewById(R.id.startButton);
        startButton.setOnClickListener(event -> startButtonOnClick());

        fileName = getExternalCacheDir().getAbsolutePath();
        fileName += "/temp.wav";

        prefs = getSharedPreferences("config", Context.MODE_PRIVATE);
        ip = prefs.getString(APP_OPS_SERVICE, "");
        edTxt = findViewById(R.id.ip);
        edTxt.setText(ip);

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
        if (!edTxt.getText().toString().contains(".")) {
            Toast.makeText(this,"IP-адрес не введен.",
                    Toast.LENGTH_SHORT).show();
        }
        else {
            onPause();
            if (recorder.isRecording()) stopRecording();
            else startRecording();
        }
    }

    private void startRecording() {
        if (ContextCompat.checkSelfPermission(this,
                Manifest.permission.RECORD_AUDIO) == PackageManager.PERMISSION_GRANTED) {
            recorder.start(fileName, false);
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
        new ClientSocket(fileName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch(id) {
            case R.id.about:
                createDialog(this);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.settings, menu);
        return true;
    }

    @Override
    protected void onPause() {
        super.onPause();
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString(APP_OPS_SERVICE, edTxt.getText().toString()).apply();
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (prefs.contains(APP_OPS_SERVICE)) {
            ip = prefs.getString(APP_OPS_SERVICE, "");
        }
    }

    public void createDialog(Activity activity) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        String about = "Спасибо что скачали приложение!\n\n" +
                "При нахождении недочетов в приложении можете написать на почту или в дискорд канал (кнопка 'Bug report'):\n" +
                "all1992ex@gmail.com\n\n" +
                "Если есть желание финансово поддержать проект, то нажмите кнопку 'Donate'.\n" +
                "При желании можете указать ник, мы вас добавим в\n" +
                "список спонсоров и отобразим в этом окне.\nСпасибо! =)";
        builder.setTitle("О нас")
                .setMessage(about)
                .setCancelable(true)
                .setNegativeButton(Html.fromHtml("<font color='#7A2B2B'>Bug report (Discord)</font>"),
                        (dialog, id) -> {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://discord.gg/8gQVk4bzDH"));
                            startActivity(browserIntent);
                        })
                .setPositiveButton(Html.fromHtml("<font color='#FF7F27'>Donate</font>"),
                        (dialog, id) -> {
                            Intent browserIntent = new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("https://www.donationalerts.com/r/sashflacko"));
                            startActivity(browserIntent);
                        })
                .setNeutralButton(Html.fromHtml("<font color='#323232'>Close</font>"),
                        (dialog, id) -> dialog.cancel());
        builder.create().show();
    }
}