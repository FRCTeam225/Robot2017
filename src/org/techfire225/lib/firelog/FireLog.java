package org.techfire225.lib.firelog;
import java.io.FileWriter;
import java.io.IOException;
import java.util.concurrent.ArrayBlockingQueue;
import org.techfire225.lib.framework.Publisher;

public class FireLog extends Publisher<FireLogSample> {
	ArrayBlockingQueue<FireLogSample> queue = new ArrayBlockingQueue<FireLogSample>(100);
	FileWriter writer = null;
	BroadcastTCPServer server;
	
	volatile FireLogSample latest = null;
	
	public FireLog(String path, int port) throws IOException {
		server = new BroadcastTCPServer(port);
		writer = new FileWriter(path);
		thread.start();
	}
	
	public FireLog(int port) throws IOException {
		writer = null;
		server = new BroadcastTCPServer(port);
		thread.start();
	}

	public FireLogSample getLatestLog() {
		return latest;
	}
	
	Thread thread = new Thread() {
		public void run() {
			while ( !interrupted() ) {
				try {
					// Write out everything buffered at this point
					FireLogSample sample = null;
					while ( (sample = queue.poll()) != null ) {
						latest = sample;
						String data = sample.toJSON() + "\n";
						if ( writer != null ) {
							try {
								writer.write(data);
							} catch (IOException e) {
								System.out.println("Logfile write failed");
								e.printStackTrace();
							}		
						}
						server.broadcast(data);
						publish(sample);
						
						Thread.sleep(5);
					}
					
					Thread.sleep(5);
				} catch (InterruptedException interrupted) {
					break;
				} catch (Exception e) {
					System.out.println("Firelog");
					e.printStackTrace();
				}
			}
		}
	};
	
	public void log(FireLogSample sample) {
		try { 
			if ( !queue.offer(sample) )
				System.out.println("FireLog queue is full, threw out sample");
		} 
		catch (Exception ex) {
			System.out.println("FireLog error");
			ex.printStackTrace();
		}
	}
	
}
