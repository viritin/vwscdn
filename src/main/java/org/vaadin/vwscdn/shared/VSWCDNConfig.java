package org.vaadin.vwscdn.shared;

public interface VSWCDNConfig {

    public static final String BASE_URL = "http://sami2.app.fi/rws/";
//    public static final String BASE_URL = "http://localhost:8080/vaadin-wscdn-1.0-SNAPSHOT/";
    public static final String SERVICE_BASE_URL = BASE_URL + "api/";
    public static final String COMPILE_SERVICE_URL = SERVICE_BASE_URL + "compiler/compile";
    public static final String WS_BASE_URL = BASE_URL + "ws/";
}
