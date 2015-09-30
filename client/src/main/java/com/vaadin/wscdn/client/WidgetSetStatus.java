package com.vaadin.wscdn.client;

import java.util.Date;
import java.util.List;

public class WidgetSetStatus {

    private PublishState publishState = PublishState.NOT_FOUND;
    private String id;
    private String info;
    private String compileHost;
    private Date compileStartTime;
    private Date compileEndTime;
    private long compileTime;
    private String compileStyle;
    private String vaadinVersion;
    private List<AddonInfo> addons;
    private String publicUrl;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Date getCompileStartTime() {
        return compileStartTime;
    }

    public void setCompileStartTime(Date compileStartTime) {
        this.compileStartTime = compileStartTime;
    }

    public Date getCompileEndTime() {
        return compileEndTime;
    }

    public void setCompileEndTime(Date compileEndTimeMs) {
        this.compileEndTime = compileEndTimeMs;
    }

    public long getCompileTime() {
        return compileTime;
    }

    public void setCompileTime(long compileTimeMs) {
        this.compileTime = compileTimeMs;
    }

    public String getInfo() {
        return info;
    }

    public void setInfo(String info) {
        this.info = info;
    }

    public String getCompileHost() {
        return compileHost;
    }

    public void setCompileHost(String compileHost) {
        this.compileHost = compileHost;
    }

    public PublishState getPublishState() {
        return publishState;
    }

    public void setPublishState(PublishState state) {
        this.publishState = state;
    }

    public String getCompileStyle() {
        return compileStyle;
    }

    public void setCompileStyle(String compileStyle) {
        this.compileStyle = compileStyle;
    }

    public String getVaadinVersion() {
        return vaadinVersion;
    }

    public void setVaadinVersion(String vaadinVersion) {
        this.vaadinVersion = vaadinVersion;
    }

    public List<AddonInfo> getAddons() {
        return addons;
    }

    public void setAddons(List<AddonInfo> addons) {
        this.addons = addons;
    }

    public String getPublicUrl() {
        return publicUrl;
    }

    public void setPublicUrl(String publicUrl) {
        this.publicUrl = publicUrl;
    }

}
