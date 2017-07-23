package com.hyperkit.welding.configurations;

import com.hyperkit.welding.Configuration;
import com.hyperkit.welding.annotations.IntegerParameter;
import com.hyperkit.welding.annotations.Parameter;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Render2DConfiguration extends Configuration {
	
	public Render2DConfiguration(String name) {
		super(name);
	}
	
	// Samples
	
	private IntegerProperty samples = new SimpleIntegerProperty(10);
	
	public int getSamples() {
		return samples.get();
	}
	public void setSamples(int samples) {
		this.samples.set(samples);
	}
	@Parameter(name = "Diagrammpunkte", unit = "Punkte")
	@IntegerParameter(min = 1)
	public IntegerProperty samplesProperty() {
		return samples;
	}

}
