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

import org.apache.commons.io.FileUtils;
import org.apache.commons.lang.SystemUtils;
import org.apache.log4j.Logger;
import org.apache.velocity.Template;
import org.apache.velocity.app.Velocity;
import org.jdom2.Document;
import org.jdom2.JDOMException;
import org.jdom2.input.SAXBuilder;

import javax.swing.*;
import java.io.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Locale;
import java.util.MissingResourceException;
import java.util.Properties;
import java.util.ResourceBundle;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class UserSettings {
    private static Logger LOG = Logger.getLogger(UserSettings.class);
    private static Properties props = null;
    public static final String RESOURCE_FOLDER = "wasr/";
    public static final String DEFAULT_TEMPLATE = "default";
    public static final String SELECTED_TEMPLATE = "template";

    public static final String PROJECT_FOLDER = "project.folder";
    public static final String CUSTOM_WORKING_FOLDER = "custom.working.folder";
    public static final String LAST_FOLDER = "lastfolder";

    private static ResourceBundle templateBundle = null;

    public static synchronized File getFolder() {
        File home = SystemUtils.getUserHome();
        File wasr = new File(home, ".wasr");
        if (!wasr.exists()) {
            wasr.mkdirs();
        }
        return wasr;
    }

    public static File getLastFolder() {
        if (getProperties().containsKey(LAST_FOLDER)) {
            String path = getProperties().getProperty(LAST_FOLDER);
            return new File(path);
        } else {
            return getFolder();
        }
    }

    public static void setLastFolder(File folder) {
        if (folder.exists()) {
            if (!folder.isDirectory()) {
                folder = folder.getParentFile();
            }
            getProperties().setProperty(LAST_FOLDER, folder.getPath());
            saveProperties();
        }
    }

    public static synchronized Properties getProperties() {
        if (props == null) {
            File propsFile = new File(getFolder(), "wasr.properties");
            if (!propsFile.exists()) {
                LOG.warn("Properties file not found. Copying from default.");
                copyResource("wasr/wasr.properties", propsFile);

            }

            props = new Properties();
            try {
                FileInputStream fis = new FileInputStream(propsFile);
                props.load(fis);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        return props;
    }

    public static synchronized void saveProperties() {
        Properties props = getProperties();
        File propsFile = new File(getFolder(), "wasr.properties");
        try {
            FileWriter fw = new FileWriter(propsFile);
            props.store(fw, "WASR Properties");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            LOG.warn(e);//
        }
    }

    public static void copyResource(String fromResource, String toFilename) {
        File toFile = new File(getFolder(), toFilename);
        copyResource(fromResource, toFile);
    }

    public static void copyResource(String fromResource, File toFile) {
        LOG.info("Loading from resource: " + fromResource);
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(fromResource);
        if (in == null) {
            in = ClassLoader.getSystemClassLoader().getResourceAsStream(RESOURCE_FOLDER + fromResource);
        }

        if (in == null) {
            LOG.error("Default resource could not be located: " + fromResource);
        } else {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();

            LOG.debug("Reading from resource...");
            try {
                int b = in.read();

                while (b > -1) {
                    baos.write(b);
                    b = in.read();
                }

                LOG.debug("Writing to file...");

                baos.flush();
            } catch (IOException e) {
                e.printStackTrace();
            }
            byte[] bytes = baos.toByteArray();

            ByteArrayInputStream bais = new ByteArrayInputStream(bytes);
            try {
                if (!toFile.exists()) {
                    toFile.createNewFile();   // not sure if this is needed
                }
                FileUtils.copyInputStreamToFile(bais, toFile);
                bais.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static Document getDomResource(String resourceName) {
        InputStream in = ClassLoader.getSystemClassLoader().getResourceAsStream(RESOURCE_FOLDER + resourceName);
        if (in == null) {
            File customFile = new File(getFolder(), resourceName);
            if (customFile.exists()) {
                try {
                    in = new BufferedInputStream(new FileInputStream(customFile));
                } catch (FileNotFoundException ignore) {
                    // already checked if it exists.
                }
            }
        }

        if (in == null) {
            return null;
        }

        SAXBuilder builder = new SAXBuilder();
        try {
            return builder.build(in);
        } catch (JDOMException e) {
            LOG.warn(e);
            return null;
        } catch (IOException e) {
            LOG.warn(e);
            return null;
        }
    }

    public static void resetTemplateBundle() {
        templateBundle = null;
    }

    public static ResourceBundle getSelectedTemplateBundle() {
        if (templateBundle == null) {
            String selectedTemplate = getProperties().getProperty(SELECTED_TEMPLATE, DEFAULT_TEMPLATE);
            if (DEFAULT_TEMPLATE.equals(selectedTemplate)) {
                templateBundle = ResourceBundle.getBundle(RESOURCE_FOLDER + DEFAULT_TEMPLATE);
            } else {
                try {
                    URL[] urls = {getFolder().toURI().toURL()};
                    URLClassLoader classloader = new URLClassLoader(urls);
                    templateBundle = ResourceBundle.getBundle(selectedTemplate, Locale.getDefault(), classloader);
                } catch (MalformedURLException e) {
                    LOG.warn("Could not load resource bundle for " + selectedTemplate + ".  Using default.");
                    templateBundle = ResourceBundle.getBundle(RESOURCE_FOLDER + DEFAULT_TEMPLATE);
                }
            }
        }
        return templateBundle;
    }

    public static Icon getIconResource(String iconPath) {
        URL imageResource = templateBundle.getClass().getResource(iconPath); // just need classloader that can see
        // resources
        return new ImageIcon(imageResource);
    }

    public static String getSelectedTemplateResource(String resource, String defaultValue) {
        try {
            return getSelectedTemplateBundle().getString(resource);
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }

    @Deprecated
    public static Template getSelectedVelocityTemplate() {
        String templateName = getProperties().getProperty(SELECTED_TEMPLATE, DEFAULT_TEMPLATE);
        if (DEFAULT_TEMPLATE.equals(templateName)) {
            return Velocity.getTemplate(RESOURCE_FOLDER + DEFAULT_TEMPLATE + ".vm");
        } else {
            Template t = Velocity.getTemplate(templateName + ".vm");
            if (t == null) {
                LOG.warn("Could not find selected template (" + templateName + ").  Using default.");
                return Velocity.getTemplate(RESOURCE_FOLDER + DEFAULT_TEMPLATE + ".vm");
            } else {
                return t;
            }

        }
    }

    public static ResourceBundle getAppBundle() {
        return ResourceBundle.getBundle(RESOURCE_FOLDER + "AppBundle");
    }

    public static String getAppResource(String resource, String defaultValue) {
        try {
            return getAppBundle().getString(resource);
        } catch (MissingResourceException e) {
            return defaultValue;
        }
    }

    public static String getAppResource(String resource) {
        return getAppResource(resource, resource);
    }

    @Deprecated
    public static File getWorkingFolder() {
        String path = getProperties().getProperty(CUSTOM_WORKING_FOLDER);
        if (path != null) {
            File workingFolder = new File(path);
            if (!workingFolder.exists()) {
                workingFolder.mkdirs();
            }
            return workingFolder;
        } else {
            return null;
        }
    }
}
