package com.hyperkit.welding.models;

import com.hyperkit.welding.Model;
import com.hyperkit.welding.configurations.models.CircularModelConfiguration;

public class CircularModel extends Model<CircularModelConfiguration> {

	public CircularModel(CircularModelConfiguration configuration) {
		super(configuration);
	}

	@Override
	public double calculateTemperature(double x, double y, double z) {
		// Get samples
		
		int samples = configuration.getSamples();
		//System.out.println("Quellen: " + samples);
		
		// Get heat
		
		double heat = configuration.getTotalHeat() / 4.1868 / samples;
		//System.out.println("W‰rmeeintrag: " + heat);
		
		// Get radius
		
		double radius = configuration.getRadius() / 10;
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
		
		for (int sample = 0; sample < samples; sample++) {
			double sample_x = radius * Math.cos(Math.PI * 2 * sample / samples);
			double sample_y = radius * Math.sin(Math.PI * 2 * sample / samples);
			
			//System.out.println("Sample y: " + sample_y);
			
			double distance = Math.sqrt(Math.pow(x - sample_x, 2) + Math.pow(y - sample_y, 2) + Math.pow(z, 2));
			
			//System.out.println("Square root: " + square_root);
			
			double denominator = 2 * Math.PI * thermal_conductivity * distance;
			
			//System.out.println("Denominator: " + denominator);
			
			sum += heat * Math.exp(welding_speed / 2 / thermal_diffusivity * (x - distance)) / denominator;
		}
		
		//System.out.println("Calculated temperature: x = " + x + ", y = " + y + ", z = " + z + ", sum = " + sum);
		
		return sum + start_temperature;
		
	}

}
