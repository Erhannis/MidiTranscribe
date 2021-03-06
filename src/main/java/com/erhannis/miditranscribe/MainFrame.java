/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.erhannis.miditranscribe;

import com.erhannis.mathnstuff.Stringable;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.sound.midi.MidiDevice;
import javax.sound.midi.MidiMessage;
import javax.sound.midi.MidiSystem;
import javax.sound.midi.MidiUnavailableException;
import javax.sound.midi.Receiver;
import javax.sound.midi.Transmitter;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import jcsp.helpers.JcspUtils;
import jcsp.helpers.NameParallel;
import jcsp.lang.Alternative;
import jcsp.lang.AltingChannelInput;
import jcsp.lang.Any2OneChannel;
import jcsp.lang.CSProcess;
import jcsp.lang.Channel;
import jcsp.lang.ChannelOutput;
import jcsp.lang.Guard;
import jcsp.lang.ProcessManager;
import jcsp.util.InfiniteBuffer;
import org.wmn4j.io.musicxml.MusicXmlWriter;
import org.wmn4j.notation.ChordBuilder;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Clef.Symbol;
import org.wmn4j.notation.Duration;
import org.wmn4j.notation.DurationalBuilder;
import org.wmn4j.notation.KeySignature;
import org.wmn4j.notation.KeySignatures;
import org.wmn4j.notation.MeasureBuilder;
import org.wmn4j.notation.NoteBuilder;
import org.wmn4j.notation.PartBuilder;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.RestBuilder;
import org.wmn4j.notation.Score;
import org.wmn4j.notation.ScoreBuilder;
import org.wmn4j.notation.TimeSignature;

/**
 *
 * @author erhannis
 */
public class MainFrame extends javax.swing.JFrame {
    private static final String DEFAULT_INPUT_MIDI = "hw:1"; // A hack for my own convenience; change it at will

    private final ChannelOutput<MidiMessage> rxMidiOut;

    private DurationalFrame previewFrame;

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
        //Clef clef = Clef.of(Symbol.G, 2);
        Clef clef = Clef.of(Symbol.F, 2);
        TimeSignature timeSignature = TimeSignature.of(2, 4);
        //KeySignature keySignature = KeySignatures.CMAJ_AMIN;
        KeySignature keySignature = KeySignatures.EMAJ_CSHARPMIN;
        int TRANSPOSE = -1;

        Any2OneChannel<MidiMessage> rxMidiChannel = Channel.<MidiMessage>any2one(new InfiniteBuffer<>());
        AltingChannelInput<MidiMessage> rxMidiIn = rxMidiChannel.in();
        rxMidiOut = JcspUtils.logDeadlock(rxMidiChannel.out());

        Any2OneChannel<DurationalBuilder> previewDurationalChannel = Channel.<DurationalBuilder>any2one();
        AltingChannelInput<DurationalBuilder> previewDurationalIn = previewDurationalChannel.in();
        ChannelOutput<DurationalBuilder> previewDurationalOut = JcspUtils.logDeadlock(previewDurationalChannel.out());

        Any2OneChannel<DurationalBuilder> createDurationalChannel = Channel.<DurationalBuilder>any2one();
        AltingChannelInput<DurationalBuilder> createDurationalIn = createDurationalChannel.in();
        ChannelOutput<DurationalBuilder> createDurationalOut = JcspUtils.logDeadlock(createDurationalChannel.out());

        Any2OneChannel<String> saveScoreChannel = Channel.<String>any2one();
        AltingChannelInput<String> saveScoreIn = saveScoreChannel.in();
        ChannelOutput<String> saveScoreOut = JcspUtils.logDeadlock(saveScoreChannel.out());

        new ProcessManager(new NameParallel(new CSProcess[]{
            new MTProcess(rxMidiIn, previewDurationalOut, createDurationalOut),
            () -> {
                Thread.currentThread().setName("UIProcess");

                PartBuilder pb = new PartBuilder("melody");
                int mbi = 1;
                MeasureBuilder mb = new MeasureBuilder(mbi++)
                        .setClef(clef) //TODO Do
                        .setTimeSignature(timeSignature) //TODO Do
                        .setKeySignature(keySignature); //TODO Do

                Alternative alt = new Alternative(new Guard[]{previewDurationalIn, createDurationalIn, saveScoreIn});
                while (true) {
                    try {
                        switch (alt.priSelect()) {
                            case 0: { // previewDurationalIn
                                DurationalBuilder db = previewDurationalIn.read();
                                if (TRANSPOSE != 0) {
                                    db = Utils.transpose(db, TRANSPOSE);
                                }
                                System.out.println("preview: " + db);
                                previewFrame.setDurational(db.build());
                                break;
                            }
                            case 1: { // createDurationalIn
                                DurationalBuilder db = createDurationalIn.read();
                                if (TRANSPOSE != 0) {
                                    db = Utils.transpose(db, TRANSPOSE);
                                }
                                if (mb.isFull() || mb.isOverflowing()) {
                                    pb.add(mb);
                                    mb = MeasureBuilder.withAttributesOf(mb);
                                    mb.setNumber(mbi++);
                                }
                                mb.addToVoice(0, db);
                                System.out.println("create: " + db);
                                break;
                            }
                            case 2: { // saveScoreIn
                                String filename = saveScoreIn.read();
                                ScoreBuilder sb = new ScoreBuilder();
                                pb.add(mb);
                                sb.addPart(pb);
                                Score score = sb.build();
                                try {
                                    MusicXmlWriter.writerFor(score, new File(filename).toPath()).write();
                                } catch (IOException ex) {
                                    Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
                                }
                                break;
                            }
                        }
                    } catch (Throwable t) {
                        t.printStackTrace();
                    }
                }
            }
        })).start();

