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


import org.jdom2.Document;
import org.jdom2.output.Format;
import org.jdom2.output.XMLOutputter;
import wasr.WASRFrame;

import java.awt.event.ActionEvent;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class SaveDocAction extends MenuAction {

    public SaveDocAction() {
        super("SaveReportAction");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {

        File file = new File(WASRFrame.getInstance().getCurrentReport().getReportFolder(), "report.xml");

        Document wasrDoc = WASRFrame.getInstance().getCurrentReport().getReportDOM();

        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            FileOutputStream fos = new FileOutputStream(file);
            BufferedOutputStream bos = new BufferedOutputStream(fos);
            XMLOutputter outputter = new XMLOutputter();
            outputter.setFormat(Format.getPrettyFormat());
            outputter.output(wasrDoc, bos);
            bos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();  // TODO: push save errors up to UI.
        }
    }


}
