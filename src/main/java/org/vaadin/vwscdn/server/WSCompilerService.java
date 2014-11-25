/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.server;

import java.io.BufferedReader;
import org.vaadin.vwscdn.shared.WidgetSetInfo;
import org.vaadin.vwscdn.shared.WidgetInfo;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import javax.ws.rs.Produces;
import org.vaadin.vwscdn.shared.RemoteWidgetSet;

/**
 *
 *
 * @author se
 */
@Path("/vwscdn")
public class WSCompilerService {

    //TODO: make this dynamic
    private final String publicWidgetSetBaseURL = "http://localhost:8080/vaadin-wscdn-1.0-SNAPSHOT/ws/";

    //TODO: make this dynamic
    private final File COMPILER_ROOT_DIR = new File("/Users/se/ws/compiler");
    private final File GEN_SRC_DIR = new File(COMPILER_ROOT_DIR, "tmp-src");
    private final File TMP_DIR = new File(COMPILER_ROOT_DIR, "tmp");
    private final File VAADIN_JAR_DIR = new File(COMPILER_ROOT_DIR, "vaadin");
    private final File ADDON_JAR_DIR = new File(COMPILER_ROOT_DIR, "addons");
    private List<Addon> allAddons;
    private static final String CONNECTOR_LOADER_TEMPLATE = "OptimizedConnectorBundleLoaderFactory.txt";
    private static final String CONNECTOR_LOADER_CLASSNAME = "OptimizedConnectorBundleLoaderFactory";
    private static final String EAGER_CONNECTORS_PLACEHOLDER = "[EAGER_CONNECTORS]";
    private static final String EAGER_CONNECTOR_TEMPLATE = "eagerConnectors.add([CLASSNAME]);\n";
    private static final String EAGER_CLASSNAME_PLACEHOLDER = "[CLASSNAME]";
    
    public WSCompilerService() {
    }
    
    private static final String NA = "error";
    
    @Produces("text/plain")
    @GET
    @Path("/ping")
    public String test() {
        return "Reply at " + new Date();
    }
    
    @Produces("application/json")
    @POST
    @Path("/compile")
    @Consumes("application/json")
    public RemoteWidgetSet compileWidgetSet(WidgetSetInfo info) {
        if (info == null) {
            return new RemoteWidgetSet();
        }

        // Create hash from component data 
        String id = buildId(info);

        // Try to find an existing widgetset
        String widgetset = findPreCompiledWidgetset(id);

        // If not found, compile a new one
        if (widgetset == null) {
            widgetset = gwtCompile(id, info);
        }

        // Return a URL to the public widgetset
        if (widgetset != null) {
            RemoteWidgetSet res = new RemoteWidgetSet();
            res.setWidgetSetName(widgetset);
            res.setWidgetSetUrl(publicWidgetSetBaseURL + widgetset + "/" + widgetset + ".nocache.js");
            return res;
        }

        // This should never happen
        return new RemoteWidgetSet();
    }
    
    private String buildId(WidgetSetInfo info) {
        StringBuilder hash = new StringBuilder();
        for (WidgetInfo ci : info.getEager()) {
            String fqn = ci.getFqn();
            hash.append(fqn);
            String v = ci.getVersion();
            if (v != null) {
                hash.append(v);
            }
        }
        return calculateMD5(hash.toString());
    }
    
