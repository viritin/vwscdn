package com.vaadin.wscdn.client;

import com.vaadin.ui.Component;
import com.vaadin.ui.UI;

/**
 * Runtime widget set configuration interface.
 *
 * Service URL and usage scope can be specified, but Vaadin version is
 * automatically bound to <code>com.vaadin.shared.Version</code> at runtime.
 */
public interface WidgetSetConfiguration {

    /**
     * Use with all Vaadin UIs.
     *
     * This registers to VaadinService directly using default service URL.
     */
    void init();

    /**
     * Use in a single Vaadin UI only.
     *
     * This uses default service URL.
     *
     * @param ui The UI where to use this widget set.
     */
    void init(UI ui);

    /**
     * Use the given service URL.
     *
     * @param serviceUrl
     */
    void init(String serviceUrl);

    /**
     * Add and add-on dependency to this Widget set.
     *
     * @param groupId Maven group id
     * @param artifactId Maven artifact id
     * @param version Maven version
     * @return Returns this instance
     */
    WidgetSetConfiguration addon(String groupId, String artifactId, String version);

    /**
     * Make this component to use "eager" loading mechanism.
     *
     * @param componentClass Vaadin component that should be loaded eagerly to
     * client-side.
     *
     * @return Returns this instance
     */
    WidgetSetConfiguration eager(Class<? extends Component> componentClass);

}
