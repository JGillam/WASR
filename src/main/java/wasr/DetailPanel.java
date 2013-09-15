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

import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class DetailPanel extends JPanel {

    private static DetailPanel instance = new DetailPanel();
    private static final String BLANK = "blank";
    private static final String DETAIL = "detail";

    public static DetailPanel getInstance() {
        return instance;
    }

    CardLayout cards = new CardLayout();
    JPanel details = new JPanel();


    private DetailPanel() {
        this.setBorder(new LineBorder(Color.gray));
        this.setLayout(cards);
        this.add(BLANK, new JPanel());

        details.setLayout(new DetailLayoutManager());

        this.add(DETAIL, new JScrollPane(details));
    }

    public void setBlank() {
        cards.show(this, BLANK);
    }

    public static void setDetails(DocNode node) {
        DetailPanel dp = getInstance();
        dp.cards.show(dp, BLANK);
        dp.details.removeAll();
        dp.setBorder(BorderFactory.createTitledBorder(UserSettings.getSelectedTemplateResource(node.getId(),
                node.getId())));
        node.renderDetailPanel(dp.details);
        dp.cards.show(dp, DETAIL);
        WASRFrame.getInstance().getTabs().setSelectedIndex(0);
    }

}
