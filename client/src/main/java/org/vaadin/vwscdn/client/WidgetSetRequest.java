package org.vaadin.vwscdn.client;

import com.vaadin.ui.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WidgetSetRequest {

    boolean asynchronous = false;

    String vaadinVersion;
    List<String> eager;
    List<AddonInfo> addons;

    public WidgetSetRequest() {
    }

    public boolean isAsynchronous() {
        return asynchronous;
    }

    public void setAsynchronous(boolean asynchronous) {
        this.asynchronous = asynchronous;
    }

    public String getVaadinVersion() {
        return vaadinVersion;
    }

    public void setVaadinVersion(String vaadinVersion) {
        this.vaadinVersion = vaadinVersion;
    }

    public List<String> getEager() {
        return eager;
    }

    private void setEager(List<String> eager) {
        this.eager = eager;
    }

    public List<AddonInfo> getAddons() {
        return addons;
    }

    private void setAddons(List<AddonInfo> addons) {
        this.addons = addons;
    }

    /* Create a new info. */
    static public WidgetSetRequest create(String vaadinVersion, AddonInfo... addonInfos) {
        WidgetSetRequest info = new WidgetSetRequest();
        info.setVaadinVersion(vaadinVersion);
        info.setAddons(Arrays.asList(addonInfos));
        return info;
    }

    public WidgetSetRequest eager(Class<? extends Component> componentClass) {
        return eager(componentClass.getCanonicalName());
    }

    public WidgetSetRequest eager(String componentFqn) {
        if (eager == null) {
            eager = new ArrayList<>();
        }
        eager.add(componentFqn);
        return this;
    }

    public WidgetSetRequest addon(AddonInfo addonInfo) {
        if (addons == null) {
            addons = new ArrayList<>();
        }
        addons.add(addonInfo);
        return this;
    }

    public WidgetSetRequest addon(String groupId, String artifactId, String version) {
        return addon(new AddonInfo(groupId, artifactId, version));
    }

    @Override
    public String toString() {
        return "WidgetSetRequest{" + "asynchronous=" + asynchronous + ", vaadinVersion=" + vaadinVersion + ", eager=" + eager + ", addons=" + addons + '}';
    }

    /* Unique and human-readable string defining the widgetset. */
    public String toWidgetsetString() {
        return "{vaadinVersion=" + vaadinVersion + ", eager=" + eager + ", addons=" + addons + '}';
    }
    
}
