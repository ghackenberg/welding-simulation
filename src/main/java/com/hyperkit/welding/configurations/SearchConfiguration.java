package com.hyperkit.welding.configurations;

import com.hyperkit.welding.Configuration;
import com.hyperkit.welding.annotations.DoubleParameter;
import com.hyperkit.welding.annotations.LongParameter;
import com.hyperkit.welding.annotations.Parameter;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.LongProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleLongProperty;

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
	@Parameter(name = "Zieltemperatur", unit = "�C", order = 0)
	@DoubleParameter(min = 0)
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
	@Parameter(name = "Temperaturgenaugigkeit", unit = "�C", order = 1)
	@DoubleParameter(min = 0)
	public DoubleProperty temperatureThresholdProperty() {
		return temperature_threshold;
	}
	
	// Initial position min
	
	private DoubleProperty initial_position_min = new SimpleDoubleProperty(0);
	
	public double getInitialPositionMin() {
		return initial_position_min.get();
	}
	public void setInitialPositionMin(double initial_position_min) {
		this.initial_position_min.set(initial_position_min);
	}
	@Parameter(name = "Initiale Position (min)", unit = "mm", order = 2)
	@DoubleParameter
	public DoubleProperty initialPositionMinProperty() {
		return initial_position_min;
	}
	
	// Initial position max
	
	private DoubleProperty initial_position_max = new SimpleDoubleProperty(0);
	
	public double getInitialPositionMax() {
		return initial_position_max.get();
	}
	public void setInitialPositionMax(double initial_position_max) {
		this.initial_position_max.set(initial_position_max);
	}
	@Parameter(name = "Initiale Position (max)", unit = "mm", order = 3)
	@DoubleParameter
	public DoubleProperty initialPositionMaxProperty() {
		return initial_position_max;
	}
	
	// Initial step size

	private DoubleProperty initial_step_size = new SimpleDoubleProperty(1);
	
	public double getInitialStepSize() {
		return initial_step_size.get();
	}
	public void setInitialStepSize(double initial_step_size) {
		this.initial_step_size.set(initial_step_size);
	}
	@Parameter(name = "Initiale Schrittweite", unit = "mm", order = 4)
	@DoubleParameter(min = 0)
	public DoubleProperty initialStepSizeProperty() {
		return initial_step_size;
	}
	
	// Inneres Zeitfenster

	private LongProperty inner_limit = new SimpleLongProperty(1000);
	
	public double getInnerLimit() {
		return inner_limit.get();
	}
	public void setInnerLimit(long inner_limit) {
		this.inner_limit.set(inner_limit);
	}
	@Parameter(name = "Inneres Zeitfenster", unit = "ms", order = 5)
	@LongParameter(min = 1)
	public LongProperty innerLimitProperty() {
		return inner_limit;
	}
	
	// �u�eres Zeitfenster

	private LongProperty outer_limit = new SimpleLongProperty(1000000);
	
	public double getOuterLimit() {
		return outer_limit.get();
	}
	public void setOuterLimit(long outer_limit) {
		this.outer_limit.set(outer_limit);
	}
	@Parameter(name = "�u�eres Zeitfenster", unit = "ms", order = 6)
	@LongParameter(min = 1)
	public LongProperty outerLimitProperty() {
		return outer_limit;
	}
	
}
