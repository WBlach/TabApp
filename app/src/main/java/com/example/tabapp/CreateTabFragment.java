package com.example.tabapp;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.RadioButton;
import android.widget.TextView;

import be.tarsos.dsp.AudioDispatcher;
import be.tarsos.dsp.AudioEvent;
import be.tarsos.dsp.AudioProcessor;
import be.tarsos.dsp.io.android.AudioDispatcherFactory;
import be.tarsos.dsp.onsets.OnsetHandler;
import be.tarsos.dsp.onsets.PercussionOnsetDetector;
import be.tarsos.dsp.pitch.PitchDetectionHandler;
import be.tarsos.dsp.pitch.PitchDetectionResult;
import be.tarsos.dsp.pitch.PitchProcessor;


/**
 * A simple {@link Fragment} subclass.
 * Use the {@link CreateTabFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class CreateTabFragment extends Fragment {

    private TextView nText;
    private TextView fText;
    private Button recordButton;
    boolean ButtonClicked = false;
    private View view;
    private static long lastExecutionTime = 0;
    private static final long TIME_ELAPSED = 125;
    boolean  ConditionOnset = false;

    public CreateTabFragment() {
        // Required empty public constructor
    }

    public CreateTabFragment newInstance() {
        CreateTabFragment fragment = new CreateTabFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_create_tab, container, false);
        nText = (TextView) view.findViewById(R.id.noteText);
        fText = (TextView) view.findViewById(R.id.freqText);
        recordButton = (Button) view.findViewById(R.id.recordButton);
        recordButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!ButtonClicked){
                    /* AudioDispatcher reads sound input from the microphone and and sends it to an AudioProcessor object*/
                    AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(22050,2048,0);
                    PercussionOnsetDetector onsetDetector = getOnsetDetector();
                    AudioProcessor pitchProcessor = getPitchProcessor();
                    dispatcher.addAudioProcessor(pitchProcessor);
                    dispatcher.addAudioProcessor(onsetDetector);
                    new Thread(dispatcher,"Audio Dispatcher").start();
                }
                ButtonClicked = !ButtonClicked;
            }
        });
        return view;
    }

    private void changeState(float f){
        FindNote fNote = new FindNote();
        System.out.println(fNote.findNote(f));
        nText.setText(fNote.findNote(f));
        fText.setText(String.valueOf(f));
        ConditionOnset = false;
    }

    @NonNull
    private AudioProcessor getPitchProcessor() {
        PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent event){

                final float pitchInHz = pitchDetectionResult.getPitch();
                if(ConditionOnset && (pitchInHz > 82.41f) && getActivity() != null)
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            changeState(pitchInHz);
                        }
                    });
                ConditionOnset = false;
            }
        };
        return new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.MPM,
                22050, 2048, pitchDetectionHandler);
    }

    @NonNull
    private PercussionOnsetDetector getOnsetDetector(){
        return new PercussionOnsetDetector(22050,2048,
                new OnsetHandler() {
                    @Override
                    public void handleOnset(double time, double salience) {
                        long currentTime = System.currentTimeMillis();
                        if(currentTime - lastExecutionTime >= TIME_ELAPSED)
                        {
                            ConditionOnset = true;
                            lastExecutionTime = currentTime;
                        }
                    }
                },60, 1.2);
    }
}