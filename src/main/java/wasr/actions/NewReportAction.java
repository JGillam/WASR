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

package wasr.actions;

import org.apache.log4j.Logger;
import wasr.Report;
import wasr.ReportException;
import wasr.UserSettings;
import wasr.WASRFrame;
import wasr.templates.ReportTemplate;
import wasr.templates.TemplateManager;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
@SuppressWarnings("ConstantConditions")
public class NewReportAction extends MenuAction {
    private static Logger LOG = Logger.getLogger(NewReportAction.class);

    File selectedReportFolder;

    public NewReportAction() {
        super("NewReportAction");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        final JTextField folderTxt = new JTextField();
        folderTxt.setText(UserSettings.getAppResource("NewReport.SelectPath.Text"));
        JButton folderBtn = new JButton(UserSettings.getAppResource("NewReport.SelectPath.Button"));
        folderBtn.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser chooser = new JFileChooser(UserSettings.getLastFolder());
                chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
                chooser.setMultiSelectionEnabled(false);
                chooser.setDialogTitle(UserSettings.getAppResource("NewReport.SelectFolderDialog.Title"));
                int result = chooser.showSaveDialog(WASRFrame.getInstance());
                if (result == JFileChooser.APPROVE_OPTION) {
                    File chosen = chooser.getSelectedFile();

                    //noinspection ConstantConditions
                    if (chosen != null) {
                        if (!chosen.exists() || chosen.listFiles().length == 0) {
                            LOG.debug("Folder is empty.  Selection good.");
                            folderTxt.setText(chosen.getPath());
                            folderTxt.setToolTipText(chosen.getPath());
                            selectedReportFolder = chosen;
                            UserSettings.setLastFolder(chosen.getParentFile());
                        } else {
                            JOptionPane.showMessageDialog(WASRFrame.getInstance(),
                                    UserSettings.getAppResource("NewReport.Exception.FolderNotEmpty"),
                                    UserSettings.getAppResource("NewReport.ErrorMessage.Title"), JOptionPane.ERROR_MESSAGE);
                        }
                    }
                }
            }
        });

        Box reportFolderRow = Box.createHorizontalBox();
        reportFolderRow.add(new JLabel(UserSettings.getAppResource("NewReport.SelectPath.Label")));
        reportFolderRow.add(folderTxt);
        reportFolderRow.add(folderBtn);

        JComboBox<Object> chooseTemplate = new JComboBox<Object>(TemplateManager.listTemplates().toArray());
        chooseTemplate.setSelectedIndex(0);
        Box templateRow = Box.createHorizontalBox();
        templateRow.add(new JLabel(UserSettings.getAppResource("NewReport.SelectTemplate.Label")));
        templateRow.add(chooseTemplate);

        Box page = Box.createVerticalBox();
        page.add(reportFolderRow);
        page.add(templateRow);

        int result = JOptionPane.showOptionDialog(WASRFrame.getInstance(), page, UserSettings.getAppResource("NewReport.Title"), JOptionPane.OK_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
        if (result == JOptionPane.OK_OPTION) {
            setupNewReport(selectedReportFolder, (ReportTemplate) chooseTemplate.getSelectedItem());
        }
    }

    private void setupNewReport(File reportFolder, ReportTemplate reportTemplate) {
        try {
            Report report = Report.newReport(reportFolder, reportTemplate);
            WASRFrame.getInstance().setCurrentReport(report);
        } catch (ReportException e) {
            JOptionPane.showMessageDialog(WASRFrame.getInstance(), e.getMessage(),
                    UserSettings.getAppResource("NewReport.ErrorMessage.Title"), JOptionPane.ERROR_MESSAGE);
            LOG.warn("Could not generate new report", e);
        }

        UserSettings.getProperties().setProperty(UserSettings.SELECTED_TEMPLATE, reportTemplate.getBasename());  //todo: this can probably be removed
        UserSettings.saveProperties();
        UserSettings.resetTemplateBundle();
    }
}
