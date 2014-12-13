package org.vaadin.vwscdn.sample;

import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import org.vaadin.vwscdn.client.WidgetSet;
import org.vaadin.vwscdn.client.DefaultWidgetSetService;

public class CustomWidgetSetService extends DefaultWidgetSetService {

    @Override
    public WidgetSet create() {
        return WidgetSet.create()
                .eager(Label.class)
                .eager(TextField.class)
                .addon("org.vaadin.virkki", "paperstack", "2.0.0")
                .addon("com.vaadin.addon", "vaadin-charts", "1.1.7");
    }
}
