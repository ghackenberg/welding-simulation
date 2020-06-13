package com.hyperkit.welding.configurations;

import com.hyperkit.welding.Configuration;
import com.hyperkit.welding.annotations.IntegerParameter;
import com.hyperkit.welding.annotations.Parameter;

import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;

public class Render2DConfiguration extends Configuration {
	
	public Render2DConfiguration() {
		super("2D-Darstellungsparameter");
	}
	
	// Samples
	
	private IntegerProperty xySamples = new SimpleIntegerProperty(200);
	
	public int getXYSamples() {
		return xySamples.get();
	}
	public void setXYSamples(int xySamples) {
		this.xySamples.set(xySamples);
	}
	@Parameter(name = "XY-Diagrammpunkte", unit = "Punkte")
	@IntegerParameter(min = 1)
	public IntegerProperty xySamplesProperty() {
		return xySamples;
	}
	
	// Samples
	
	private IntegerProperty yzSamples = new SimpleIntegerProperty(200);
	
	public int getYZSamples() {
		return yzSamples.get();
	}
	public void setYZSamples(int yzSamples) {
		this.yzSamples.set(yzSamples);
	}
	@Parameter(name = "YZ-Diagrammpunkte", unit = "Punkte")
	@IntegerParameter(min = 1)
	public IntegerProperty yzSamplesProperty() {
		return yzSamples;
	}
	
	// Samples
	
	private IntegerProperty yzxSamples = new SimpleIntegerProperty(20);
	
	public int getYZXSamples() {
		return yzxSamples.get();
	}
	public void setYZXSamples(int yzxSamples) {
		this.yzxSamples.set(yzxSamples);
	}
	@Parameter(name = "YZ-Überlagerungspunkte", unit = "Punkte")
	@IntegerParameter(min = 2)
	public IntegerProperty yzxSamplesProperty() {
		return yzxSamples;
	}
	
	// Samples
	
	private IntegerProperty xzSamples = new SimpleIntegerProperty(200);
	
	public int getXZSamples() {
		return xzSamples.get();
	}
	public void setXZSamples(int xzSamples) {
		this.xzSamples.set(xzSamples);
	}
	@Parameter(name = "XZ-Diagrammpunkte", unit = "Punkte")
	@IntegerParameter(min = 1)
	public IntegerProperty xzSamplesProperty() {
		return xzSamples;
	}

}
