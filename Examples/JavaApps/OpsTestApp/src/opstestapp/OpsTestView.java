/*
 * OpsTestView.java
 */

package opstestapp;

import ops.Notifier;
import org.jdesktop.application.Action;
import org.jdesktop.application.ResourceMap;
import org.jdesktop.application.SingleFrameApplication;
import org.jdesktop.application.FrameView;
import org.jdesktop.application.TaskMonitor;
import java.util.Observable;
import java.util.Observer;
import java.util.Vector;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;
import javax.swing.Icon;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.*;
import javax.swing.text.*;
//import configlib.exception.FormatException;
import ops.Participant;
import ops.OPSConfig;
import ops.Domain;
import ops.Topic;
import ops.KeyFilterQoSPolicy;

import pizza.PizzaData;
import pizza.PizzaDataSubscriber;
import pizza.PizzaDataPublisher;
import pizza.VessuvioData;
import pizza.VessuvioDataSubscriber;
import pizza.VessuvioDataPublisher;
import PizzaProject.PizzaProjectTypeFactory;

import java.awt.Point;
import java.awt.Rectangle;
import java.net.Inet4Address;
import java.net.NetworkInterface;
import java.util.Enumeration;
import ops.ConfigurationException;
import ops.Listener;
import ops.OPSObject;

/**
 * The application's main frame.
 */
public class OpsTestView extends FrameView implements IOpsHelperListener, Listener<ops.Error> {

    private String defaultDomain = "PizzaDomain";
    private String OtherDomain = "OtherPizzaDomain";

    private Participant participant = null;
    private Participant otherParticipant = null;

    private class MyTopicInfo {
        public String domain;
        public String topName;
        public String typeName;

        public COpsHelper helper;
        public Participant part;
        public JCheckBox checkBox;

        public MyTopicInfo(String dom, String top, String typ)
        {
            this.domain = dom;
            this.topName = top;
            this.typeName = typ;
        }
    }
    Vector<MyTopicInfo> MyTopicInfoList = new Vector<MyTopicInfo>();

    private pizza.PizzaData pizzaData = new PizzaData();
    private pizza.VessuvioData vessuvioData = new VessuvioData();

    private long deadLineEventIntervall = 0;
    private int Counter = 0;

    private Timer sendTimer = null;
    
