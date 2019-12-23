package com.hyperkit.welding.configurations.models;

import com.hyperkit.welding.annotations.DoubleParameter;
import com.hyperkit.welding.annotations.IntegerParameter;
import com.hyperkit.welding.annotations.Parameter;
import com.hyperkit.welding.configurations.ModelConfiguration;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class CircularModelConfiguration extends ModelConfiguration {
	
	// Samples
	
	private IntegerProperty samples = new SimpleIntegerProperty(20);
	
	public int getSamples() {
		return samples.get();
	}
	public void setSamples(int samples) {
		this.samples.set(samples);
	}
	@Parameter(name = "Wärmequellen", unit = "Quellen")
	@IntegerParameter(min = 0)
	public IntegerProperty samplesProperty() {
		return samples;
	}
	
	// Radius

	private DoubleProperty radius = new SimpleDoubleProperty(2);
	
	public double getRadius() {
		return radius.get();
	}
	public void setRadius(double radius) {
		this.radius.set(radius);
	}
	@Parameter(name = "Pendelradius", unit = "mm")
	@DoubleParameter(min = 0)
	public DoubleProperty radiusProperty() {
		return radius;
	}
	
}
