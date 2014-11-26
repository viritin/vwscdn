package org.vaadin.vwscdn.server;

import java.io.File;
import java.io.FilenameFilter;
import java.net.JarURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author se
 */
public class Addon {

    public static List getAllAddonWidgetSets(File dir) {
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
    final String name;
    final String version;
    private final File jarFile;
    private final List<String> widgetsets;

    public Addon(String name, String version, File jarFile, List<String> widgetsets) {
        this.name = name;
        this.version = version;
        this.jarFile = jarFile;
        this.widgetsets = widgetsets;
    }

    public String getName() {
        return name;
    }

    public String getVersion() {
        return version;
    }

    public File getJarFile() {
        return jarFile;
    }

    @Override
    public String toString() {
        return name + " " + version;
    }

    public List<String> getWidgetsets() {
        return widgetsets;
    }

    @Override
    public int hashCode() {
        int result = getClass().hashCode();
        result *= ((name == null) ? 1 : name.hashCode());
        result *= ((version == null) ? 1 : version.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        Addon other = (Addon) obj;
        if (name == null) {
            if (other.name != null) {
                return false;
            }
        } else if (!name.equals(other.name)) {
            return false;
        }
        if (version == null) {
            if (other.version != null) {
                return false;
            }
        } else if (!version.equals(other.version)) {
            return false;
        }
        return true;
    }

    static Addon readFrom(File file) {
        try {
            URL url = new URL("file:" + file.getCanonicalPath());
            url = new URL("jar:" + url.toExternalForm() + "!/");
            JarURLConnection conn = (JarURLConnection) url.openConnection();
            JarFile jarFile = conn.getJarFile();
            if (jarFile != null) {
                Manifest manifest = jarFile.getManifest();
                if (manifest == null) {
                    // No manifest so this is not a Vaadin Add-on
                    return null;
                }
                Attributes attrs = manifest.getMainAttributes();
                String value = attrs.getValue("Vaadin-Widgetsets");
                if (value != null) {
                    String name = attrs.getValue("Implementation-Title");
                    String version = attrs.getValue("Implementation-Version");
                    if (name == null || version == null) {
                        // A jar file with Vaadin-Widgetsets but name or version
                        // missing. Most probably vaadin.jar itself, skipping it
                        // here
                        return null;
                    }
                    List<String> widgetsets = new ArrayList<String>();
                    String[] widgetsetNames = value.split(",");
                    for (String wName : widgetsetNames) {
                        String widgetsetname = wName.trim().intern();
                        if (!widgetsetname.equals("")) {
                            widgetsets.add(widgetsetname);
                        }
                    }
                    if (!widgetsets.isEmpty()) {
                        return new Addon(name, version, file, widgetsets);
                    }
                }
            }
        } catch (Exception e) {
            Logger.getLogger(WSCompilerService.class.getName()).log(Level.WARNING, "Failed to include jar", e);
        }
        return null;
    }

    public static Addon findAddon(String name, String version, List<Addon> allAddons) {
        for (Addon a : allAddons) {
            if (a.getName().equals(name) && a.getVersion().equals(version)) {
                return a;
            }
        }
        return null;
    }
    
}
