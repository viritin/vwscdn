package in.virit.vwscdn.directory;

import java.util.Date;
import java.util.List;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Addon {

    /* basic */
    private String avgRating;
    private String linkUrl;
    private String name;
    private String proAccount;
    private String summary;

    /* details */
    private String groupId;
    private String artifactId;
    private String version;
    private List<License> licenses;
    private String maturity;
    private Date oldestRelease;
    private Date released;

    
    public Addon() {        
    }
    
    public String getAvgRating() {
        return avgRating;
    }

    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    public String getLinkUrl() {
        return linkUrl;
    }

    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProAccount() {
        return proAccount;
    }

    public void setProAccount(String proAccount) {
        this.proAccount = proAccount;
    }

    public String getSummary() {
        return summary;
    }

    public void setSummary(String summary) {
        this.summary = summary;
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

    public List<License> getLicenses() {
        return licenses;
    }

    public void setLicenses(List<License> licenses) {
        this.licenses = licenses;
    }

    public String getMaturity() {
        return maturity;
    }

    public void setMaturity(String maturity) {
        this.maturity = maturity;
    }

    public Date getOldestRelease() {
        return oldestRelease;
    }

    public void setOldestRelease(Date oldestRelease) {
        this.oldestRelease = oldestRelease;
    }

    public Date getReleased() {
        return released;
    }

    public void setReleased(Date released) {
        this.released = released;
    }

}
