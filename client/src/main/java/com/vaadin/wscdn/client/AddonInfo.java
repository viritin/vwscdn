package com.vaadin.wscdn.client;

import java.util.Objects;
import javax.xml.bind.annotation.XmlTransient;

/**
 * Vaadin add-on metadata.
 */
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
    public String toFullMavenId() {
        return groupId + "." + artifactId + "-" + version;
    }

    public String toMavenPomSnippet() {
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

    @Override
    public String toString() {
        return "{" + "groupId=" + groupId + ", artifactId=" + artifactId + ", version=" + version + '}';
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 41 * hash + Objects.hashCode(this.groupId);
        hash = 41 * hash + Objects.hashCode(this.artifactId);
        hash = 41 * hash + Objects.hashCode(this.version);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final AddonInfo other = (AddonInfo) obj;
        if (!Objects.equals(this.groupId, other.groupId)) {
            return false;
        }
        if (!Objects.equals(this.artifactId, other.artifactId)) {
            return false;
        }
        if (!Objects.equals(this.version, other.version)) {
            return false;
        }
        return true;
    }

    
}
