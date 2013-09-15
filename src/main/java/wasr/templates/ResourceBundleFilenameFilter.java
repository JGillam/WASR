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
import java.io.FilenameFilter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * User: Jason Gillam
 * Date: 7/12/13
 * Time: 8:57 PM
 */
public class ResourceBundleFilenameFilter implements FilenameFilter {

    Pattern p;

    public ResourceBundleFilenameFilter(String bundlename) {
        p = Pattern.compile("^" + bundlename + "(_[a-zA-Z]{2,4})?\\.properties$");
    }


    @Override
    public boolean accept(File dir, String name) {
        Matcher m = p.matcher(name);
        return m.find();
    }

}
