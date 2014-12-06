package org.vaadin.vwscdn.client;

import javax.xml.bind.annotation.XmlTransient;

public class AddonInfo {

    private String groupId;
    private String artifactId;
    private String version;

    public AddonInfo() {    
    }
    
    public AddonInfo(String groupId, String artifactId, String version) {
        this.groupId = groupId;
        this.artifactId = artifactId;
        this.version = version;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public String getArtifactId() {
        return artifactId;
    }

    public void setArtifactId(String artifactId) {
        this.artifactId = artifactId;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    @XmlTransient
    public String getFullMavenId() {
        return groupId + "." + artifactId + "-" + version;
    }

    @XmlTransient
    public String getMavenPomSnippet() {
        StringBuilder b = new StringBuilder();
        b.append("<dependency><groupId>");
        b.append(getGroupId());
        b.append("</groupId><artifactId>");
        b.append(getArtifactId());
        b.append("</artifactId><version>");
        b.append(getVersion());
        b.append("</version></dependency>");
        return b.toString();
    }
}
