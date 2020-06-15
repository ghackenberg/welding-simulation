package com.hyperkit.welding.structures;

import com.hyperkit.welding.structures.extremes.DeepestX;
import com.hyperkit.welding.structures.extremes.WidestX;

public class Span {

	private double origin_x;
	private double origin_y;
	
	private WidestX widest_x;
	private DeepestX deepest_x;
	
	private Range extreme_x;
	private Range extreme_min_y;
	private Range extreme_max_y;
	
	public Span(double origin_x, double origin_y, Range extreme_x, Range extreme_max_y) {
		this.origin_x = origin_x;
		this.origin_y = origin_y;
		
		this.extreme_x = extreme_x;
		this.extreme_max_y = extreme_max_y;
	}
	
	public double getOriginX() {
		return origin_x;
	}
	public double getOriginY() {
		return origin_y;
	}
	
	public void setWidestX(WidestX widest_x) {
		this.widest_x = widest_x;
	}
	public WidestX getWidestX() {
		return widest_x;
	}	
	public void setDeepestX(DeepestX deepest_x) {
		this.deepest_x = deepest_x;
	}
	public DeepestX getDeepestX() {
		return deepest_x;
	}
	
	public Range getExtremeX() {
		return extreme_x;
	}
	public void setExtremeMinY(Range extreme_min_y) {
		this.extreme_min_y = extreme_min_y;
	}
	public Range getExtremeMinY() {
		return extreme_min_y;
	}
	public Range getExtremeMaxY() {
		return extreme_max_y;
	}
	
	@Override
	public String toString() {
		return "([" + origin_x + ", " + extreme_x + "], " + origin_y + ")";
	}
	
}
