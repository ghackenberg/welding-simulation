package com.hyperkit.welding.configurations;

import com.hyperkit.welding.Configuration;
import com.hyperkit.welding.annotations.DoubleParameter;
import com.hyperkit.welding.annotations.Parameter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;

public abstract class ModelConfiguration extends Configuration {
	
	public ModelConfiguration() {
		super("Modellparameter");
	}
	
	// Start temperature
	
	private DoubleProperty start_temperature = new SimpleDoubleProperty(0);
	
	public double getStartTemperature() {
		return start_temperature.get();
	}
	public void setStartTemperature(double start_temperature) {
		this.start_temperature.set(start_temperature);
	}
	@Parameter(name = "Ausgangstemperatur", unit = "�C")
	@DoubleParameter(min = 0)
	public DoubleProperty startTemperatureProperty() {
		return start_temperature;
	}
	
	// Total heat
	
	private DoubleProperty total_heat = new SimpleDoubleProperty(5362.5);
	
	public double getTotalHeat() {
		return total_heat.get();
	}
	public void setTotalHeat(double total_heat) {
		this.total_heat.set(total_heat);
	}
	@Parameter(name = "W�rmeeintrag", unit = "W")
	@DoubleParameter(min = 0)
	public DoubleProperty totalHeatProperty() {
		return total_heat;
	}
	
	// Welding speed
	
	private DoubleProperty welding_speed = new SimpleDoubleProperty(60);
	
	public double getWeldingSpeed() {
		return welding_speed.get();
	}
	public void setWeldingSpeed(double welding_speed) {
		this.welding_speed.set(welding_speed);
	}
	@Parameter(name = "Schwei�geschwindigkeit", unit = "cm/min")
	@DoubleParameter(min = 0)
	public DoubleProperty weldingSpeedProperty() {
		return welding_speed;
	}
	
	// Thermal diffusivity
	
	private DoubleProperty thermal_diffusivity = new SimpleDoubleProperty(6.18);
	
	public double getThermalDiffusivity() {
		return thermal_diffusivity.get();
	}
	public void setThermalDiffusivity(double thermal_diffusivity) {
		this.thermal_diffusivity.set(thermal_diffusivity);
	}
	@Parameter(name = "Temperaturleitf�higkeit", unit = "mm�/s")
	@DoubleParameter(min = 0)
	public DoubleProperty thermalDiffusivityProperty() {
		return thermal_diffusivity;
	}
	
	// Thermal conductivity
	
	private DoubleProperty thermal_conductivity = new SimpleDoubleProperty(29);
	
	public double getThermalConductivity() {
		return thermal_conductivity.get();
	}
	public void setThermalConductivity(double thermal_conductivity) {
		this.thermal_conductivity.set(thermal_conductivity);
	}
	@Parameter(name = "W�rmeleitf�higkeit", unit = "W/(mK)")
	@DoubleParameter(min = 0)
	public DoubleProperty thermalConductivityProperty() {
		return thermal_conductivity;
	}
	
}
