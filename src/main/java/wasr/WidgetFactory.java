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
import wasr.widgets.FieldUIWidget;
import wasr.widgets.JTextFieldUIWidget;

import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Properties;

/**
 * User: Jason Gillam
 * Date: 11/7/12
 * Time: 10:14 PM
 */
public class WidgetFactory {
    private static Logger LOG = Logger.getLogger(WidgetFactory.class);
    private static Properties props = null;
    private static final String RESOURCE_FOLDER = "wasr/";

    private static Properties getProps() {
        if (props == null) {

            InputStream in = ClassLoader.getSystemResourceAsStream(RESOURCE_FOLDER + "widgets.properties");
            props = new Properties();
            try {
                props.load(in);
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return props;
    }

    public static FieldUIWidget createWidget(String type, String nodeId, String fieldId) {
        String classname = getProps().getProperty(type);
        classname = (classname != null) ? classname : type;
        LOG.info("Creating widget for " + classname);
        Class clazz = null;
        if (classname == null) {
            return null;
        }
        try {
            clazz = Class.forName(classname);
        } catch (ClassNotFoundException e) {
            clazz = JTextFieldUIWidget.class;
        }

        try {
            Constructor c = clazz.getConstructor(String.class, String.class);
            Object o = c.newInstance(nodeId, fieldId);
            if (o instanceof FieldUIWidget) {
                return (FieldUIWidget) o;
            } else {
                return null;
            }
        } catch (NoSuchMethodException e) {
            return null;
        } catch (InstantiationException e) {
            return null;
        } catch (IllegalAccessException e) {
            return null;
        } catch (InvocationTargetException e) {
            return null;
        }
    }
}
