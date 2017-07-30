package org.techfire225.lib.framework;

import java.util.ArrayList;

import org.techfire225.lib.firelog.FireLog;
import org.techfire225.lib.firelog.FireLogSample;

import edu.wpi.first.wpilibj.Notifier;
import edu.wpi.first.wpilibj.Timer;

public class Looper {
	Notifier notifier;
	
	public interface Updateable {
		void update(FireLogSample log);
	}
	
	volatile boolean running = false;
	ArrayList<Updateable> runnables = new ArrayList<>();
	
	FireLog logger;
	
	public Looper(int hz, FireLog logger) {
		notifier = new Notifier(runner);
		notifier.startPeriodic(1.0/(double)hz);
		this.logger = logger;
	}
	
	public synchronized void addLoop(Updateable r) {
		runnables.add(r);
	}
	
	public synchronized void removeLoop(Updateable r) {
		runnables.remove(r);
	}
	
	public void enable()  {
		running = true;
	}
	
	public void disable() {
		running = false;
	}
	
	final Runnable runner = new Runnable() {

		@Override
		public void run() {
			if ( !running )
				return;
			
			FireLogSample log = new FireLogSample();
			synchronized (Looper.this) {
				for ( Updateable run : runnables ) {
					try {
						run.update(log);
					} catch (Exception e) {
						System.out.println("A loop failed to execute");
						e.printStackTrace();
					}
				}
					
				
				log.stamp(Timer.getFPGATimestamp());
				if ( logger != null )
					logger.log(log);
			}
		}
		
	};
}
