package com.example.tabapp;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        getPermissions();

        //NOTE: if sampling freq is 4kHz, program returns frequencies 1 octave higher than expected - why?
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(8000, 1024, 512);
        PitchDetectionHandler pitchDetectionHandler = (pitchDetectionResult, audioEvent) -> {
            final float pitchInHz = pitchDetectionResult.getPitch();
            runOnUiThread(() -> {
                TextView nText = (TextView) findViewById(R.id.noteText);
                TextView fText = (TextView) findViewById(R.id.freqText);
                if(pitchDetectionResult.isPitched()){
                    FindNote fNote = new FindNote();
                    nText.setText((String)fNote.findNote(pitchInHz));
                    fText.setText(String.valueOf(pitchInHz));

                }
                else{
                    nText.setText("Waiting for input");
                    fText.setText("");
                }
            });
        };

        AudioProcessor audioProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.FFT_YIN,
                8000, 1024, pitchDetectionHandler);
        dispatcher.addAudioProcessor(audioProcessor);
        new Thread(dispatcher,"Audio Dispatcher").start();

    }

    private void getPermissions(){
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},123);
        }
    }

}