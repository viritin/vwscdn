package com.vaadin.wscdn.client;

import com.vaadin.server.VaadinService;
import com.vaadin.wscdn.annotations.WidgetSet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.shared.Version;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import com.vaadin.wscdn.annotations.WidgetSetType;

/**
 * Client entry point.
 */
@WidgetSet(WidgetSetType.DEFAULT)
public class DefaultWidgetSet implements WidgetSetConfiguration {

    private WidgetSetRequest wsRequest;

    public DefaultWidgetSet() {
        this.wsRequest = new WidgetSetRequest();
    }

    @Override
    public DefaultWidgetSet eager(Class<? extends Component> componentClass) {
        wsRequest.eager(componentClass);
        return this;
    }

    @Override
    public DefaultWidgetSet addon(String groupId, String artifactId, String version) {
        wsRequest.addon(groupId, artifactId, version);
        return this;
    }

    /**
     * Init with current VaadinServletService and default service url.
     */
    @Override
    public void init() {
        init((String) null);
    }

    /**
     * Init with current VaadinServletService.
     */
    @Override
    public void init(String serviceUrl) {
        Connection vwscdn = new Connection(VaadinServletService.getCurrent(), serviceUrl);
        useRemoteWidgetset(VaadinServletService.getCurrent(), wsRequest, vwscdn);
    }

    public void initWithResponse(WidgetSetResponse ws) {
        VaadinServletService.getCurrent().addSessionInitListener(new SessionInitListener(ws));
    }

    @Override
    public void init(UI ui) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    private WidgetSetResponse useRemoteWidgetset(VaadinService service, WidgetSetRequest info, Connection connection) {
        if (service == null) {
            throw new IllegalArgumentException("VaadinService cannot be null when initializing remote widgetset.");
        }
        info.setVaadinVersion(Version.getFullVersion());
        WidgetSetResponse ws = connection.queryRemoteWidgetSet(info, false);
        service.addSessionInitListener(new SessionInitListener(ws));
        return ws;
    }

}
