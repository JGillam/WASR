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
import javax.swing.event.TreeSelectionEvent;
import javax.swing.event.TreeSelectionListener;
import javax.swing.tree.DefaultMutableTreeNode;
import javax.swing.tree.DefaultTreeModel;
import javax.swing.tree.TreePath;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class DocTreePanel extends JComponent implements TreeSelectionListener {
    private static Logger LOG = Logger.getLogger(DocTreePanel.class);
    static private DocTreePanel instance = new DocTreePanel();

    private JTree tree;
    private DefaultTreeModel treeModel;
    private DocNode lastSelected = null;

    public static DocTreePanel getInstance() {
        return instance;
    }

    private DocTreePanel() {
        treeModel = new DefaultTreeModel(new DefaultMutableTreeNode("new report"), true);
        tree = new JTree(treeModel);
        tree.setRootVisible(false);
        tree.addTreeSelectionListener(this);
        tree.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseReleased(MouseEvent mouseEvent) {
                if (mouseEvent.isPopupTrigger() || mouseEvent.getButton() == MouseEvent.BUTTON3) { // TODO: Test this with other OS.  isPopupTrigger doesn't work for Mac, but Button3 does
                    TreePath path = tree.getPathForLocation(mouseEvent.getX(), mouseEvent.getY());
                    if (path != null) {
                        DefaultMutableTreeNode node = (DefaultMutableTreeNode) path.getLastPathComponent();
                        if (node instanceof DocNode) {
                            JPopupMenu popup = ((DocNode) node).getPopup();
                            if (popup != null) {
                                popup.show(tree, mouseEvent.getX(), mouseEvent.getY());
                            }
                            LOG.debug("Right click on " + ((DocNode) node).getLabel());
                        }
                    }
                }
            }
        });
        JScrollPane scrollpane = new JScrollPane(tree);
        this.setLayout(new GridLayout(1, 1));
        this.add(scrollpane);
    }

    public void setReport(Report report) {
        tree.removeAll();
        treeModel.setRoot(report.getTreeRoot());
        tree.setRootVisible(true);
        tree.setSelectionRow(1);
    }

    @Override
    public void valueChanged(TreeSelectionEvent treeSelectionEvent) {
        DocNode selectedNode = (DocNode) treeSelectionEvent.getPath().getLastPathComponent();
        LOG.debug("Selected: " + selectedNode);
        if (selectedNode != null && selectedNode != lastSelected) {
            saveLastSelected();
            DetailPanel.setDetails(selectedNode);
            lastSelected = selectedNode;
        }
    }

    public void saveLastSelected() {
        if (lastSelected != null) {
            lastSelected.saveFromWidgets();
        }
    }

    public JTree getTree() {
        return tree;
    }

    public DefaultTreeModel getTreeModel() {
        return treeModel;
    }

    public DocNode getLastSelected() {
        return lastSelected;
    }
}
