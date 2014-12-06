
Remote widget set compilation service
===

This project consists of four parts:
 - Widgetset compiler service
 - Widgetset resource serving servlet
 - Client library used in Vaadin application
 - Sample application
 
Running the project
---

To run the sample and service at localhost use:

    mvn -Dvwscdn.config=/path/to/working/directory/service-config.properties jetty:run


Service uses a working directory to build/publish widgetsets. By default it is the folder where the configuration 
file is, but different folder can be also specified in the config file.

Maven is needed for the compilation. Either install maven in the working directory to <workdir>/maven or
specify the installation in the configuration file.

Configuration sample:

    # Optional. By default parent directory of this config file is used. 
    # base.dir=/Users/se/rws
    
    # Optional. Public URL for the service. Used for URL generation.
    # By default the service context uri is used runtime.
    # service.url=http://localhost:8080/wscdn-service-2.0-SNAPSHOT/
      
    # Optional. By default base.dir/maven is used.
    maven.home=/usr/local/Cellar/maven/3.2.1/libexec
    
    #GWT compiler parameters
    compile.draft=false
    compile.localworkers=2
    compile.jvmargs=-Xmx512m


The widgetset compiler service
---
The service uses Maven to compile the requested widgetset on the fly. For configuration a 

Using the client
---

To use in the application add the following to your application Servlet class:


    @WebServlet(value = {"/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = HelloWorldUI.class)
    public static class Servlet extends VaadinServlet {

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();

            //TODO: Here this is just hand crafted for now. Automate. Externalize.
            WidgetSetInfo ws = new WidgetSetInfo()
                    .eager(new WidgetInfo(TextField.class))
                    .eager(new WidgetInfo(Label.class))
                    .addon(new AddonInfo("com.vaadin.addon", "vaadin-charts", "1.1.7"))
                    .addon(new AddonInfo("org.vaadin.virkki", "paperstack", "2.0.0"))
                    .addon(new AddonInfo("org.vaadin.addon", "idle", "1.0.1"));

            // Intialize the widgetset. This might take a while at first run.
            VWSCDN remote = new VWSCDN(getService(), "http://localhost:8080/vwscdn");
            remote.useRemoteWidgetset(ws);
        }
    }
