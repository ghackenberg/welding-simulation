package com.hyperkit.welding;

import java.text.DecimalFormat;

import com.hyperkit.welding.configurations.SearchConfiguration;
import com.hyperkit.welding.exceptions.SearchException;

public class Search {
	
	protected static final DecimalFormat FORMAT = new DecimalFormat("0.00");
	
	private Model<?> model;
	private SearchConfiguration configuration;
	
	public Search(Model<?> model, SearchConfiguration configuration) {
		this.model = model;
		this.configuration = configuration;
	}
	
	public Model<?> getModel() {
		return model;
	}
	
	public SearchConfiguration getConfiguration() {
		return configuration;
	}
	
	public Range findMaximumY(double x, double z) throws SearchException {
		
		System.out.println("[Search.findMaximumY(" + x + ", " + z + ")] Starting ...");
		
		long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		double limit_temperature = configuration.getLimitTemperature();
		double temperature_threshold = configuration.getTemperatureThershold();		
		double step_size = configuration.getInitialStepSize();
		
		double current_y = 0;
		double previous_y = 0;
		
		double current_temperature = model.calculateTemperature(x, current_y, z);
		double previous_temperature = model.calculateTemperature(x, previous_y, z);
		
		if (current_temperature < limit_temperature - temperature_threshold) {
			throw new SearchException("Die initiale Position bei der Suche der Y-Grenze liegt mit " + FORMAT.format(current_temperature) + "°C nicht innerhalb der Isotherm-Grenzen. Bitte passen sie dich Parameter an.");
		}
		
		// Execute the search
		
		while (Math.abs(previous_temperature - limit_temperature) > temperature_threshold) {
			if (System.currentTimeMillis() - outer_timestamp > configuration.getOuterLimit()) {
				throw new SearchException("Das äußere Zeitfenster wurde bei der Suche der Y-Grenze überschritten. Bitte passen Sie die Parameter an.");
			}
			
			long inner_timestamp = System.currentTimeMillis();
			
			if (current_temperature > limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das erste innere Zeitfenster wurde bei der Suche der Y-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_y = current_y;
					previous_temperature = current_temperature;
					
					current_y += step_size;
					current_temperature = model.calculateTemperature(x, current_y, z);
				} while (current_temperature > limit_temperature);
			} else if (current_temperature < limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das zweite innere Zeitfenster wurde bei der Suche der Y-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_y = current_y;
					previous_temperature = current_temperature;
					
					current_y -= step_size;
					current_temperature = model.calculateTemperature(x, current_y, z);
				} while (current_temperature < limit_temperature);
			}
			
