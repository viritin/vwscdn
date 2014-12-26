package org.vaadin.vwscdn;

import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.vaadin.vwscdn.directory.Addon;
import org.vaadin.vwscdn.directory.Directory;

/**
 *
 * @goal add
 */
public class DirectoryAddMojo extends AbstractMojo {

 
    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        List<Addon> list = Directory.listAll();
        for (Addon a : list) {
            System.out.println(a);
        }

    }

}
