package in.virit.vwscdn.client;

import in.virit.vwscdn.annotations.WidgetSet;
import com.vaadin.server.VaadinServletService;
import com.vaadin.ui.Component;
import com.vaadin.ui.UI;
import in.virit.vwscdn.annotations.WidgetSetType;

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
        vwscdn.useRemoteWidgetset(wsRequest);
    }

    public void initWithResponse(WidgetSetResponse ws) {
        VaadinServletService.getCurrent().addSessionInitListener(new Connection.SessionInitListener(ws));
    }

    @Override
    public void init(UI ui) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

}
