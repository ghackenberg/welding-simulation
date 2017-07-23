package com.hyperkit.welding;

import com.hyperkit.welding.configurations.SearchConfiguration;
import com.hyperkit.welding.exceptions.SearchException;

public class Search {
	
	// Limit the number of iterations
	private static final long OUTER_LIMIT = 10000;
	private static final long INNER_LIMIT = 100;
	
	private Model model;
	private SearchConfiguration configuration;
	
	public Search(Model model, SearchConfiguration configuration) {
		this.model = model;
		this.configuration = configuration;
	}
	
	public Model getModel() {
		return model;
	}
	
	public Range findMaximumY(double x, double z) throws SearchException {
		
		long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		double limit_temperature = configuration.getLimitTemperature();
		double temperature_threshold = 0.001; //configuration.getTemperatureThershold();
		
		double step_size = model.getConfiguration().getDiameter() / 2.0;
		
		double lower_y = 0;
		double upper_y = 0;
		
		double lower_temperature = model.calculateTemperature(x, lower_y, z);
		double upper_temperature = model.calculateTemperature(x, upper_y, z);
		
		// Execute the search
		
		while (limit_temperature < lower_temperature || limit_temperature - lower_temperature > temperature_threshold || limit_temperature > upper_temperature || upper_temperature - limit_temperature > temperature_threshold) {
			
			System.out.println("Step size: " + step_size);
			
			if (step_size == 0.0) {
				throw new SearchException("Problem: x = " + x + ", lower_y = " + lower_y + ", upper_y = " + upper_y + ", z = " + z + ", step_size = " + step_size + ", limit_tempature = " + limit_temperature + ", lower_temperature = " + lower_temperature + ", upper_temperature = " + upper_temperature);
			}
			
			if (System.currentTimeMillis() - outer_timestamp > OUTER_LIMIT) {
				throw new SearchException("Problem 1: x = " + x + ", y = " + lower_y + ", z = " + z + ", step_size = " + step_size + ", limit_tempature = " + limit_temperature + ", lower_temperature = " + lower_temperature + ", upper_temperature = " + upper_temperature);
			}
			
			// Update lower limit
			
			if (lower_temperature > limit_temperature) {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Execute the search
				
				do {
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 2: x = " + x + ", y = " + lower_y + ", z = " + z + ", step_size = " + step_size + ", lower_temperature = " + lower_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					lower_y -= step_size;
					lower_temperature = model.calculateTemperature(x, lower_y, z);
					
				} while (lower_temperature > limit_temperature);
				
			}
			else {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Initialize the search variables
				
				double next_temperature;
				
				// Execute the search
				
				while (lower_y + step_size <= 0 && (next_temperature = model.calculateTemperature(x, lower_y + step_size, z)) < limit_temperature) {
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 3: x = " + x + ", y = " + lower_y + ", z = " + z + ", step_size = " + step_size + ", next_temperature = " + next_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					lower_y += step_size;
					lower_temperature = next_temperature;
				}
			}
			
			// Update upper limit
			
			if (upper_temperature < limit_temperature) {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Execute the search
				
				do {
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 4: x = " + x + ", y = " + lower_y + ", z = " + z + ", step_size = " + step_size + ", upper_temperature = " + upper_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					upper_y += step_size;
					upper_temperature = model.calculateTemperature(x, upper_y, z);
					
				} while (upper_temperature < limit_temperature);
				
			} else {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Initialize the search variables
				
				double next_temperature;
				
				// Execute the search
				
				while ((next_temperature = model.calculateTemperature(x, upper_y - step_size, z)) > limit_temperature) {
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 5: x = " + x + ", y = " + lower_y + ", z = " + z + ", step_size = " + step_size + ", next_temperature = " + next_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					upper_y -= step_size;
					upper_temperature = next_temperature;
				}
			}
			
			// Update step size
			
			step_size /= 2.0;
		}
		
		// Return the range
		
		return new Range(lower_y, upper_y);
	}
	
