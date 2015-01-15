package in.virit.vwscdn.client;

import com.vaadin.server.VaadinService;
import com.vaadin.shared.Version;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

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
    }

    public WidgetSetResponse queryRemoteWidgetSet(WidgetSetRequest request, boolean asynchronous) {
        try {
            return target.queryParam(QUERY_PARAM_ASYNC_COMPILE, asynchronous)
                    .request(MediaType.APPLICATION_JSON)
                    .post(Entity.json(request), WidgetSetResponse.class);

        } catch (Exception ex) {
            Logger.getLogger(Connection.class.getName()).log(Level.SEVERE, "Failed to connect service " + target.getUri() + "", ex);
        }
        return null;
    }

}
