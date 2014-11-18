package org.vaadin.vwscdn.sample;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import javax.servlet.ServletException;
import org.vaadin.vwscdn.client.WidgetSetCDNClient;
import org.vaadin.vwscdn.shared.WidgetInfo;
import org.vaadin.vwscdn.shared.RemoteWidgetSet;

@Theme("valo")
@SuppressWarnings("serial")
public class HelloWorldUI extends UI {

    private int clickCounter = 0;
    private Label clickCounterLabel;

    @WebServlet(value = {"/app/*", "/VAADIN/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = HelloWorldUI.class)
    public static class Servlet extends VaadinServlet {

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();

            //TODO: Here this is just hand crafted. Automate. Externalize.
            WidgetSetCDNClient c = new WidgetSetCDNClient();
            RemoteWidgetSet ws = c.getWidgetSetURL(VaadinServlet.PORTLET_CONTEXT, new WidgetInfo(TextField.class),
                    new WidgetInfo(Label.class), new WidgetInfo("addon:vaadin-charts","1.1.7"));

            // Get the widgetset            
            getService().addSessionInitListener(new WidgetSetCDNClient.SessionInitListener(ws));
        }

    }

    @Override
    protected void init(VaadinRequest request) {
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        layout.setSpacing(true);
        setContent(layout);

        layout.addComponent(new Label("Hello World!"));
        layout.addComponent(new Label("Greetings from server."));
        layout.addComponent(new Label("I have "
                + Runtime.getRuntime().availableProcessors()
                + " processors and "
                + (Runtime.getRuntime().totalMemory() / 1000000)
                + " MB total memory."));

        Button button = new Button("Click Me");
        button.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                clickCounter++;
                clickCounterLabel.setValue("Clicks: " + clickCounter);
                Notification.show("Thank you for clicking.");

            }
        });

        layout.addComponent(button);
        layout.addComponent(clickCounterLabel = new Label("Clicks: 0"));
    }

}
