package com.hyperkit.welding.configurations;

import com.hyperkit.welding.Configuration;
import com.hyperkit.welding.annotations.DoubleParameter;
import com.hyperkit.welding.annotations.Parameter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class Render3DConfiguration extends Configuration {
	
	public Render3DConfiguration() {
		super("3D-Darstellungsparameter");
	}
	
	// Camera X
	
	private DoubleProperty camera_x = new SimpleDoubleProperty(-1);
	
	public double getCameraX() {
		return camera_x.get();
	}
	public void setCameraX(double samples) {
		this.camera_x.set(samples);
	}
	@Parameter(name = "Kamera X-Position", unit = "mm")
	@DoubleParameter(min = -10, max = 0, step = 0.1)
	public DoubleProperty cameraXProperty() {
		return camera_x;
	}
	
	// Camera Y
	
	private DoubleProperty camera_y = new SimpleDoubleProperty(1);
	
	public double getCameraY() {
		return camera_y.get();
	}
	public void setCameraY(double samples) {
		this.camera_y.set(samples);
	}
	@Parameter(name = "Kamera Y-Position", unit = "mm")
	@DoubleParameter(min = 0, max = 10.0, step = 0.1)
	public DoubleProperty cameraYProperty() {
		return camera_y;
	}
	
	// Camera Z
	
	private DoubleProperty camera_z = new SimpleDoubleProperty(-1);
	
	public double getCameraZ() {
		return camera_z.get();
	}
	public void setCameraZ(double samples) {
		this.camera_z.set(samples);
	}
	@Parameter(name = "Kamera Z-Position", unit = "mm")
	@DoubleParameter(min = -10, max = 0, step = 0.1)
	public DoubleProperty cameraZProperty() {
		return camera_z;
	}
	
	// Eye X
	
	private DoubleProperty eye_x = new SimpleDoubleProperty(0);
	
	public double getEyeX() {
		return eye_x.get();
	}
	public void setEyeX(double samples) {
		this.eye_x.set(samples);
	}
	@Parameter(name = "Fokus X-Position", unit = "mm")
	@DoubleParameter(min = 0, max = 10, step = 0.1)
	public DoubleProperty eyeXProperty() {
		return eye_x;
	}
	
	// Eye Y
	
	private DoubleProperty eye_y = new SimpleDoubleProperty(0);
	
	public double getEyeY() {
		return eye_y.get();
	}
	public void setEyeY(double samples) {
		this.eye_y.set(samples);
	}
	@Parameter(name = "Fokus Y-Position", unit = "mm")
	@DoubleParameter(min = -10, max = 0, step = 0.1)
	public DoubleProperty eyeYProperty() {
		return eye_y;
	}
	
	// Eye Z
	
	private DoubleProperty eye_z = new SimpleDoubleProperty(0);
	
	public double getEyeZ() {
		return eye_z.get();
	}
	public void setEyeZ(double samples) {
		this.eye_z.set(samples);
	}
	@Parameter(name = "Fokus Z-Position", unit = "mm")
	@DoubleParameter(min = -10, max = 10, step = 0.1)
	public DoubleProperty eyeZProperty() {
		return eye_z;
	}
	
	// Samples
	
	private DoubleProperty samples = new SimpleDoubleProperty(0.1);
	
	public double getSamples() {
		return samples.get();
	}
	public void setSamples(double samples) {
		this.samples.set(samples);
	}
	@Parameter(name = "Schrittweite", unit = "mm")
	@DoubleParameter(min = 0, max = 0.1, step = 0.001)
	public DoubleProperty samplesProperty() {
		return samples;
	}

}
