
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

    mvn jetty:run

Or, if you run the service locally also:

    mvn -Dvwscdn.config=/path/to/working/directory/service-config.properties -Dvwscdn.local jetty:run

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
The service uses Maven to compile the requested widgetset on the fly. Generation creates a unique ID for the widgetset based on the the following configuration:
- Vaadin version 
- Eager loading of widgets (by default deferred loading is used)
- addon dependencies from Vaadin directory using Maven

Due to slow GWT compilation, the first run takes time. But as widgetsets are cached, the following requests just serve the static file.


Using the client
---

Add the following dependency to your maven pom.xml:

        <dependency>
            <groupId>org.vaadin.vwscdn</groupId>
            <artifactId>vwscdn-client</artifactId>
            <version>4.0-SNAPSHOT</version>
        </dependency>


To use in the application add the following to your application Servlet class. The MyWidgetSetService class is automatically generated during build.


    @WebServlet(value = {"/*"}, asyncSupported = true)
    @VaadinServletConfiguration(productionMode = false, ui = HelloWorldUI.class)
    public static class Servlet extends VaadinServlet {
        
        // This is automatically generated widgetset
        WidgetSetConfiguration ws = new org.vaadin.vwscdn.GeneratedWidgetSet();
        
        // This is a manually created/edited widgetset:
        // WidgetSetConfiguration ws = new MyWidgetSet();

        // This is the default widgetset without any addons:
        // WidgetSetConfiguration ws = new org.vaadin.vwscdn.client.DefaultWidgetSet();

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
                <groupId>org.vaadin.vwscdn</groupId>
                <artifactId>vwscdn-maven-plugin</artifactId>
                <version>3.0-SNAPSHOT</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>generate</goal>
                        </goals>
                    </execution>
                </executions>
            </plugin>
            <plugin>
                <groupId>org.codehaus.mojo</groupId>
                <artifactId>build-helper-maven-plugin</artifactId>
                <version>1.9.1</version>
                <executions>
                    <execution>
                        <phase>generate-sources</phase>
                        <goals>
                            <goal>add-source</goal>
                        </goals>
                        <configuration>
                            <sources>
                                <source>${project.build.directory}/generated-sources/vwscdn</source>
                            </sources>
                        </configuration>
                    </execution>
                </executions>
            </plugin>
        </plugins>
        <pluginManagement>
            <plugins>
                <plugin>
                    <groupId>corg.vaadin.vwscdn</groupId>
                    <artifactId>vwscdn-maven-plugin</artifactId>
                    <version>2.0-SNAPSHOT</version>
                </plugin>
            </plugins>
        </pluginManagement>
    </build>
    


If you want to run generation of org.vaadin.vwscdn.GeneratedWidgetSet.java by hand you can use the following command:

     mvn -e vwscdn:generate

Online status
---

The status of online published widget sets can be seen at:
[sami.app.fi/rws/api/compiler/status](http://sami.app.fi/rws/api/compiler/status)

Individual widgetset content / status can is visible through:
     sami.app.fi/rws/api/compiler/ws/<WIDGETSET_ID>
    
For example, the default widget set status [is visible here](http://sami.app.fi/rws/api/compiler/ws/vwscdnfac2b5204c77574f464e00e56dbb0a0f).

Directory integration
---

The Maven plugin supports searching and installing Vaadin add-ons from the Directory. to search for add-ons use the following:

     mvn vwscdn:dir -Dsearch=[search term]

And if you wish to add the matched add-ons to the project as a dependency, use `-Dadd` parameter. For example:

     mvn vwscdn:dir -Dsearch=switch -Dadd

Which adds the matching "Switch" component to your project pom.xml as dependency.

     [INFO] --- vwscdn-maven-plugin:3.0-SNAPSHOT:dir (default-cli) @ vwscdn-sample ---
     Switch - Switch is a decorated toggle checkbox.
     	Rating: 4.9 / 5
     	Maven: org.vaadin.teemu:switch:2.0.1 (ADDED)
     [INFO] ------------------------------------------------------------------------
     [INFO] BUILD SUCCESS
     [INFO] ------------------------------------------------------------------------


