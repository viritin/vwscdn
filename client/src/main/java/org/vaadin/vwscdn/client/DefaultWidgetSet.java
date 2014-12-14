package org.vaadin.vwscdn.client;

import org.vaadin.vwscdn.annotations.WidgetSet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import org.vaadin.vwscdn.annotations.WidgetSetType;

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
        init((VaadinServletService) VaadinServletService.getCurrent(), null);
    }

    /**
     * Init with current VaadinServletService.
     */
    @Override
    public void init(String serviceUrl) {
        init((VaadinServletService) VaadinServletService.getCurrent(), serviceUrl);
    }

    public void init(VaadinServletService service, String serviceUrl) {
        Connection vwscdn = new Connection(service, serviceUrl);
        vwscdn.useRemoteWidgetset(wsRequest);
    }

    @Override
    public void init(UI ui) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
