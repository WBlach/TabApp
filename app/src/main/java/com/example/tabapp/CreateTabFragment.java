package com.example.tabapp;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.material.tabs.TabLayout;

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
    private String filename = "";
    private boolean ButtonClicked = false;
    private StringBuffer stringBuffer;
    private static long LAST_EXECUTION_TIME = 0;
    private static final long TIME_ELAPSED = 125;
    private boolean ConditionOnset = false;
    private View view;

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
        stringBuffer = new StringBuffer();
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
                    SoundProcessing();
                }
                else{
                    saveDialog();
                }
                ButtonClicked = !ButtonClicked;
            }
        });
        return view;
    }

    void SoundProcessing(){
        /* AudioDispatcher reads sound input from the microphone and and sends it to an AudioProcessor object*/
        AudioDispatcher dispatcher = AudioDispatcherFactory.fromDefaultMicrophone(44100,7168,3584);
        PercussionOnsetDetector onsetDetector = getOnsetDetector();
        AudioProcessor pitchProcessor = getPitchProcessor();
        dispatcher.addAudioProcessor(pitchProcessor);
        dispatcher.addAudioProcessor(onsetDetector);
        new Thread(dispatcher,"Audio Dispatcher").start();
    }

    @NonNull
    private AudioProcessor getPitchProcessor(){
        PitchDetectionHandler pitchDetectionHandler = new PitchDetectionHandler() {
            @Override
            public void handlePitch(PitchDetectionResult pitchDetectionResult, AudioEvent event){
                final float pitchInHz = pitchDetectionResult.getPitch();
                if(ConditionOnset && ButtonClicked){
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run(){
                            updateText(pitchInHz);
                        }
                    });
                }
                ConditionOnset = false;
            }
        };
        return new PitchProcessor(PitchProcessor.PitchEstimationAlgorithm.MPM,
                44100, 7168, pitchDetectionHandler);
    }

    @NonNull
    private PercussionOnsetDetector getOnsetDetector(){
        return new PercussionOnsetDetector(44100,7168,
                new OnsetHandler() {
                    @Override
                    public void handleOnset(double time, double salience) {
                        long currentTime = System.currentTimeMillis();
                        if(currentTime - LAST_EXECUTION_TIME >= TIME_ELAPSED)
                        {
                            ConditionOnset = true;
                            LAST_EXECUTION_TIME = currentTime;
                        }
                    }
                },60, 1.2);
    }

    private void saveDialog(){
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Choose file name");
        View viewInflated = LayoutInflater.from(getContext()).inflate(R.layout.save_menu, (ViewGroup) getView(), false);
        final EditText input = (EditText) viewInflated.findViewById(R.id.input);
        builder.setView(viewInflated);

        builder.setPositiveButton(android.R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                filename = input.getText().toString();
                TouchFile touchFile = new TouchFile();
                touchFile.saveToCSV(getActivity().getApplicationContext(),stringBuffer.toString(),filename);
                stringBuffer.setLength(0);
            }
        });

        builder.setNegativeButton(android.R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        builder.show();
    }

    private void updateText(float f){
        FindNote fNote = new FindNote();
        String[]temp = fNote.getNote(f);
        fText.setText(temp[0]);
        nText.setText(temp[1]);
        stringBuffer.append(temp[1]+";");
    }
}