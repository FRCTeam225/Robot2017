package org.techfire225.firevision2017;

import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.net.Socket;

import org.techfire225.lib.framework.Publisher;

public class VisionClient extends Publisher<VisionMessage> implements Runnable {
	static final boolean VERBOSE = false;
	
	Socket client = null;
	ObjectInputStream in;
	OutputStream out;
	String addr;
	
	AdbBridge adb;
	
	Thread thread;
	
	double lastDistance = 0;
	double lastThetaError = 0;
	
	boolean isAlive = false;
	long lastRecv = 0;
	int port, mjpgPort;
	public VisionClient(int port, int mjpgPort) {
		adb = AdbBridge.getInstance();
		
		this.port = port;
		this.mjpgPort = mjpgPort;
		
		this.addr = "127.0.0.1";
		
		thread = new Thread(this);
		thread.start();
	}
	
	public void run() {
		System.out.println("Starting ADB");
		int nFailures = 0;
		adb.restartAdb();
		doPortForward();
		
		while ( !thread.isInterrupted() ) {
			try {
				if ( client == null || !client.isConnected() ) {
					if ( VERBOSE )
						System.out.println("Reconnecting to vision... "+addr+":"+port);
					client = new Socket(addr, port);
					client.setSoTimeout(2000);
					in = new ObjectInputStream(client.getInputStream());
					out = client.getOutputStream();

				}
			} catch (Exception e) {
				if ( VERBOSE )
					e.printStackTrace();
				
				if ( nFailures == 5 ) {
					//System.out.println("Redoing ADB port forward...");
					doPortForward();
					nFailures = 0;
				}
				nFailures++;
			}
			
			while ( client != null && client.isConnected() ) {
				try { 
					VisionMessage pkt = (VisionMessage) in.readObject();
					isAlive = true;
					if ( pkt == null ) 
						break;
					pkt.stamp();
					publish(pkt);
					lastDistance = pkt.distance;
					lastThetaError = pkt.theta;
					lastRecv = System.currentTimeMillis();
				} catch (Exception e) {
					//e.printStackTrace();
					break;
				}
			}
			
			try {
				client.close();
				client = null;
			} catch (Exception e) {
				client = null;
			}
			
			isAlive = false;
			
			try {
				Thread.sleep(1000);
			} catch(Exception e) {}
		}
	}
	
	public boolean isConnected() {
		return isAlive;
	}
	
	private void doPortForward() {
		try { 
			adb.portForward(port, port);
			adb.portForward(mjpgPort, mjpgPort);
		}
		catch (Exception e) { System.out.println("Failed to start vision"); }
	}
	
	public double getLastDistance() {
		return lastDistance;
	}
	
	public double getLastThetaError() {
		return lastThetaError;
	}
	
}
