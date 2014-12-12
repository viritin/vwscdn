package org.vaadin.vwscdn.client;

import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.Component;

/**
 * Client entry point.
 */
public class WidgetSet {

    private final VWSCDN vwscdn;
    private final WidgetSetInfo ws;

    public static WidgetSet create() {
        return new WidgetSet((VaadinServletService) VaadinServletService.getCurrent());
    }

    public static WidgetSet create(String serviceUrl) {
        return new WidgetSet((VaadinServletService) VaadinServletService.getCurrent(),serviceUrl);
    }

    private WidgetSet(VaadinServletService service) {
        this(service, null);
    }

    private WidgetSet(VaadinServletService service, String serviceUrl) {
        this.vwscdn = new VWSCDN(service, serviceUrl);
        this.ws = new WidgetSetInfo();
    }

    public WidgetSet eager(Class<? extends Component> componentClass) {
        ws.eager(componentClass);
        return this;
    }

    public WidgetSet addon(String groupId, String artifactId, String version) {
        ws.addon(groupId, artifactId, version);
        return this;
    }

    public void init() {
        vwscdn.useRemoteWidgetset(ws);
    }

}