			step_size /= 10.0;
		}
		
		double inner_y = Math.min(previous_y, current_y);
		double outer_y = Math.max(previous_y,  current_y);
		
		System.out.println("[Search.findMaximumY(" + x + ", " + z + ")] Returning [" + inner_y + ", " + outer_y + "]");
		
		return new Range(inner_y, outer_y);
	}
	
	public Range findMinimumZ(double x, double y) throws SearchException {
		
		System.out.println("[Search.findMinimumZ(" + x + ", " + y + ")] Starting ...");
		
		long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		double limit_temperature = configuration.getLimitTemperature();
		double temperature_threshold = configuration.getTemperatureThershold();		
		double step_size = configuration.getInitialStepSize();
		
		double current_z = 0;
		double previous_z = 0;
		
		double current_temperature = model.calculateTemperature(x, y, current_z);
		double previous_temperature = model.calculateTemperature(x, y, previous_z);

		if (current_temperature < limit_temperature - temperature_threshold) {
			throw new SearchException("Die initiale Position bei der Suche der Z-Grenze liegt mit " + FORMAT.format(current_temperature) + "°C nicht innerhalb der Isotherm-Grenzen. Bitte passen sie dich Parameter an.");
		}
		
		// Execute the search
		
		while (Math.abs(previous_temperature - limit_temperature) > temperature_threshold) {
			if (System.currentTimeMillis() - outer_timestamp > configuration.getOuterLimit()) {
				throw new SearchException("Das äußere Zeitfenster wurde bei der Suche der Z-Grenze überschritten. Bitte passen Sie die Parameter an.");
			}
			
			long inner_timestamp = System.currentTimeMillis();
			
			if (current_temperature > limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das erste innere Zeitfenster wurde bei der Suche der Z-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_z = current_z;
					previous_temperature = current_temperature;
					
					current_z -= step_size;
					current_temperature = model.calculateTemperature(x, y, current_z);
				} while (current_temperature > limit_temperature);
			} else if (current_temperature < limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das zweite innere Zeitfenster wurde bei der Suche der Z-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_z = current_z;
					previous_temperature = current_temperature;
					
					current_z += step_size;
					current_temperature = model.calculateTemperature(x, y, current_z);
				} while (current_temperature < limit_temperature);
			}
			
			step_size /= 10.0;
		}
		
		double outer_z = Math.min(previous_z, current_z);
		double inner_z = Math.max(previous_z,  current_z);
		
		System.out.println("[Search.findMinimumZ(" + x + ", " + y + ")] Returning [" + inner_z + ", " + outer_z + "]");
		
		return new Range(inner_z, outer_z);
	}
	
	public Range findMinimumX(double start_x, double y, double z) throws SearchException {
		
		System.out.println("[Search.findMinimumX(" + y + ", " + z + ")] Starting ...");
		
		long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		double limit_temperature = configuration.getLimitTemperature();
		double temperature_threshold = configuration.getTemperatureThershold();		
		double step_size = configuration.getInitialStepSize();
		
		double current_x = start_x;
		double previous_x = start_x;
		
		double current_temperature = model.calculateTemperature(current_x, y, z);
		double previous_temperature = model.calculateTemperature(previous_x, y, z);

		if (current_temperature < limit_temperature - temperature_threshold) {
			throw new SearchException("Die initiale Position bei der Suche der unteren X-Grenze liegt mit " + FORMAT.format(current_temperature) + "°C nicht innerhalb der Isotherm-Grenzen. Bitte passen sie dich Parameter an.");
		}
		
		// Execute the search
		
		while (Math.abs(previous_temperature - limit_temperature) > temperature_threshold) {
			if (System.currentTimeMillis() - outer_timestamp > configuration.getOuterLimit()) {
				throw new SearchException("Das äußere Zeitfenster wurde bei der Suche der unteren X-Grenze überschritten. Bitte passen Sie die Parameter an.");
			}
			
			long inner_timestamp = System.currentTimeMillis();
			
			if (current_temperature > limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das erste innere Zeitfenster wurde bei der Suche der unteren X-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_x = current_x;
					previous_temperature = current_temperature;
					
					current_x -= step_size;
					current_temperature = model.calculateTemperature(current_x, y, z);
				} while (current_temperature > limit_temperature);
			} else if (current_temperature < limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das zweite innere Zeitfenster wurde bei der Suche der unteren X-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_x = current_x;
					previous_temperature = current_temperature;
					
					current_x += step_size;
					current_temperature = model.calculateTemperature(current_x, y, z);
				} while (current_temperature < limit_temperature);
			}
			
			step_size /= 10.0;
		}
		
		double outer_x = Math.min(previous_x, current_x);
		double inner_x = Math.max(previous_x,  current_x);
		
		System.out.println("[Search.findMinimumX(" + y + ", " + z + ")] Returning [" + inner_x + ", " + outer_x + "]");
		
		return new Range(inner_x, outer_x);
	}
	
	public Range findMaximumX(double start_x, double y, double z) throws SearchException {
		
		System.out.println("[Search.findMaximumX(" + y + ", " + z + ")] Starting ...");
		
		long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		double limit_temperature = configuration.getLimitTemperature();
		double temperature_threshold = configuration.getTemperatureThershold();		
		double step_size = configuration.getInitialStepSize();
		
		double current_x = start_x;
		double previous_x = start_x;
		
		double current_temperature = model.calculateTemperature(current_x, y, z);
		double previous_temperature = model.calculateTemperature(previous_x, y, z);

		if (current_temperature < limit_temperature - temperature_threshold) {
			throw new SearchException("Die initiale Position bei der Suche der oberen X-Grenze liegt mit " + FORMAT.format(current_temperature) + "°C nicht innerhalb der Isotherm-Grenzen. Bitte passen sie dich Parameter an.");
		}
		
		// Execute the search
		
		while (Math.abs(previous_temperature - limit_temperature) > temperature_threshold) {
			if (System.currentTimeMillis() - outer_timestamp > configuration.getOuterLimit()) {
				throw new SearchException("Das äußere Zeitfenster wurde bei der Suche der oberen X-Grenze überschritten. Bitte passen Sie die Parameter an.");
			}
			
			long inner_timestamp = System.currentTimeMillis();
			
			if (current_temperature > limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das erste innere Zeitfenster wurde bei der Suche der oberen X-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_x = current_x;
					previous_temperature = current_temperature;
					
					current_x += step_size;
					current_temperature = model.calculateTemperature(current_x, y, z);
				} while (current_temperature > limit_temperature);
			} else if (current_temperature < limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das zweite innere Zeitfenster wurde bei der Suche der oberen X-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_x = current_x;
					previous_temperature = current_temperature;
					
					current_x -= step_size;
					current_temperature = model.calculateTemperature(current_x, y, z);
				} while (current_temperature < limit_temperature);
			}
			
			step_size /= 10.0;
		}
		
		double inner_x = Math.min(previous_x, current_x);
		double outer_x = Math.max(previous_x,  current_x);
		
		System.out.println("[Search.findMaximumX(" + y + ", " + z + ")] Returning [" + inner_x + ", " + outer_x + "]");
		
		return new Range(inner_x, outer_x);
	}
	
	public double findWidestX(double min_x, double max_x) throws SearchException {
		if (max_x - min_x > 0.000000001) {
			final int steps = 10;
			
			double delta_x = max_x - min_x;
			double step_x = delta_x / steps;
			
			double opt_x = min_x;
			double opt_w = findMaximumY(opt_x, 0).getInnerValue();
			
			for (int step = 1; step <= steps; step++) {
				double next_x = min_x + step_x * step;
				double next_t = model.calculateTemperature(next_x, 0, 0);
				if (next_t > configuration.getLimitTemperature() + configuration.getTemperatureThershold()) {
					double next_w = findMaximumY(next_x, 0).getInnerValue();
					if (next_w > opt_w) {
						opt_x = next_x;
						opt_w = next_w;
					}
				}
			}
			
			return findWidestX(opt_x - step_x, opt_x + step_x);
		} else {
			return (min_x + max_x) / 2;
		}
	}
	
	public double findDeepestX(double min_x, double max_x) throws SearchException {
		if (max_x - min_x > 0.000000001) {
			final int steps = 10;
			
			double delta_x = max_x - min_x;
			double step_x = delta_x / steps;
			
			double opt_x = min_x;
			double opt_d = findMinimumZ(opt_x, 0).getInnerValue();
			
			for (int step = 0; step <= steps; step++) {
				double next_x = min_x + step_x * step;
				double next_t = model.calculateTemperature(next_x, 0, 0);
				if (next_t > configuration.getLimitTemperature() + configuration.getTemperatureThershold()) {
					double next_d = findMinimumZ(next_x, 0).getInnerValue();
					if (next_d < opt_d) {
						opt_x = next_x;
						opt_d = next_d;
					}
				}
			}
			
			return findDeepestX(opt_x - step_x, opt_x + step_x);
		} else {
			return (min_x + max_x) / 2;
		}
	}
	
}
