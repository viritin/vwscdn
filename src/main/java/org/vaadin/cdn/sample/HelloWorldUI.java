package org.vaadin.cdn.sample;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.BootstrapFragmentResponse;
import com.vaadin.server.BootstrapListener;
import com.vaadin.server.BootstrapPageResponse;
import com.vaadin.server.SessionInitEvent;
import com.vaadin.server.SessionInitListener;
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
import org.jsoup.nodes.DataNode;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.vaadin.cdn.client.WidgetSetCDNClient;
import org.vaadin.cdn.server.ComponentInfo;
import org.vaadin.cdn.server.WidgetSetInfo;
import org.vaadin.cdn.shared.RemoteWidgetSet;

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
            RemoteWidgetSet ws = c.getWidgetSetURL("7.3.4", new ComponentInfo(TextField.class),
                    new ComponentInfo(Label.class), new ComponentInfo("addon:vaadin-charts","1.1.7"));

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
