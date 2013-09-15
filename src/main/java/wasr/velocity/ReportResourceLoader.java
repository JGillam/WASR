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

package wasr.velocity;

import org.apache.commons.collections.ExtendedProperties;
import org.apache.log4j.Logger;
import org.apache.velocity.exception.ResourceNotFoundException;
import org.apache.velocity.runtime.resource.Resource;
import org.apache.velocity.runtime.resource.loader.FileResourceLoader;
import wasr.WASRFrame;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;

/**
 * User: Jason Gillam
 * Date: 7/21/13
 * Time: 10:12 AM
 */
public class ReportResourceLoader extends FileResourceLoader {
    private static Logger LOG = Logger.getLogger(ReportResourceLoader.class);


    @Override
    public InputStream getResourceStream(String name) throws ResourceNotFoundException {
        File resourceFile = new File(WASRFrame.getInstance().getCurrentReport().getReportFolder(), "template/" + name);
        String templatePath = resourceFile.getPath();
        if (!resourceFile.exists()) {
            resourceFile = new File(WASRFrame.getInstance().getCurrentReport().getReportFolder(), name);
        }
        try {
            return new FileInputStream(resourceFile);
        } catch (FileNotFoundException e) {
            throw new ResourceNotFoundException("Could not locate resource file: " + resourceFile.getPath() + " or " +
                    templatePath);
        }
    }

    @Override
    public boolean isSourceModified(Resource resource) {
        return true;  //todo: implement caching
    }

    @Override
    public long getLastModified(Resource resource) {
        return 0;  //todo: implement caching
    }

    @Override
    public boolean resourceExists(String name) {
        try {
            File templateFile = new File(WASRFrame.getInstance().getCurrentReport().getReportFolder(),
                    "template/" + name);
            File resourceFile = new File(WASRFrame.getInstance().getCurrentReport().getReportFolder(), name);
            return templateFile.exists() || resourceFile.exists();
        } catch (NullPointerException e) {  // TODO: Clean this up
            return false;
        }
    }

    public ReportResourceLoader() {
        super();
        LOG.debug("Constructed");

    }

    @Override
    public void init(ExtendedProperties configuration) {
        super.init(configuration);
        LOG.debug("Initialized");
    }
}
