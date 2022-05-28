/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.erhannis.miditranscribe;

import com.erhannis.mathnstuff.utils.BagMap;
import com.erhannis.mathnstuff.utils.ListMap;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import javax.sound.midi.MidiMessage;
import jcsp.lang.Alternative;
import jcsp.lang.AltingChannelInput;
import jcsp.lang.Any2OneChannel;
import jcsp.lang.CSProcess;
import jcsp.lang.Channel;
import jcsp.lang.ChannelOutput;
import jcsp.lang.Guard;
import org.wmn4j.notation.ChordBuilder;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.DurationalBuilder;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.RestBuilder;

/**
 *
 * @author erhannis
 */
public class MTProcess implements CSProcess {

    private final AltingChannelInput<MidiMessage> rxMidiIn;
    private final ChannelOutput<DurationalBuilder> previewDurationalOut;
    private final ChannelOutput<DurationalBuilder> createDurationalOut;

    public MTProcess(AltingChannelInput<MidiMessage> rxMidiIn, ChannelOutput<DurationalBuilder> previewDurationalOut, ChannelOutput<DurationalBuilder> createDurationalOut) {
        this.rxMidiIn = rxMidiIn;
        this.previewDurationalOut = previewDurationalOut;
        this.createDurationalOut = createDurationalOut;
    }

    @Override
    public void run() {
        Thread.currentThread().setName("MTProcess");
        Alternative alt = new Alternative(new Guard[]{rxMidiIn});
        ListMap<Integer, Boolean> notes = new ListMap<>();
        while (true) {
            boolean send = false;
            boolean create = false;
            Duration duration = Duration.of(1, 1);
            switch (alt.priSelect()) {
                case 0: // rxMidiIn
                    MidiMessage msg = rxMidiIn.read();
                    if (Utils.isMidiNoteOn(msg)) {
                        int pitch = Utils.getMidiPitch(msg);
                        if (36 <= pitch && pitch <= 48) { // Lowest C to C above, inclusive
                            // Special key, triggers completion
                            int num = 1;
                            int den = 8;
                            switch (pitch) {
                                // White keys
                                case 36:
                                    num = 1;
                                    break;
                                case 38:
                                    num = 2;
                                    break;
                                case 40:
                                    num = 3;
                                    break;
                                case 41:
                                    num = 4;
                                    break;
                                case 43:
                                    num = 5;
                                    break;
                                case 45:
                                    num = 6;
                                    break;
                                case 47:
                                    num = 7;
                                    break;
                                case 48:
                                    num = 8;
                                    break;
                                // Black keys //TODO Figure out what these should do
                                case 37:
                                    break;
                                case 39:
                                    break;
                                case 42:
                                    break;
                                case 44:
                                    break;
                                case 46:
                                    break;
                            }
                            duration = Duration.of(num, den);
                            send = true;
                            create = true;
                        } else {
                            notes.put(true, Utils.getMidiChannel(msg), Utils.getMidiPitch(msg));
                            send = true;
                        }
                    } else if (Utils.isMidiNoteOff(msg)) {
                        notes.remove(Utils.getMidiChannel(msg), Utils.getMidiPitch(msg));
                        send = true;
                    } else {
                        System.err.println("Unhandled midi message: " + Arrays.toString(msg.getMessage()));
                    }
                    break;
            }

            if (send) {
                ArrayList<NoteBuilder> nbs = new ArrayList<>();
                for (List<Integer> note : notes.map.keySet()) {
                    int channel = note.get(0); //TODO Use?
                    int pitch = note.get(1);
                    nbs.add(new NoteBuilder(Utils.midiToPitch(pitch), duration));
                }
                DurationalBuilder db;
                if (nbs.isEmpty()) {
                    db = new RestBuilder(duration);
                } else {
                    db = new ChordBuilder(nbs);
                }
                if (create) {
                    createDurationalOut.write(db);
                } else {
                    previewDurationalOut.write(db);
                }
            }
        }
    }
}
