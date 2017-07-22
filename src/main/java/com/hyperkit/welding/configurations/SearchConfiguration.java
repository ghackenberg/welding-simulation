package com.hyperkit.welding.configurations;

import com.hyperkit.welding.Configuration;
import com.hyperkit.welding.annotations.DoubleParameter;
import com.hyperkit.welding.annotations.Parameter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public class SearchConfiguration extends Configuration {
	
	public SearchConfiguration() {
		super("Suchparameter");
	}
	
	// Limit temperature
	
	private DoubleProperty limit_temperature = new SimpleDoubleProperty(1500);
	
	public double getLimitTemperature() {
		return limit_temperature.get();
	}
	public void setLimitTemperature(double limit_temperature) {
		this.limit_temperature.set(limit_temperature);
	}
	@Parameter(name = "Zieltemperatur", unit = "°C")
	@DoubleParameter()
	public DoubleProperty limitTemperatureProperty() {
		return limit_temperature;
	}
	
	// Temperature threshold

	private DoubleProperty temperature_threshold = new SimpleDoubleProperty(5);
	
	public double getTemperatureThershold() {
		return temperature_threshold.get();
	}
	public void setTemperatureThreshold(double temperature_threshold) {
		this.temperature_threshold.set(temperature_threshold);
	}
	@Parameter(name = "Temperaturgenaugigkeit", unit = "°C")
	@DoubleParameter()
	public DoubleProperty temperatureThresholdProperty() {
		return temperature_threshold;
	}
	
}
