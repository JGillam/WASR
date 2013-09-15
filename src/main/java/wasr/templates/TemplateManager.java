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

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import wasr.Report;
import wasr.UserSettings;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class TemplateManager {

    private static Logger LOG = Logger.getLogger(TemplateManager.class);
    private static TemplateManager instance = new TemplateManager();

    private TemplateManager() {
        LOG.debug("Initializing TemplateManager");
        initializeDefaults();
    }

    public static List<ReportTemplate> listTemplates() {
        return instance.templates();
    }

    private List<ReportTemplate> templates() {
        List<ReportTemplate> reportTemplates = new ArrayList<ReportTemplate>();
        File folder = new File(UserSettings.getFolder(), "templates");

        File[] files = folder.listFiles();
        for (File file : files) {
            if (file.isDirectory()) {
                reportTemplates.add(new ReportTemplate(file));
            }
        }
        return reportTemplates;
    }

    public void initializeDefaults() {
        String DEFAULT_TEMPLATE = "default";

        File defaultTemplateFolder = new File(UserSettings.getFolder(), "templates/" + DEFAULT_TEMPLATE);
        if (!defaultTemplateFolder.exists()) {
            defaultTemplateFolder.mkdirs();
        }

        ReportTemplate defaultReportTemplate = new ReportTemplate(DEFAULT_TEMPLATE, defaultTemplateFolder);

        File velocityTemplate = defaultReportTemplate.getVelocityTemplate();
        UserSettings.copyResource(DEFAULT_TEMPLATE + ".vm", velocityTemplate);

        File domTemplate = defaultReportTemplate.getDomTemplate();
        UserSettings.copyResource(DEFAULT_TEMPLATE + ".xml", domTemplate);

        File defaultResourceBundle = defaultReportTemplate.getDefaultResourceBundle();
        UserSettings.copyResource(DEFAULT_TEMPLATE + ".properties", defaultResourceBundle);
    }

    public static ReportTemplate copyTemplate(ReportTemplate source, Report report) throws IOException {
        File reportTemplateFolder = new File(report.getReportFolder(), "template");
        FileUtils.copyDirectory(source.getFolder(), reportTemplateFolder);
        return new ReportTemplate(reportTemplateFolder);
    }

}
