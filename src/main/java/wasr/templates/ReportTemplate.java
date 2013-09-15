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

package wasr.templates;

import java.io.File;

/**
 * User: Jason Gillam
 * Date: 7/12/13
 * Time: 7:43 PM
 */
public class ReportTemplate {
    public static final String ID_ATTR = "id";
    public static final String ROOT_ELEM = "root";
    public static final String NODE_ELEM = "node";
    public static final String FIELD_ELEM = "field";
    public static final String ORD_ATTR = "ord";
    public static final String LABELFIELD_ATTR = "labelfield";
    public static final String COMPONENT_ATTR = "component";

    File folder;


    public ReportTemplate(String basename, File baseFolder) {
        this.folder = baseFolder;
    }

    public ReportTemplate(File templateFile) {
        this.folder = templateFile;
    }

    public String getBasename() {
        return folder.getName();
    }

    public File getVelocityTemplate() {
        return new File(folder, "view.vm");
    }

    public File getDomTemplate() {
        return new File(folder, "model.xml");
    }

    public File getDefaultResourceBundle() {
        return new File(folder, "resource.properties");
    }

    public File getFolder() {
        return folder;
    }

    public String toString() {
        return getBasename();
    }

}
