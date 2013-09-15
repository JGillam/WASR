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

import org.apache.log4j.Logger;
import org.jdom2.Element;
import wasr.*;

import javax.swing.tree.TreePath;
import java.awt.event.ActionEvent;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class DocNodeActionAdd extends DocNodeAction {
    private static Logger LOG = Logger.getLogger(DocNodeActionAdd.class);

    Element childTemplate;
    DocNode parentNode;

    public DocNodeActionAdd(DocNode node, Element nodeToAdd) {
        super(node);
        this.childTemplate = nodeToAdd;
        this.parentNode = node;
        String name = UserSettings.getAppResource("DocNodeActionAdd");
        String id = nodeToAdd.getAttributeValue("id");
        name = name.replaceAll("%1", UserSettings.getSelectedTemplateResource(id, id));
        this.putValue(NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        try {
            DocNode newNode = parentNode.addChildFromTemplate(childTemplate);
            DocTreePanel.getInstance().getTreeModel().nodeStructureChanged(getNode());
            TreePath newNodePath = new TreePath(newNode.getPath());
            DocTreePanel.getInstance().getTree().expandPath(newNodePath);
            DocTreePanel.getInstance().getTree().setSelectionPath(newNodePath);
        } catch (ReportException e) {
            WASRFrame.errorPop("AddNode.Exception.Title", e);
        }
    }
}
