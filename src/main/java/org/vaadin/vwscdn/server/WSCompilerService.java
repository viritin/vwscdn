/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.server;

import org.vaadin.vwscdn.shared.WidgetSetInfo;
import java.io.File;
import java.security.MessageDigest;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;

import javax.ws.rs.Produces;
import org.vaadin.vwscdn.compiler.MavenWsCompiler;
import org.vaadin.vwscdn.shared.AddonInfo;
import org.vaadin.vwscdn.shared.VSWCDNConfig;
import org.vaadin.vwscdn.shared.RemoteWidgetSet;

/**
 *
 *
 * @author se
 */
@Path("/vwscdn")
public class WSCompilerService {

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
            try {
                //Old one: widgetset = WidgetSetCompiler.compileWidgetset(id, info);
                widgetset = MavenWsCompiler.compileWidgetSet(id, info, ServerConfig.COMPILER_ROOT_DIR, ServerConfig.PUBLIC_ROOT_DIR);
            } catch (Exception ex) {
                Logger.getLogger(WSCompilerService.class.getName()).log(Level.SEVERE, null, ex);
                widgetset = null;
            }
        }

        // Return a URL to the public widgetset
        if (widgetset != null) {
            RemoteWidgetSet res = new RemoteWidgetSet();
            res.setWidgetSetName(widgetset);
            res.setWidgetSetUrl(VSWCDNConfig.WS_BASE_URL + widgetset + "/" + widgetset + ".nocache.js");
            return res;
        }

        // This should never happen
        RemoteWidgetSet res = new RemoteWidgetSet();
        res.setWidgetSetName("Failed to compile widgetset '" + widgetset + "'");
        return res;
    }

    private String buildId(WidgetSetInfo info) {
        StringBuilder hash = new StringBuilder();
        hash.append(info.getVaadinVersion());
        for (AddonInfo ci : info.getAddons()) {
            String fqn = ci.getFullMavenId();
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
        File wsFile = new File(ServerConfig.PUBLIC_ROOT_DIR, wsName);
        return (wsFile.exists() && wsFile.canRead()) ? wsName : null;
    }

}
