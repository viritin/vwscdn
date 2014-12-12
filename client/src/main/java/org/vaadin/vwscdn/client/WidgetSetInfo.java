package org.vaadin.vwscdn.client;

import com.vaadin.ui.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 *
 * @author se
 */
public class WidgetSetInfo {

    String vaadinVersion;
    List<String> eager;
    List<AddonInfo> addons;

    public WidgetSetInfo() {
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

    /* Create the info */
    static public WidgetSetInfo create(String vaadinVersion, AddonInfo... addonInfos) {
        WidgetSetInfo info = new WidgetSetInfo();
        info.setVaadinVersion(vaadinVersion);
        info.setAddons(Arrays.asList(addonInfos));
        return info;
    }

    public WidgetSetInfo eager(Class<? extends Component> componentClass) {
        return eager(componentClass.getCanonicalName());
    }

    public WidgetSetInfo eager(String componentFqn) {
        if (eager == null) {
            eager = new ArrayList<>();
        }
        eager.add(componentFqn);
        return this;
    }

    public WidgetSetInfo addon(AddonInfo addonInfo) {
        if (addons == null) {
            addons = new ArrayList<>();
        }
        addons.add(addonInfo);
        return this;
    }

    public WidgetSetInfo addon(String groupId, String artifactId, String version) {
        return addon(new AddonInfo(groupId, artifactId, version));
    }

}
