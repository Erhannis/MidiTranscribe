/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.miditranscribe;

import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.util.Objects;
import org.wmn4j.notation.Chord;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.KeySignature;
import org.wmn4j.notation.Note;
import org.wmn4j.notation.Pitch;
import org.wmn4j.notation.Pitch.Accidental;
import org.wmn4j.notation.Pitch.Base;
import org.wmn4j.notation.Rest;

/**
 *
 * @author mewer
 */
public class DurationalPanel extends javax.swing.JPanel {

    public DurationalPanel(Durational d) {
        this();
        this.setDurational(d);
    }

    /**
     * Creates new form ImagePanel
     */
    public DurationalPanel() {
        initComponents();
    }

    private double scale = 1.0;
    private Clef clef;
    private KeySignature keySignature;
    private Durational durational;

    public void setDurational(Durational durational) {
        if (!Objects.equals(this.durational, durational)) {
            this.durational = durational;
            recalc();
        }
    }

    public Durational getDurational() {
        return durational;
    }

    public void setClef(Clef clef) {
        if (!Objects.equals(this.clef, clef)) {
            this.clef = clef;
            recalc();
        }
    }

    public Clef getClef() {
        return clef;
    }

    public void setKeySignature(KeySignature keySignature) {
        if (!Objects.equals(this.keySignature, keySignature)) {
            this.keySignature = keySignature;
            recalc();
        }
    }

    public KeySignature getKeySignature() {
        return keySignature;
    }

    public void recalc() {
        int width = WIDTH;
        int height = HEIGHT;
        Dimension size = this.getMinimumSize();
        size.setSize(width * scale, height * scale);
        this.setMinimumSize(size);
        this.setPreferredSize(size);
        this.invalidate();
        this.repaint();
    }

    public double getScale() {
        return scale;
    }

    public void setScale(double scale) {
        if (scale != this.scale) {
            this.scale = scale;
            recalc();
        }
    }

    public void zoomOut() {
        setScale(getScale() * 0.5);
    }

    public void zoomIn() {
        setScale(getScale() * 2);
    }

    private static final int WIDTH = 500;
    private static final int HEIGHT = 200;

    private static final int LINE_WIDTH = 40;
    private static final int SMALL_LINE_WIDTH = 20;
    private static final int LINE_SPACING = 10;
    private static final int NOTE_D = LINE_SPACING;
    private static final int OX = 60;
    private static final int OY = 30;

    @Override
    protected void paintComponent(Graphics g0) {
        super.paintComponent(g0); //To change body of generated methods, choose Tools | Templates.
        Graphics2D g = (Graphics2D) g0;
        g.scale(scale, -scale);
        g.translate(0, -HEIGHT);

        // Bar lines
        for (int i = 0; i < 5; i++) {
            int y = OY + (i * LINE_SPACING);
            g.drawLine(0, y, OX + LINE_WIDTH, y);
        }

        // Key signature
        {
            int dx = -OX/2;
            int smidge = LINE_SPACING / 2;
            for (Base b : keySignature.getFlats()) {
                int y = OY + ((LINE_SPACING * Utils.getLineSpace(clef, Pitch.of(b, Accidental.FLAT, 4))) / 2);
                drawAccidental(g, Accidental.FLAT, OX+(dx+=smidge), y);
            }
            for (Base b : keySignature.getSharps()) {
                int y = OY + ((LINE_SPACING * Utils.getLineSpace(clef, Pitch.of(b, Accidental.SHARP, 4))) / 2);
                drawAccidental(g, Accidental.SHARP, OX+(dx+=smidge), y);
            }
        }

        if (durational instanceof Rest) {
            int xmid = OX + (LINE_WIDTH / 2);
            int y1 = OY + (4 * LINE_SPACING);
            int y2 = OY + (3 * LINE_SPACING);
            int y3 = OY + (2 * LINE_SPACING);
            int y4 = OY + (0 * LINE_SPACING);
            int smidge = LINE_WIDTH / 4;
            g.drawLine(xmid - smidge, y1, xmid + smidge, y2);
            g.drawLine(xmid + smidge, y2, xmid - smidge, y3);
            g.drawLine(xmid - smidge, y3, xmid + smidge, y4);
        } else if (durational instanceof Note) {
            int xmid = OX + (LINE_WIDTH / 2);
            int y = OY + ((LINE_SPACING * Utils.getLineSpace(clef, ((Note) durational).getPitch())) / 2);
            g.drawOval(xmid - (NOTE_D / 2), y - (NOTE_D / 2), NOTE_D, NOTE_D);
        } else if (durational instanceof Chord) {
            Chord c = (Chord) durational;
            int minls = 0;
            int maxls = 7;
            for (Note n : c) {
                int ls = Utils.getLineSpace(clef, n.getPitch());
                minls = Math.min(minls, ls);
                maxls = Math.max(maxls, ls);
                int xmid = OX + (LINE_WIDTH / 2);
                int y = OY + ((LINE_SPACING * ls) / 2);
                g.drawOval(xmid - (NOTE_D / 2), y - (NOTE_D / 2), NOTE_D, NOTE_D);
                if (Utils.qShowAccidental(keySignature, n.getPitch())) {
                    Accidental acc = n.getPitch().getAccidental();
                    drawAccidental(g, acc, xmid, y);
                }
            }
            int OFFSET = (LINE_WIDTH - SMALL_LINE_WIDTH) / 2;
            for (int i = minls / 2; i <= maxls / 2; i++) {
                int y = OY + (i * LINE_SPACING);
                g.drawLine(OX + OFFSET, y, OX + OFFSET + SMALL_LINE_WIDTH, y);
            }
        }
    }

    private void drawAccidental(Graphics2D g, Accidental acc, int noteX, int noteY) {
        int smidge = (int)(LINE_SPACING * 0.3);
        switch (acc) {
            case DOUBLE_FLAT:
                throw new RuntimeException("UNHANDLED ACCIDENTAL");
            case FLAT:
                g.drawString("p", noteX - 5 * smidge, noteY + smidge);
                break;
            case NATURAL:
                g.drawString("N", noteX - 5 * smidge, noteY + smidge);
                break;
            case SHARP:
                g.drawString("#", noteX - 5 * smidge, noteY + smidge);
                break;
            case DOUBLE_SHARP:
                throw new RuntimeException("UNHANDLED ACCIDENTAL");
        }
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents
    // Variables declaration - do not modify//GEN-BEGIN:variables
    // End of variables declaration//GEN-END:variables
}
