package com.hyperkit.welding;

import org.jfree.data.xy.XYSeries;

public class Integrator {

	public static double calculateArea(XYSeries series) {
		double area = 0;
		
		for (int index = 1; index < series.getItemCount(); index++) {
			area += calculateArea(series, index);
		}
		
		return area;
	}
	
	private static double calculateArea(XYSeries series, int sample) {
		return calculateArea(series.getX(sample - 1), series.getY(sample - 1), series.getX(sample), series.getY(sample));
	}
	
	private static double calculateArea(Number previous_y, Number previous_z, Number current_y, Number current_z) {
		return calculateArea(previous_y.doubleValue(), previous_z.doubleValue(), current_y.doubleValue(), current_z.doubleValue());
	}
	
	private static double calculateArea(double previous_y, double previous_z, double current_y, double current_z) {
		double diff_y = current_y - previous_y;
		double diff_z = current_z - previous_z;
		
		//System.out.println(previous_y + " " + current_y + " " + diff_y + " " + diff_z);
		
		return - diff_y * current_z + diff_y * diff_z * 0.5;
	}
	
}
