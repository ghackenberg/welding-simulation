package com.hyperkit.welding;

public class Span {

	private double origin_x;
	private Range extreme_x;
	private double y;
	private double z;
	
	public Span(double origin_x, Range extreme_x, double y, double z) {
		this.origin_x = origin_x;
		this.extreme_x = extreme_x;
		this.y = y;
		this.z = z;
	}
	
	public double getOriginX() {
		return origin_x;
	}
	
	public Range getExtremeX() {
		return extreme_x;
	}
	
	public double getY() {
		return y;
	}
	
	public double getZ() {
		return z;
	}
	
	@Override
	public String toString() {
		return "([" + origin_x + ", " + extreme_x + "], " + y + ", " + z + ")";
	}
	
}
