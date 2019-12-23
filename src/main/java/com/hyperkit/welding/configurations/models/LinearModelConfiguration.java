package com.hyperkit.welding.configurations.models;

import com.hyperkit.welding.annotations.DoubleParameter;
import com.hyperkit.welding.annotations.IntegerParameter;
import com.hyperkit.welding.annotations.Parameter;
import com.hyperkit.welding.configurations.ModelConfiguration;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class LinearModelConfiguration extends ModelConfiguration {
	
	// Samples
	
	private IntegerProperty samples = new SimpleIntegerProperty(20);
	
	public int getSamples() {
		return samples.get();
	}
	public void setSamples(int samples) {
		this.samples.set(samples);
	}
	@Parameter(name = "Wärmequellen", unit = " * 2 + 1 Quellen")
	@IntegerParameter(min = 0)
	public IntegerProperty samplesProperty() {
		return samples;
	}
	
	// Radius

	private DoubleProperty diameter = new SimpleDoubleProperty(2);
	
	public double getDiameter() {
		return diameter.get();
	}
	public void setDiameter(double diameter) {
		this.diameter.set(diameter);
	}
	@Parameter(name = "Pendelbreite", unit = "mm")
	@DoubleParameter(min = 0)
	public DoubleProperty diameterProperty() {
		return diameter;
	}
	
}
