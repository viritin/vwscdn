/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.vaadin.wscdn.client;

import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.SessionInitEvent;
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

/* Session initialization listener to override the javascript to load widgetset */
public class SessionInitListener implements com.vaadin.server.SessionInitListener {
    private final WidgetSetResponse ws;

    public SessionInitListener(WidgetSetResponse ws) {
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
                if (ws != null && ws.getStatus() == PublishState.AVAILABLE) {
                    Document document = response.getDocument();
                    Element scriptTag = document.getElementsByTag("script").last();
                    String script = scriptTag.html();
                    scriptTag.html("");
                    script = script.replaceAll("\"widgetset\": \".*\"", "\"widgetset\": \"" + ws.getWidgetSetName() + "\"");
                    script = script.replace("});", ",\"widgetsetUrl\":\"" + ws.getWidgetSetUrl() + "\"});");
                    scriptTag.appendChild(new DataNode(script, scriptTag.baseUri()));
                }
            }
        });
    }

}
