/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.shared;

import java.util.ArrayList;
import java.util.Arrays;
import org.vaadin.vwscdn.shared.WidgetInfo;
import java.util.List;

/**
 *
 * @author se
 */
public class WidgetSetInfo {

    String vaadinVersion;
    List<WidgetInfo> eager;
    List<WidgetInfo> lazy;

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

    public void setEager(List<WidgetInfo> eager) {
        this.eager = eager;
    }

    public List<WidgetInfo> getLazy() {
        return lazy;
    }

    public void setLazy(List<WidgetInfo> lazy) {
        this.lazy = lazy;
    }

    /* Create the info */
    static public WidgetSetInfo create(String vaadinVersion, WidgetInfo... componentInfo) {
        WidgetSetInfo info = new WidgetSetInfo();
        info.setVaadinVersion(vaadinVersion);
        info.setLazy(Arrays.asList(componentInfo));
        return info;
    }

    public WidgetSetInfo lazy(WidgetInfo widgetInfo) {
        if (lazy == null) {
            lazy = new ArrayList<>();
        }
        lazy.add(widgetInfo);
        return this;
    }

    public WidgetSetInfo eager(WidgetInfo widgetInfo) {
        if (eager == null) {
            eager = new ArrayList<>();
        }
        eager.add(widgetInfo);
        return this;
    }

    public WidgetSetInfo add(WidgetInfo widgetInfo) {
        return lazy(widgetInfo);
    }

    public WidgetSetInfo add(WidgetInfo widgetInfo, boolean eager) {
        return eager ? eager(widgetInfo) : lazy(widgetInfo);
    }

}
