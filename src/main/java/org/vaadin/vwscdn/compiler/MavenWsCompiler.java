/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.compiler;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintStream;
import org.vaadin.vwscdn.shared.WidgetSetInfo;

public class MavenWsCompiler {

    private final File baseDir;
    private final File pomTemplate;
    private File pomFile;
    private File src;
    private String widgetSetId;
    private final WidgetSetInfo widgetSetInfo;

    public MavenWsCompiler(File baseDir, File pomTemplate, String widgetSetId, WidgetSetInfo widgetSetInfo) {
        this.baseDir = baseDir;
        this.pomTemplate = pomTemplate;
        this.widgetSetId = widgetSetId;
        this.widgetSetInfo = widgetSetInfo;
    }

    private void initProjectStructure() throws IOException {
        src = new File(baseDir, "src");
        src.mkdir();
        pomFile = new File(src, "pom.xml");
        pomFile.createNewFile();
        copyPomFile(pomTemplate, pomFile, widgetSetId, widgetSetInfo);
    }

    private static void copyPomFile(File templatePom, File pomFile, String widgetsetId, WidgetSetInfo info) throws IOException {

        StringBuilder addonDeps = new StringBuilder();
        for (AddonInfo a: info.getAddOns())
        

        try (BufferedReader r = new BufferedReader(new FileReader(templatePom));
                PrintStream w = new PrintStream(pomFile)) {
            String l;
            while ((l = r.readLine()) != null) {
                w.print(l.replace("[WS_ID]", widgetsetId));
                w.print(l.replace("[VAADIN_VERSION]", info.getVaadinVersion()));
                w.print(l.replace("[COMPILE_STYLE]", "OBFUSCATED"));
                w.print(l.replace("[COMPILE_DRAFT]", "" + false));
                w.print(l.replace("<!-- ADDON_DEPS -->", addonDeps));
                w.print("\n");
            }
        }
    }

}
