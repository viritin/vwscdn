/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.vaadin.vwscdn.server.WSCompilerService;
import static org.vaadin.vwscdn.server.WSCompilerService.COMPILER_ROOT_DIR;
import org.vaadin.vwscdn.server.WidgetSetServlet;
import org.vaadin.vwscdn.shared.AddonInfo;
import org.vaadin.vwscdn.shared.WidgetSetInfo;

public class WidgetSetCompiler {

    private static final File GEN_SRC_DIR = new File(COMPILER_ROOT_DIR, "tmp-src");
    private static final File TMP_DIR = new File(COMPILER_ROOT_DIR, "tmp");
    private static final File VAADIN_JAR_DIR = new File(COMPILER_ROOT_DIR, "vaadin");
    private static final File ADDON_JAR_DIR = new File(COMPILER_ROOT_DIR, "addons");
    private static List<Addon> allAddons;

    public static void printArguments(List<String> argsStr) {
        for (String arg : argsStr) {
            System.out.print(arg + " ");
        }
        System.out.println("");
    }

    private String wsName;
    private File targetDir;
    private File tempDir;
    private List<File> classpathEntries;

    private Process compilerProcess;
    private boolean calcelCompilation;
    private final Logger compilerLogger;

    public WidgetSetCompiler(String wsName, File targetDir, File tempDir, List<File> classpathEntries) {
        this.compilerLogger = Logger.getLogger(WidgetSetCompiler.class.getName());
        this.wsName = wsName;
        this.targetDir = targetDir;
        this.tempDir = tempDir;
        this.classpathEntries = classpathEntries;
    }

    public void compileWidgetset() throws IOException, InterruptedException {

        File unitCacheDirectory = new File(tempDir, "unit-cache");
        if (unitCacheDirectory.exists()) {
            unitCacheDirectory.delete();
        }

        boolean someNotExists = false;
        StringBuilder nonExistedFiles = new StringBuilder("ERROR: Can't found files: ");
        for (File classpathEntry : classpathEntries) {
            if (!classpathEntry.exists()) {
                someNotExists = true;
                nonExistedFiles.append(classpathEntry.getName()).append(" (").append(classpathEntry.getAbsolutePath()).append(")<br/>");
            }
        }

        if (someNotExists) {
            System.out.println(nonExistedFiles.toString());
            compilerLogger.log(Level.SEVERE, nonExistedFiles.toString());
            cancel();
        }

        ArrayList<String> args = new ArrayList<String>();
        args.add(getJava());

        args.add("-Djava.awt.headless=true");
        args.add("-Dgwt.nowarn.legacy.tools");
        args.add("-Dgwt.usearchives=false");
        args.add("-Xss8M");
        args.add("-Xmx512M");
        args.add("-XX:MaxPermSize=512M");

        //args.add("-verbose:class");
        if (System.getProperty("os.name").equals("mac")) {
            args.add("-XstartOnFirstThread");
        }

        args.add("-classpath");
        args.add(getClassPathArg(this.classpathEntries));

        String compilerClass = "com.google.gwt.dev.Compiler";
        args.add(compilerClass);

        args.add("-war");
        args.add(targetDir.getAbsolutePath());

        args.add("-localWorkers");
        args.add("" + Runtime.getRuntime().availableProcessors());

        args.add("-XfragmentCount");
        args.add("-1");

        args.add("-logLevel");
        args.add("INFO");

        args.add(wsName);

        final String[] argsStr = new String[args.size()];
        args.toArray(argsStr);
        printArguments(args);

        calcelCompilation = false;
        compilerProcess = new ProcessBuilder(argsStr).start();

        if (compilerLogger != null) {
            ExecutorService executor = Executors.newFixedThreadPool(2);
            executor.execute(new Runnable() {
                public void run() {
                    try {
                        BufferedReader stdInput = new BufferedReader(
                                new InputStreamReader(compilerProcess.getInputStream()));
                        String s = null;
                        while ((s = stdInput.readLine()) != null) {
                            System.out.println(s);
                            compilerLogger.log(Level.INFO, s);
                        }
                    } catch (IOException e) {
                    }
                }
            });

            executor.execute(new Runnable() {
                public void run() {
                    try {
                        BufferedReader stdError = new BufferedReader(
                                new InputStreamReader(compilerProcess.getErrorStream()));
                        String s = null;
                        while ((s = stdError.readLine()) != null) {
                            System.out.println(s);
                            compilerLogger.log(Level.INFO, s);
                        }
                    } catch (IOException e) {
                    }
                }
            });
        }

        compilerProcess.waitFor();

        if (compilerProcess.exitValue() != 0 && compilerLogger != null
                && !calcelCompilation) {
            compilerLogger.log(Level.SEVERE, "ERROR: Compilation ended due to an error.");
        }
    }

