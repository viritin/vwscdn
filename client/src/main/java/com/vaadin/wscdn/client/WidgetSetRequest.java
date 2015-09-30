package com.vaadin.wscdn.client;

import com.vaadin.ui.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class WidgetSetRequest {

    public static final String COMPILE_STYLE_OBFUSCATED = "OBF";
    public static final String COMPILE_STYLE_PRETTY = "PRETTY";
    public static final String COMPILE_STYLE_DETAILED = "DETAILED";

    String vaadinVersion;
    List<String> eager;
    List<AddonInfo> addons;
    String compileStyle = COMPILE_STYLE_OBFUSCATED;

    public WidgetSetRequest() {
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

    public String getCompileStyle() {
        return compileStyle;
    }

    public void setCompileStyle(String compileStyle) {
        this.compileStyle = compileStyle;
    }

    /**
     * Create a new info.
     *
     * @param style
     * @param vaadinVersion
     * @param addonInfos
     * @return
     */
    static public WidgetSetRequest create(String style, String vaadinVersion, AddonInfo... addonInfos) {
        WidgetSetRequest info = new WidgetSetRequest();
        info.setVaadinVersion(vaadinVersion);
        info.setCompileStyle(style);
        info.setAddons(Arrays.asList(addonInfos));
        return info;
    }

    /**
     * Create a new info.
     *
     * @param vaadinVersion
     * @param addonInfos
     * @return *
     */
    static public WidgetSetRequest create(String vaadinVersion, AddonInfo... addonInfos) {
        return create(COMPILE_STYLE_OBFUSCATED, vaadinVersion, addonInfos);
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

    public WidgetSetRequest vaadin(String vaadinVersion) {
        setVaadinVersion(vaadinVersion);
        return this;
    }

    public WidgetSetRequest style(String compileStyle) {
        setCompileStyle(compileStyle);
        return this;
    }

    @Override
    public String toString() {
        return "WidgetSetRequest{style=" + compileStyle + ", vaadinVersion=" + vaadinVersion + ", eager=" + eager + ", addons=" + addons + '}';
    }

    /* Unique and human-readable string defining the widgetset. */
    public String toWidgetsetString() {
        SortedSet<String> eagerNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (eager != null) {
            eagerNames.addAll(eager);
        }

        SortedSet<String> addonNames = new TreeSet<>(String.CASE_INSENSITIVE_ORDER);
        if (addons != null) {
            for (AddonInfo ci : addons) {
                addonNames.add(ci.toFullMavenId());
            }
        }

        return "{style=" + compileStyle + ", vaadinVersion=" + vaadinVersion + ", eager=" + eagerNames + ", addons=" + addonNames + '}';
    }

}
