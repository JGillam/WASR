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
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.jdom2.Document;
import org.jdom2.Element;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;
import wasr.templates.ReportTemplate;
import wasr.templates.TemplateManager;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;

/**
 * User: Jason Gillam
 * Date: 7/14/13
 * Time: 5:16 PM
 */
@SuppressWarnings("ConstantConditions")
public class Report {
    private static Logger LOG = Logger.getLogger(Report.class);
    ReportTemplate reportTemplate;
    Document reportDOM;
    Document templateDOM;
    DocNode treeRoot;
    File reportFolder;


    public static Report newReport(File reportFolder, ReportTemplate reportTemplate) throws ReportException {
        try {
            return new Report(reportFolder, reportTemplate);
        } catch (IOException e) {
            LOG.warn("IOException while creating new report", e);
            throw new ReportException();
        } catch (JDOMException e) {
            LOG.warn("JDOMException while creating new report", e);
            throw new ReportException();
        }
    }

    public static Report loadReport(File reportFolder) throws ReportException {
        return new Report(reportFolder);
    }

    private Report(File reportFolder, ReportTemplate copyFromTemplate) throws ReportException, IOException, JDOMException {
        if (!reportFolder.exists()) {
            //noinspection ResultOfMethodCallIgnored
            reportFolder.mkdirs();
        } else if (reportFolder.listFiles().length > 0) {
            throw new ReportException("NewReport.Exception.FolderNotEmpty");
        }

        this.reportFolder = reportFolder;

        this.reportTemplate = TemplateManager.copyTemplate(copyFromTemplate, this);
        LOG.info("Using template: " + copyFromTemplate);

        loadTemplateDOM();

        Element templateRoot = templateDOM.getRootElement();
        String rootName = templateRoot.getAttributeValue("id");
        Element rootElement = new Element(rootName);
        reportDOM = new Document(rootElement);
        treeRoot = new DocNode(templateRoot, rootElement);
        treeRoot.initChildrenFromTemplate();
    }

    private void loadTemplateDOM() throws JDOMException, IOException {
        templateDOM = buildDOM(this.reportTemplate.getDomTemplate());
    }

    private void loadReportDOM() throws JDOMException, IOException {
        reportDOM = buildDOM(new File(reportFolder, "report.xml"));
    }

    private Document buildDOM(File file) throws JDOMException, IOException {
        FileInputStream fis = new FileInputStream(file);
        BufferedInputStream in = new BufferedInputStream(fis);
        SAXBuilder builder = new SAXBuilder();
        return builder.build(in);
    }

    private Report(File reportFolder) throws ReportException {
        this.reportFolder = reportFolder;
        if (!reportFolder.isDirectory()) {
            throw new ReportException("LoadReport.Exception.NotAFolder");
        }
        this.reportTemplate = new ReportTemplate(new File(reportFolder, "template"));
        try {
            loadTemplateDOM();
            loadReportDOM();
            treeRoot = new DocNode(templateDOM.getRootElement(), reportDOM.getRootElement());
            treeRoot.initChildrenFromDOM();
        } catch (JDOMException e) {
            throw new ReportException("LoadReport.Exception.Parsing", e);
        } catch (IOException e) {
            throw new ReportException("LoadReport.Exception.IO", e);
        }

    }

    public ReportTemplate getReportTemplate() {
        return reportTemplate;
    }

    public Document getReportDOM() {
        return reportDOM;
    }

    public Document getTemplateDOM() {
        return templateDOM;
    }

    public DocNode getTreeRoot() {
        return treeRoot;
    }

    public File getReportFolder() {
        return reportFolder;
    }

    public Template getVelocityTemplate() {
        return Velocity.getTemplate(reportTemplate.getVelocityTemplate().getName());
    }
}
