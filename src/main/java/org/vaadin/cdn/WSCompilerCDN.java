/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.cdn;

import javax.annotation.PostConstruct;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import org.vaadin.cdn.rest.WidgetSetInfo;

/**
 *
 * @author se
 */
public class WSCompilerCDN {

    private Client client;
    private WebTarget target;

    public WSCompilerCDN() {
        client = ClientBuilder.newClient();
        target = client.target(
                "http://localhost:8080/webresources/wscdn/compileWidgetSet");
    }

    public String getCompiledWidgetset(WidgetSetInfo widgetsetInfo) {
        return target.queryParam("q", widgetsetInfo)
                .request(MediaType.APPLICATION_JSON)
                .get(String.class);
    }    
}
