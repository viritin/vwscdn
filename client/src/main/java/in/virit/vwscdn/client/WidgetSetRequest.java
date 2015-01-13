package in.virit.vwscdn.client;

import com.vaadin.ui.Component;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.SortedSet;
import java.util.TreeSet;

public class WidgetSetRequest {

    String vaadinVersion;
    List<String> eager;
    List<AddonInfo> addons;

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
        return "WidgetSetRequest{" + ", vaadinVersion=" + vaadinVersion + ", eager=" + eager + ", addons=" + addons + '}';
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

        return "{vaadinVersion=" + vaadinVersion + ", eager=" + eagerNames + ", addons=" + addonNames + '}';
    }

}
