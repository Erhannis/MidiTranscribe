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
                        if (36 <= pitch && pitch <= 47) {
                            // Special key, triggers completion
                            System.err.println("//DO //TODO Do duration");
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
                for (List<Integer> note: notes.map.keySet()) {
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