    public static String getClassPathArg(List<File> classpathEntries) {
        StringBuilder classpath = new StringBuilder();
        for (File classpathEntry : classpathEntries) {
            classpath.append(classpathEntry).append(File.pathSeparator);
        }

        String classpathString = classpath.toString().trim();
        return classpathString.endsWith(File.pathSeparator)
                ? classpathString + " "
                : classpathString + File.pathSeparator + " ";
    }

    public void cancel() {
        calcelCompilation = true;
        compilerProcess.destroy();
    }

    /**
     * Returns the proper Java command.
     *
     * First checks $JAVA_HOME/bin/java, then returns 'java', which is expected
     * to be found from PATH.
     *
     * @return name of java executable.
     */
    private String getJava() {
        String javaHome = System.getenv("JAVA_HOME");
        String path = javaHome + File.separator + "bin" + File.separator + "java";

        // Can't use isExecutable() for this check as it's Java 6.
        File file = new File(path);
        if (file.exists()) {
            return path;
        }

        return "java";
    }

    public static String compileWidgetset(String id, WidgetSetInfo info) {

        String wsName = "ws" + id;

        // Generate classpath
        List<File> cp = new ArrayList<File>();
        cp.addAll(getCoreJars(new File(VAADIN_JAR_DIR, info.getVaadinVersion())));
        cp.add(GEN_SRC_DIR);

        // Intialize if not yet initialized
        if (allAddons == null) {
            allAddons = Addon.getAllAddonWidgetSets(ADDON_JAR_DIR);
        }

        // Generate widgetset file
        Set<String> wss = new HashSet<>();

        // Process all requested addons for widgetset include.
        // TODO: We sound really index all the classes in addons and build an
        // index based on that. Now we just use the jar name
        List<Addon> includedAddons = new ArrayList<>();
        for (AddonInfo ci : info.getAddons()) {
            // TODO: NOT WORKING ANYMORE: Addon match = Addon.findAddon(ci.getFqn().substring(6), ci.getVersion(), allAddons);
            //if (match != null) {
            //    includedAddons.add(match);
            //} else {
            Logger.getLogger(WSCompilerService.class.getName()).log(Level.WARNING, "Addon not found " + ci.getFullMavenId());
            //}
        }
        for (Addon addon : includedAddons) {
            cp.add(addon.getJarFile());
            wss.addAll(addon.getWidgetsets());
        }
        wss.add("com.vaadin.DefaultWidgetSet");

        // Generate WidgetSetInfo XML
        try {
            File javaFile = CompilerUtils.generateConnectorBundleLoaderFactory(GEN_SRC_DIR, info.getEager());
            int res = CompilerUtils.compileJava(javaFile, cp);
            assert res == 0;
            CompilerUtils.generateGwtXml(GEN_SRC_DIR, wsName, wss);
        } catch (IOException ex) {
            Logger.getLogger(WSCompilerService.class.getName()).log(Level.SEVERE, null, ex);
            return null;
        }

        // Run the compiler
        WidgetSetCompiler compiler = new WidgetSetCompiler(wsName, WidgetSetServlet.PUBLIC_ROOT_DIR, TMP_DIR, cp);

        try {
            compiler.compileWidgetset();
            return wsName;
        } catch (Exception ex) {
            Logger.getLogger(WSCompilerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    public static List<File> getCoreJars(File vaadinCoreDir) {
        File[] jars = vaadinCoreDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        return Arrays.asList(jars);
    }
}
