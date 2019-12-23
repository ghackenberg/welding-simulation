package com.hyperkit.welding;

import com.hyperkit.welding.configurations.ModelConfiguration;

public abstract class Model<T extends ModelConfiguration> {

	protected T configuration;
	
	public Model(T configuration) {
		this.configuration = configuration;
	}
	
	public T getConfiguration() {
		return configuration;
	}
	
	public abstract double calculateTemperature(double x, double y, double z);
	
}
