package org.vaadin.vwscdn.sample;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import javax.servlet.ServletException;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.DataSeries;
import org.vaadin.addons.idle.Idle;
import org.vaadin.virkki.paperstack.PaperStack;
import org.vaadin.vwscdn.client.WidgetSetConfiguration;

@Theme("valo")
@SuppressWarnings("serial")
public class HelloWorldUI extends UI {

    private int clickCounter = 0;
    private Label clickCounterLabel;

    @WebServlet(value = {"/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = HelloWorldUI.class)
    public static class Servlet extends VaadinServlet {

        // TODO: It should be possible to inject this.
        // And also possible to choose DEFAULT, MANUAL or GENERATED version
        // using the org.vaadin.vwscdn.annotations.WidgetSetType
        
        
        // This is automatically generated widgetset, it is automatically registered as well
        // WidgetSetConfiguration ws = new org.vaadin.vwscdn.GeneratedWidgetSet();
        
        // This is a manually created/edited widgetset:
        // WidgetSetConfiguration ws = new MyWidgetSet();

        // This is the default widgetset without any addons:
        // WidgetSetConfiguration ws = new org.vaadin.vwscdn.client.DefaultWidgetSet();

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            // If you have manually defined widgetset 
            // TODO Maybe you'd want to use the weblistener there as well??
            // ws.init();
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        Idle.track(this, 1000, new Idle.Listener() {

            @Override
            public void userInactive() {
                Notification.show("Inactive");
            }

            @Override
            public void userActive() {
            }
        });
        layout.addComponent(new Label("Hello World!"));
        PaperStack stack = new PaperStack();
        layout.addComponent(stack);

        stack.addComponent(new Label("Testing PaperStack!"));
        Chart chart = new Chart(ChartType.PIE);
        chart.getConfiguration().addSeries(new DataSeries(new String[]{"A", "B"}, new Number[]{1, 2, 3, 4}));
        stack.addComponent(chart);
        layout.addComponent(clickCounterLabel = new Label("Clicks: 0"));
    }

}
