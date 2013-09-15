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
import org.jdom2.Element;
import wasr.widgets.FieldUIWidget;
import wasr.widgets.JTextFieldUIWidget;

import java.util.HashMap;
import java.util.Map;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class FieldUI {
    private static Logger LOG = Logger.getLogger(FieldUI.class);

    private static Map<String, FieldUIWidget> widgetMap = new HashMap<String, FieldUIWidget>();

    public static FieldUIWidget getFieldUIWidget(String nodeId, Element fieldElement) {
        String fieldId = fieldElement.getAttribute("id").getValue();
        String name = nodeId + '.' + fieldId;
        FieldUIWidget widget = widgetMap.get(name);
        if (widget == null) {
            String componentType = fieldElement.getAttribute("component") == null ? "JTextField" : fieldElement.getAttribute("component").getValue();
            widget = WidgetFactory.createWidget(componentType, nodeId, fieldId);

            if (widget == null) {
                widget = new JTextFieldUIWidget(nodeId, fieldId);
            }

            LOG.debug("Created and cached widget for " + nodeId + "." + fieldId);
            widgetMap.put(name, widget);
        }
        return widget;
    }

}
