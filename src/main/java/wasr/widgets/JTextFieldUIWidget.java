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

import javax.swing.*;
import java.awt.*;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class JTextFieldUIWidget extends FieldUIWidget {

    private JTextField textField;
    JPanel widgetPanel = new JPanel(new GridLayout(1, 1));

    public JTextFieldUIWidget(String nodeId, String fieldId) {
        super(nodeId, fieldId);
        textField = new JTextField();
        widgetPanel.add(textField);
        widgetPanel.setBorder(BorderFactory.createTitledBorder(this.getLabel()));
    }


    @Override
    public JComponent getComponent() {
        return widgetPanel;
    }

    @Override
    public String getValue() {
        return textField.getText();
    }

    @Override
    public void setValue(String value) {
        textField.setText(value);
    }
}
