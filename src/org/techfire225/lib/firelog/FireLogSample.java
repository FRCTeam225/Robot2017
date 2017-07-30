package org.techfire225.lib.firelog;
import com.google.gson.JsonObject;

public class FireLogSample {
	double t;
	
	// Note: JsonObject inserts are not O(1)
	final JsonObject topics = new JsonObject();
	
	public FireLogSample() {
	}
	
	public void put(String name, Number value) {
		try {
			topics.addProperty(name, value);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("firelog.put failed");
		}
	}
	
	
	public void put(String name, String value) {
		try {
			topics.addProperty(name, value);
		} catch (Exception e) {
			e.printStackTrace();
			System.out.println("firelog.put failed");
		}
	}
	
	public void stamp(double t) {
		this.t = t;
	}
	
	
	public String toJSON() {
		JsonObject out = new JsonObject();
		out.addProperty("t", t);
		out.add("topics", topics);
		return out.toString();
	}
	
}
