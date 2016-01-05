package utils;

import java.util.ArrayList;

public class HRIR {

	private int distance;
	private int elevation;
	private int azimuth;
	private double[] left;
	private double[] right;
	
	public int getDistance() {
		return distance;
	}
	public void setDistance(int distance) {
		this.distance = distance;
	}
	public int getElevation() {
		return elevation;
	}
	public void setElevation(int elevation) {
		this.elevation = elevation;
	}
	public int getAzimuth() {
		return azimuth;
	}
	public void setAzimuth(int azimuth) {
		this.azimuth = azimuth;
	}
	public double[] getLeft() {
		return left;
	}
	public void setLeft(double[] left) {
		this.left = left;
	}
	public double[] getRight() {
		return right;
	}
	public void setRight(double[] right) {
		this.right = right;
	}
	
	
	
}
