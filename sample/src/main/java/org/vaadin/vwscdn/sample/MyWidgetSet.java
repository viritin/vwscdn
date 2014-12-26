package org.vaadin.vwscdn.sample;

import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.vaadin.vwscdn.client.DefaultWidgetSet;

import org.vaadin.vwscdn.annotations.WidgetSet;
import static org.vaadin.vwscdn.annotations.WidgetSetType.MANUAL;

@WidgetSet(MANUAL)
public class MyWidgetSet extends DefaultWidgetSet {

    public MyWidgetSet() {
        super();
        eager(Label.class);
        eager(TextField.class);
        addon("org.vaadin.virkki", "paperstack", "2.0.0");
        addon("com.vaadin.addon", "vaadin-charts", "1.1.7");
        addon("org.vaadin.addon","confirmdialog", "2.1.2");
        }
}
