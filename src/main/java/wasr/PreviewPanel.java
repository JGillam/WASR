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

import org.apache.log4j.Logger;

import javax.swing.*;
import javax.swing.text.html.HTMLDocument;
import java.awt.*;
import java.net.MalformedURLException;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class PreviewPanel extends JPanel {
    private static Logger LOG = Logger.getLogger(PreviewPanel.class);

    private static PreviewPanel instance = new PreviewPanel();

    JEditorPane editorPane = new JEditorPane();
    JScrollPane scrollPane;

    private PreviewPanel() {
        super(new BorderLayout());
        editorPane.setContentType("text/html");
        scrollPane = new JScrollPane(editorPane);
        this.add(scrollPane, BorderLayout.CENTER);
    }

    public static PreviewPanel getInstance() {
        return instance;
    }

    public void setContent(String content) {
        try {
            HTMLDocument doc = (HTMLDocument) editorPane.getDocument();
            doc.setBase(WASRFrame.getInstance().getCurrentReport().getReportFolder().toURI().toURL());
        } catch (MalformedURLException e) {
            LOG.warn(e);
        }
        editorPane.setText(content);
        editorPane.setCaretPosition(0);
    }

}
