
Remote widget set compilation service
===

This project consists of parts:
 - Widgetset compiler service (+ servlet serving the compiled widgetsets)
 - Client library used in Vaadin application
 - Maven plugin that generates the widgetset configuration during project build
 - Maven archetype for creating easily new projects using the service
 - A sample application
 
Creating a new project that uses the service
---
With the Maven archetype you can easily create a project setup that uses the central compilation service.

    mvn archetype:generate -DarchetypeCatalog=http://virit.in/maven2/archetype-catalog.xml

This will prompt for project artifactId and groupId and generate a simple Vaadin application project.

Running the sample project
---

To run the sample at localhost use:

    cd sample
    mvn jetty:run

Or, if you run the service locally also:

    cd service
    mvn -Dvwscdn.config=/path/to/working/directory/service-config.properties -Dvwscdn.local wildfly:run

Service uses a working directory to build/publish widgetsets. By default it is the folder where the configuration 
file is, but different folder can be also specified in the config file.

Maven is needed for the compilation. Either install maven in the working directory to <workdir>/maven or
specify the installation in the configuration file.

Configuration sample:

    # Optional. By default parent directory of this config file is used. 
    # base.dir=/Users/se/vwscdn
    
    # Optional. Public URL for the service. Used for URL generation.
    # By default the service context uri is used runtime.
    # service.url=http://localhost:8080/wscdn-service-2.0-SNAPSHOT/
      
    # Optional. By default base.dir/maven is used.
    maven.home=/usr/local/Cellar/maven/3.2.1/libexec
    
    #GWT compiler parameters
    compile.draft=false
    compile.localworkers=2
    compile.jvmargs=-Xmx512m
    compile.debug


The widgetset compiler service
---
The service uses Maven to compile the requested widgetset on the fly. Generation creates a unique ID for the widgetset based on the the following configuration:
- Vaadin version 
- Eager loading of widgets (by default deferred loading is used)
- addon dependencies from Vaadin directory using Maven

Due to slow GWT compilation, the first run takes time. But as widgetsets are cached, the following requests just serve the static file.


Using the client
---

Add the following dependency to your maven pom.xml:

        <dependency>
            <groupId>in.virit.vwscdn</groupId>
            <artifactId>vwscdn-client</artifactId>
            <version>LATEST</version>
        </dependency>

The client also requires a jax-rs 2 implementation. Unless you are running in a modern Java EE 7 server, also provide e.g. resteasy like this

        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-client</artifactId>
            <version>3.0.10.Final</version>
        </dependency>
        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-jaxrs</artifactId>
            <version>3.0.10.Final</version>
        </dependency>
        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-jaxb-provider</artifactId>
            <version>3.0.10.Final</version>
        </dependency>
        
        <dependency>
            <groupId>org.jboss.resteasy</groupId>
            <artifactId>resteasy-jackson2-provider</artifactId>
            <version>3.0.10.Final</version>
        </dependency>

To use in the application add the following to your application Servlet class. The MyWidgetSetService class is automatically generated during build.


    @WebServlet(value = {"/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = HelloWorldUI.class)
    public static class Servlet extends VaadinServlet {
        
        // This is automatically generated widgetset
        WidgetSetConfiguration ws = new in.virit.vwscdn.GeneratedWidgetSet();
        
        // This is a manually created/edited widgetset:
        // WidgetSetConfiguration ws = new MyWidgetSet();

        // This is the default widgetset without any addons:
        // WidgetSetConfiguration ws = new in.virit.vwscdn.client.DefaultWidgetSet();

        @Override
        protected void servletInitialized() throws ServletException {
            super.servletInitialized();
            ws.init();
        }
    }

Maven build plugin
---

Maven plugin can be used to automatically generate 
org.vaadin.vwscdn.GeneratedWidgetSet.java code for the client. 
It uses the project classpath to resolve the included add-ons. 

First, install the maven plugin to your project:

    <build>
        <plugins>
            <plugin>
                <groupId>in.virit.vwscdn</groupId>
                <artifactId>vwscdn-maven-plugin</artifactId>
                <version>LATEST</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
        </plugins>
    </build>
    


If you want to run generation of in.virit.vwscdn.GeneratedWidgetSet.java by hand you can use the following command:

     mvn -e vwscdn:generate

Online status
---

The status of online published widget sets can be seen at:
[cdn.virit.in/api/compiler/status](http://cdn.virit.in/api/compiler/status)

Individual widgetset content / status can is visible through:
     cdn.virit.in/api/compiler/ws/<WIDGETSET_ID>
    
For example, the default widget set status [is visible here](http://cdn.virit.in/api/compiler/ws/vwscdnfac2b5204c77574f464e00e56dbb0a0f).

Directory integration
---

The Maven plugin supports searching and installing Vaadin add-ons from the Directory. To search for add-ons use the following:

     mvn vwscdn:dir -Dsearch=[search term]

And if you wish to add the matched add-ons to the project as a dependency, use `-Dadd` parameter. For example:

     mvn vwscdn:dir -Dsearch=switch -Dadd

Which adds the matching "Switch" component to your project pom.xml.

     [INFO] --- vwscdn-maven-plugin:3.0-SNAPSHOT:dir (default-cli) @ vwscdn-sample ---
     Switch - Switch is a decorated toggle checkbox.
     	Rating: 4.9 / 5
     	Maven: org.vaadin.teemu:switch:2.0.1 (ADDED)
     [INFO] ------------------------------------------------------------------------
     [INFO] BUILD SUCCESS
     [INFO] ------------------------------------------------------------------------


