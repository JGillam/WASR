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
import org.jdom2.Attribute;
import org.jdom2.Element;
import wasr.actions.DocNodeActionAdd;
import wasr.actions.DocNodeActionRemove;
import wasr.templates.ReportTemplate;
import wasr.widgets.FieldUIWidget;

import javax.swing.*;
import javax.swing.tree.DefaultMutableTreeNode;
import java.util.*;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class DocNode extends DefaultMutableTreeNode {
    private static Logger LOG = Logger.getLogger(DocNode.class);

    private Element template;
    private Element element;
    private String id;
    //private Map<String, String> fieldValues = new HashMap<String, String>();


    public DocNode(Element templateElement, Element docElement) throws ReportException {
        if (templateElement == null) {
            throw new ReportException("DocNode.Exception.NullTemplateNode");
        }
        if (docElement == null) {
            throw new ReportException("DocNode.Exception.NullDocNode");
        }
        this.template = templateElement;
        this.element = docElement;
        id = getTemplateAttribute(ReportTemplate.ID_ATTR, null);

        if (id != null) {
            updateLabel();
        } else {
            LOG.warn("Node missing id: " + template);
        }
    }

    @Deprecated
    public DocNode(Element template) {
        this.template = template;
        id = getTemplateAttribute(ReportTemplate.ID_ATTR, null);

        if (id != null) {
            updateLabel();
        } else {
            LOG.warn("Node missing id: " + template);
        }
    }


    public String getId() {
        return id;
    }

    public String getLabel() {
        return getUserObject() == null ? id : getUserObject().toString();
    }

    public Element getTemplate() {
        return template;
    }

    public String getOrd() {
        return getTemplateAttribute(ReportTemplate.ORD_ATTR, "1");
    }


    public void initChildrenFromTemplate() throws ReportException {
        boolean allowsChildren = false;
        for (Element child : template.getChildren(ReportTemplate.FIELD_ELEM)) {
            Element field = new Element(child.getAttributeValue(ReportTemplate.ID_ATTR));
            element.addContent(field);
        }

        for (Element childTemplate : template.getChildren(ReportTemplate.NODE_ELEM)) {
            allowsChildren = true;
            String ord = getAttributeValue(childTemplate, ReportTemplate.ORD_ATTR, "1");
            if ("+1".contains(ord)) {
                addChildFromTemplate(childTemplate);
            }
            //else if ("*?".contains(ord)) {
            // ignore since '0' is an acceptable value
            //} else {
            // TODO: implement logic for discrete numerical values for ord (e.g. 2, 3, 4)
            //}
        }
        setAllowsChildren(allowsChildren);
    }

    public void initChildrenFromDOM() throws ReportException {
        boolean allowsChildren = false;
        for (Element templateChild : template.getChildren(ReportTemplate.NODE_ELEM)) {
            allowsChildren = true;
            String typeid = templateChild.getAttributeValue(ReportTemplate.ID_ATTR);
            for (Element elementChild : element.getChildren(typeid)) {
                DocNode childNode = new DocNode(templateChild, elementChild);
                this.add(childNode);
                childNode.initChildrenFromDOM();
            }
        }
        setAllowsChildren(allowsChildren);
    }

    public void expandTemplate() {


    }

    public DocNode addChildFromTemplate(Element fromTemplateElement) throws ReportException {
        String typeID = fromTemplateElement.getAttributeValue(ReportTemplate.ID_ATTR);
        Element newElement = new Element(typeID);
        element.addContent(newElement);
        DocNode childNode = new DocNode(fromTemplateElement, newElement);
        this.add(childNode);
        childNode.initChildrenFromTemplate();
        return childNode;
    }

    private void updateLabel() {
        String oldLabel = getLabel();
        String newLabel = null;

        String labelFieldID = getTemplateAttribute(ReportTemplate.LABELFIELD_ATTR, null);

        if (labelFieldID != null) {
            Element labelField = element.getChild(labelFieldID);
            if (labelField != null) {
                newLabel = labelField.getContentSize() == 0 ? null : labelField.getText();  // todo: check for CData
            }
        }

        if (newLabel == null || newLabel.equals("")) {
            try {
                newLabel = UserSettings.getSelectedTemplateBundle().getString(id);
            } catch (MissingResourceException e) {
                newLabel = id;
            }
        }

        if (!newLabel.equals(oldLabel)) {
            setUserObject(newLabel);
            DocTreePanel.getInstance().getTreeModel().nodeChanged(this);

        }
    }

    public void renderDetailPanel(JComponent panel) {
        for (Element field : template.getChildren(ReportTemplate.FIELD_ELEM)) {
            FieldUIWidget widget = FieldUI.getFieldUIWidget(id, field);
            panel.add(widget.getComponent());
            widget.setValue(getFieldValue(field));
        }
    }

    private String getFieldValue(Element templateField) {
        String fieldId = templateField.getAttributeValue(ReportTemplate.ID_ATTR);
        return getField(fieldId);
    }

    private String getField(String fieldId) {
        if (fieldId != null) {
            Element fieldElement = element.getChild(fieldId);
            return fieldElement == null ? "" : fieldElement.getValue(); //todo: CDATA?
        } else {
            return "";
        }
    }

    public void saveFromWidgets() {
        for (Element field : template.getChildren(ReportTemplate.FIELD_ELEM)) {
            String value = FieldUI.getFieldUIWidget(id, field).getValue();
            String fieldId = getAttributeValue(field, ReportTemplate.ID_ATTR);
            setField(fieldId, value);
        }
        updateLabel();
    }

    private void setField(String fieldId, String value) {
        if (fieldId != null) {
            Element fieldElement = element.getChild(fieldId);
            fieldElement.setText(value);  // todo: CDATA?
        }
    }

    public JPopupMenu getPopup() {
        JPopupMenu popup = buildPopupMenu(template);
        return popup.getSubElements().length == 0 ? null : popup;
    }

    private JPopupMenu buildPopupMenu(Element template) {
        JPopupMenu popup = new JPopupMenu();
        String ord = getAttributeValue(template, ReportTemplate.ORD_ATTR, "1");     // ord will follow syntax of regex
        // greedy quantifiers
        // ?, *, +, or a number
        int sCount = getSiblingCount();

        if ("?*".contains(ord) || ("+".equals(ord) && sCount > 1)) {
            popup.add(new DocNodeActionRemove(this));
        }


        for (Element tChild : template.getChildren(ReportTemplate.NODE_ELEM)) {
            String cOrd = getAttributeValue(tChild, ReportTemplate.ORD_ATTR, "1");
            String cID = getAttributeValue(tChild, ReportTemplate.ID_ATTR);

            int cCount = 0;

            if ("?".equals(cOrd)) {   // optimization: only need cCount when cOrd="?"
                for (int i = 0; i < getChildCount(); i++) {
                    if (cID.equals(((DocNode) getChildAt(i)).getId())) {
                        cCount++;
                    }
                }
            }

            if ("+*".contains(cOrd) || ("?".equals(cOrd) && cCount == 0)) {  // only add child when cOrd allows it
                popup.add(new DocNodeActionAdd(this, tChild));
            }
        }
        return popup;
    }

    private static String getAttributeValue(Element element, String attributeName) {
        return getAttributeValue(element, attributeName, null);
    }

    private static String getAttributeValue(Element element, String attributeName, String defaultValue) {
        Attribute attribute = element.getAttribute(attributeName);
        return attribute == null ? defaultValue : attribute.getValue();
    }

    private String getTemplateAttribute(String attributeName, String defaultValue) {
        Attribute attribute = template.getAttribute(attributeName);
        return attribute == null ? defaultValue : attribute.getValue();
    }


    public Element saveAsDOM() {
        Element docNode = new Element("doc-node");
//        docNode.setAttribute("id", id);
//        for (String fieldKey : fieldValues.keySet()) {
//            String fieldValue = fieldValues.get(fieldKey);
//            Element fieldNode = new Element("field-node");
//            fieldNode.setAttribute("key", fieldKey);
//            fieldNode.setAttribute("value", fieldValue);
//            docNode.addContent(fieldNode);
//        }
//
//        for (Enumeration children = children(); children.hasMoreElements(); ) {
//            DocNode child = (DocNode) children.nextElement();
//            docNode.addContent(child.saveAsDOM());
//        }
        return docNode;
    }

    public void loadFromDOM(Element content) {
//        for (Element field : content.getChildren("field-node")) {
//            String key = field.getAttribute("key").getValue();
//            String value = field.getAttribute("value").getValue();
//            fieldValues.put(key, value);
//        }
//
//        for (Element child : content.getChildren("doc-node")) {
//            String id = child.getAttribute("id").getValue();
//            for (Element tChild : template.getChildren("node")) {
//                if (tChild.getAttribute("id").getValue().equals(id)) {
//                    DocNode childNode = new DocNode(tChild);
//                    this.add(childNode);
//                    childNode.loadFromDOM(child);
//                }
//            }
//        }
    }

    public void buildContext(Map<String, Object> context) {
        for (Element field : template.getChildren(ReportTemplate.FIELD_ELEM)) {
            String fieldName = field.getAttributeValue(ReportTemplate.ID_ATTR);
            context.put(fieldName, getField(fieldName));
        }

        for (Enumeration children = children(); children.hasMoreElements(); ) {
            DocNode childNode = (DocNode) children.nextElement();
            Map<String, Object> childContext = new HashMap<String, Object>();
            childNode.buildContext(childContext);

            String cOrd = childNode.getOrd();
            if ("+*".contains(cOrd)) {
                List<Map> list = (List<Map>) context.get(childNode.getId());
                if (list == null) {
                    list = new ArrayList<Map>();
                    context.put(childNode.getId(), list);
                }
                list.add(childContext);
            } else {
                context.put(childNode.getId(), childContext);
            }
        }
    }
}