	public Range findMaximumZ(double x, double y) throws SearchException {
		
		long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		double limit_temperature = configuration.getLimitTemperature();
		double temperature_threshold = configuration.getTemperatureThershold();
		
		double step_size = model.getConfiguration().getDiameter() / 2.0;
		
		double lower_z = 0;
		double upper_z = 0;
		
		double lower_temperature = model.calculateTemperature(x, y, lower_z);
		double upper_temperature = model.calculateTemperature(x, y, upper_z);
		
		// Execute the search
		
		while (limit_temperature < lower_temperature || limit_temperature - lower_temperature > temperature_threshold || limit_temperature > upper_temperature || upper_temperature - limit_temperature > temperature_threshold) {
			
			//System.out.println("Step size: " + step_size);
			
			if (step_size == 0.0) {
				throw new SearchException("Problem: x = " + x + ", y = " + y + ", lower_z = " + lower_z + ", upper_z = " + upper_z + ", step_size = " + step_size + ", limit_tempature = " + limit_temperature + ", lower_temperature = " + lower_temperature + ", upper_temperature = " + upper_temperature);
			}
			
			if (System.currentTimeMillis() - outer_timestamp > OUTER_LIMIT) {
				throw new SearchException("Problem 6: x = " + x + ", y = " + y + ", z = " + lower_z + ", step_size = " + step_size + ", limit_tempature = " + limit_temperature + ", lower_temperature = " + lower_temperature + ", upper_temperature = " + upper_temperature);
			}
			
			// Update lower limit
			
			if (lower_temperature > limit_temperature) {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Execute the search
				
				do {
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 7: x = " + x + ", y = " + y + ", z = " + lower_z + ", step_size = " + step_size + ", lower_temperature = " + lower_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					lower_z -= step_size;
					lower_temperature = model.calculateTemperature(x, y, lower_z);
					
				} while(lower_temperature > limit_temperature);
				
			}
			else {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Initialize the search variables
				
				double next_temperature;
				
				// Execute the search
				
				while (lower_z + step_size <= 0 && (next_temperature = model.calculateTemperature(x, y, lower_z + step_size)) < limit_temperature) {
					// Check the iteration number
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 8: x = " + x + ", y = " + y + ", z = " + lower_z + ", step_size = " + step_size + ", next_temperature = " + next_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					lower_z += step_size;
					lower_temperature = next_temperature;
				}
			}
			
			// Update upper limit
			
			if (upper_temperature < limit_temperature) {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Execute the search
				
				do {
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 9: x = " + x + ", y = " + y + ", z = " + lower_z + ", step_size = " + step_size + ", upper_temperature = " + upper_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					upper_z += step_size;
					upper_temperature = model.calculateTemperature(x, y, upper_z);
					
				} while (upper_temperature < limit_temperature);
				
			} else {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Initialize the search variables
				
				double next_temperature;
				
				// Execute the search
				
				while ((next_temperature = model.calculateTemperature(x, y, upper_z - step_size)) > limit_temperature) {
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 10: x = " + x + ", y = " + y + ", z = " + lower_z + ", step_size = " + step_size + ", next_temperature = " + next_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					upper_z -= step_size;
					upper_temperature = next_temperature;
				}
			}
			
			// Update step size
			
			step_size /= 2.0;	
		}
		
		return new Range(lower_z, upper_z);
	}
	
	public Range findMaximumX(double y, double z) throws SearchException {
		
		long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		double limit_temperature = configuration.getLimitTemperature();
		double temperature_threshold = configuration.getTemperatureThershold();
		
		double step_size = model.getConfiguration().getDiameter() / 2.0;
		
		double lower_x = 0;
		double upper_x = 0;
		
		double lower_temperature = model.calculateTemperature(lower_x, y, z);
		double upper_temperature = model.calculateTemperature(upper_x, y, z);
		
		// Execute the search
		
		while (limit_temperature < lower_temperature || limit_temperature - lower_temperature > temperature_threshold || limit_temperature > upper_temperature || upper_temperature - limit_temperature > temperature_threshold) {
			
			System.out.println("Step size: " + step_size + ", lower_x: " + lower_x + ", upper_x: " + upper_x + ", lower_temp: " + lower_temperature + ", upper_temp: " + upper_temperature);
			
			if (step_size == 0.0) {
				throw new SearchException("Problem: lower_x = " + lower_x + ", upper x = " + upper_x + ", y = " + y + ", z = " + z + ", step_size = " + step_size + ", limit_tempature = " + limit_temperature + ", lower_temperature = " + lower_temperature + ", upper_temperature = " + upper_temperature);
			}
			
			if (System.currentTimeMillis() - outer_timestamp > OUTER_LIMIT) {
				throw new SearchException("Problem 6: x = " + lower_x + ", y = " + y + ", z = " + z + ", step_size = " + step_size + ", limit_tempature = " + limit_temperature + ", lower_temperature = " + lower_temperature + ", upper_temperature = " + upper_temperature);
			}
			
			// Update lower limit
			
			if (lower_temperature > limit_temperature) {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Execute the search
				
				do {
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 7: x = " + lower_x + ", y = " + y + ", z = " + z + ", step_size = " + step_size + ", lower_temperature = " + lower_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					lower_x += step_size;
					lower_temperature = model.calculateTemperature(lower_x, y, z);
					
				} while(lower_temperature > limit_temperature);
				
			}
			else {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Initialize the search variables
				
				double next_temperature;
				
				// Execute the search
				
				while (lower_x - step_size >= 0 && (next_temperature = model.calculateTemperature(lower_x - step_size, y, z)) < limit_temperature) {
					// Check the iteration number
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 8: x = " + lower_x + ", y = " + y + ", z = " + z + ", step_size = " + step_size + ", next_temperature = " + next_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					lower_x -= step_size;
					lower_temperature = next_temperature;
				}
			}
			
			// Update upper limit
			
			if (upper_temperature < limit_temperature) {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Execute the search
				
				do {
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 9: x = " + lower_x + ", y = " + y + ", z = " + z + ", step_size = " + step_size + ", upper_temperature = " + upper_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					upper_x -= step_size;
					upper_temperature = model.calculateTemperature(upper_x, y, z);
					
				} while (upper_temperature < limit_temperature);
				
			} else {
				
				long inner_timestamp = System.currentTimeMillis();
				
				// Initialize the search variables
				
				double next_temperature;
				
				// Execute the search
				
				while ((next_temperature = model.calculateTemperature(upper_x + step_size, y, z)) > limit_temperature) {
					
					if (System.currentTimeMillis() - inner_timestamp > INNER_LIMIT) {
						throw new SearchException("Problem 10: x = " + lower_x + ", y = " + y + ", z = " + z + ", step_size = " + step_size + ", next_temperature = " + next_temperature + ", limit_tempature = " + limit_temperature);
					}
					
					// Update the search variables
					
					upper_x += step_size;
					upper_temperature = next_temperature;
				}
			}
			
			// Update step size
			
			step_size /= 2.0;	
		}
		
		return new Range(lower_x, upper_x);
	}
	
}
