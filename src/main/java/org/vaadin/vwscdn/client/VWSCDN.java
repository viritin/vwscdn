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
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.vaadin.vwscdn.shared.WidgetSetInfo;
import org.vaadin.vwscdn.shared.RemoteWidgetSet;


public class VWSCDN {

    private static final String DEFAULT_WIDGETSET_CDN_URL = "http://localhost:8080/vaadin-wscdn-1.0-SNAPSHOT/vwscdn/compile";

    private Client client;
    private WebTarget target;
    private final VaadinService service;

    public VWSCDN(VaadinService service) {
        this(service, DEFAULT_WIDGETSET_CDN_URL);
    }

    public VWSCDN(VaadinService service, String vwscdnUrl) {
        this.client = ClientBuilder.newClient();
        this.target = client.target(vwscdnUrl);
        this.service = service;

    }

    public void useRemoteWidgetset(WidgetSetInfo info) {

        // Get remote widgetset
        RemoteWidgetSet ws = getRemoteWidgetSet(info);

        // Rewrite the bootstrap            
        service.addSessionInitListener(new VWSCDN.SessionInitListener(ws));

    }

    public RemoteWidgetSet getRemoteWidgetSet(WidgetSetInfo info) {
        return target
                .request(MediaType.APPLICATION_JSON)
                .post(Entity.json(info), RemoteWidgetSet.class);
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