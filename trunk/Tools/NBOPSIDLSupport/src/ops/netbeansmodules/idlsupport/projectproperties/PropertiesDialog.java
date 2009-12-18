/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

/*
 * PropertiesDialog.java
 *
 * Created on 2009-aug-12, 16:33:05
 */
package ops.netbeansmodules.idlsupport.projectproperties;

import java.io.File;
import javax.swing.JFileChooser;
import ops.netbeansmodules.idlsupport.OPSIDLProject;
import ops.netbeansmodules.util.FileHelper;
import ops.netbeansmodules.util.JarFileFilter;

/**
 *
 * @author angr
 */
public class PropertiesDialog extends javax.swing.JDialog
{
    private OPSProjectProperties properties;
    private String currentDirectory;
    private OPSIDLProject project;

    /** Creates new form PropertiesDialog */
    public PropertiesDialog(java.awt.Frame parent, boolean modal, OPSProjectProperties properties, String currentDirectory, OPSIDLProject project)
    {
        super(parent, modal);
        initComponents();
        this.properties = properties;
        this.currentDirectory = currentDirectory;

        genCppCheckBox.setSelected(properties.generateCpp);
        genJavaCheckBox.setSelected(properties.generateJava);
        buildJavaCheckBox.setSelected(properties.buildJava);

        domainIDTextField.setText(properties.debugProjDomainID);
        buildDebugProjectCheckBox.setSelected(properties.buildDebugProject);
        defaultConfigFileTextField.setText(properties.defaultOPSTopicConfigFile);

        jarDepList.setListData(properties.javaBuildJarDependencies);
        this.project = project;

        vsExampleEnableCheckBox.setSelected(properties.vsExampleEnabled);
        vsExampleTopicTextField.setText(properties.vsExampleTopicName);
        vsExampleDataTypeTextField.setText(properties.vsExampleDataType);
        vsExampleDomainTextField.setText(properties.vsExampleDomainID);


    }

    private void applyNewProperties()
    {
        properties.buildJava = buildJavaCheckBox.isSelected();
        properties.generateCpp = genCppCheckBox.isSelected();
        properties.generateJava = genJavaCheckBox.isSelected();
        properties.defaultOPSTopicConfigFile = defaultConfigFileTextField.getText();
        properties.buildDebugProject = buildDebugProjectCheckBox.isSelected();
        properties.debugProjDomainID = domainIDTextField.getText();
        properties.vsExampleEnabled = vsExampleEnableCheckBox.isSelected();
        properties.vsExampleTopicName = vsExampleTopicTextField.getText();
        properties.vsExampleDataType = vsExampleDataTypeTextField.getText();
        properties.vsExampleDomainID = vsExampleDomainTextField.getText();
    }

