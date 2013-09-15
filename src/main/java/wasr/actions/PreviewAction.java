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
import org.apache.velocity.Template;
import org.apache.velocity.VelocityContext;
import org.apache.velocity.exception.ParseErrorException;
import org.apache.velocity.exception.ResourceNotFoundException;
import wasr.*;

import java.awt.event.ActionEvent;
import java.io.StringWriter;
import java.util.HashMap;
import java.util.Map;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class PreviewAction extends MenuAction {
    private static Logger LOG = Logger.getLogger(PreviewAction.class);

    public PreviewAction() {
        super("PreviewAction");
    }

    public void actionPerformed(ActionEvent actionEvent) {
        DocTreePanel.getInstance().saveLastSelected();
        Map map = new HashMap();

        Report report = WASRFrame.getInstance().getCurrentReport();

        DocNode root = report.getTreeRoot();
        root.buildContext(map);
        VelocityContext context = new VelocityContext();

        for (Object key : map.keySet()) {
            context.put((String) key, map.get(key));
        }

        Template template;

        try {
            template = report.getVelocityTemplate();
            StringWriter w = new StringWriter();
            template.merge(context, w);
            PreviewPanel.getInstance().setContent(w.toString());
            WASRFrame.getInstance().getTabs().setSelectedIndex(1);
        } catch (ResourceNotFoundException e) {
            LOG.warn("Could not find resource", e);
        } catch (ParseErrorException e) {
            LOG.warn("Could not parse template", e);
        }

    }
}
