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

import wasr.UserSettings;
import wasr.WASRFrame;

import javax.swing.*;
import java.awt.event.ActionEvent;
import java.io.File;

public class CopyDefaultAction extends MenuAction {
    public CopyDefaultAction() {
        super("CopyDefaultAction");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        String templateName;
        File templateFile;
        File docTreeFile;
        File l10nFile;
        File workingFolder = UserSettings.getFolder();

        templateName = JOptionPane.showInputDialog(WASRFrame.getInstance(),
                UserSettings.getAppBundle().getString("SelectTemplateDialogMsg"),
                UserSettings.getAppBundle().getString("SelectTemplateDialogTitle"),
                JOptionPane.QUESTION_MESSAGE);
        templateFile = new File(workingFolder, templateName + ".vm");
        docTreeFile = new File(workingFolder, templateName + ".xml");
        l10nFile = new File(workingFolder, templateName + ".properties");

        if (templateName != null && templateFile.exists() || docTreeFile.exists() || l10nFile.exists()) {
            JOptionPane.showMessageDialog(WASRFrame.getInstance(), UserSettings.getAppBundle().getString("SelectTemplate.ErrTemplateExists"));
        }

        if (templateName != null) {
            UserSettings.copyResource(UserSettings.DEFAULT_TEMPLATE + ".vm", templateFile);
            UserSettings.copyResource(UserSettings.DEFAULT_TEMPLATE + ".xml", docTreeFile);
            UserSettings.copyResource(UserSettings.DEFAULT_TEMPLATE + ".properties", l10nFile);
        }
    }
}
