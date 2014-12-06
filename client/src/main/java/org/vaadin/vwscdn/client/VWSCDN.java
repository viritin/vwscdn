/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.client;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.VaadinService;
import com.vaadin.shared.Version;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class VWSCDN {

    public static final String COMPILE_SERVICE_URL = "http://sami.app.fi/rws";

    private Client client;
    private WebTarget target;
    private final VaadinService service;

    public VWSCDN(VaadinService service) {
        this(service, COMPILE_SERVICE_URL);
    }

    public VWSCDN(VaadinService service, String vwscdnUrl) {
        this.client = ClientBuilder.newClient();
        vwscdnUrl = vwscdnUrl.endsWith("/") ? vwscdnUrl : vwscdnUrl + "/";
        this.target = client.target(vwscdnUrl + "api/compiler/compile");
        this.service = service;
    }

    public void useRemoteWidgetset(WidgetSetInfo info) {
        // Take the Vaadin version
        info.setVaadinVersion(Version.getFullVersion());

        // Get remote widgetset
        RemoteWidgetSet ws = getRemoteWidgetSet(info);

        // Rewrite the bootstrap            
        service.addSessionInitListener(new VWSCDN.SessionInitListener(ws));

    }

    public RemoteWidgetSet getRemoteWidgetSet(WidgetSetInfo info) {
        try {
            return target
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(info), RemoteWidgetSet.class);

        } catch (javax.ws.rs.NotFoundException ex) {
            Logger.getLogger(VWSCDN.class.getName()).log(Level.SEVERE, "Failed to connect service " + target.getUri() + "", ex);
        }
        return null;
    }

    /* Session initialization listener to override the javascript to load widgetset */
    public static class SessionInitListener implements com.vaadin.server.SessionInitListener {

        private final RemoteWidgetSet ws;

        public SessionInitListener(RemoteWidgetSet ws) {
            this.ws = ws;
        }

        @Override
        public void sessionInit(SessionInitEvent event) {
            event.getSession().addBootstrapListener(new BootstrapListener() {

                @Override
                public void modifyBootstrapFragment(BootstrapFragmentResponse response) {
                }

                @Override
                public void modifyBootstrapPage(BootstrapPageResponse response) {
                    // Update the bootstrap page
                    Document document = response.getDocument();
                    Element scriptTag = document.getElementsByTag("script").last();
                    String script = scriptTag.html();
                    scriptTag.html("");
                    script = script.replaceAll("\"widgetset\": \".*\"", "\"widgetset\": \"" + ws.getWidgetSetName() + "\"");
                    script = script.replace("});", ",\"widgetsetUrl\":\"" + ws.getWidgetSetUrl() + "\"});");
                    scriptTag.appendChild(new DataNode(script, scriptTag.baseUri()));

                }
            });
        }

    }

}
