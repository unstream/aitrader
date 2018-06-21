package net.unstream.visualisation;

public class DataPoint {
	private int time;
	private double guthaben;

	public DataPoint() {

	}

	public DataPoint(int time, double guthaben) {
		this.time = time;
		this.guthaben = guthaben;
	}

	public int getTime() {
		return time;
	}

	public void setTime(int time) {
		this.time = time;
	}

	public double getGuthaben() {
		return guthaben;
	}

	public void setGuthaben(double guthaben) {
		this.guthaben = guthaben;
	}

}
