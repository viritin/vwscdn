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
import java.io.FileWriter;
import java.io.IOException;
import java.net.MalformedURLException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;

import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;

/**
 * Generates necessary VWSCDN client code.
 *
 */
@Mojo(name = "generate", defaultPhase = LifecyclePhase.GENERATE_SOURCES, requiresDependencyResolution = ResolutionScope.COMPILE)
public class VWSCDNMojo
        extends AbstractMojo {

    /**
     * The maven project descriptor
     *
     */
    @Parameter(property = "project", defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * Output directory for generated source files.
     *
     */
    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources/vwscdn")
    private File outputDirectory;

    public void execute()
            throws MojoExecutionException {

        String className = "GeneratedWidgetSet";

        // Use same package as Maven plugin
        File packageDirectory = new File(outputDirectory,
                VWSCDNMojo.class.getPackage().getName().replace(".", "/"));
        packageDirectory.mkdirs();
        File outputFile = new File(packageDirectory, className + ".java");

        try (FileWriter out = new FileWriter(outputFile)) {
            List cp = project.getCompileClasspathElements();
            Map<String, URL> urls = new HashMap<>();
            for (Object object : cp) {
                String path = (String) object;
                urls.put(path, new File(path).toURI().toURL());
            }
            Map<String, URL> availableWidgetSets = ClassPathExplorer
                    .getAvailableWidgetSets(urls);
            Set<Artifact> artifacts = project.getArtifacts();

            out.write(
                    "package " + VWSCDNMojo.class.getPackage().getName() + ";\n\n");
            out.write("import org.vaadin.vwscdn.client.DefaultWidgetSet;\n");
            out.write("import org.vaadin.vwscdn.annotations.WidgetSet;\n"
                    + "import javax.servlet.annotation.WebListener;\n"
                    + "import javax.servlet.http.HttpSessionEvent;\n");
            out.write(
                    "import static org.vaadin.vwscdn.annotations.WidgetSetType.GENERATED;\n");
            out.write("\n@WebListener\n");
            out.write("@WidgetSet(GENERATED)\n");
            out.write(
                    "public class " + className + " extends DefaultWidgetSet implements javax.servlet.http.HttpSessionListener {\n"
                    + "    private boolean inited = false;\n"
                    + "\n"
                    + "    public " + className + "() { \n"
                    + "        super(); \n");
            Set<Artifact> requiredArtifacts = new HashSet<>();
            for (String name : availableWidgetSets.keySet()) {
                URL url = availableWidgetSets.get(name);
                for (Artifact a : artifacts) {
                    String u = url.toExternalForm();
                    if (u.contains(a.getArtifactId())
                            && u.contains(a.getBaseVersion()) && !u.contains(
                                    "vaadin-client")) {
                        requiredArtifacts.add(a);
                    }
                }
            }
            for (Artifact a : requiredArtifacts) {
                String aid = a.getArtifactId();
                String gid = a.getGroupId();
                String v = a.getBaseVersion();
                out.write("\n        addon(\""
                        + gid + "\", \"" + aid + "\", \"" + v + "\");");
            }
            out.write("\n"
                    + "    }\n");
            out.write("\n    @Override\n"
                    + "    public void sessionCreated(HttpSessionEvent se) {\n"
                    + "        if(!inited) {\n"
                    + "            init();\n"
                    + "            inited = true;\n"
                    + "        }\n"
                    + "    }\n"
                    + "\n"
                    + "    @Override\n"
                    + "    public void sessionDestroyed(HttpSessionEvent se) {\n"
                    + "    }\n"
                    + "");

            out.write("}\n");

            System.out.println(
                    requiredArtifacts.size() + " addons widget set found.");
            System.out.println("Widget Set created to " + outputFile.
                    getAbsolutePath() + ".");

            project.addCompileSourceRoot("target/generated-sources/vwscdn");
        } catch (DependencyResolutionRequiredException | MalformedURLException ex) {
            Logger.getLogger(VWSCDNMojo.class.getName()).log(Level.SEVERE, null,
                    ex);
        } catch (IOException ex) {
            Logger.getLogger(VWSCDNMojo.class.getName()).log(Level.SEVERE, null,
                    ex);
        }

    }
}
