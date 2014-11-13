/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.cdn.rest;

import com.vaadin.tools.WidgetsetCompiler;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import javax.ws.rs.Produces;

/**
 *
 *
 * @author se
 */
@Path("/vwscdn")
public class WSCompilerService {

    //TODO: make this dynamic
    private String publicWidgetSetBaseURL = "http://localhost:8080/vaadin-wscdn-1.0-SNAPSHOT/ws/";

    //TODO: make this dynamic
    private String PUBLIC_ROOT = "/tmp/ws";

    public WSCompilerService() {
    }

    private static String NA = "error";

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
    public String compileWidgetSet(WidgetSetInfo info) {
        if (info == null) {
            return NA;
        }

        // Create hash from component data 
        String id = buildId(info);

        // Try to find an existing widgetset
        File widgetset = findPreCompiledWidgetset(id);

        // If not found, compile a new one
        if (widgetset == null) {
            widgetset = gwtCompile(id, info);
        }

        // Return a URL to the public widgetset
        if (widgetset != null) {
            return publicWidgetSetBaseURL + widgetset.getName();
        }

        // This should never happen
        return NA;
    }

    private String buildId(WidgetSetInfo info) {
        StringBuffer hash = new StringBuffer();
        for (ComponentInfo ci : info.getEager()) {
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

            StringBuffer hexString = new StringBuffer();

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

    private boolean runCompiler(String[] compilerParams) {
        try {
            com.google.gwt.dev.Compiler.main(compilerParams);
            return true;
        } catch (Exception ex) {
            Logger.getLogger(WSCompilerService.class.getName()).log(Level.SEVERE, null, ex);
        }
        return false;
    }

    private File findPreCompiledWidgetset(String id) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private File gwtCompile(String id, WidgetSetInfo info) {

        File wsFile = new File(PUBLIC_ROOT, id);

        // TODO: generate classpath
        // TODO: generate .gwt.xml
        // TODO: generate .gwt.xml
        
        System.setProperty("gwt.persistentunitcachedir", "/Users/se/NetBeansProjects/spreadsheet-charts/target");

        // Parameters
        String logLevel = "-logLevel INFO";
        String targetDir = "-war /Users/se/NetBeansProjects/spreadsheet-charts/src/main/webapp/VAADIN/widgetsets";
        String workers = "-localWorkers 4";
        String fragments = "-XfragmentCount -1";
        String deploy = "-deploy /Users/se/NetBeansProjects/spreadsheet-charts/target/gwt-deploy";
        String gen = "-gen /Users/se/NetBeansProjects/spreadsheet-charts/target/.generated fi.app.charts.gwt.DesktopWidgetSet";
        String[] compilerParams = new String[]{
            logLevel, targetDir, workers, fragments, deploy, gen
        };

        if (!runCompiler(compilerParams)) {
            return null;
        }
        return wsFile;
    }

}
