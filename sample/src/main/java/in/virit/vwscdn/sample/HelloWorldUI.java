package in.virit.vwscdn.sample;

import javax.servlet.annotation.WebServlet;

import com.vaadin.annotations.Theme;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import javax.servlet.ServletException;
import com.vaadin.data.Property;
import org.vaadin.virkki.paperstack.PaperStack;
import in.virit.vwscdn.client.WidgetSetConfiguration;
import org.vaadin.teemu.ratingstars.RatingStars;

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
        
        PaperStack stack = new PaperStack();
        layout.addComponent(stack);
        
        stack.addComponent(new Label("This is PaperStack!"));
        stack.addComponent(clickCounterLabel = new Label("Ratings: 0"));
        RatingStars stars = new RatingStars();
        stars.setCaption("Rate me:");
        stars.addValueChangeListener(new Property.ValueChangeListener() {
            
            @Override
            public void valueChange(Property.ValueChangeEvent event) {
                clickCounterLabel.setValue("" + (++clickCounter));
            }
            
        });
        layout.addComponent(stars);
    }
    
}
