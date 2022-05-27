/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erhannis.miditranscribe;

import javax.sound.midi.MidiMessage;
import org.wmn4j.notation.Pitch;

/**
 *
 * @author erhannis
 */
public class Utils {
    public static Pitch midiToPitch(int pitch) {
        System.err.println("//DO //TODO Do");
        return Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 0);
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
