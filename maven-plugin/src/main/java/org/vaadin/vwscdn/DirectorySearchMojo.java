package org.vaadin.vwscdn;

import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.vaadin.vwscdn.directory.Addon;
import org.vaadin.vwscdn.directory.Directory;

/**
 *
 * @goal dir
 */
@Mojo(name = "dir")
public class DirectorySearchMojo extends AbstractMojo {

    /**
     * @parameter expression="${search}"
     */
    private String search;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (search == null) {
            throw new MojoFailureException("Missing search parameter.");
        }
        List<Addon> list = Directory.listAll();
        for (Addon a : list) {
            if (a.getSummary() != null && a.getSummary().toLowerCase().contains(search.toLowerCase())
                    || (a.getName().toLowerCase().contains(search.toLowerCase()))) {
                System.out.println(a.getName() + ": " + a.getLinkUrl() + " '" + a.getSummary() + "' (Rating: " + a.getAvgRating() + "/5)");
            }
        }

    }

}
