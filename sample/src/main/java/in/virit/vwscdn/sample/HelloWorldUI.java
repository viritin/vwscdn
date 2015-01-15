package in.virit.vwscdn.sample;

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
import in.virit.vwscdn.client.WidgetSetConfiguration;
import org.vaadin.teemu.ratingstars.RatingStars;
import org.vaadin.teemu.switchui.Switch;

@Theme("valo")
@SuppressWarnings("serial")
public class HelloWorldUI extends UI {

    private int clickCounter = 0;
    private Label clickCounterLabel;

    @WebServlet(value = {"/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = HelloWorldUI.class)
    public static class Servlet extends VaadinServlet {    

        WidgetSetConfiguration ws = new MyWidgetSet();
        
        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();            
            // Uncomment this one to use your own MyWidgetSet
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

        stack.addComponent(new Label("This is PaperStack!"));
        Chart chart = new Chart(ChartType.PIE);
        chart.getConfiguration().addSeries(new DataSeries(new String[]{"A", "B"}, new Number[]{1, 2, 3, 4}));
        stack.addComponent(chart);
        layout.addComponent(clickCounterLabel = new Label("Clicks: 0"));
        layout.addComponent(new RatingStars());
        layout.addComponent(new Switch("TEEMU"));
    }

}