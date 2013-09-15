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

import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import wasr.DocNode;
import wasr.DocTreePanel;
import wasr.WASRFrame;

import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.event.ActionEvent;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class PublishHTMLAction extends MenuAction {

    public PublishHTMLAction() {
        super("PublishHTMLAction");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        JFileChooser chooser = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("HTML files", "html");
        chooser.setAcceptAllFileFilterUsed(false);
        chooser.setFileFilter(filter);
        chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = chooser.showSaveDialog(WASRFrame.getInstance());
        if (JFileChooser.APPROVE_OPTION == result) {
            File file = chooser.getSelectedFile();

            DocTreePanel.getInstance().saveLastSelected();
            Map map = new HashMap();
            DocNode root = (DocNode) DocTreePanel.getInstance().getTreeModel().getRoot();
            root.buildContext(map);
            VelocityContext context = new VelocityContext();

            for (Object key : map.keySet()) {
                context.put((String) key, map.get(key));
            }

            Template template;
            FileWriter w = null;
            try {
                template = WASRFrame.getInstance().getCurrentReport().getVelocityTemplate();
                w = new FileWriter(file);
                template.merge(context, w);
                w.flush();
                //WASRFrame.getInstance().getTabs().setSelectedIndex(1);
            } catch (ResourceNotFoundException e) {
                e.printStackTrace();
            } catch (ParseErrorException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                if (w != null) {
                    try {
                        w.close();
                    } catch (IOException ignore) {

                    }
                }
            }

        }
    }
}
