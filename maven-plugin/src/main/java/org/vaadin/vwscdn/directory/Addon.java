package org.vaadin.vwscdn.directory;

import java.util.Map;
import javax.annotation.Generated;

@Generated("org.jsonschema2pojo")
public class Addon {

    private String avgRating;
    private String linkUrl;
    private String name;
    private String proAccount;
    private String summary;

    /**
     *
     * @return The avgRating
     */
    public String getAvgRating() {
        return avgRating;
    }

    /**
     *
     * @param avgRating The avgRating
     */
    public void setAvgRating(String avgRating) {
        this.avgRating = avgRating;
    }

    /**
     *
     * @return The linkUrl
     */
    public String getLinkUrl() {
        return linkUrl;
    }

    /**
     *
     * @param linkUrl The linkUrl
     */
    public void setLinkUrl(String linkUrl) {
        this.linkUrl = linkUrl;
    }

    /**
     *
     * @return The name
     */
    public String getName() {
        return name;
    }

    /**
     *
     * @param name The name
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     *
     * @return The proAccount
     */
    public String getProAccount() {
        return proAccount;
    }

    /**
     *
     * @param proAccount The proAccount
     */
    public void setProAccount(String proAccount) {
        this.proAccount = proAccount;
    }

    /**
     *
     * @return The summary
     */
    public String getSummary() {
        return summary;
    }

    /**
     *
     * @param summary The summary
     */
    public void setSummary(String summary) {
        this.summary = summary;
    }

    @Override
    public String toString() {
        return "Addon{" + "avgRating=" + avgRating + ", linkUrl=" + linkUrl + ", name=" + name + ", proAccount=" + proAccount + ", summary=" + summary + '}';
    }

}
