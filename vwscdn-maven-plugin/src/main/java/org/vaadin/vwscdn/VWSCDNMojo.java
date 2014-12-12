package org.vaadin.vwscdn;

/*
 * Copyright 2001-2005 The Apache Software Foundation.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
import java.io.File;
import java.net.MalformedURLException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.project.MavenProject;

/**
 * Updates Vaadin widgetsets based on other widgetset packages on the classpath.
 * It is assumed that the project does not directly contain other GWT modules.
 *
 * @goal generate
 * @requiresDependencyResolution compile
 * @phase process-classes
 */
public class VWSCDNMojo
        extends AbstractMojo {

    /**
     * The maven project descriptor
     *
     * @parameter expression="${project}"
     * @required
     * @readonly
     */
    private MavenProject project;

    public void execute()
            throws MojoExecutionException {

        try {
            List cp = project.getCompileClasspathElements();
            Map<String, URL> urls = new HashMap<>();
            for (Object object : cp) {
                String path = (String) object;
                urls.put(path, new File(path).toURI().toURL());
            }
            Map<String, URL> availableWidgetSets = ClassPathExplorer
                    .getAvailableWidgetSets(urls);
            Set<Artifact> artifacts = project.getArtifacts();

            System.out.println("@WebServlet(value = {\"/*\"}, asyncSupported = true)\n"
                    + "@VaadinServletConfiguration(productionMode = false, ui = HelloWorldUI.class)\n"
                    + "public static class Servlet extends VaadinServlet {\n"
                    + "\n"
                    + "    @Override\n"
                    + "    protected void servletInitialized() throws ServletException {\n"
                    + "        super.servletInitialized();\n"
                    + "\n"
                    + "        WidgetSetInfo ws = new WidgetSetInfo()\n");
            boolean first = true;
            for (String name : availableWidgetSets.keySet()) {
                URL url = availableWidgetSets.get(name);
                for (Artifact a : artifacts) {
                    String u = url.toExternalForm();
                    if (u.contains(a.getArtifactId())
                            && u.contains(a.getBaseVersion())) {
                        String aid = a.getArtifactId();
                        String gid = a.getGroupId();
                        String v = a.getBaseVersion();
                        if (!first) {
                            System.out.println("");
                        } else {
                            first = false;
                        }
                        System.out.print("                .addon(new AddonInfo(\"" + gid + "\", \"" + aid + "\", \"" + v + "\"))");
                    }

                }

            }
            if (!first) {
                System.err.println(";");
            }
            System.out.println(""
                    + "\n"
                    + "        // Intialize the widgetset. This might take a while at first run.\n"
                    + "        VWSCDN remote = new VWSCDN(getService());\n"
                    + "        remote.useRemoteWidgetset(ws);\n"
                    + "    }\n"
                    + "}"
            );

        } catch (DependencyResolutionRequiredException | MalformedURLException ex) {
            Logger.getLogger(VWSCDNMojo.class.getName()).log(Level.SEVERE, null, ex);
        }

    }
}
