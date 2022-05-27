/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/GUIForms/JFrame.java to edit this template
 */
package com.erhannis.miditranscribe;

import abc.notation.MusicElement;
import abc.notation.Note;
import abc.notation.Tune;
import abc.notation.Voice;
import abc.ui.swing.JScoreComponent;
import com.erhannis.mathnstuff.Stringable;
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

/**
 *
 * @author erhannis
 */
public class MainFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    public MainFrame() {
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
        
        JScoreComponent score = new JScoreComponent();
        jSplitPane1.setRightComponent(score);
        
        Tune tune = new Tune();
        tune.getMusic().getVoice("Piano").addElement(new Note((byte)5));
        tune.getMusic().getVoice("Piano").addElement(new Note((byte)100));
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            int i = 0;
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                Voice v = tune.getMusic().getVoice("Piano");
                try {
                    v.addElement(new Note((byte)(i++)));
                    score.refresh();
                    System.out.println("success " + i);
                } catch (Throwable t) {
                    System.err.println("ERROR: " + i + " " + t);
                    //t.printStackTrace();
                    v.remove(v.size()-1);
                }
            }
        });
        score.setTune(tune);
        
        score.getBounds().width = 100;
        score.getBounds().height = 100;
        
        score.refresh();
        score.invalidate();
        score.validate();
        score.revalidate();
        score.repaint();
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
        jButton1 = new javax.swing.JButton();
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

        jButton1.setText("jButton1");
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
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
                            .addComponent(jButton1))
                        .addGap(0, 538, Short.MAX_VALUE)))
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
                .addComponent(jButton1)
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

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
    }//GEN-LAST:event_jButton1ActionPerformed

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
    private javax.swing.JComboBox<Stringable<MidiDevice>> cbMidiIn;
    private javax.swing.JComboBox<Stringable<MidiDevice>> cbMidiOut;
    private javax.swing.JButton jButton1;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JSplitPane jSplitPane1;
    // End of variables declaration//GEN-END:variables
}
