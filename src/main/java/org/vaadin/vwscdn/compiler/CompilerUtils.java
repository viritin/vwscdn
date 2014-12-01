package org.vaadin.vwscdn.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import org.vaadin.vwscdn.server.WSCompilerService;
import org.vaadin.vwscdn.shared.AddonInfo;
import org.vaadin.vwscdn.shared.WidgetInfo;
import org.vaadin.vwscdn.shared.WidgetSetInfo;

/**
 * Some file copying utilities for widget set compilation.
 *
 */
public class CompilerUtils {

    private static final String CONNECTOR_LOADER_TEMPLATE = "OptimizedConnectorBundleLoaderFactory.txt";
    private static final String CONNECTOR_LOADER_CLASSNAME = "OptimizedConnectorBundleLoaderFactory";
    private static final String EAGER_CONNECTORS_PLACEHOLDER = "[EAGER_CONNECTORS]";
    private static final String EAGER_CONNECTOR_TEMPLATE = "eagerConnectors.add([CLASSNAME].class.getName());\n            ";
    private static final String EAGER_CLASSNAME_PLACEHOLDER = "[CLASSNAME]";

    private static String POM_TEMPLATE_RESOURCE = "/pom-template.xml";

    public static void copyPomFile(File pomFile, String widgetsetId, WidgetSetInfo info) throws IOException {

        pomFile.createNewFile();

        StringBuilder addonDeps = new StringBuilder();
        for (AddonInfo a : info.getAddons()) {
            addonDeps.append(a.getMavenPomSnippet());
            addonDeps.append("\n");
        }

        try (BufferedReader r = new BufferedReader(new InputStreamReader(MavenWsCompiler.class.getResourceAsStream(POM_TEMPLATE_RESOURCE)));
                PrintStream w = new PrintStream(pomFile)) {
            String l;
            while ((l = r.readLine()) != null) {
                l = l.replace("[WS_ID]", widgetsetId);
                l = l.replace("[VAADIN_VERSION]", info.getVaadinVersion());
                l = l.replace("[COMPILE_STYLE]", "OBFUSCATED");
                l = l.replace("[COMPILE_DRAFT]", "" + false);
                l = l.replace("<!-- ADDON_DEPS -->", addonDeps);
                w.print(l+"\n");
            }
        }
    }

    public static void generateGwtXml(File widgetsetDir, String wsName,
            Set<String> includeWs) throws IOException {

        File widgetsetFile = new File(widgetsetDir, wsName + ".gwt.xml");
        if (!widgetsetFile.exists() && !widgetsetFile.createNewFile()) {
            throw new IOException("Could not create file " + widgetsetFile);
        }

        try (PrintStream printStream = new PrintStream(new FileOutputStream(
                widgetsetFile))) {
            printStream.print("<!DOCTYPE module PUBLIC \"-//Google Inc.//"
                    + "DTD Google Web Toolkit 2.5.1//EN\" \"http://google-"
                    + "web-toolkit.googlecode.com/svn/tags/2.5.1/distro-sou"
                    + "rce/core/src/gwt-module.dtd\">\n");
            printStream.print("<module>\n");
            for (String ws : includeWs) {
                printStream.print("<inherits name=\"" + ws + "\" />\n");
            }
            printStream.print("<generate-with class=\""
                    + CONNECTOR_LOADER_CLASSNAME
                    + "\">\n"
                    + "<when-type-assignable class=\"com.vaadin.client.metadata.ConnectorBundleLoader\" />\n"
                    + "</generate-with>\n");
            printStream.print("\n</module>\n");
        }
    }

    public static File generateConnectorBundleLoaderFactory(File srcDir, List<WidgetInfo> eagerWidgets) {
        // Return file
        File javaFile = new File(srcDir, CONNECTOR_LOADER_CLASSNAME + ".java");

        // Fill the template for eager connectors
        StringBuilder eagerStr = new StringBuilder();
        for (WidgetInfo w : eagerWidgets) {
            eagerStr.append(EAGER_CONNECTOR_TEMPLATE.replace(EAGER_CLASSNAME_PLACEHOLDER, w.getFqn()));
        }

        // Copy file and add/replace eager connectors
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(WSCompilerService.class.getResourceAsStream("/" + CONNECTOR_LOADER_TEMPLATE)));
            PrintStream w = new PrintStream(javaFile);
            String l = null;
            while ((l = r.readLine()) != null) {
                w.print(l.replace(EAGER_CONNECTORS_PLACEHOLDER, eagerStr));
                w.print("\n");
            }
            w.close();
            r.close();
        } catch (IOException ex) {
            Logger.getLogger(WSCompilerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return javaFile;

    }

    public static int compileJava(File fileToCompile, List<File> classPath) {
        int compilationResult = -1;
        try {
            JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
            System.out.println(compiler.getClass().getName());
            List<String> options = new ArrayList<String>();
            options.add("-classpath");
            options.add(WidgetSetCompiler.getClassPathArg(classPath));
            options.add("-s");
            options.add(fileToCompile.getParent());
            options.add(fileToCompile.getCanonicalPath());
            WidgetSetCompiler.printArguments(options);
            compilationResult = compiler.run(null, null, null, options.toArray(new String[options.size()]));
        } catch (IOException ex) {
            Logger.getLogger(WSCompilerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return compilationResult;
    }



}
