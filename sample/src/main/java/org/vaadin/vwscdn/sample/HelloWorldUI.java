package org.vaadin.vwscdn.sample;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import javax.servlet.ServletException;
import com.vaadin.addon.charts.Chart;
import com.vaadin.addon.charts.model.ChartType;
import com.vaadin.addon.charts.model.DataSeries;
import org.vaadin.addons.idle.Idle;
import org.vaadin.virkki.paperstack.PaperStack;
import org.vaadin.vwscdn.client.WidgetSet;

@Theme("valo")
@SuppressWarnings("serial")
public class HelloWorldUI extends UI {

    private int clickCounter = 0;
    private Label clickCounterLabel;

    @WebServlet(value = {"/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = HelloWorldUI.class)
    public static class Servlet extends VaadinServlet {

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();

            // Intialize the widgetset. This might take a while at first run.
            WidgetSet.create()
                    .eager(TextField.class)
                    .eager(Label.class)
                    .addon("com.vaadin.addon", "vaadin-charts", "1.1.7")
                    .addon("org.vaadin.virkki", "paperstack", "2.0.0")
                    .addon("org.vaadin.addon", "idle", "1.0.1")
                    .init();
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