    /** This method is called from within the constructor to
     * initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is
     * always regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        mainPanel = new javax.swing.JPanel();
        jPanel1 = new javax.swing.JPanel();
        genCppCheckBox = new javax.swing.JCheckBox();
        genJavaCheckBox = new javax.swing.JCheckBox();
        jPanel2 = new javax.swing.JPanel();
        buildJavaCheckBox = new javax.swing.JCheckBox();
        jScrollPane1 = new javax.swing.JScrollPane();
        jarDepList = new javax.swing.JList();
        jLabel1 = new javax.swing.JLabel();
        addJarDepButton = new javax.swing.JButton();
        removeJarDepButton = new javax.swing.JButton();
        jPanel3 = new javax.swing.JPanel();
        defaultConfigFileLabel = new javax.swing.JLabel();
        defaultConfigFileTextField = new javax.swing.JTextField();
        changeDefaultConfigFileButton = new javax.swing.JButton();
        genConfigFromSourceCommentsCheckBox = new javax.swing.JCheckBox();
        domainIDTextField = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        buildDebugProjectCheckBox = new javax.swing.JCheckBox();
        jPanel4 = new javax.swing.JPanel();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        jPanel5 = new javax.swing.JPanel();
        vsExampleEnableCheckBox = new javax.swing.JCheckBox();
        vsExampleDomainTextField = new javax.swing.JTextField();
        vsExampleTopicTextField = new javax.swing.JTextField();
        vsExampleDataTypeTextField = new javax.swing.JTextField();
        jLabel3 = new javax.swing.JLabel();
        jLabel4 = new javax.swing.JLabel();
        jLabel5 = new javax.swing.JLabel();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.title")); // NOI18N
        setIconImage(null);
        setIconImages(null);
        setLocationByPlatform(true);
        setResizable(false);

        jPanel1.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.jPanel1.border.title"))); // NOI18N

        genCppCheckBox.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.genCppCheckBox.text")); // NOI18N

        genJavaCheckBox.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.genJavaCheckBox.text")); // NOI18N

        javax.swing.GroupLayout jPanel1Layout = new javax.swing.GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(genCppCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(genJavaCheckBox)
                .addContainerGap(258, Short.MAX_VALUE))
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(genCppCheckBox)
                    .addComponent(genJavaCheckBox))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel2.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.jPanel2.border.title"))); // NOI18N

        buildJavaCheckBox.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.buildJavaCheckBox.text")); // NOI18N

        jScrollPane1.setViewportView(jarDepList);

        jLabel1.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.jLabel1.text")); // NOI18N

        addJarDepButton.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.addJarDepButton.text")); // NOI18N
        addJarDepButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addJarDepButtonActionPerformed(evt);
            }
        });

        removeJarDepButton.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.removeJarDepButton.text")); // NOI18N

        javax.swing.GroupLayout jPanel2Layout = new javax.swing.GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, javax.swing.GroupLayout.DEFAULT_SIZE, 344, Short.MAX_VALUE)
                    .addComponent(jLabel1)
                    .addComponent(buildJavaCheckBox)
                    .addGroup(jPanel2Layout.createSequentialGroup()
                        .addComponent(addJarDepButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeJarDepButton)))
                .addContainerGap())
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel2Layout.createSequentialGroup()
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(buildJavaCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel1)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jScrollPane1, javax.swing.GroupLayout.PREFERRED_SIZE, 40, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel2Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(addJarDepButton)
                    .addComponent(removeJarDepButton)))
        );

        jPanel3.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.jPanel3.border.title"))); // NOI18N

        defaultConfigFileLabel.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.defaultConfigFileLabel.text")); // NOI18N

        defaultConfigFileTextField.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.defaultConfigFileTextField.text")); // NOI18N

        changeDefaultConfigFileButton.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.changeDefaultConfigFileButton.text")); // NOI18N
        changeDefaultConfigFileButton.setEnabled(false);

        genConfigFromSourceCommentsCheckBox.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.genConfigFromSourceCommentsCheckBox.text")); // NOI18N
        genConfigFromSourceCommentsCheckBox.setEnabled(false);

        domainIDTextField.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.domainIDTextField.text")); // NOI18N

        jLabel2.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.jLabel2.text")); // NOI18N

        buildDebugProjectCheckBox.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.buildDebugProjectCheckBox.text")); // NOI18N

        javax.swing.GroupLayout jPanel3Layout = new javax.swing.GroupLayout(jPanel3);
        jPanel3.setLayout(jPanel3Layout);
        jPanel3Layout.setHorizontalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel3Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel3Layout.createSequentialGroup()
                        .addComponent(buildDebugProjectCheckBox)
                        .addContainerGap())
                    .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                                .addGroup(jPanel3Layout.createSequentialGroup()
                                    .addComponent(defaultConfigFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 236, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                    .addComponent(changeDefaultConfigFileButton))
                                .addComponent(defaultConfigFileLabel)
                                .addComponent(genConfigFromSourceCommentsCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, 323, Short.MAX_VALUE))
                            .addGap(31, 31, 31))
                        .addGroup(jPanel3Layout.createSequentialGroup()
                            .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                                .addComponent(domainIDTextField, javax.swing.GroupLayout.Alignment.LEADING)
                                .addComponent(jLabel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, 235, Short.MAX_VALUE))
                            .addContainerGap()))))
        );
        jPanel3Layout.setVerticalGroup(
            jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel3Layout.createSequentialGroup()
                .addComponent(buildDebugProjectCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addComponent(defaultConfigFileLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel3Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(defaultConfigFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(changeDefaultConfigFileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(genConfigFromSourceCommentsCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(domainIDTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        cancelButton.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.cancelButton.text")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.okButton.text")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout jPanel4Layout = new javax.swing.GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, jPanel4Layout.createSequentialGroup()
                .addContainerGap(146, Short.MAX_VALUE)
                .addComponent(okButton)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(cancelButton)
                .addContainerGap())
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel4Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel4Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(okButton)
                    .addComponent(cancelButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        jPanel5.setBorder(javax.swing.BorderFactory.createTitledBorder(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.jPanel5.border.title"))); // NOI18N

        vsExampleEnableCheckBox.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.vsExampleEnableCheckBox.text")); // NOI18N

        vsExampleDomainTextField.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.vsExampleDomainTextField.text")); // NOI18N

        vsExampleTopicTextField.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.vsExampleTopicTextField.text")); // NOI18N
        vsExampleTopicTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                vsExampleTopicTextFieldActionPerformed(evt);
            }
        });

        vsExampleDataTypeTextField.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.vsExampleDataTypeTextField.text")); // NOI18N

        jLabel3.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.jLabel3.text")); // NOI18N

        jLabel4.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.jLabel4.text")); // NOI18N

        jLabel5.setText(org.openide.util.NbBundle.getMessage(PropertiesDialog.class, "PropertiesDialog.jLabel5.text")); // NOI18N

        javax.swing.GroupLayout jPanel5Layout = new javax.swing.GroupLayout(jPanel5);
        jPanel5.setLayout(jPanel5Layout);
        jPanel5Layout.setHorizontalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(vsExampleEnableCheckBox)
                    .addGroup(jPanel5Layout.createSequentialGroup()
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vsExampleDomainTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 104, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel3))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(vsExampleTopicTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 105, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(jLabel4))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(jLabel5)
                            .addComponent(vsExampleDataTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 101, javax.swing.GroupLayout.PREFERRED_SIZE))))
                .addContainerGap(32, Short.MAX_VALUE))
        );
        jPanel5Layout.setVerticalGroup(
            jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(jPanel5Layout.createSequentialGroup()
                .addComponent(vsExampleEnableCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel3)
                    .addComponent(jLabel4)
                    .addComponent(jLabel5))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel5Layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(vsExampleDomainTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vsExampleTopicTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(vsExampleDataTypeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
        );

        javax.swing.GroupLayout mainPanelLayout = new javax.swing.GroupLayout(mainPanel);
        mainPanel.setLayout(mainPanelLayout);
        mainPanelLayout.setHorizontalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(jPanel5, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel2, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(jPanel1, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addGroup(javax.swing.GroupLayout.Alignment.LEADING, mainPanelLayout.createSequentialGroup()
                        .addGap(106, 106, 106)
                        .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                    .addComponent(jPanel3, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );
        mainPanelLayout.setVerticalGroup(
            mainPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(mainPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jPanel1, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel2, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel3, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jPanel5, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(11, 11, 11)
                .addComponent(jPanel4, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(mainPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_cancelButtonActionPerformed
    {//GEN-HEADEREND:event_cancelButtonActionPerformed
        // TODO add your handling code here:
        this.dispose();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_okButtonActionPerformed
    {//GEN-HEADEREND:event_okButtonActionPerformed
        // TODO add your handling code here:
        applyNewProperties();
        project.save();
        this.dispose();
    }//GEN-LAST:event_okButtonActionPerformed

    private void addJarDepButtonActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_addJarDepButtonActionPerformed
    {//GEN-HEADEREND:event_addJarDepButtonActionPerformed
        // TODO add your handling code here:
        JFileChooser fc = new JFileChooser(currentDirectory);
        fc.setFileFilter(new JarFileFilter());
        fc.setAcceptAllFileFilterUsed(false);
        fc.setVisible(true);

        int result = fc.showOpenDialog(this);

        if(result == JFileChooser.APPROVE_OPTION)
        {
            properties.javaBuildJarDependencies.add(new JarDependency(FileHelper.unixSlashed(FileHelper.getRelativePath(new File(currentDirectory), new File(fc.getSelectedFile().getPath())))));
        }
        jarDepList.updateUI();
    }//GEN-LAST:event_addJarDepButtonActionPerformed

    private void vsExampleTopicTextFieldActionPerformed(java.awt.event.ActionEvent evt)//GEN-FIRST:event_vsExampleTopicTextFieldActionPerformed
    {//GEN-HEADEREND:event_vsExampleTopicTextFieldActionPerformed
        // TODO add your handling code here:
}//GEN-LAST:event_vsExampleTopicTextFieldActionPerformed

    /**
     * @param args the command line arguments
     */
    public static void main(String args[])
    {
//        java.awt.EventQueue.invokeLater(new Runnable()
//        {
//
//            public void run()
//            {
//                PropertiesDialog dialog = new PropertiesDialog(new javax.swing.JFrame(), true);
//                dialog.addWindowListener(new java.awt.event.WindowAdapter()
//                {
//
//                    public void windowClosing(java.awt.event.WindowEvent e)
//                    {
//                        System.exit(0);
//                    }
//                });
//                dialog.setVisible(true);
//            }
//        });
    }

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addJarDepButton;
    private javax.swing.JCheckBox buildDebugProjectCheckBox;
    private javax.swing.JCheckBox buildJavaCheckBox;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton changeDefaultConfigFileButton;
    private javax.swing.JLabel defaultConfigFileLabel;
    private javax.swing.JTextField defaultConfigFileTextField;
    private javax.swing.JTextField domainIDTextField;
    private javax.swing.JCheckBox genConfigFromSourceCommentsCheckBox;
    private javax.swing.JCheckBox genCppCheckBox;
    private javax.swing.JCheckBox genJavaCheckBox;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JLabel jLabel4;
    private javax.swing.JLabel jLabel5;
    private javax.swing.JPanel jPanel1;
    private javax.swing.JPanel jPanel2;
    private javax.swing.JPanel jPanel3;
    private javax.swing.JPanel jPanel4;
    private javax.swing.JPanel jPanel5;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JList jarDepList;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JButton okButton;
    private javax.swing.JButton removeJarDepButton;
    private javax.swing.JTextField vsExampleDataTypeTextField;
    private javax.swing.JTextField vsExampleDomainTextField;
    private javax.swing.JCheckBox vsExampleEnableCheckBox;
    private javax.swing.JTextField vsExampleTopicTextField;
    // End of variables declaration//GEN-END:variables
}
