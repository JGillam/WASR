/*
 * Copyright 2013 Jason Gillam
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package wasr;

import org.apache.log4j.BasicConfigurator;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.velocity.app.Velocity;
import wasr.actions.*;

import javax.swing.*;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import java.awt.event.ActionEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.lang.reflect.InvocationTargetException;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class WASRFrame extends JFrame {
    private static final String FRAME_TITLE = "WASR v0.2a";
    private static WASRFrame instance = new WASRFrame();
    private static Logger LOG = Logger.getLogger(WASRFrame.class);

    public static WASRFrame getInstance() {
        return instance;
    }

    private JTabbedPane tabs = new JTabbedPane();
    private TemplateSelector templateSelector = null;
    private Report currentReport;

    private WASRFrame() {
        super(FRAME_TITLE);
        this.setJMenuBar(setupMenuBar());
        this.setSize(800, 600);
        this.setContentPane(setupContentPane());

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent windowEvent) {
                shutdown();
            }
        });
        templateSelector = new TemplateSelector();
    }

    public void shutdown() {
        if (getCurrentReport() != null) {      // TODO: skip if no changes detected, otherwise prompt
            SaveDocAction saveAction = new SaveDocAction();
            LOG.debug("Saving...");
            saveAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED, "Save on exit"));
        }


        LOG.debug("Shutting down...");
        System.exit(0);
    }

    private JMenuBar setupMenuBar() {
        JMenuBar menuBar = new JMenuBar();

        JMenu fileMenu = new JMenu(UserSettings.getAppResource("MenuFile"));
        fileMenu.add(new NewReportAction());
        fileMenu.add(new SaveDocAction());
        fileMenu.add(new LoadReportAction());
        fileMenu.add(new PreviewAction());

        JMenu filePublishMenu = new JMenu(UserSettings.getAppResource("MenuFilePublish"));
        fileMenu.add(filePublishMenu);
        filePublishMenu.add(new PublishHTMLAction());

        fileMenu.add(new ExitAction());
        menuBar.add(fileMenu);

        //JMenu customCopyMenu = new JMenu(UserSettings.getAppResource("MenuCustomizeCopy"));
        //customCopyMenu.add(new CopyDefaultAction("CopyDefaultTemplateAction", "default.vm", "template.vm"));
        //customCopyMenu.add(new CopyDefaultAction("CopyDefaultDocTreeAction", "default.xml", "doctree.xml"));
        //customCopyMenu.add(new CopyDefaultAction("CopyDefaultL10NAction", "default.properties", "default.properties"));


        //JMenu customMenu = new JMenu(UserSettings.getAppResource("MenuCustomize"));
        //customMenu.add(new CopyDefaultAction());
        //customMenu.add(new SelectTemplateAction());
        //customMenu.add(new RevertDefaultsAction());

        //menuBar.add(customMenu);
        return menuBar;
    }

    public JTabbedPane getTabs() {
        return tabs;
    }

    private JComponent setupContentPane() {
        JSplitPane splitPane = new JSplitPane(JSplitPane.HORIZONTAL_SPLIT);
        splitPane.add(DocTreePanel.getInstance());
        tabs.add(UserSettings.getAppResource("TabDetails"), DetailPanel.getInstance());
        tabs.add(UserSettings.getAppResource("TabPreview"), PreviewPanel.getInstance());
        tabs.addChangeListener(new ChangeListener() {
            @Override
            public void stateChanged(ChangeEvent changeEvent) {
                if (tabs.getSelectedIndex() == 1) {
                    new PreviewAction().actionPerformed(new ActionEvent(this, 1, ""));
                }
            }
        });

        splitPane.add(tabs);
        splitPane.setDividerLocation(200);

        return splitPane;
    }

    public void showTemplateManager() {
        templateSelector.setVisible(true);
    }

    public static void main(String[] args) {
        BasicConfigurator.configure();
        Logger.getRootLogger().setLevel(Level.ALL);

        Velocity.setProperty(Velocity.RESOURCE_LOADER, "report, class");
        Velocity.setProperty("report.resource.loader.description", "WASR Velocity Report resource loader");
        Velocity.setProperty("report.resource.loader.class", "wasr.velocity.ReportResourceLoader");
        Velocity.setProperty("report.resource.loader.cache", "false");
        Velocity.setProperty("class.resource.loader.description", "Velocity Classpath resource loader");
        Velocity.setProperty("class.resource.loader.class", "org.apache.velocity.runtime.resource.loader.ClasspathResourceLoader");
        Velocity.init();

        WASRFrame frame = WASRFrame.getInstance();
        frame.setLocationByPlatform(true);
        frame.setVisible(true);
        frame.init();
    }

    private void init() {
        JOptionPane pane = new JOptionPane(UserSettings.getAppResource("StartupDialog.MainPrompt"),
                JOptionPane.QUESTION_MESSAGE);
        pane.setOptionType(JOptionPane.YES_NO_OPTION);
        pane.setMessageType(JOptionPane.QUESTION_MESSAGE);
        final Object[] options = new Object[2];
        options[0] = UserSettings.getAppResource("StartupDialog.NewReport");
        options[1] = UserSettings.getAppResource("StartupDialog.LoadReport");
        pane.setOptions(options);
        JDialog startupDialog = pane.createDialog(this, UserSettings.getAppResource("StartupDialog.Title"));
        startupDialog.setVisible(true);
        final Object selectedValue = pane.getValue();

        try {
            SwingUtilities.invokeAndWait(new Runnable() {
                @Override
                public void run() {
                    Action reportAction = null;
                    if (options[0].equals(selectedValue)) {
                        reportAction = new NewReportAction();
                    } else if (options[1].equals(selectedValue)) {
                        reportAction = new LoadReportAction();
                    } else {
                        LOG.info("No startup action selected.");
                    }

                    if (reportAction != null) {
                        reportAction.actionPerformed(new ActionEvent(this, ActionEvent.ACTION_PERFORMED,
                                "Startup Action"));
                    }
                }
            });
        } catch (InterruptedException e) {
            LOG.warn("Thread Interrupted", e);
        } catch (InvocationTargetException e) {
            LOG.warn("Invocation target exception", e);
        }

    }

    public Report getCurrentReport() {
        return currentReport;
    }

    public void setCurrentReport(Report currentReport) {
        this.currentReport = currentReport;
        DocTreePanel.getInstance().setReport(this.currentReport);
    }

    public static void errorPop(String title, Exception e) {
        LOG.trace(title, e);
        JOptionPane.showMessageDialog(getInstance(),
                UserSettings.getAppResource(e.getMessage()),
                UserSettings.getAppResource(title), JOptionPane.ERROR_MESSAGE);
    }
}

