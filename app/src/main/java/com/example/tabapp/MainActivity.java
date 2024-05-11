package com.example.tabapp;


import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.ComplexOnsetDetector;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.onsets.PrintOnsetHandler;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;

public class MainActivity extends AppCompatActivity {

    private TextView nText;
    private TextView fText;
    private static long lastExecutionTime = 0;
    private static final long TIME_ELAPSED = 100;
    boolean ConditionOnset = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        nText = (TextView)findViewById(R.id.noteText);
        fText = (TextView)findViewById(R.id.freqText);
        getPermissions();

        /* AudioDispatcher reads sound input from the microphone and and sends it to an AudioProcessor object*/
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100,7600,3800);

        PercussionOnsetDetector onsetDetector = new PercussionOnsetDetector(44100,7600,
                new OnsetHandler() {
                    @Override
                    public void handleOnset(double time, double salience) {
                        long currentTime = System.currentTimeMillis();
                        if(currentTime-lastExecutionTime>=TIME_ELAPSED)
                        {
                            ConditionOnset = true;
                            lastExecutionTime = currentTime;
                        }
                    }
                },60, 1);

        PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent event){

                final float pitchInHz = pitchDetectionResult.getPitch();
                if(ConditionOnset && (pitchInHz > 82.41f))
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changeState(pitchInHz);
                        }
                    });
                ConditionOnset = false;
            }
        };

        AudioProcessor pitchProcessor = new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.MPM,
                44100, 7600, pitchDetectionHandler);
        dispatcher.addAudioProcessor(pitchProcessor);
        dispatcher.addAudioProcessor(onsetDetector);
        new Thread(dispatcher,"Audio Dispatcher").start();
    }

    private void changeState(float f){
        FindNote fNote = new FindNote();
        nText.setText(fNote.findNote(f));
        fText.setText(String.valueOf(f));
        System.out.println(fNote.findNote(f));
        ConditionOnset = false;
    }

    private void getPermissions(){
        if(ActivityCompat.checkSelfPermission(MainActivity.this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED){
            requestPermissions(new String[]{Manifest.permission.RECORD_AUDIO},123);
        }
    }

}