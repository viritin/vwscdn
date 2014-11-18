/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.shared;

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
}