    private static String calculateMD5(String string) {
        try {
            byte[] bytesOfMessage = string.getBytes("UTF-8");
            MessageDigest md = MessageDigest.getInstance("MD5");
            byte[] thedigest = md.digest(bytesOfMessage);
            
            StringBuilder hexString = new StringBuilder();
            
            for (int i = 0; i < thedigest.length; i++) {
                String hex = Integer.toHexString(0xFF & thedigest[i]);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("MD5 failed", e);
        }
    }
    
    private String findPreCompiledWidgetset(String id) {
        String wsName = "ws" + id;
        File wsFile = new File(WidgetSetServlet.PUBLIC_ROOT_DIR, wsName);
        return (wsFile.exists() && wsFile.canRead()) ? wsName : null;
    }
    
    private String gwtCompile(String id, WidgetSetInfo info) {
        
        String wsName = "ws" + id;

        // Generate classpath
        List<File> cp = new ArrayList<File>();
        cp.addAll(getCoreJars(new File(VAADIN_JAR_DIR, info.getVaadinVersion())));
        cp.add(GEN_SRC_DIR);

        // Intialize if not yet initialized
        if (allAddons == null) {
            allAddons = getAllAddonWidgetSets(ADDON_JAR_DIR);
        }

        // Generate widgetset file
        Set<String> wss = new HashSet<>();

        // Process all requested addons
        // TODO: We sound really index all the classes in addons and build an
        // index based on that. Now we just use the jar name
        List<Addon> includedAddons = new ArrayList<>();
        for (WidgetInfo ci : info.getEager()) {
            if (ci.getFqn().startsWith("addon:")) {
                Addon match = findAddon(ci.getFqn().substring(6), ci.getVersion(), allAddons);
                if (match != null) {
                    includedAddons.add(match);
                } else {
                    Logger.getLogger(WSCompilerService.class.getName()).log(Level.WARNING, "Addon not found " + ci.getFqn().substring(6));
                    
                }
            }
        }
        for (Addon addon : includedAddons) {
            cp.add(addon.getJarFile());
            wss.addAll(addon.getWidgetsets());
        }
        wss.add("com.vaadin.DefaultWidgetSet");

        // Generate WidgetSetInfo XML
        try {
            generateGwtXml(GEN_SRC_DIR, wsName, wss);
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
    
    private void generateConnectorBundleLoaderFactory(File srcDir, List<WidgetInfo> eagerWidgets) {

        // Fill the template for eager connectors
        StringBuilder eagerStr = new StringBuilder();
        for (WidgetInfo w : eagerWidgets) {
            eagerStr.append(EAGER_CONNECTOR_TEMPLATE.replace(EAGER_CLASSNAME_PLACEHOLDER, w.getFqn()));
        }

        // Copy file and add/replace eager connectors
        try {
            BufferedReader r = new BufferedReader(new InputStreamReader(WSCompilerService.class.getResourceAsStream(CONNECTOR_LOADER_TEMPLATE)));
            PrintStream w = new PrintStream(new File(srcDir, CONNECTOR_LOADER_CLASSNAME + ".java"));
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
    }
    
    private void generateGwtXml(File widgetsetDir, String wsName,
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
    
    private Set<String> getIncludeWidgetsets(List<Addon> includeAddons) {
        Set<String> widgetsets = new HashSet<String>();
        if (includeAddons != null) {
            for (Addon addon : includeAddons) {
                widgetsets.addAll(addon.getWidgetsets());
            }
        }
        widgetsets.add("com.vaadin.DefaultWidgetSet");
        return widgetsets;
    }
    
    public static List<File> getCoreJars(File vaadinCoreDir) {
        File[] jars = vaadinCoreDir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        return Arrays.asList(jars);
    }
    
    public static List<Addon> getAllAddonWidgetSets(File dir) {
        List<Addon> addons = new ArrayList<Addon>();
        File[] jars = dir.listFiles(new FilenameFilter() {
            public boolean accept(File dir, String name) {
                return name.endsWith(".jar");
            }
        });
        for (File jar : jars) {
            Addon addon = Addon.readFrom(jar);
            if (addon != null) {
                addons.add(addon);
                Logger.getLogger(WSCompilerService.class.getName()).log(Level.INFO, "Found " + addon.name + " (" + addon.version + ")");
            }
        }
        Logger.getLogger(WSCompilerService.class.getName()).log(Level.INFO, "Found total " + addons.size() + " addons ");
        return addons;
    }
    
    private Addon findAddon(String name, String version, List<Addon> allAddons) {
        for (Addon a : allAddons) {
            if (a.getName().equals(name) && a.getVersion().equals(version)) {
                return a;
            }
        }
        return null;
    }
    
}
