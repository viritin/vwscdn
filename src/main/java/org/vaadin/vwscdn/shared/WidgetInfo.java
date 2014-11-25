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

    String fqn;
    String version;
    boolean lazy;

    public WidgetInfo() {
    }

    public WidgetInfo(Class<? extends Component> aClass) {
        this(aClass, false);
    }

    public WidgetInfo(Class<? extends Component> aClass, boolean lazy) {
        this.fqn = aClass.getCanonicalName();
        this.version = null;
        this.lazy = lazy;
    }

    public WidgetInfo(String fqn, String version) {
        this(fqn, version, false);
    }

    public WidgetInfo(String fqn, String version, boolean lazy) {
        this.fqn = fqn;
        this.version = version;
        this.lazy = lazy;
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
