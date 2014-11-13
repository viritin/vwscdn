/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.cdn.rest;

import java.util.List;

/**
 *
 * @author se
 */
public class WidgetSetInfo {
    
    String vaadinVersion;
    List<ComponentInfo> eager;
    List<ComponentInfo> lazy;

    public String getVaadinVersion() {
        return vaadinVersion;
    }

    public void setVaadinVersion(String vaadinVersion) {
        this.vaadinVersion = vaadinVersion;
    }

    public List<ComponentInfo> getEager() {
        return eager;
    }

    public void setEager(List<ComponentInfo> eager) {
        this.eager = eager;
    }

    public List<ComponentInfo> getLazy() {
        return lazy;
    }

    public void setLazy(List<ComponentInfo> lazy) {
        this.lazy = lazy;
    }
    
    
}
