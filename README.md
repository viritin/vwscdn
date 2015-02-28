
Remote widget set compilation service
===
This project consists of parts:
 - Maven plugin that generates the widgetset configuration during project build
 - Maven archetype for creating easily new projects using the service
 - Client library used in Vaadin application at runtime


The service uses Maven to compile the requested widgetset on the fly. Generation creates a unique ID for the widgetset based on the the following configuration:
- Vaadin version 
- all addon dependencies in the project
- eager loading of widgets (by default deferred loading is used)
- GWT compilation style (e.g. OBF, PRETTY)

Maven plugin can be used to automatically generate 
org.vaadin.vwscdn.GeneratedWidgetSet.java code for the client. 
It uses the project classpath to resolve the included add-ons. 


Using Maven plugin in a Vaadin project
---

The plugin is available in Maven central repository. To use the VWSCDN build plugin in your build, add the following:

     </plugins>
         <plugin>
            <groupId>in.virit.vwscdn</groupId>
            <artifactId>vwscdn-maven-plugin</artifactId>
            <version>1.3.0</version>
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

*NOTE:* Remove all widgetset annotations in you Servlet classes and also remove the .gwt.xml file.

If you want to run generation of in.virit.vwscdn.GeneratedWidgetSet.java by hand you can use the following command:

     mvn -e vwscdn:generate

 
Creating a new project using the archetype
---
With the Maven archetype you can easily create a project setup that uses the central compilation service.

    mvn archetype:generate -DarchetypeCatalog=http://virit.in/maven2/archetype-catalog.xml

This will prompt for project artifactId and groupId and generate a simple Vaadin application project.

Running the sample project
---

To run the sample at localhost use:

    cd sample
    mvn jetty:run