    public OpsTestView(SingleFrameApplication app) {
        super(app);

        initComponents();

        // status bar initialization - message timeout, idle icon and busy animation, etc
        ResourceMap resourceMap = getResourceMap();
        int messageTimeout = resourceMap.getInteger("StatusBar.messageTimeout");
        messageTimer = new Timer(messageTimeout, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                statusMessageLabel.setText("");
            }
        });
        messageTimer.setRepeats(false);
        int busyAnimationRate = resourceMap.getInteger("StatusBar.busyAnimationRate");
        for (int i = 0; i < busyIcons.length; i++) {
            busyIcons[i] = resourceMap.getIcon("StatusBar.busyIcons[" + i + "]");
        }
        busyIconTimer = new Timer(busyAnimationRate, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                busyIconIndex = (busyIconIndex + 1) % busyIcons.length;
                statusAnimationLabel.setIcon(busyIcons[busyIconIndex]);
            }
        });
        idleIcon = resourceMap.getIcon("StatusBar.idleIcon");
        statusAnimationLabel.setIcon(idleIcon);
        progressBar.setVisible(false);

        // connecting action tasks to status bar via TaskMonitor
        TaskMonitor taskMonitor = new TaskMonitor(getApplication().getContext());
        taskMonitor.addPropertyChangeListener(new java.beans.PropertyChangeListener() {
            public void propertyChange(java.beans.PropertyChangeEvent evt) {
                String propertyName = evt.getPropertyName();
                if ("started".equals(propertyName)) {
                    if (!busyIconTimer.isRunning()) {
                        statusAnimationLabel.setIcon(busyIcons[0]);
                        busyIconIndex = 0;
                        busyIconTimer.start();
                    }
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(true);
                } else if ("done".equals(propertyName)) {
                    busyIconTimer.stop();
                    statusAnimationLabel.setIcon(idleIcon);
                    progressBar.setVisible(false);
                    progressBar.setValue(0);
                } else if ("message".equals(propertyName)) {
                    String text = (String)(evt.getNewValue());
                    statusMessageLabel.setText((text == null) ? "" : text);
                    messageTimer.restart();
                } else if ("progress".equals(propertyName)) {
                    int value = (Integer)(evt.getNewValue());
                    progressBar.setVisible(true);
                    progressBar.setIndeterminate(false);
                    progressBar.setValue(value);
                }
            }
        });

        jTextFieldDeadlineInterval.setText("0");
        jTextFieldNumExtraBytes.setText("0");
        jTextFieldSendPeriod.setText("1000");

        MyTopicInfoList.add(new MyTopicInfo("PizzaDomain",      "PizzaTopic",         "pizza.PizzaData"));
        MyTopicInfoList.add(new MyTopicInfo("PizzaDomain",      "VessuvioTopic",      "pizza.VessuvioData"));
        MyTopicInfoList.add(new MyTopicInfo("PizzaDomain",      "PizzaTopic2",        "pizza.PizzaData"));
        MyTopicInfoList.add(new MyTopicInfo("PizzaDomain",      "VessuvioTopic2",     "pizza.VessuvioData"));
        MyTopicInfoList.add(new MyTopicInfo("PizzaDomain",      "TcpPizzaTopic",      "pizza.PizzaData"));
        MyTopicInfoList.add(new MyTopicInfo("PizzaDomain",      "TcpVessuvioTopic",   "pizza.VessuvioData"));
        MyTopicInfoList.add(new MyTopicInfo("PizzaDomain",      "TcpPizzaTopic2",     "pizza.PizzaData"));
        MyTopicInfoList.add(new MyTopicInfo("PizzaDomain",      "TcpVessuvioTopic2",  "pizza.VessuvioData"));
        MyTopicInfoList.add(new MyTopicInfo("OtherPizzaDomain", "OtherPizzaTopic",    "pizza.PizzaData"));
        MyTopicInfoList.add(new MyTopicInfo("OtherPizzaDomain", "OtherVessuvioTopic", "pizza.VessuvioData"));
        MyTopicInfoList.add(new MyTopicInfo("PizzaDomain",      "UdpPizzaTopic",      "pizza.PizzaData"));
        MyTopicInfoList.add(new MyTopicInfo("PizzaDomain",      "UdpVessuvioTopic",   "pizza.VessuvioData"));

        for(int i = 0; i < MyTopicInfoList.size(); i++) {
            MyTopicInfo info = MyTopicInfoList.elementAt(i);
            info.helper = new COpsHelper(this);
        }

        MyTopicInfoList.elementAt(0).checkBox = jCheckBox01;
        MyTopicInfoList.elementAt(1).checkBox = jCheckBox02;
        MyTopicInfoList.elementAt(2).checkBox = jCheckBox03;
        MyTopicInfoList.elementAt(3).checkBox = jCheckBox04;
        MyTopicInfoList.elementAt(4).checkBox = jCheckBox05;
        MyTopicInfoList.elementAt(5).checkBox = jCheckBox06;
        MyTopicInfoList.elementAt(6).checkBox = jCheckBox07;
        MyTopicInfoList.elementAt(7).checkBox = jCheckBox08;
        MyTopicInfoList.elementAt(8).checkBox = jCheckBox09;
        MyTopicInfoList.elementAt(9).checkBox = jCheckBox10;
        MyTopicInfoList.elementAt(10).checkBox = jCheckBox11;
        MyTopicInfoList.elementAt(11).checkBox = jCheckBox12;

        int sendPeriod = 1000;
        sendTimer = new Timer(sendPeriod, new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                WriteToAll();
            }
        });
        sendTimer.setRepeats(true);
        sendTimer.stop();
    }

    private void Log(String str)
    {
        jTextArea1.append(str);

        int topLine = jTextArea1.getLineCount() - 8;

        try {
            JViewport viewport = jScrollPane1.getViewport();
            int offset = jTextArea1.getLineStartOffset(topLine);
            Rectangle startLocation = jTextArea1.modelToView(offset);
            viewport.setViewPosition(new Point(startLocation.x, startLocation.y));
        } catch(BadLocationException e) {
        }
    }

    public void OnLog(final String str)
    {
        //Post changes to gui-thread in a safe way
        SwingUtilities.invokeLater(new Runnable()
        {
            public void run()
            {
                Log(str);
            }
        });
    }

    public void onNewEvent(Notifier<ops.Error> notifier, ops.Error arg)
    {
        OnLog("###!!! " + arg.getErrorMessage());
    }

    public void OnData(final String topName, final pizza.PizzaData Data)
    {
        OnLog("[ " + topName + " ] New PizzaData: " + Data.cheese + "\n");
    }

    public void OnData(final String topName, final pizza.VessuvioData Data)
    {
        OnLog("[ " + topName + " ] New VessuvioData: " + Data.cheese + ", Ham length: " + Data.ham.length() + "\n");
    }

    public void OnData(final String topName, final OPSObject Data)
    {
        if (Data instanceof VessuvioData) {
            OnData(topName, (VessuvioData)Data);
        } else if (Data instanceof PizzaData) {
            OnData(topName, (PizzaData)Data);
        }
    }

    @Action
    public void showAboutBox() {
        if (aboutBox == null) {
            JFrame mainFrame = OpsTestApp.getApplication().getMainFrame();
            aboutBox = new OpsTestAboutBox(mainFrame);
            aboutBox.setLocationRelativeTo(mainFrame);
        }
        OpsTestApp.getApplication().show(aboutBox);
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
        jButtonCreateParticipant = new javax.swing.JButton();
        jButtonCreateSubscriber = new javax.swing.JButton();
        jButtonCreatePublisher = new javax.swing.JButton();
        jButtonSendMessage = new javax.swing.JButton();
        jScrollPane1 = new javax.swing.JScrollPane();
        jTextArea1 = new javax.swing.JTextArea();
        jButtonDeleteSubscriber = new javax.swing.JButton();
        jButtonDeletePublisher = new javax.swing.JButton();
        jButtonStartSubscriber = new javax.swing.JButton();
        jButtonStopSubscriber = new javax.swing.JButton();
        jTextFieldDeadlineInterval = new javax.swing.JTextField();
        jLabel1 = new javax.swing.JLabel();
        jButtonDeadlineIntervalSet = new javax.swing.JButton();
        jButton1 = new javax.swing.JButton();
        jCheckBox01 = new javax.swing.JCheckBox();
        jCheckBox03 = new javax.swing.JCheckBox();
        jCheckBox02 = new javax.swing.JCheckBox();
        jCheckBox06 = new javax.swing.JCheckBox();
        jCheckBox05 = new javax.swing.JCheckBox();
        jCheckBox04 = new javax.swing.JCheckBox();
        jCheckBox10 = new javax.swing.JCheckBox();
        jCheckBox08 = new javax.swing.JCheckBox();
        jCheckBox07 = new javax.swing.JCheckBox();
        jCheckBox09 = new javax.swing.JCheckBox();
        jTextFieldSendPeriod = new javax.swing.JTextField();
        jTextFieldNumExtraBytes = new javax.swing.JTextField();
        jLabel2 = new javax.swing.JLabel();
        jLabel3 = new javax.swing.JLabel();
        jCheckBoxAutoSend = new javax.swing.JCheckBox();
        jCheckBox11 = new javax.swing.JCheckBox();
        jCheckBox12 = new javax.swing.JCheckBox();
        menuBar = new javax.swing.JMenuBar();
        javax.swing.JMenu fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem exitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu helpMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem aboutMenuItem = new javax.swing.JMenuItem();
        statusPanel = new javax.swing.JPanel();
        javax.swing.JSeparator statusPanelSeparator = new javax.swing.JSeparator();
        statusMessageLabel = new javax.swing.JLabel();
        statusAnimationLabel = new javax.swing.JLabel();
        progressBar = new javax.swing.JProgressBar();

        mainPanel.setName("mainPanel"); // NOI18N
        mainPanel.setLayout(new org.netbeans.lib.awtextra.AbsoluteLayout());

        org.jdesktop.application.ResourceMap resourceMap = org.jdesktop.application.Application.getInstance(opstestapp.OpsTestApp.class).getContext().getResourceMap(OpsTestView.class);
        jButtonCreateParticipant.setText(resourceMap.getString("jButtonCreateParticipant.text")); // NOI18N
        jButtonCreateParticipant.setName("jButtonCreateParticipant"); // NOI18N
        jButtonCreateParticipant.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateParticipantActionPerformed(evt);
            }
        });
        mainPanel.add(jButtonCreateParticipant, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 11, -1, -1));

        jButtonCreateSubscriber.setText(resourceMap.getString("jButtonCreateSubscriber.text")); // NOI18N
        jButtonCreateSubscriber.setName("jButtonCreateSubscriber"); // NOI18N
        jButtonCreateSubscriber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreateSubscriberActionPerformed(evt);
            }
        });
        mainPanel.add(jButtonCreateSubscriber, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 40, -1, -1));

        jButtonCreatePublisher.setText(resourceMap.getString("jButtonCreatePublisher.text")); // NOI18N
        jButtonCreatePublisher.setName("jButtonCreatePublisher"); // NOI18N
        jButtonCreatePublisher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonCreatePublisherActionPerformed(evt);
            }
        });
        mainPanel.add(jButtonCreatePublisher, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 69, 120, -1));

        jButtonSendMessage.setText(resourceMap.getString("jButtonSendMessage.text")); // NOI18N
        jButtonSendMessage.setName("jButtonSendMessage"); // NOI18N
        jButtonSendMessage.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonSendMessageActionPerformed(evt);
            }
        });
        mainPanel.add(jButtonSendMessage, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 190, -1, -1));

        jScrollPane1.setName("jScrollPane1"); // NOI18N

        jTextArea1.setColumns(20);
        jTextArea1.setRows(5);
        jTextArea1.setName("jTextArea1"); // NOI18N
        jScrollPane1.setViewportView(jTextArea1);

        mainPanel.add(jScrollPane1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 260, 694, 240));

        jButtonDeleteSubscriber.setText(resourceMap.getString("jButtonDeleteSubscriber.text")); // NOI18N
        jButtonDeleteSubscriber.setName("jButtonDeleteSubscriber"); // NOI18N
        jButtonDeleteSubscriber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeleteSubscriberActionPerformed(evt);
            }
        });
        mainPanel.add(jButtonDeleteSubscriber, new org.netbeans.lib.awtextra.AbsoluteConstraints(137, 40, -1, -1));

        jButtonDeletePublisher.setText(resourceMap.getString("jButtonDeletePublisher.text")); // NOI18N
        jButtonDeletePublisher.setName("jButtonDeletePublisher"); // NOI18N
        jButtonDeletePublisher.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeletePublisherActionPerformed(evt);
            }
        });
        mainPanel.add(jButtonDeletePublisher, new org.netbeans.lib.awtextra.AbsoluteConstraints(137, 69, 117, -1));

        jButtonStartSubscriber.setText(resourceMap.getString("jButtonStartSubscriber.text")); // NOI18N
        jButtonStartSubscriber.setName("jButtonStartSubscriber"); // NOI18N
        jButtonStartSubscriber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStartSubscriberActionPerformed(evt);
            }
        });
        mainPanel.add(jButtonStartSubscriber, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 103, 120, -1));

        jButtonStopSubscriber.setText(resourceMap.getString("jButtonStopSubscriber.text")); // NOI18N
        jButtonStopSubscriber.setName("jButtonStopSubscriber"); // NOI18N
        jButtonStopSubscriber.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonStopSubscriberActionPerformed(evt);
            }
        });
        mainPanel.add(jButtonStopSubscriber, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 132, 120, -1));

        jTextFieldDeadlineInterval.setText(resourceMap.getString("jTextFieldDeadlineInterval.text")); // NOI18N
        jTextFieldDeadlineInterval.setName("jTextFieldDeadlineInterval"); // NOI18N
        mainPanel.add(jTextFieldDeadlineInterval, new org.netbeans.lib.awtextra.AbsoluteConstraints(137, 188, 29, -1));

        jLabel1.setText(resourceMap.getString("jLabel1.text")); // NOI18N
        jLabel1.setName("jLabel1"); // NOI18N
        mainPanel.add(jLabel1, new org.netbeans.lib.awtextra.AbsoluteConstraints(10, 191, -1, -1));

        jButtonDeadlineIntervalSet.setText(resourceMap.getString("jButtonDeadlineIntervalSet.text")); // NOI18N
        jButtonDeadlineIntervalSet.setName("jButtonDeadlineIntervalSet"); // NOI18N
        jButtonDeadlineIntervalSet.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButtonDeadlineIntervalSetActionPerformed(evt);
            }
        });
        mainPanel.add(jButtonDeadlineIntervalSet, new org.netbeans.lib.awtextra.AbsoluteConstraints(176, 187, -1, -1));

        jButton1.setText(resourceMap.getString("jButton1.text")); // NOI18N
        jButton1.setName("jButton1"); // NOI18N
        jButton1.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jButton1ActionPerformed(evt);
            }
        });
        mainPanel.add(jButton1, new org.netbeans.lib.awtextra.AbsoluteConstraints(630, 30, -1, -1));

        jCheckBox01.setText(resourceMap.getString("jCheckBox01.text")); // NOI18N
        jCheckBox01.setName("jCheckBox01"); // NOI18N
        mainPanel.add(jCheckBox01, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 10, -1, -1));

        jCheckBox03.setText(resourceMap.getString("jCheckBox03.text")); // NOI18N
        jCheckBox03.setName("jCheckBox03"); // NOI18N
        mainPanel.add(jCheckBox03, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 50, -1, -1));

        jCheckBox02.setText(resourceMap.getString("jCheckBox02.text")); // NOI18N
        jCheckBox02.setName("jCheckBox02"); // NOI18N
        mainPanel.add(jCheckBox02, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 30, -1, -1));

        jCheckBox06.setText(resourceMap.getString("jCheckBox06.text")); // NOI18N
        jCheckBox06.setName("jCheckBox06"); // NOI18N
        mainPanel.add(jCheckBox06, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 110, -1, -1));

        jCheckBox05.setText(resourceMap.getString("jCheckBox05.text")); // NOI18N
        jCheckBox05.setName("jCheckBox05"); // NOI18N
        mainPanel.add(jCheckBox05, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 90, -1, -1));

        jCheckBox04.setText(resourceMap.getString("jCheckBox04.text")); // NOI18N
        jCheckBox04.setName("jCheckBox04"); // NOI18N
        mainPanel.add(jCheckBox04, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 70, -1, -1));

        jCheckBox10.setText(resourceMap.getString("jCheckBox10.text")); // NOI18N
        jCheckBox10.setName("jCheckBox10"); // NOI18N
        mainPanel.add(jCheckBox10, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 190, -1, -1));

        jCheckBox08.setText(resourceMap.getString("jCheckBox08.text")); // NOI18N
        jCheckBox08.setName("jCheckBox08"); // NOI18N
        mainPanel.add(jCheckBox08, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 150, -1, -1));

        jCheckBox07.setText(resourceMap.getString("jCheckBox07.text")); // NOI18N
        jCheckBox07.setName("jCheckBox07"); // NOI18N
        mainPanel.add(jCheckBox07, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 130, -1, -1));

        jCheckBox09.setText(resourceMap.getString("jCheckBox09.text")); // NOI18N
        jCheckBox09.setName("jCheckBox09"); // NOI18N
        mainPanel.add(jCheckBox09, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 170, -1, -1));

        jTextFieldSendPeriod.setText(resourceMap.getString("jTextFieldSendPeriod.text")); // NOI18N
        jTextFieldSendPeriod.setName("jTextFieldSendPeriod"); // NOI18N
        mainPanel.add(jTextFieldSendPeriod, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 110, 60, -1));

        jTextFieldNumExtraBytes.setText(resourceMap.getString("jTextFieldNumExtraBytes.text")); // NOI18N
        jTextFieldNumExtraBytes.setMinimumSize(new java.awt.Dimension(50, 20));
        jTextFieldNumExtraBytes.setName("jTextFieldNumExtraBytes"); // NOI18N
        mainPanel.add(jTextFieldNumExtraBytes, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 155, 60, -1));

        jLabel2.setText(resourceMap.getString("jLabel2.text")); // NOI18N
        jLabel2.setName("jLabel2"); // NOI18N
        mainPanel.add(jLabel2, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 140, -1, -1));

        jLabel3.setText(resourceMap.getString("jLabel3.text")); // NOI18N
        jLabel3.setName("jLabel3"); // NOI18N
        mainPanel.add(jLabel3, new org.netbeans.lib.awtextra.AbsoluteConstraints(560, 95, -1, -1));

        jCheckBoxAutoSend.setText(resourceMap.getString("jCheckBoxAutoSend.text")); // NOI18N
        jCheckBoxAutoSend.setName("jCheckBoxAutoSend"); // NOI18N
        jCheckBoxAutoSend.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                jCheckBoxAutoSendActionPerformed(evt);
            }
        });
        mainPanel.add(jCheckBoxAutoSend, new org.netbeans.lib.awtextra.AbsoluteConstraints(660, 190, 50, -1));

        jCheckBox11.setText(resourceMap.getString("jCheckBox11.text")); // NOI18N
        jCheckBox11.setName("jCheckBox11"); // NOI18N
        mainPanel.add(jCheckBox11, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 210, -1, -1));

        jCheckBox12.setText(resourceMap.getString("jCheckBox12.text")); // NOI18N
        jCheckBox12.setName("jCheckBox12"); // NOI18N
        mainPanel.add(jCheckBox12, new org.netbeans.lib.awtextra.AbsoluteConstraints(280, 230, -1, -1));

        menuBar.setName("menuBar"); // NOI18N

        fileMenu.setText(resourceMap.getString("fileMenu.text")); // NOI18N
        fileMenu.setName("fileMenu"); // NOI18N

        javax.swing.ActionMap actionMap = org.jdesktop.application.Application.getInstance(opstestapp.OpsTestApp.class).getContext().getActionMap(OpsTestView.class, this);
        exitMenuItem.setAction(actionMap.get("quit")); // NOI18N
        exitMenuItem.setName("exitMenuItem"); // NOI18N
        fileMenu.add(exitMenuItem);

        menuBar.add(fileMenu);

        helpMenu.setText(resourceMap.getString("helpMenu.text")); // NOI18N
        helpMenu.setName("helpMenu"); // NOI18N

        aboutMenuItem.setAction(actionMap.get("showAboutBox")); // NOI18N
        aboutMenuItem.setName("aboutMenuItem"); // NOI18N
        helpMenu.add(aboutMenuItem);

        menuBar.add(helpMenu);

        statusPanel.setName("statusPanel"); // NOI18N

        statusPanelSeparator.setName("statusPanelSeparator"); // NOI18N

        statusMessageLabel.setName("statusMessageLabel"); // NOI18N

        statusAnimationLabel.setHorizontalAlignment(javax.swing.SwingConstants.LEFT);
        statusAnimationLabel.setName("statusAnimationLabel"); // NOI18N

        progressBar.setName("progressBar"); // NOI18N

        javax.swing.GroupLayout statusPanelLayout = new javax.swing.GroupLayout(statusPanel);
        statusPanel.setLayout(statusPanelLayout);
        statusPanelLayout.setHorizontalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(statusPanelSeparator, javax.swing.GroupLayout.DEFAULT_SIZE, 718, Short.MAX_VALUE)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addComponent(statusMessageLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 548, Short.MAX_VALUE)
                .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(statusAnimationLabel)
                .addContainerGap())
        );
        statusPanelLayout.setVerticalGroup(
            statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(statusPanelLayout.createSequentialGroup()
                .addComponent(statusPanelSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 2, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(statusPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(statusMessageLabel)
                    .addComponent(statusAnimationLabel)
                    .addComponent(progressBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(3, 3, 3))
        );

        setComponent(mainPanel);
        setMenuBar(menuBar);
        setStatusBar(statusPanel);
    }// </editor-fold>//GEN-END:initComponents

    private void jButtonCreateParticipantActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateParticipantActionPerformed
        // TODO add your handling code here:
        if (participant == null) {
            try
            {
                //Create Participant
                participant = Participant.getInstance(defaultDomain, defaultDomain);
                participant.addTypeSupport(new PizzaProjectTypeFactory());
                participant.addListener(this);
                otherParticipant = Participant.getInstance(OtherDomain, OtherDomain);
                otherParticipant.addTypeSupport(new PizzaProjectTypeFactory());
                otherParticipant.addListener(this);
                Log("Created Participant\n");

                for(int i = 0; i < MyTopicInfoList.size(); i++) {
                    MyTopicInfo info = MyTopicInfoList.elementAt(i);
                    if (info.domain.equals(defaultDomain)) {
                        info.part = participant;
                    } else if (info.domain.equals(OtherDomain)) {
                        info.part = otherParticipant;
                    }
                }
            }
            catch (ConfigurationException e)
            {
                Log("Exception: " + e.getMessage());
            }
        } else {
            Log("Participant already created!!\n");
        }
    }//GEN-LAST:event_jButtonCreateParticipantActionPerformed

    private void jButtonCreateSubscriberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreateSubscriberActionPerformed
        // TODO add your handling code here:
        for(int i = 0; i < MyTopicInfoList.size(); i++) {
            MyTopicInfo info = MyTopicInfoList.elementAt(i);
            if (info.checkBox.isSelected()) {
                info.helper.CreateSubscriber(info.part, info.topName);
            }
        }
    }//GEN-LAST:event_jButtonCreateSubscriberActionPerformed

    private void jButtonCreatePublisherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonCreatePublisherActionPerformed
        // TODO add your handling code here:
        for(int i = 0; i < MyTopicInfoList.size(); i++) {
            MyTopicInfo info = MyTopicInfoList.elementAt(i);
            if (info.checkBox.isSelected()) {
                info.helper.CreatePublisher(info.part, info.topName);
            }
        }
    }//GEN-LAST:event_jButtonCreatePublisherActionPerformed
    
    private void jButtonSendMessageActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonSendMessageActionPerformed
        // TODO add your handling code here:
        WriteToAll();
    }

    private void WriteToAll() {
        Counter++;
        pizzaData.cheese = "Java " + Counter;

        vessuvioData.cheese = "Java " + Counter;

        int NumExtraBytes = 0;
        try
        {
            NumExtraBytes = Integer.parseInt(jTextFieldNumExtraBytes.getText().trim());
        }
        catch (NumberFormatException nfe)
        {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }
        StringBuilder sb = new StringBuilder();
        sb.setLength(NumExtraBytes);
        vessuvioData.ham = sb.toString();

        for(int i = 0; i < MyTopicInfoList.size(); i++) {
            MyTopicInfo info = MyTopicInfoList.elementAt(i);
            if (info.checkBox.isSelected()) {
                if (info.typeName.equals(PizzaData.getTypeName())) {
                    info.helper.Write(pizzaData);
                }
                if (info.typeName.equals(VessuvioData.getTypeName())) {
                    info.helper.Write(vessuvioData);
                }
            }
        }
    }//GEN-LAST:event_jButtonSendMessageActionPerformed

    private void jButtonDeleteSubscriberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeleteSubscriberActionPerformed
        // TODO add your handling code here:
        for(int i = 0; i < MyTopicInfoList.size(); i++) {
            MyTopicInfo info = MyTopicInfoList.elementAt(i);
            if (info.checkBox.isSelected()) {
                info.helper.DeleteSubscriber();
            }
        }
    }//GEN-LAST:event_jButtonDeleteSubscriberActionPerformed

    private void jButtonDeletePublisherActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeletePublisherActionPerformed
        // TODO add your handling code here:
        for(int i = 0; i < MyTopicInfoList.size(); i++) {
            MyTopicInfo info = MyTopicInfoList.elementAt(i);
            if (info.checkBox.isSelected()) {
                info.helper.DeletePublisher();
            }
        }
    }//GEN-LAST:event_jButtonDeletePublisherActionPerformed

    private void jButtonStartSubscriberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStartSubscriberActionPerformed
        // TODO add your handling code here:
        for(int i = 0; i < MyTopicInfoList.size(); i++) {
            MyTopicInfo info = MyTopicInfoList.elementAt(i);
            if (info.checkBox.isSelected()) {
                info.helper.StartSubscriber(deadLineEventIntervall);
            }
        }
    }//GEN-LAST:event_jButtonStartSubscriberActionPerformed

    private void jButtonStopSubscriberActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonStopSubscriberActionPerformed
        // TODO add your handling code here:
        for(int i = 0; i < MyTopicInfoList.size(); i++) {
            MyTopicInfo info = MyTopicInfoList.elementAt(i);
            if (info.checkBox.isSelected()) {
                info.helper.StopSubscriber(true);
            }
        }
    }//GEN-LAST:event_jButtonStopSubscriberActionPerformed

    private void jButtonDeadlineIntervalSetActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButtonDeadlineIntervalSetActionPerformed
        // TODO add your handling code here:
        try
        {
            int tmp = Integer.parseInt(jTextFieldDeadlineInterval.getText().trim());
            deadLineEventIntervall = tmp * 1000;

            for(int i = 0; i < MyTopicInfoList.size(); i++) {
                MyTopicInfo info = MyTopicInfoList.elementAt(i);
                if (info.checkBox.isSelected()) {
                    info.helper.SetDeadLineInterval(deadLineEventIntervall);
                }
            }
        }
        catch (NumberFormatException nfe)
        {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }
    }//GEN-LAST:event_jButtonDeadlineIntervalSetActionPerformed

    private void jButton1ActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jButton1ActionPerformed
        // TODO add your handling code here:
        try
        {
            Enumeration<NetworkInterface> nets = NetworkInterface.getNetworkInterfaces();
            for (NetworkInterface netint : java.util.Collections.list(nets))
            {
                java.util.List<java.net.InterfaceAddress> ifAddresses = netint.getInterfaceAddresses();
                for (java.net.InterfaceAddress ifAddress : ifAddresses) {
                    if (ifAddress.getAddress() instanceof Inet4Address) {
                        // split "hostname/127.0.0.1/8 [0.255.255.255]"
                        // String s[] = ifAddress.toString().split("/");
                        Log("IP: " + ifAddress.getAddress() + ", Broadcast: " + ifAddress.getBroadcast());
                        Log(", PrefixLength: " + ifAddress.getNetworkPrefixLength() + "\n");

                    }
                }
            }

            Log("127.0.0.1 --> " + Domain.DoSubnetTranslation("127.0.0.1") + "\n");
            Log("192.168.0.0/255.255.255.0 --> " + Domain.DoSubnetTranslation("192.168.0.0/255.255.255.0") + "\n");
            Log("192.168.0.34 --> " + Domain.DoSubnetTranslation("192.168.0.34") + "\n");

        }
        catch (Exception e)
        {
        }

    }//GEN-LAST:event_jButton1ActionPerformed

    private void jCheckBoxAutoSendActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_jCheckBoxAutoSendActionPerformed
        // TODO add your handling code here:
        try
        {
            if (jCheckBoxAutoSend.isSelected()) {
                int tmp = Integer.parseInt(jTextFieldSendPeriod.getText().trim());
                sendTimer.setInitialDelay(tmp);
                sendTimer.setDelay(tmp);
                sendTimer.start();
            } else {
                sendTimer.stop();
            }
        }
        catch (NumberFormatException nfe)
        {
            System.out.println("NumberFormatException: " + nfe.getMessage());
        }
    }//GEN-LAST:event_jCheckBoxAutoSendActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton jButton1;
    private javax.swing.JButton jButtonCreateParticipant;
    private javax.swing.JButton jButtonCreatePublisher;
    private javax.swing.JButton jButtonCreateSubscriber;
    private javax.swing.JButton jButtonDeadlineIntervalSet;
    private javax.swing.JButton jButtonDeletePublisher;
    private javax.swing.JButton jButtonDeleteSubscriber;
    private javax.swing.JButton jButtonSendMessage;
    private javax.swing.JButton jButtonStartSubscriber;
    private javax.swing.JButton jButtonStopSubscriber;
    private javax.swing.JCheckBox jCheckBox01;
    private javax.swing.JCheckBox jCheckBox02;
    private javax.swing.JCheckBox jCheckBox03;
    private javax.swing.JCheckBox jCheckBox04;
    private javax.swing.JCheckBox jCheckBox05;
    private javax.swing.JCheckBox jCheckBox06;
    private javax.swing.JCheckBox jCheckBox07;
    private javax.swing.JCheckBox jCheckBox08;
    private javax.swing.JCheckBox jCheckBox09;
    private javax.swing.JCheckBox jCheckBox10;
    private javax.swing.JCheckBox jCheckBox11;
    private javax.swing.JCheckBox jCheckBox12;
    private javax.swing.JCheckBox jCheckBoxAutoSend;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JLabel jLabel3;
    private javax.swing.JScrollPane jScrollPane1;
    private javax.swing.JTextArea jTextArea1;
    private javax.swing.JTextField jTextFieldDeadlineInterval;
    private javax.swing.JTextField jTextFieldNumExtraBytes;
    private javax.swing.JTextField jTextFieldSendPeriod;
    private javax.swing.JPanel mainPanel;
    private javax.swing.JMenuBar menuBar;
    private javax.swing.JProgressBar progressBar;
    private javax.swing.JLabel statusAnimationLabel;
    private javax.swing.JLabel statusMessageLabel;
    private javax.swing.JPanel statusPanel;
    // End of variables declaration//GEN-END:variables

    private final Timer messageTimer;
    private final Timer busyIconTimer;
    private final Icon idleIcon;
    private final Icon[] busyIcons = new Icon[15];
    private int busyIconIndex = 0;

    private JDialog aboutBox;
}
