package com.hyperkit.welding.models;

import com.hyperkit.welding.Model;
import com.hyperkit.welding.configurations.models.LinearModelConfiguration;

public class LinearModel extends Model<LinearModelConfiguration> {
	
	public LinearModel(LinearModelConfiguration configuration) {
		super(configuration);
	}
	
	public double calculateTemperature(double x, double y, double z) {
		// Get samples
		
		int samples = configuration.getSamples();
		//System.out.println("Quellen: " + samples);
		
		// Get heat
		
		double heat = configuration.getTotalHeat() / 4.1868 / (samples * 2 + 1);
		//System.out.println("W‰rmeeintrag: " + heat);
		
		// Get radius
		
		double radius = configuration.getDiameter() / 2 / 10;
		//System.out.println("Pendelradius: " + radius);
		
		// Get welding speed
		
		double welding_speed = configuration.getWeldingSpeed() / 60;
		//System.out.println("Schweiﬂgeschwindigkeit: " + welding_speed);
		
		// Get thermal diffusivity
		
		double thermal_diffusivity = configuration.getThermalDiffusivity() / 100;
		//System.out.println("Temperaturleitf‰higkeit: " + thermal_diffusivity);
		
		// Get thermal conductivity
		
		double thermal_conductivity = configuration.getThermalConductivity() / 100 / 4.1868;
		//System.out.println("W‰rmeleitf‰higkeit: " + thermal_conductivity);
		
		// Get start temperature
		
		double start_temperature = configuration.getStartTemperature();
		//System.out.println("Ausgangstemperatur: " + start_temperature);
		
		// Calculate sum
		
		double sum = 0;
		
		for (int sample = -samples; sample <= samples; sample++) {
			double sample_y;
			
			if (samples == 0) {
				sample_y = 0;
			} else {
				sample_y = radius / samples * sample;
			}
			
			//System.out.println("Sample y: " + sample_y);
			
			double distance = Math.sqrt(Math.pow(x - 0, 2) + Math.pow(y - sample_y, 2) + Math.pow(z - 0, 2));
			
			//System.out.println("Square root: " + square_root);
			
			double denominator = 2 * Math.PI * thermal_conductivity * distance;
			
			//System.out.println("Denominator: " + denominator);
			
			sum += heat / denominator * Math.exp(welding_speed / 2 / thermal_diffusivity * (x - distance));
		}
		
		//System.out.println("Calculated temperature: x = " + x + ", y = " + y + ", z = " + z + ", sum = " + sum);
		
		return sum + start_temperature;
	}
	
}
