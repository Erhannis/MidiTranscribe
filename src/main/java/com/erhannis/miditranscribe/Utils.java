/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erhannis.miditranscribe;

import java.util.HashMap;
import javax.sound.midi.MidiMessage;
import org.wmn4j.notation.Pitch;

/**
 *
 * @author erhannis
 */
public class Utils {
    public static HashMap<Integer, Pitch> MIDI_TO_PITCH = new HashMap<Integer, Pitch>() {{
        for (int octave = 0; octave < 7; octave++) {
            int offset = (octave-4)*12;
            put(60+offset, Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, octave));
            put(61+offset, Pitch.of(Pitch.Base.C, Pitch.Accidental.SHARP, octave));
            put(62+offset, Pitch.of(Pitch.Base.D, Pitch.Accidental.NATURAL, octave));
            put(63+offset, Pitch.of(Pitch.Base.D, Pitch.Accidental.SHARP, octave));
            put(64+offset, Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, octave));
            put(65+offset, Pitch.of(Pitch.Base.F, Pitch.Accidental.NATURAL, octave));
            put(66+offset, Pitch.of(Pitch.Base.F, Pitch.Accidental.SHARP, octave));
            put(67+offset, Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, octave));
            put(68+offset, Pitch.of(Pitch.Base.G, Pitch.Accidental.SHARP, octave));
            put(69+offset, Pitch.of(Pitch.Base.A, Pitch.Accidental.NATURAL, octave));
            put(70+offset, Pitch.of(Pitch.Base.A, Pitch.Accidental.SHARP, octave));
            put(71+offset, Pitch.of(Pitch.Base.B, Pitch.Accidental.NATURAL, octave));
        }
    }};
    
    public static Pitch midiToPitch(int pitch) {
        return MIDI_TO_PITCH.get(pitch);
    }
    
    public static int getMidiChannel(MidiMessage msg) {
        return msg.getMessage()[0] & 0x0F;
    }
    
    public static boolean isMidiNoteOn(MidiMessage msg) {
        return (msg.getMessage()[0] & 0xF0) == 0b10010000;
    }

    public static boolean isMidiNoteOff(MidiMessage msg) {
        return (msg.getMessage()[0] & 0xF0) == 0b10000000;
    }
    
    public static int getMidiPitch(MidiMessage msg) {
        return msg.getMessage()[1];
    }

    public static int getMidiVelocity(MidiMessage msg) {
        return msg.getMessage()[2];
    }
}
