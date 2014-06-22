package com.khairil.host;

import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.servlet.ServletContextHandler;
import org.eclipse.jetty.servlet.ServletHolder;

/**
 * Starts the command servlet.
 * 
 */
public class Launcher {

    private static final int DEFAULT_PORT = 8070;

    public static void main(String[] args) throws Exception {
        int hostPort = args.length > 0 ? Integer.valueOf(args[0]) : DEFAULT_PORT;
        setContainerPort(args);

        Server server = new Server(hostPort);
        ServletContextHandler context = new ServletContextHandler(ServletContextHandler.SESSIONS);
        context.setContextPath("/");
        server.setHandler(context);
        context.addServlet(new ServletHolder(new CommandServlet()), "/*");
        server.start();
    }

    /**
     * Sets the client container port. Default is 8070. This port must be the same as the port used by the servlet in
     * the client's container.
     * 
     * @param containerPort
     *            The containerPort to set.
     */
    private static void setContainerPort(String... args) {
        String port = args.length > 1 ? args[1] : String.valueOf(DEFAULT_PORT);
        System.setProperty("com.khairil.container.port", port);
    }
}
