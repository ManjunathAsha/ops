/*
 * PlotterMainPanel.java
 *
 * Created on den 25 april 2008, 10:35
 */

package opsdebugger2.tabpanels.plotter;

/**
 *
 * @author  angr
 */
public class PlotterMainPanel extends javax.swing.JPanel {

    /** Creates new form PlotterMainPanel */
    public PlotterMainPanel() {
        initComponents();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        plotAreaPanel1 = new opsdebugger2.tabpanels.plotter.PlotAreaPanel();

        setName("Form"); // NOI18N

        plotAreaPanel1.setName("plotAreaPanel1"); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(this);
        this.setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plotAreaPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 599, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(plotAreaPanel1, javax.swing.GroupLayout.DEFAULT_SIZE, 470, Short.MAX_VALUE)
        );
    }// </editor-fold>//GEN-END:initComponents


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private opsdebugger2.tabpanels.plotter.PlotAreaPanel plotAreaPanel1;
    // End of variables declaration//GEN-END:variables

}