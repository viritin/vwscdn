package org.vaadin.vwscdn;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import org.apache.maven.model.Dependency;
import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;

public class PomUtils {

    /**
     * Load pom.xml form this project.
     *
     * @param project
     * @return
     */
    public static Model getProjectPom(MavenProject project) {
        try {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            String pomLibFile = project.getFile().getAbsolutePath();
            return reader.read(new FileReader(pomLibFile));
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PomUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException | XmlPullParserException ex) {
            Logger.getLogger(PomUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
        return null;
    }

    /**
     * Save/replace this projects pom.xml.
     *
     * @param project
     * @param pomModel
     */
    public static void saveToProject(MavenProject project, Model pomModel) {
        try {
            new MavenXpp3Writer().write(new FileOutputStream(project.getFile()), pomModel);
        } catch (FileNotFoundException ex) {
            Logger.getLogger(PomUtils.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(PomUtils.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Find a dependency in pom.
     *
     * @param pomModel
     * @param groupId
     * @param artifactId
     * @return
     */
    public static Dependency findDependency(Model pomModel, String groupId, String artifactId) {
        List<Dependency> deps = pomModel.getDependencies();
        for (Dependency dep : deps) {
            if (dep.getArtifactId().equals(artifactId) && dep.getGroupId().equals(groupId)) {
                return dep;
            }
        }
        return null;
    }

    /**
     * Adds a new dependency or updates the version of existing one.
     *
     * @param pomModel
     * @param groupId
     * @param artifactId
     * @param version
     */
    public static void addOrReplaceDependency(Model pomModel, String groupId, String artifactId, String version) {
        Dependency dep = findDependency(pomModel, groupId, artifactId);
        if (dep == null) {
            dep = new Dependency();
            dep.setGroupId(groupId);
            dep.setArtifactId(artifactId);
            dep.setVersion(version);
            pomModel.getDependencies().add(dep);
        } else {
            dep.setVersion(version);
        }
    }
}
