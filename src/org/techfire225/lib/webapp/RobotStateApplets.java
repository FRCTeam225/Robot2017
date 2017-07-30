package org.techfire225.lib.webapp;

import java.io.IOException;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.techfire225.firevision2017.AdbBridge;
import org.techfire225.lib.firelog.FireLogSample;
import org.techfire225.robot.Robot;

import com.google.gson.JsonObject;

public class RobotStateApplets {
	public static class LatestLog extends HttpServlet {
		private static final long serialVersionUID = 7888425758718454864L;

		protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
			if ( Robot.logger100hz == null ) {
				res.setContentType("text/plain");
				res.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
				res.getWriter().println("Logger is not running");
			}
			else {
				FireLogSample log = Robot.logger100hz.getLatestLog();
				if ( log == null ) {
					res.setContentType("application/json");
					res.setStatus(HttpServletResponse.SC_OK);
					res.getWriter().println("Logger running, but nothing has been logged yet.");
				}
				else {
					res.setContentType("application/json");
					res.setStatus(HttpServletResponse.SC_OK);
					res.getWriter().println(log.toJSON());
				}
			}
		}
	}
	
	public static class PhoneState extends HttpServlet {
		private static final long serialVersionUID = -2367235659049852547L;

		protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
			JsonObject out = new JsonObject();
			if ( Robot.visionClient == null )
				out.addProperty("connected", false);
			else {
				out.addProperty("connected", Robot.visionClient.isConnected());
				out.addProperty("distance", Robot.visionClient.getLastDistance());
				out.addProperty("theta", Robot.visionClient.getLastThetaError());
				out.addProperty("hatTheta", 0);
			}
			
			res.setContentType("application/json");
			res.setStatus(HttpServletResponse.SC_OK);
			res.getWriter().println(out.toString());
		}
	}
	
	public static class PhoneRestart extends HttpServlet {
		private static final long serialVersionUID = -2367235659049852547L;

		protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
			AdbBridge.getInstance().restartApp();
			
			JsonObject out = new JsonObject();
			out.addProperty("success", true);
			res.setContentType("application/json");
			res.setStatus(HttpServletResponse.SC_OK);
			res.getWriter().println(out.toString());
		}
	}
	
	public static class PhoneKill extends HttpServlet {
		private static final long serialVersionUID = -2367235659049852547L;

		protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
			AdbBridge.getInstance().killApp();
			
			JsonObject out = new JsonObject();
			out.addProperty("success", true);
			res.setContentType("application/json");
			res.setStatus(HttpServletResponse.SC_OK);
			res.getWriter().println(out.toString());
		}
	}
	
	public static class ErrorReporting extends HttpServlet {
		private static final long serialVersionUID = -2367235659049852547L;

		protected void doGet(HttpServletRequest req, HttpServletResponse res) throws IOException {
			res.setContentType("application/json");
			res.setStatus(HttpServletResponse.SC_OK);
			res.getWriter().println(ErrorReporter.toJSON());
		}
	}
}
