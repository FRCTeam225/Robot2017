package org.techfire225.lib.webapp;

import org.eclipse.jetty.websocket.api.Session;
import org.eclipse.jetty.websocket.api.WebSocketListener;
import org.eclipse.jetty.websocket.servlet.WebSocketServlet;
import org.eclipse.jetty.websocket.servlet.WebSocketServletFactory;
import org.techfire225.lib.firelog.FireLogSample;
import org.techfire225.lib.framework.Publisher;
import org.techfire225.robot.Robot;

public class StateSocket extends WebSocketServlet  {
	private static final long serialVersionUID = -6042298400375548987L;

	@Override
	public void configure(WebSocketServletFactory factory) {
		factory.register(StateSocketAdapter.class);
	}
	
	public static class StateSocketAdapter implements WebSocketListener, Publisher.Subscriber<FireLogSample> {
		
		Session session;
		boolean invalid = false;
		
		public StateSocketAdapter() {
			
		}
		
		@Override
		public void onWebSocketConnect(Session session) {
			this.session = session;
	        Robot.logger100hz.subscribe(this);
		}
		
		@Override
		public void onWebSocketClose(int statusCode, String reason) {
	        Robot.logger100hz.unsubscribe(this);
	        invalid = true;
	    }
		
		@Override
		public void onWebSocketError(Throwable cause) {
			Robot.logger100hz.unsubscribe(this);
			invalid = true;
	    }
	
		@Override
		public void receiveMessage(FireLogSample msg) {
			if ( invalid || !session.isOpen() )
				return;
			
			try {
				session.getRemote().sendStringByFuture(msg.toJSON());
			} catch (Exception e) {
				invalid = true;
			}
		}

		@Override
		public void onWebSocketBinary(byte[] arg0, int arg1, int arg2) {
		}

		@Override
		public void onWebSocketText(String arg0) {
		}
	}


}
