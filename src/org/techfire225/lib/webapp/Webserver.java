package org.techfire225.lib.webapp;

import org.eclipse.jetty.server.Handler;
import org.eclipse.jetty.server.Server;
import org.eclipse.jetty.server.handler.HandlerList;
import org.eclipse.jetty.server.handler.ResourceHandler;
import org.eclipse.jetty.servlet.ServletContextHandler;

public class Webserver {
	
	public Webserver() throws Exception {
		Server server = new Server(5801);
		
		ServletContextHandler servlets = new ServletContextHandler(ServletContextHandler.SESSIONS);
        
		servlets.addServlet(RedirectApplet.class, "/");
		servlets.addServlet(ConstantsApplets.List.class, "/constants/list");
		servlets.addServlet(ConstantsApplets.Update.class, "/constants/update");
		
		servlets.addServlet(RobotStateApplets.ErrorReporting.class, "/state/errors");
		servlets.addServlet(RobotStateApplets.PhoneState.class, "/state/phone");
		servlets.addServlet(RobotStateApplets.PhoneRestart.class, "/state/phone/restart");
		servlets.addServlet(RobotStateApplets.PhoneKill.class, "/state/phone/kill");
		servlets.addServlet(RobotStateApplets.LatestLog.class, "/state/latest");
		servlets.addServlet(StateSocket.class, "/state/socket");
		servlets.addServlet(SlowStateSocket.class, "/state/slowsocket");
		
		servlets.addServlet(AutonomousApplets.AutonomousList.class, "/auto/list");
		servlets.addServlet(AutonomousApplets.AutonomousSelect.class, "/auto/set");
		
		ResourceHandler resource_handler = new ResourceHandler();
		resource_handler.setWelcomeFiles(new String[]{ "index.html" });
		resource_handler.setResourceBase(Webserver.class.getClassLoader().getResource("www/").toExternalForm());
		

        HandlerList handlers = new HandlerList();
        handlers.setHandlers(new Handler[] { resource_handler, servlets });
        server.setHandler(handlers);

		server.start();
	}
}
