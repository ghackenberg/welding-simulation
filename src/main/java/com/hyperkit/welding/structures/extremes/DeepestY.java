package com.hyperkit.welding.structures.extremes;

import com.hyperkit.welding.structures.Range;

public class DeepestY {

	private double x;
	private double y;
	private Range min_z;
	
	public DeepestY(double x, double y, Range min_z) {
		this.x = x;
		this.y = y;
		this.min_z = min_z;
	}
	
	public double getX() {
		return x;
	}
	
	public double getY() {
		return y;
	}
	
	public Range getMinZ() {
		return min_z;
	}
	
}
