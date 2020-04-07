package com.hyperkit.welding;

public class Range {

	private double inner_value;
	private double outer_value;
	
	public Range(double inner_value, double outer_value) {
		this.inner_value = inner_value;
		this.outer_value = outer_value;
	}
	
	public double getInnerValue() {
		return inner_value;
	}
	
	public double getOuterValue() {
		return outer_value;
	}
	
	@Override
	public String toString() {
		return "[" + inner_value + ", " + outer_value + "]";
	}
	
}
