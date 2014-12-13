package org.vaadin.vwscdn.client;

/**
 * WidgetSet service API.
 */
public interface WidgetSetService {

    /* Create new widgetset. */
    public WidgetSet create();

    /* Create and init new widgetset with current VaadinService. */
    public void init();

}