        initComponents();

        ArrayList<Stringable<MidiDevice>> transmitters = new ArrayList<>();
        transmitters.add(new Stringable<>(null, "None"));
        ArrayList<Stringable<MidiDevice>> receivers = new ArrayList<>();
        receivers.add(new Stringable<>(null, "None"));

        // https://stackoverflow.com/a/7219095/513038
        System.out.println("MIDI TX START");
        MidiDevice.Info[] infos = MidiSystem.getMidiDeviceInfo();
        System.out.println("MIDI DEVICES: " + infos.length);
        for (int i = 0; i < infos.length; i++) {
            System.out.println("");
            System.out.println("MIDI DEV #" + i);
            try {
                MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
                //does the device have any transmitters?
                //if it does, add it to the device list
                System.out.println("Info: " + infos[i]);
                System.out.println("Device: " + device);

                Transmitter trans = device.getTransmitter();
                trans.setReceiver(new Receiver() {
                    @Override
                    public void send(MidiMessage message, long timeStamp) {
                        System.out.println("MIDI " + device.getDeviceInfo() + " SEND " + Arrays.toString(message.getMessage()));
                    }

                    @Override
                    public void close() {
                        System.out.println("MIDI " + device.getDeviceInfo() + " CLOSE");
                    }
                });

                device.open();
                System.out.println("MIDI 0 " + device.getDeviceInfo() + " OPEN");
                transmitters.add(new Stringable<>(device, device.getDeviceInfo() + ""));
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }
        System.out.println("");
        System.out.println("MIDI TX DONE");

        System.out.println("");
        System.out.println("");
        System.out.println("");

        System.out.println("MIDI RX START");
        System.out.println("MIDI DEVICES: " + infos.length);
        for (int i = 0; i < infos.length; i++) {
            System.out.println("");
            System.out.println("MIDI DEV #" + i);
            try {
                MidiDevice device = MidiSystem.getMidiDevice(infos[i]);
                //does the device have any transmitters?
                //if it does, add it to the device list
                System.out.println("Info: " + infos[i]);
                System.out.println("Device: " + device);

                Receiver rx = device.getReceiver();
                device.getReceiver();

                device.open();
                System.out.println("MIDI 0 " + device.getDeviceInfo() + " OPEN");
                device.close();
                receivers.add(new Stringable<>(device, device.getDeviceInfo() + ""));
            } catch (MidiUnavailableException e) {
                e.printStackTrace();
            }
        }
        System.out.println("");
        System.out.println("MIDI RX DONE");

        Stringable<MidiDevice>[] devicesArray = (Stringable<MidiDevice>[]) Array.newInstance(Stringable.class, 0);
        cbMidiIn.setModel(new DefaultComboBoxModel<Stringable<MidiDevice>>(transmitters.toArray(devicesArray)));
        cbMidiOut.setModel(new DefaultComboBoxModel<Stringable<MidiDevice>>(receivers.toArray(devicesArray)));

        for (Stringable<MidiDevice> smd : transmitters) {
            if (smd.name.contains(DEFAULT_INPUT_MIDI)) {
                cbMidiIn.getModel().setSelectedItem(smd);
                btnGoActionPerformed(null);
                break;
            }
        }
        
        ScoreBuilder sb = new ScoreBuilder();
        sb.addPart(new PartBuilder("melody")
                .add(new MeasureBuilder()
                        .setClef(clef)
                        .setTimeSignature(timeSignature)
                        .setKeySignature(keySignature)
                        //TODO Set preview clef etc., too
                        .addToVoice(0, new RestBuilder(Duration.of(1, 4)))
                        .addToVoice(0, new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Duration.of(1, 4)))
                        .addToVoice(0, new ChordBuilder(Arrays.asList(
                                new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Duration.of(1, 2)),
                                new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Duration.of(1, 2)),
                                new NoteBuilder(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Duration.of(1, 2))
                        )))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(60), Duration.of(1, 16)))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(61), Duration.of(1, 16)))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(62), Duration.of(1, 16)))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(63), Duration.of(1, 16)))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(64), Duration.of(1, 16)))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(65), Duration.of(1, 16)))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(66), Duration.of(1, 16)))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(67), Duration.of(1, 16)))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(68), Duration.of(1, 16)))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(69), Duration.of(1, 16)))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(70), Duration.of(1, 16)))
                        .addToVoice(0, new NoteBuilder(Utils.MIDI_TO_PITCH.get(71), Duration.of(1, 1)))
                )
        );
        Score score = sb.build();
        try {
            MusicXmlWriter.writerFor(score, new File("example.xml").toPath()).write();
        } catch (IOException ex) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, ex);
        }

        //jSplitPane1.setRightComponent();
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveScoreOut.write(System.currentTimeMillis() + ".xml");
            }
        });

        previewFrame = new DurationalFrame(new ChordBuilder(Arrays.asList(
                new NoteBuilder(Pitch.of(Pitch.Base.C, Pitch.Accidental.NATURAL, 4), Duration.of(1, 2)),
                new NoteBuilder(Pitch.of(Pitch.Base.E, Pitch.Accidental.NATURAL, 4), Duration.of(1, 2)),
                new NoteBuilder(Pitch.of(Pitch.Base.G, Pitch.Accidental.NATURAL, 4), Duration.of(1, 2))
        )).build()
        );
        previewFrame.setClef(clef);
        previewFrame.setKeySignature(keySignature);
        previewFrame.setVisible(true);
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jSplitPane1 = new javax.swing.JSplitPane();
        jPanel1 = new javax.swing.JPanel();
        cbMidiIn = new javax.swing.JComboBox<>();
        cbMidiOut = new javax.swing.JComboBox<>();
        btnGo = new javax.swing.JButton();
        btnSave = new javax.swing.JButton();
        jPanel2 = new javax.swing.JPanel();

        setDefaultCloseOperation(javax.swing.WindowConstants.EXIT_ON_CLOSE);

        jSplitPane1.setDividerLocation(200);
        jSplitPane1.setOrientation(javax.swing.JSplitPane.VERTICAL_SPLIT);

        cbMidiIn.setToolTipText("Midi in");

        cbMidiOut.setToolTipText("Midi out");

        btnGo.setText("Go");
        btnGo.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnGoActionPerformed(evt);
            }
        });

        btnSave.setText("Save");
        btnSave.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                btnSaveActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(cbMidiIn, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(cbMidiOut, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(btnGo)
                            .addComponent(btnSave))
                        .addGap(0, 557, Short.MAX_VALUE)))
                .addContainerGap())
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(cbMidiIn, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cbMidiOut, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(btnGo)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 70, Short.MAX_VALUE)
                .addComponent(btnSave)
                .addContainerGap())
        );

        jSplitPane1.setTopComponent(jPanel1);

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 643, Short.MAX_VALUE)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 406, Short.MAX_VALUE)
        );

        jSplitPane1.setRightComponent(jPanel2);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(jSplitPane1, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void btnGoActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnGoActionPerformed
        try {
            MidiDevice mdFrom = ((Stringable<MidiDevice>) cbMidiIn.getSelectedItem()).val;
            MidiDevice mdTo = ((Stringable<MidiDevice>) cbMidiOut.getSelectedItem()).val;

            if (mdFrom != null) {
                mdFrom.open();
                Transmitter tx = mdFrom.getTransmitter();
                Receiver rx0 = null;
                if (mdTo != null) {
                    mdTo.open();
                    rx0 = mdTo.getReceiver();
                }
                Receiver rx = rx0;
                tx.setReceiver(new Receiver() {
                    @Override
                    public void send(MidiMessage message, long timeStamp) {
                        System.out.println("MIDI " + mdFrom.getDeviceInfo() + " SEND " + Arrays.toString(message.getMessage()));
                        rxMidiOut.write(message);
                        if (mdTo != null) {
                            rx.send(message, timeStamp);
                        }
                    }

                    @Override
                    public void close() {
                        System.out.println("MIDI " + mdFrom.getDeviceInfo() + " CLOSE");
                    }
                });
            }
        } catch (Throwable t) {
            Logger.getLogger(MainFrame.class.getName()).log(Level.SEVERE, null, t);
        }
    }//GEN-LAST:event_btnGoActionPerformed

    private void btnSaveActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_btnSaveActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_btnSaveActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new MainFrame().setVisible(true);
            }
        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton btnGo;
    private javax.swing.JButton btnSave;
    private javax.swing.JComboBox<Stringable<MidiDevice>> cbMidiIn;
    private javax.swing.JComboBox<Stringable<MidiDevice>> cbMidiOut;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables
}
