package com.hyperkit.welding;

public class Range {

	private double lower_value;
	private double upper_value;
	
	public Range(double lower_value, double upper_value) {
		this.lower_value = lower_value;
		this.upper_value = upper_value;
	}
	
	public double getLowerValue() {
		return lower_value;
	}
	
	public double getUpperValue() {
		return upper_value;
	}
	
}
