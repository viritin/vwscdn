/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WidgetSetCompiler {

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
}
