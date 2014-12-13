package org.vaadin.vwscdn.client;

/**
 * Default widgetset service without any add-ons.
 *
 */
public class DefaultWidgetSetService implements WidgetSetService {

    @Override
    public WidgetSet create() {
        return WidgetSet.create();
    }

    @Override
    public void init() {
        create().init();
    }

}
