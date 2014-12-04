package org.vaadin.vwscdn.server;

import java.io.File;
import org.vaadin.vwscdn.shared.VSWCDNConfig;

public interface ServerConfig extends VSWCDNConfig {

    /** Server working directory. */
    public static final File BASE_DIR = new File("/home/dev/rws");
    //public static final File BASE_DIR = new File("/Users/se/ws");
    
    public static final File COMPILER_ROOT_DIR = new File(BASE_DIR, "compiler");
    public static File PUBLIC_ROOT_DIR = new File(BASE_DIR, "public");
    
    /** Maven home directory. */
    public static final File MAVEN_HOME = new File("/home/dev/java/apache-maven-3.2.3"); 
    // public static final String MAVEN_HOME = "/usr/local/Cellar/maven/3.2.1/libexec";
}
