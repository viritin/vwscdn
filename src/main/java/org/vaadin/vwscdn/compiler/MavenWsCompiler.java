/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.compiler;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collections;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.commons.io.FileUtils;
import org.apache.maven.shared.invoker.DefaultInvocationRequest;
import org.apache.maven.shared.invoker.DefaultInvoker;
import org.apache.maven.shared.invoker.InvocationRequest;
import org.apache.maven.shared.invoker.InvocationResult;
import org.apache.maven.shared.invoker.Invoker;
import org.apache.maven.shared.invoker.MavenInvocationException;
import org.vaadin.vwscdn.server.WidgetSetServlet;
import org.vaadin.vwscdn.shared.WidgetSetInfo;

public class MavenWsCompiler {

    public static final String MAVEN_HOME = "/usr/local/Cellar/maven/3.2.1/libexec";

    private final File baseDir;
    private File pomFile;
    private File src;
    private String widgetSetId;
    private final WidgetSetInfo widgetSetInfo;

    public static String compileWidgetSet(String id, WidgetSetInfo info, File workDir, File toDir) throws MavenInvocationException, IOException {
        MavenWsCompiler compiler = new MavenWsCompiler(workDir,
                id, info);
        compiler.compile();
        return id;
    }

    public MavenWsCompiler(File baseDir, String widgetSetId, WidgetSetInfo wsInfo) {
        this.baseDir = baseDir;
        this.widgetSetId = widgetSetId;
        this.widgetSetInfo = wsInfo;
        this.src = new File(baseDir, "src/main/java");
        this.src.mkdirs();
        this.pomFile = new File(baseDir, "pom.xml");

        try {
            CompilerUtils.copyPomFile(pomFile, widgetSetId, widgetSetInfo);
            CompilerUtils.generateConnectorBundleLoaderFactory(src, wsInfo.getEager());
            CompilerUtils.generateGwtXml(src, widgetSetId, Collections.EMPTY_SET);
        } catch (IOException ex) {
            Logger.getLogger(MavenWsCompiler.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    private void compile() throws MavenInvocationException, IOException {
        compile(this.pomFile, "compile");
        compile(this.pomFile, "vaadin:update-widgetset");
        compile(this.pomFile, "vaadin:compile");
        FileUtils.copyDirectory(new File(baseDir, "public/" + widgetSetId), new File(WidgetSetServlet.PUBLIC_ROOT_DIR, widgetSetId));
    }

    private static void compile(File pom, String goal) throws MavenInvocationException, IOException {
        InvocationRequest request = new DefaultInvocationRequest();
        request.setPomFile(pom);
        request.setGoals(Arrays.asList(goal));

        Invoker invoker = new DefaultInvoker();
        invoker.setMavenHome(new File(MAVEN_HOME));
        InvocationResult result = invoker.execute(request);
        if (result.getExitCode() != 0) {
            throw new IOException("Build failed: " + result.getExitCode(), result.getExecutionException());
        }
    }

}
