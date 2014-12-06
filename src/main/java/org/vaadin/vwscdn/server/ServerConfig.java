package org.vaadin.vwscdn.server;

import java.io.File;

public interface ServerConfig {

    public static final String BASE_URL = "http://sami2.app.fi/rws/";
//    public static final String BASE_URL = "http://localhost:8080/vaadin-wscdn-1.0-SNAPSHOT/";
    public static final String SERVICE_BASE_URL = BASE_URL + "api/";
    public static final String COMPILE_SERVICE_URL = SERVICE_BASE_URL + "compiler/compile";
    public static final String WS_BASE_URL = BASE_URL + "ws/";
    public static final String WS_MISSING_PATH = "missingWidgetSet";
    public static final String WS_MISSING_URL = WS_BASE_URL + WS_MISSING_PATH;

    public static final File BASE_DIR = new File("/home/dev/rws");
    //public static final File BASE_DIR = new File("/Users/se/ws");

    public static final File COMPILER_ROOT_DIR = new File(BASE_DIR, "compiler");
    public static File PUBLIC_ROOT_DIR = new File(BASE_DIR, "public");
    public static final File MAVEN_HOME = new File("/home/dev/java/apache-maven-3.2.3");
    // public static final String MAVEN_HOME = "/usr/local/Cellar/maven/3.2.1/libexec";
}
