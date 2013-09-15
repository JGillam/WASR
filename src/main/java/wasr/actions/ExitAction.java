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

import wasr.WASRFrame;

import java.awt.event.ActionEvent;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class ExitAction extends MenuAction {

    public ExitAction() {
        super("ExitAction");
    }

    @Override
    public void actionPerformed(ActionEvent actionEvent) {
        WASRFrame.getInstance().shutdown();
    }
}
