package in.virit.vwscdn.sample;

import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import in.virit.vwscdn.client.DefaultWidgetSet;

import in.virit.vwscdn.annotations.WidgetSet;
import static in.virit.vwscdn.annotations.WidgetSetType.MANUAL;

/**
 * This is a sample manually managed widgetset.
 *
 * Typically not needed, but if you wish to use vwscdn without the maven plugin
 * you need to do this by hand.
 *
 * @author Sami Ekblad
 */
@WidgetSet(MANUAL)
public class MyWidgetSet extends DefaultWidgetSet {

    public MyWidgetSet() {
        super();
        eager(Label.class);
        eager(TextField.class);
        addon("org.vaadin.virkki", "paperstack", "2.0.0");
        addon("com.vaadin.addon", "vaadin-charts", "1.1.7");
        addon("org.vaadin.addon", "confirmdialog", "2.1.2");
    }
}
