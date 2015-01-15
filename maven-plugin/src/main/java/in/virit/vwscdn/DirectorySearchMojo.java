package in.virit.vwscdn;

import java.util.List;
import org.apache.maven.model.Model;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.apache.maven.project.MavenProject;
import in.virit.vwscdn.directory.Addon;
import in.virit.vwscdn.directory.Directory;

@Mojo(name = "dir")
public class DirectorySearchMojo extends AbstractMojo {

    @Parameter(property = "search")
    private String search;

    @Parameter(property = "add", defaultValue = "false")
    private boolean add;

    @Parameter(property = "project", defaultValue = "${project}", required = true, readonly = true)
    private MavenProject project;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        if (search == null) {
            throw new MojoFailureException("Missing search parameter.");
        }
        List<Addon> list = Directory.search("7", search);
        Model model = project.getModel();
        for (Addon a : list) {
            System.out.println(a.getName() + " - " + a.getSummary());
            System.out.println("\tRating: " + a.getAvgRating() + " / 5");
            if (a.getGroupId() == null || a.getArtifactId() == null) {
                System.out.print("\tMaven: n/a");
            } else {
                System.out.print("\tMaven: " + a.getGroupId() + ":" + a.getArtifactId() + ":" + a.getVersion());
            }
            if (PomUtils.findDependency(model, a.getGroupId(), a.getArtifactId()) != null) {
                System.out.println(" (in pom.xml)");
            } else if (add && a.getArtifactId() != null && a.getGroupId() != null) {
                PomUtils.addDependency(model, a.getGroupId(), a.getArtifactId(), a.getVersion());
                System.out.println(" (ADDED)");
            } else if (a.getArtifactId() != null && a.getGroupId() != null) {
                System.out.println(" (not present)");
            } else {
            }
        }
    }
}