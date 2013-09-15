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


import java.awt.*;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class DetailLayoutManager implements LayoutManager {

    @Override
    public void addLayoutComponent(String s, Component component) {
        //ignore - not used by this layout manager
    }

    @Override
    public void removeLayoutComponent(Component component) {
        //ignore - not used by this layout manager
    }

    @Override
    public Dimension preferredLayoutSize(Container container) {
        int w = 0;
        int h = 0;

        int count = container.getComponentCount();

        for (int i = 0; i < count; i++) {
            Component c = container.getComponent(i);
            w = Math.max(w, c.getPreferredSize().width);
            h = h + c.getPreferredSize().height;
        }

        Insets i = container.getInsets();

        return new Dimension(w + i.left + i.right, h + i.top + i.bottom);
    }

    @Override
    public Dimension minimumLayoutSize(Container container) {
        int w = 0;
        int h = 0;

        int count = container.getComponentCount();

        for (int i = 0; i < count; i++) {
            Component c = container.getComponent(i);
            w = Math.max(w, c.getMinimumSize().width);
            h = h + c.getMinimumSize().height;
        }

        Insets i = container.getInsets();

        return new Dimension(w + i.left + i.right, h + i.top + i.bottom);
    }

    @Override
    public void layoutContainer(Container container) {
        Insets i = container.getInsets();

        int containerWidth = container.getSize().width - i.left - i.right;
        int y = i.top;

        int count = container.getComponentCount();

        for (int n = 0; n < count; n++) {
            Component c = container.getComponent(n);
            if (c.isVisible()) {
                int h = c.getPreferredSize().height;
                int w = Math.min(containerWidth, c.getMaximumSize().width);
                c.setBounds(i.left, y, w, h);
                y += h;
            }
        }
    }
}
