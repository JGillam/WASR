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
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.ArrayList;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class TemplateSelector extends JDialog implements ActionListener {
    private static Logger LOG = Logger.getLogger(TemplateSelector.class);

    JList list = new JList();
    JButton select;

    public TemplateSelector() {
        super(WASRFrame.getInstance(), "Template Manager", true);
        init();
    }

    private void init() {
        setLayout(new BorderLayout());
        add(new JScrollPane(list), BorderLayout.CENTER);

        select = new JButton("Select");
        select.addActionListener(this);

        JToolBar toolbar = new JToolBar();
        toolbar.add(select);

        add(toolbar, BorderLayout.SOUTH);
        setSize(100, 100);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        if (select.equals(actionEvent.getSource())) {
            String selected = (String) list.getSelectedValue();
            if (selected != null) {
                UserSettings.getProperties().setProperty(UserSettings.SELECTED_TEMPLATE, selected);
                UserSettings.saveProperties();
                UserSettings.resetTemplateBundle();
            }
        }
        setVisible(false);
    }

    private void resetList() {
        java.util.List<String> items = new ArrayList<String>();
        items.add(UserSettings.DEFAULT_TEMPLATE);
        File folder = UserSettings.getFolder();
        if (folder != null && folder.exists()) {
            File[] files = folder.listFiles();
            for (File file : files != null ? files : new File[0]) {
                String path = file.getPath();
                if (path.endsWith(".vm")) {
                    items.add(file.getName().substring(0, file.getName().lastIndexOf(".")));
                }
            }
        }

        list.setListData(items.toArray());
        String selected = UserSettings.getProperties().getProperty(UserSettings.SELECTED_TEMPLATE, UserSettings.DEFAULT_TEMPLATE);
        LOG.debug("Setting selected: " + selected);
        list.setSelectedValue(selected, true);
    }

    @Override
    public void setVisible(boolean b) {
        if (b) {
            resetList();
            this.setLocationRelativeTo(getOwner());
        }
        super.setVisible(b);
    }
}
