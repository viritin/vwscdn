/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.vwscdn.shared;

import com.vaadin.ui.Component;

/**
 *
 * @author se
 */
public class WidgetInfo {

    private String fqn;
    private String version;

    public WidgetInfo() {
    }

    public WidgetInfo(Class<? extends Component> aClass) {
        this.fqn = aClass.getCanonicalName();
        this.version = null;
    }

    public WidgetInfo(String fqn, String version) {
        this.fqn = fqn;
        this.version = version;
    }

    public String getFqn() {
        return fqn;
    }

    public void setFqn(String fqn) {
        this.fqn = fqn;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

}
