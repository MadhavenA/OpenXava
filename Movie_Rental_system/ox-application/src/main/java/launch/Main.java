package launch;

import org.apache.catalina.*;
import org.apache.catalina.core.*;
import org.apache.catalina.startup.*;
import org.apache.catalina.webresources.*;
import org.apache.log4j.*;

import java.io.*;
import java.net.*;
import java.nio.file.*;
import java.util.logging.Level;

import org.openxava.util.DBServer;

public class Main {

    private static final int PORT_NUMBER = 8080;

    public static void main(String[] args) throws Exception {
        int port = PORT_NUMBER;
        PropertyConfigurator.configure("ox-application/properties/logging.properties");
        if(args.length > 0 && args[0].length() == 4) {
            try {
                port = Integer.parseInt(args[0]);
            }catch (NumberFormatException nm){
                System.out.println("Invalid Port Number Specified, using default Port: " + PORT_NUMBER);
                port = PORT_NUMBER;
            }
        }
        run(port);
    }

    public static void run(int port) throws Exception {
        System.out.println("starting_application");

        File root = getRootFolder();
        System.setProperty("org.apache.catalina.startup.EXIT_ON_INIT_FAILURE", "true");
        Tomcat tomcat = new Tomcat();
        //Path tempPath = Files.createTempDirectory("tomcat-base-dir");
        //tomcat.setBaseDir(tempPath.toString());
        tomcat.setPort(port);
        tomcat.getConnector();
        tomcat.enableNaming();

        File webContentFolder = new File(root.getAbsolutePath(), "web");
        if (!webContentFolder.exists()) {
            webContentFolder = Files.createTempDirectory("default-doc-base").toFile();
        }
        StandardContext ctx = (StandardContext) tomcat.addWebapp("/", webContentFolder.getAbsolutePath());
        //Set execution independent of current thread context classloader (compatibility with exec:java mojo)
        ctx.setParentClassLoader(Main.class.getClassLoader());

        System.out.println("configuring app with basedir: " + webContentFolder.getAbsolutePath());

        // Declare an alternative location for your "WEB-INF/classes" dir
        // Servlet 3.0 annotation will work
        File additionWebInfClassesFolder = new File(root.getAbsolutePath(), "target/classes");
        WebResourceRoot resources = new StandardRoot(ctx);

        WebResourceSet resourceSet;
        if (additionWebInfClassesFolder.exists()) {
            resourceSet = new DirResourceSet(resources, "/WEB-INF/classes", additionWebInfClassesFolder.getAbsolutePath(), "/");
            System.out.println("loading WEB-INF resources from as '" + additionWebInfClassesFolder.getAbsolutePath() + "'");
        } else {
            resourceSet = new EmptyResourceSet(resources);
        }
        resources.addPreResources(resourceSet);
        ctx.setResources(resources);

        tomcat.start();
        System.out.println("application_started_go http://localhost:" + port + "/");
        tomcat.getServer().await();
    }

    private static File getRootFolder() {
        try {
            File root;
            String runningJarPath = Main.class.getProtectionDomain().getCodeSource().getLocation().toURI().getPath().replaceAll("\\\\", "/");
            int lastIndexOf = runningJarPath.lastIndexOf("/target/");
            if (lastIndexOf < 0) {
                root = new File("");
            } else {
                root = new File(runningJarPath.substring(0, lastIndexOf));
            }
            System.out.println("application resolved root folder: " + root.getAbsolutePath());
            return root;
        } catch (URISyntaxException ex) {
            throw new RuntimeException(ex);
        }
    }
}
