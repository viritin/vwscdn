package in.virit.vwscdn.directory;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.ArrayList;
import java.util.List;
import javax.ws.rs.Produces;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.ext.ContextResolver;
import javax.ws.rs.ext.Provider;

/**
 * Remote API to directory.
 *
 */
public class Directory {

    private static String DIRECTORY_API = "https://vaadin.com/Directory/resource/addon/all";
    private static String QUERY_DETAILS = "detailed";
    private static String QUERY_VAADIN = "vaadin";

    /**
     * List all addons from Directory.
     *
     * @param vaadinVersion
     * @return
     */
    public static List<Addon> listAll(final String vaadinVersion) {

        Client client = ClientBuilder.newClient();
        client.register(JsonConfig.class);

        WebTarget target = client.target(DIRECTORY_API);
        Addons result = target
                .queryParam(QUERY_DETAILS, true)
                .queryParam(QUERY_VAADIN, vaadinVersion)
                .request(MediaType.APPLICATION_JSON)
                .get(Addons.class);

        return result.getAddon();
    }

    public static List<Addon> search(final String vaadinVersion, final String search) {
        List<Addon> list = Directory.listAll(vaadinVersion);
        List<Addon> matches = new ArrayList<>();
        for (Addon a : list) {
            if (a.getSummary() != null && a.getSummary().toLowerCase().contains(search.toLowerCase())
                    || (a.getName().toLowerCase().contains(search.toLowerCase()))) {
                matches.add(a);
            }
        }
        return matches;
    }

    /** ObjectMapper configuration. 
     * 
     * We need to configure the ObjectMapper to allow single value 
     * as array for add-on licenses.
     * 
     */
    @Provider
    @Produces({MediaType.APPLICATION_JSON})
    public static class JsonConfig implements ContextResolver<ObjectMapper> {

        private static final ObjectMapper mapper = new ObjectMapper();

        static {
            mapper.configure(DeserializationFeature.ACCEPT_SINGLE_VALUE_AS_ARRAY, true);
        }

        @Override
        public ObjectMapper getContext(Class<?> type) {
            return mapper;
        }
    }

}
