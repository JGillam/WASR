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
import wasr.DocNode;
import wasr.DocTreePanel;
import wasr.UserSettings;

import java.awt.event.ActionEvent;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class DocNodeActionRemove extends DocNodeAction {
    private static Logger LOG = Logger.getLogger(DocNodeActionRemove.class);

    public DocNodeActionRemove(DocNode node) {
        super(node);
        String name = UserSettings.getAppBundle().getString("DocNodeActionRemove");
        name = name.replaceAll("%1", node.getLabel());
        this.putValue(NAME, name);
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        LOG.debug("actionPerformed - remove " + getNode().getLabel());
        DocTreePanel.getInstance().getTreeModel().removeNodeFromParent(getNode());
    }
}
