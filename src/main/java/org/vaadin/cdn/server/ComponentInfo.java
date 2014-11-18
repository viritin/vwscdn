/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.vaadin.cdn.server;

import com.vaadin.ui.Component;
import com.vaadin.ui.TextField;

/**
 *
 * @author se
 */
public class ComponentInfo {

    String fqn;
    String version;

    public ComponentInfo() {
    }

    public ComponentInfo(Class<? extends Component> aClass) {
        fqn = aClass.getCanonicalName();
        version = null;
    }

    public ComponentInfo(String fqn, String version) {
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
