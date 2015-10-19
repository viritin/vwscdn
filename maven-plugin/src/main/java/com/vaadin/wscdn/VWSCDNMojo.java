package com.vaadin.wscdn;

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
import com.vaadin.wscdn.client.AddonInfo;
import com.vaadin.wscdn.client.Connection;
import com.vaadin.wscdn.client.PublishState;
import com.vaadin.wscdn.client.WidgetSetRequest;
import com.vaadin.wscdn.client.WidgetSetResponse;
import java.io.File;
import java.io.IOException;
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
import org.apache.commons.io.FileUtils;
import org.apache.maven.artifact.Artifact;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.plugins.annotations.ResolutionScope;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.IOUtil;

/**
 * Generates necessary VWSCDN client code.
 *
 */
@Mojo(name = "generate",
        defaultPhase = LifecyclePhase.GENERATE_SOURCES,
        requiresOnline = true,
        requiresDependencyResolution = ResolutionScope.COMPILE)
public class VWSCDNMojo
        extends AbstractMojo {

    /**
     * The maven project descriptor
     *
     */
    @Parameter(property = "project", defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    /**
     * Should the compilation be requested as asynchronous. This is the default,
     * but if synchronous mode is needed this should be set to false.
     *
     */
    @Parameter(property = "wscdn.async", defaultValue = "true", readonly = true)
    private boolean asyncCompile;

    /**
     * FOR EXPERTS ONLY, with this flag the widgetset is downloaded into local
     * war file. With this option you'll use the widgetset CDN only for
     * compilation and the result is downloaded to local war file for serving it
     * to your users.
     */
    @Parameter(property = "wscdn.download", defaultValue = "false")
    private boolean download;

    /**
     * Compilation style for widget set. Default is the "OBF". Supported values
     * "OBF", "PRETTY", "DETAILED"
     *
     */
    @Parameter(property = "wscdn.compile.style", defaultValue = "OBF", readonly = true)
    private String compileStyle;

    /**
     * Output directory for generated source files.
     *
     */
    @Parameter(property = "outputDirectory", defaultValue = "${project.build.directory}/generated-sources/wscdn")
    private File outputDirectory;

    /**
     * Last used widgetset
     */
    @Parameter(defaultValue = "${project.build.directory}/wscdn-widgetset")
    private File lastWidgetset;

    /**
     * Output directory for generated source files.
     *
     */
    @Parameter(property = "downloadedDirectory", defaultValue = "${project.build.directory}/${project.build.finalName}/VAADIN/widgetsets")
    private File downloadedDirectory;

    @Override
    public void execute()
            throws MojoExecutionException {

        try {

            project.addCompileSourceRoot("target/generated-sources/wscdn");

            String packageName = "com.vaadin.wscdn";
            String className = "WidgetSet";

            String vaadinVersion = null;

            // Use same package as Maven plugin
            File packageDirectory = new File(outputDirectory,
                    packageName.replace(".", "/"));
            packageDirectory.mkdirs();

            List cp = project.getCompileClasspathElements();
            Map<String, URL> urls = new HashMap<>();
            for (Object object : cp) {
                String path = (String) object;
                urls.put(path, new File(path).toURI().toURL());
            }
            Map<String, URL> availableWidgetSets;
            try {
                availableWidgetSets = ClassPathExplorer
                        .getAvailableWidgetSets(urls);
            } catch (CvalChecker.InvalidCvalException ex) {
                throw new MojoExecutionException("Cval license check failed!", ex);
            }
            Set<Artifact> artifacts = project.getArtifacts();
            for (Artifact artifact : artifacts) {
                // Store the vaadin version
                if (artifact.getArtifactId().equals("vaadin-server")) {
                    vaadinVersion = artifact.getVersion();
                    break;
                }
            }
            Set<Artifact> uniqueArtifacts = new HashSet<>();

            for (String name : availableWidgetSets.keySet()) {
                URL url = availableWidgetSets.get(name);
                for (Artifact a : artifacts) {
                    String u = url.toExternalForm();
                    if (u.contains(a.getArtifactId())
                            && u.contains(a.getBaseVersion()) && !u.contains(
                                    "vaadin-client")) {
                        uniqueArtifacts.add(a);
                    }
                }
            }

            WidgetSetRequest wsReq = new WidgetSetRequest();
            for (Artifact a : uniqueArtifacts) {
                wsReq.addon(a.getGroupId(), a.getArtifactId(), a.
                        getBaseVersion());
            }
            System.out.println((wsReq.getAddons() != null ? wsReq.getAddons().
                    size() : 0) + " addons widget set found.");

            // Request compilation for the widgetset   
            wsReq.setCompileStyle(compileStyle);
            wsReq.setVaadinVersion(vaadinVersion);

            if (lastWidgetset.exists()
                    && FileUtils.readFileToString(lastWidgetset)
                    .equals(wsReq.toWidgetsetString())) {
                System.out.println("No changes in widgetset: "
                        + wsReq.toWidgetsetString());
                return;
            } else {
                FileUtils.
                        writeStringToFile(lastWidgetset, wsReq.
                                toWidgetsetString());
            }

            File outputFile = new File(packageDirectory, className + ".java");

            if (download) {
                serveLocally(wsReq, vaadinVersion, outputFile);
            } else {
                serveFromCDN(wsReq, vaadinVersion, outputFile);
            }
        } catch (IOException ex) {
            Logger.getLogger(VWSCDNMojo.class.getName()).log(Level.SEVERE, null,
                    ex);
        } catch (DependencyResolutionRequiredException ex) {
            Logger.getLogger(VWSCDNMojo.class.getName()).log(Level.SEVERE, null,
                    ex);
        }

    }

    protected void serveLocally(WidgetSetRequest wsReq, String vaadinVersion,
            File outputFile) throws IOException, MojoExecutionException {

        String wsName = null;
        String wsUrl = null;

        Connection conn = new Connection();
        wsName = conn.downloadRemoteWidgetSet(wsReq, downloadedDirectory);

        String listener = IOUtil.toString(getClass().getResourceAsStream(
                "/weblistener.tmpl"));
        listener = listener.replace("__wsUrl", "local");
        listener = listener.replace("__wsName", wsName);
        listener = listener.replace("__wsReady", "true");

        StringBuilder sb = new StringBuilder();
        if (wsReq.getAddons() != null) {
            for (AddonInfo a : wsReq.getAddons()) {
                String aid = a.getArtifactId();
                String gid = a.getGroupId();
                String v = a.getVersion();
                sb.append(" * ");
                sb.append(aid);
                sb.append(":");
                sb.append(gid);
                sb.append(":");
                sb.append(v);
                sb.append("\n");
            }
        }
        listener = listener.replace("__vaadin", " * " + vaadinVersion);
        listener = listener.replace("__style", " * " + compileStyle);
        listener = listener.replace("__addons", sb.toString());

        FileUtils.writeStringToFile(outputFile, listener);

        System.out.println("Widgetset config created to " + outputFile.
                getAbsolutePath() + ".");

    }

    protected void serveFromCDN(WidgetSetRequest wsReq, String vaadinVersion,
            File outputFile) throws IOException, MojoExecutionException {
        String wsName = null;
        String wsUrl = null;

        Connection conn = new Connection();
        WidgetSetResponse wsRes = conn.queryRemoteWidgetSet(wsReq, asyncCompile);
        if (wsRes != null && (wsRes.getStatus() == PublishState.AVAILABLE // Compiled and published
                || wsRes.getStatus() == PublishState.COMPILED // Compiled succesfully, but not yet available
                || wsRes.getStatus() == PublishState.COMPILING)) // Currently compiling the widgetset)
        {
            wsName = wsRes.getWidgetSetName();
            wsUrl = wsRes.getWidgetSetUrl();
        } else {
            throw new MojoExecutionException(
                    "Remote widgetset compilation failed: " + (wsRes != null ? wsRes.
                            getStatus() : " (no response)"));
        }

        String listener = IOUtil.toString(getClass().getResourceAsStream(
                "/weblistener.tmpl"));
        listener = listener.replace("__wsUrl", wsUrl);
        listener = listener.replace("__wsName", wsName);
        listener = listener.replace("__wsReady",
                wsRes.getStatus() == PublishState.AVAILABLE ? "true" : "false");

        StringBuilder sb = new StringBuilder();
        if (wsReq.getAddons() != null) {
            for (AddonInfo a : wsReq.getAddons()) {
                String aid = a.getArtifactId();
                String gid = a.getGroupId();
                String v = a.getVersion();
                sb.append(" * ");
                sb.append(aid);
                sb.append(":");
                sb.append(gid);
                sb.append(":");
                sb.append(v);
                sb.append("\n");
            }
        }
        listener = listener.replace("__vaadin", " * " + vaadinVersion);
        listener = listener.replace("__style", " * " + compileStyle);
        listener = listener.replace("__addons", sb.toString());

        FileUtils.writeStringToFile(outputFile, listener);

        // Print some info
        if (wsName != null && wsUrl != null) {
            System.out.println("Widgetset config created to " + outputFile.
                    getAbsolutePath() + ". Public URL: " + wsUrl);
        } else {
            System.out.println("Widget set created to " + outputFile.
                    getAbsolutePath() + ".");

        }
    }
}
