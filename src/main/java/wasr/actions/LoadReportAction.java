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

import wasr.Report;
import wasr.ReportException;
import wasr.UserSettings;
import wasr.WASRFrame;

import javax.swing.*;
import javax.swing.filechooser.FileFilter;
import java.awt.event.ActionEvent;
import java.io.File;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class LoadReportAction extends MenuAction {

    public LoadReportAction() {
        super("LoadReportAction");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser chooser = new JFileChooser();
        chooser.setFileSelectionMode(JFileChooser.FILES_AND_DIRECTORIES);
        chooser.setFileFilter(new FileFilter() {   // file filter that looks for report.xml files.
            @Override
            public boolean accept(File f) {
                if (f.isDirectory()) {
                    for (File child : f.listFiles()) {
                        if (child.getName().equals("report.xml")) {
                            return true;
                        }
                    }
                    return false;
                } else {
                    return f.getName().equals("report.xml");
                }

            }

            @Override
            public String getDescription() {
                return UserSettings.getAppResource("LoadReportFilter");
            }
        });

        chooser.setSelectedFile(UserSettings.getLastFolder());

        int result = chooser.showOpenDialog(WASRFrame.getInstance());
        if (JFileChooser.APPROVE_OPTION == result) {
            File file = chooser.getSelectedFile();
            Report report;
            try {
                if (file.isDirectory()) {
                    report = Report.loadReport(file);
                    UserSettings.setLastFolder(file);
                } else {
                    report = Report.loadReport(file.getParentFile());
                    UserSettings.setLastFolder(file.getParentFile());
                }

                WASRFrame.getInstance().setCurrentReport(report);
            } catch (ReportException e) {
                WASRFrame.errorPop("LoadReport.Exception.Title", e);
            }
        }
    }
}