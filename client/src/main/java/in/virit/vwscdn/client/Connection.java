package in.virit.vwscdn.client;

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

public class Connection {

    public static final String COMPILE_SERVICE_URL = "http://cdn.virit.in";
    public static final String COMPILE_SERVICE_URL_LOCAL = "http://localhost:8080/vwscdn";
    public static final String PARAM_VWSCDN_LOCAL = "vwscdn.local";
    public static final String QUERY_PARAM_ASYNC_COMPILE = "compile.async";

    public static String getDefaultServiceUrl() {
        return System.getProperty(PARAM_VWSCDN_LOCAL) != null ? COMPILE_SERVICE_URL_LOCAL : COMPILE_SERVICE_URL;
    }

    private Client client;
    private WebTarget target;
    private final VaadinService service;

    public Connection() {
        this(null, getDefaultServiceUrl());
    }

    public Connection(VaadinService service) {
        this(service, getDefaultServiceUrl());
    }

    public Connection(VaadinService service, String vwscdnUrl) {
        vwscdnUrl = vwscdnUrl == null ? getDefaultServiceUrl() : vwscdnUrl;
        this.client = ClientBuilder.newClient();
        vwscdnUrl = vwscdnUrl.endsWith("/") ? vwscdnUrl : vwscdnUrl + "/";
        this.target = client.target(vwscdnUrl + "api/compiler/compile");
        this.service = service;
    }

    public WidgetSetResponse useRemoteWidgetset(WidgetSetRequest info) {

        //Sanity check
        if (service == null) {
            throw new IllegalArgumentException("VaadinService cannot be null when initializing remote widgetset.");
        }

        // Take the Vaadin version
        info.setVaadinVersion(Version.getFullVersion());

        // Get remote widgetset
        WidgetSetResponse ws = getRemoteWidgetSet(info, false);

        // Rewrite the bootstrap
        service.addSessionInitListener(new Connection.SessionInitListener(ws));

        return ws;

    }

    public WidgetSetResponse getRemoteWidgetSet(WidgetSetRequest request, boolean asynchronous) {
        try {
            return target.queryParam(QUERY_PARAM_ASYNC_COMPILE, asynchronous)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(request), WidgetSetResponse.class);

        } catch (Exception ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, "Failed to connect service " + target.getUri() + "", ex);
        }
        return null;
    }

    /* Session initialization listener to override the javascript to load widgetset */
    public static class SessionInitListener implements com.vaadin.server.SessionInitListener {

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

}
