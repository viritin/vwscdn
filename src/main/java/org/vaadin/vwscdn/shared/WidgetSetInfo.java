/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.shared;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author se
 */
public class WidgetSetInfo {

    String vaadinVersion;
    List<WidgetInfo> eager;
    List<AddonInfo> addons;

    public WidgetSetInfo() {
    }

    public WidgetSetInfo(String vaadinVersion) {
        this.vaadinVersion = vaadinVersion;
    }

    public String getVaadinVersion() {
        return vaadinVersion;
    }

    public void setVaadinVersion(String vaadinVersion) {
        this.vaadinVersion = vaadinVersion;
    }

    public List<WidgetInfo> getEager() {
        return eager;
    }

    private void setEager(List<WidgetInfo> eager) {
        this.eager = eager;
    }

    public List<AddonInfo> getAddons() {
        return addons;
    }

    private void setAddons(List<AddonInfo> addons) {
        this.addons = addons;
    }

    /* Create the info */
    static public WidgetSetInfo create(String vaadinVersion, AddonInfo... addonInfos) {
        WidgetSetInfo info = new WidgetSetInfo();
        info.setVaadinVersion(vaadinVersion);
        info.setAddons(Arrays.asList(addonInfos));
        return info;
    }

    public WidgetSetInfo eager(WidgetInfo widgetInfo) {
        if (eager == null) {
            eager = new ArrayList<>();
        }
        eager.add(widgetInfo);
        return this;
    }

    public WidgetSetInfo addon(AddonInfo addonInfo) {
        if (addons == null) {
            addons = new ArrayList<>();
        }
        addons.add(addonInfo);
        return this;
    }



}
