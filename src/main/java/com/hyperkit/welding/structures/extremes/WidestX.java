package com.hyperkit.welding.structures.extremes;

import com.hyperkit.welding.structures.Range;

public class WidestX {

	private double x;
	private Range max_y;
	private double z;
	
	public WidestX(double x, Range max_y, double z) {
		this.x = x;
		this.max_y = max_y;
		this.z = z;
	}
	
	public double getX() {
		return x;
	}
	
	public Range getMaxY() {
		return max_y;
	}
	
	public double getZ() {
		return z;
	}
	
}
