/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.erhannis.miditranscribe;

import java.awt.Dimension;
import java.awt.Image;
import org.wmn4j.notation.Clef;
import org.wmn4j.notation.Durational;
import org.wmn4j.notation.KeySignature;

/**
 *
 * @author mewer
 */
public class DurationalFrame extends javax.swing.JFrame {

    /**
     * Creates new form MainFrame
     */
    public DurationalFrame(Durational durational) {
        this();
        ((DurationalPanel) panelDisplay).setDurational(durational);
        panelDisplay.repaint();
    }

    public DurationalFrame() {
        initComponents();
    }

    public void setDurational(Durational durational) {
        ((DurationalPanel) panelDisplay).setDurational(durational);
        panelDisplay.repaint();
    }

    public Durational getDurational() {
        return ((DurationalPanel) panelDisplay).getDurational();
    }

    public void setClef(Clef clef) {
        ((DurationalPanel) panelDisplay).setClef(clef);
    }

    public Clef getClef() {
        return ((DurationalPanel) panelDisplay).getClef();
    }

    public void setKeySignature(KeySignature keySignature) {
        ((DurationalPanel) panelDisplay).setKeySignature(keySignature);
    }

    public KeySignature getKeySignature() {
        return ((DurationalPanel) panelDisplay).getKeySignature();
    }
    
    public double getScale() {
        return ((DurationalPanel) panelDisplay).getScale();
    }

    public void setScale(double scale) {
        ((DurationalPanel) panelDisplay).setScale(scale);
    }

    public void zoomOut() {
        ((DurationalPanel) panelDisplay).zoomOut();
    }

    public void zoomIn() {
        ((DurationalPanel) panelDisplay).zoomIn();
    }

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        panelDisplay = new DurationalPanel();

        org.jdesktop.layout.GroupLayout panelDisplayLayout = new org.jdesktop.layout.GroupLayout(panelDisplay);
        panelDisplay.setLayout(panelDisplayLayout);
        panelDisplayLayout.setHorizontalGroup(
            panelDisplayLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 400, Short.MAX_VALUE)
        );
        panelDisplayLayout.setVerticalGroup(
            panelDisplayLayout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(0, 300, Short.MAX_VALUE)
        );

        org.jdesktop.layout.GroupLayout layout = new org.jdesktop.layout.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelDisplay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(org.jdesktop.layout.GroupLayout.LEADING)
            .add(panelDisplay, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, org.jdesktop.layout.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    /**
     * @param args the command line arguments
     */
    public static void main(String args[]) {
        /* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
         * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
         */
        try {
            for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
                if ("Nimbus".equals(info.getName())) {
                    javax.swing.UIManager.setLookAndFeel(info.getClassName());
                    break;
                }
            }
        } catch (ClassNotFoundException ex) {
            java.util.logging.Logger.getLogger(DurationalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (InstantiationException ex) {
            java.util.logging.Logger.getLogger(DurationalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (IllegalAccessException ex) {
            java.util.logging.Logger.getLogger(DurationalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        } catch (javax.swing.UnsupportedLookAndFeelException ex) {
            java.util.logging.Logger.getLogger(DurationalFrame.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
        }
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>
        //</editor-fold>

        /* Create and display the form */
        java.awt.EventQueue.invokeLater(new Runnable() {
            public void run() {
                new DurationalFrame(null).setVisible(true);
            }
        });
    }
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JPanel panelDisplay;
    // End of variables declaration//GEN-END:variables
}