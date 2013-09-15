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

package wasr.widgets;

import wasr.Icons;
import wasr.WASRFrame;

import javax.swing.*;
import javax.swing.text.BadLocationException;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import javax.swing.text.html.HTMLEditorKit;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.net.URL;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class JTextPaneUIWidget extends FieldUIWidget {

    JTextPane textPane;
    JPanel widgetPanel;

    // TODO: support bullets
    public JTextPaneUIWidget(String nodeId, String fieldId) {
        super(nodeId, fieldId);
        widgetPanel = new JPanel(new BorderLayout());
        textPane = new JTextPane();
        textPane.setEnabled(true);
        textPane.setEditable(true);
        textPane.setAutoscrolls(true);
        textPane.setContentType("text/html");
        //textPane.setEditorKit(new HTMLEditorKit());
        //System.out.println("Font height: "+textPane.getFontMetrics(textPane.getFont()).getHeight());  //todo: add row height
        JScrollPane scrollPane = new JScrollPane(textPane);
        scrollPane.setPreferredSize(new Dimension(100, 150));
        scrollPane.setMinimumSize(new Dimension(100, 150));
        JToolBar tools = new JToolBar();

        ActionMap actionMap = textPane.getActionMap();
        for (Object key : actionMap.allKeys()) {
            Action action = actionMap.get(key);
            System.out.println(key + ": " + action.getValue(Action.NAME));

        }

        //System.out.println(HTMLEditorKit.BOLD_ACTION);
        tools.add(getAction(textPane, "font-bold", Icons.TEXT_BOLD_16));
        tools.add(getAction(textPane, "font-italic", Icons.TEXT_ITALIC_16));
        tools.add(getAction(textPane, "font-underline", Icons.TEXT_UNDERLINE_16));
        tools.add(getLinkAction(textPane));


//        tools.add(textPane.getActionMap().get("InsertUnorderedList"));
//        tools.add(textPane.getActionMap().get("InsertOrderedList"));
        tools.setFloatable(false);
        widgetPanel.add(tools, BorderLayout.NORTH);
        widgetPanel.add(scrollPane, BorderLayout.CENTER);
        widgetPanel.setBorder(BorderFactory.createTitledBorder(this.getLabel()));
    }

    private Action getAction(JTextPane textPane, String actionName, String iconPath) {
        Action a = textPane.getActionMap().get(actionName);
        URL imageResource = getClass().getResource(iconPath);
        if (imageResource == null) {
            System.out.println("Image not found for " + iconPath);
        } else {
            a.putValue(Action.LARGE_ICON_KEY, new ImageIcon(imageResource));
        }

        return a;
    }

    private Action getLinkAction(JTextPane textPane) {
        return new InsertLinkAction();

        //Action a = new HTMLEditorKit.InsertHTMLTextAction();
    }

    // copy, cut-to-clipboard, paste, cut
    // font-bold, font-italic, font-underline
    // activate-link-action
    // InsertTable, InsertTableRow
    // font-size-36, font-size-8, font-size-24, font-size-10
    // InsertUnorderedListItem, InsertUnorderedList, InsertOrderedList, InsertPre

    @Override
    public JComponent getComponent() {
        return widgetPanel;
    }

    @Override
    public String getValue() {
        String text = textPane.getText().trim();
        if (text.startsWith("<html>")) {
            int startIndex = text.indexOf("<body>") + "<body>".length();
            int endIndex = text.indexOf("</body>");
            text = text.substring(startIndex, endIndex);
        }
        return text;
    }

    @Override
    public void setValue(String value) {
        textPane.setText(value);
    }

    class InsertLinkAction extends AbstractAction {
        public InsertLinkAction() {
            putValue(Action.NAME, "Link");  // todo: add icon
        }

        @Override
        public void actionPerformed(ActionEvent actionEvent) {
            String selected = textPane.getSelectedText();
            if (selected == null || selected.trim().length() == 0) { // todo: two fields - url and text
                JOptionPane.showMessageDialog(WASRFrame.getInstance(), "No text selected!");
            } else {
                String url = JOptionPane.showInputDialog(WASRFrame.getInstance(), "URL");
                if (url != null) { // todo: check for malformed url
                    String tagText = "<a href='" + url + "'>" + selected + "</a>";
                    HTMLEditorKit kit = (HTMLEditorKit) textPane.getEditorKit();

                    try {
                        textPane.getDocument().remove(textPane.getSelectionStart(), selected.length());
                        kit.insertHTML((HTMLDocument) textPane.getDocument(), textPane.getSelectionStart(), tagText, 0, 0, HTML.Tag.A);
                    } catch (BadLocationException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File TemplateManager.
                    } catch (IOException e) {
                        e.printStackTrace();  //To change body of catch statement use File | Settings | File TemplateManager.
                    }


                    //Action a = new HTMLEditorKit.InsertHTMLTextAction("Link", tagText, HTML.Tag.BODY, HTML.Tag.A);
                    //a.actionPerformed(actionEvent);
                }
            }
        }
    }
}
