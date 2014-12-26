package org.vaadin.vwscdn.directory;

import java.util.List;
import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;

/**
 * Remote API to directory.
 *
 */
public class Directory {

    private static String DIRECTORY_API = "https://vaadin.com/Directory/resource/addon/";

    /**
     * List all addons from Directory.
     */
    public static List<Addon> listAll() {
        Client client = ClientBuilder.newClient();
        WebTarget target = client.target(DIRECTORY_API + "all");
        Addons list = target
                .request(MediaType.APPLICATION_JSON)
                .get(Addons.class);

        return list.getAddon();
    }

}
