package com.example.tabapp;
import java.util.Map;
import java.util.NavigableMap;
import java.util.TreeMap;

public class FindNote {
    private NavigableMap <Float,String> freqs;

    //Constructor of the FindNote class, 
    FindNote(){
        freqs = new TreeMap<>();
        // Mapping frequencies to note names
        freqs.put(82.41f, "E2");
        freqs.put(87.31f, "F2");
        freqs.put(92.50f, "F#2/G♭2");
        freqs.put(98.00f, "G2");
        freqs.put(103.83f, "G#2/A♭2");
        freqs.put(110.00f, "A2");
        freqs.put(116.54f, "A#2/B♭2");
        freqs.put(123.47f, "B2");
        freqs.put(130.81f, "C3");
        freqs.put(138.59f, "C#3/D♭3");
        freqs.put(146.83f, "D3");
        freqs.put(155.56f, "D#3/E♭3");
        freqs.put(164.81f, "E3");
        freqs.put(174.61f, "F3");
        freqs.put(185.00f, "F#3/G♭3");
        freqs.put(196.00f, "G3");
        freqs.put(207.65f, "G#3/A♭3");
        freqs.put(220.00f, "A3");
        freqs.put(233.08f, "A#3/B♭3");
        freqs.put(246.94f, "B3");
        freqs.put(261.63f, "C4");
        freqs.put(277.18f, "C#4/D♭4");
        freqs.put(293.66f, "D4");
        freqs.put(311.13f, "D#4/E♭4");
        freqs.put(329.63f, "E4");
        freqs.put(349.23f, "F4");
        freqs.put(369.99f, "F#4/G♭4");
        freqs.put(392.00f, "G4");
        freqs.put(415.30f, "G#4/A♭4");
        freqs.put(440.00f, "A4");
        freqs.put(466.16f, "A#4/B♭4");
        freqs.put(493.88f, "B4");
        freqs.put(523.25f, "C5");
        freqs.put(554.37f, "C#5/D♭5");
        freqs.put(587.33f, "D5");
        freqs.put(622.25f, "D#5/E♭5");
        freqs.put(659.25f, "E5");
        freqs.put(698.46f, "F5");
        freqs.put(739.99f, "F#5/G♭5");
        freqs.put(783.99f, "G5");
        freqs.put(830.61f, "G#5/A♭5");
        freqs.put(880.00f, "A5");
        freqs.put(932.33f, "A#5/B♭5");
        freqs.put(987.77f, "B5");
    }

    public String[] getNote(float inputFreq){
        if(inputFreq > 987.77f)
            return new String[]{"987.77", "B5"};
        else if (inputFreq < 82.41f)
            return new String[]{"82.41", "E2"};
        else{
            float freqLower = freqs.floorKey(inputFreq);
            float freqHigher = freqs.ceilingKey(inputFreq);
            if(Math.abs(inputFreq- freqLower) < Math.abs(inputFreq- freqHigher))
                return new String[] {String.valueOf(freqLower), freqs.get(freqLower)};
            else return new String[] {String.valueOf(freqHigher), freqs.get(freqHigher)};
        }
    }
}



