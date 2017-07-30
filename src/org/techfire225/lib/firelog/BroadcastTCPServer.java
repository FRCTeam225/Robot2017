package org.techfire225.lib.firelog;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class BroadcastTCPServer extends Thread {
	ServerSocket server;
	ArrayList<Socket> clients = new ArrayList<Socket>();
	public BroadcastTCPServer(int port) throws IOException {
		server = new ServerSocket(port);
		start();
	}
	
	
	public void broadcast(String str) {
		ArrayList<Socket> failed = new ArrayList<Socket>();
		byte[] data = str.getBytes();
		synchronized(clients) {
			for ( Socket s : clients ) {
				try {
					s.getOutputStream().write(data);
				} catch (Exception e) {
					failed.add(s);
				}
			}
			
			clients.removeAll(failed);
		}
	}
	
	public void run() {
		while ( server.isBound() ) {
			try {
				Socket s = server.accept();
				synchronized(clients) {
					clients.add(s);
				}
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
