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
	
	public Range findMinimumY(final double x, final double start_y, final double z) throws SearchException {
		
		// System.out.println("[Search.findMinimumY(" + x + ", " + start_y + ", " + z + ")] Starting ...");
		
		final long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		final double limit_temperature = configuration.getLimitTemperature();
		final double temperature_threshold = configuration.getTemperatureThershold();		
		double step_size = configuration.getInitialStepSize();
		
		double current_y = start_y;
		double previous_y = start_y;
		
		double current_temperature = model.calculateTemperature(x, current_y, z);
		double previous_temperature = model.calculateTemperature(x, previous_y, z);
		
		if (current_temperature < limit_temperature) {
			throw new SearchException("Die initiale Position bei der Suche der minmalen Y-Grenze liegt mit " + FORMAT.format(current_temperature) + "°C nicht innerhalb der Isotherm-Grenzen. Bitte passen sie die Parameter an.");
		}
		
		// Execute the search
		
		do {
			if (System.currentTimeMillis() - outer_timestamp > configuration.getOuterLimit()) {
				throw new SearchException("Das äußere Zeitfenster wurde bei der Suche der minimalen Y-Grenze überschritten. Bitte passen Sie die Parameter an.");
			}
			
			final long inner_timestamp = System.currentTimeMillis();
			
			if (current_temperature > limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das erste innere Zeitfenster wurde bei der Suche der minimalen Y-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_y = current_y;
					previous_temperature = current_temperature;
					
					current_y -= step_size;
					current_temperature = model.calculateTemperature(x, current_y, z);
				} while (current_temperature > limit_temperature);
			} else if (current_temperature < limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das zweite innere Zeitfenster wurde bei der Suche der minimalen Y-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_y = current_y;
					previous_temperature = current_temperature;
					
					current_y += step_size;
					current_temperature = model.calculateTemperature(x, current_y, z);
				} while (current_temperature < limit_temperature);
			}
			
			step_size /= 10.0;
		} while (Math.abs(previous_temperature - limit_temperature) > temperature_threshold || Math.abs(current_temperature - limit_temperature) > temperature_threshold);
		
		final double inner_y = Math.max(previous_y, current_y);
		final double outer_y = Math.min(previous_y,  current_y);
		
		// System.out.println("[Search.findMinimumY(" + x + ", " + start_y + ", " + z + ")] Returning [" + inner_y + ", " + outer_y + "]");
		
		return new Range(inner_y, outer_y);
	}
	
	public Range findMaximumY(final double x, final double start_y, final double z) throws SearchException {
		
		// System.out.println("[Search.findMaximumY(" + x + ", " + start_y + ", " + z + ")] Starting ...");
		
		long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		final double limit_temperature = configuration.getLimitTemperature();
		final double temperature_threshold = configuration.getTemperatureThershold();		
		double step_size = configuration.getInitialStepSize();
		
		double current_y = start_y;
		double previous_y = start_y;
		
		double current_temperature = model.calculateTemperature(x, current_y, z);
		double previous_temperature = model.calculateTemperature(x, previous_y, z);
		
		if (current_temperature < limit_temperature) {
			throw new SearchException("Die initiale Position bei der Suche der maximalen Y-Grenze liegt mit " + FORMAT.format(current_temperature) + "°C nicht innerhalb der Isotherm-Grenzen. Bitte passen sie die Parameter an.");
		}
		
		// Execute the search
		
		do {
			if (System.currentTimeMillis() - outer_timestamp > configuration.getOuterLimit()) {
				throw new SearchException("Das äußere Zeitfenster wurde bei der Suche der maximalen Y-Grenze überschritten. Bitte passen Sie die Parameter an.");
			}
			
			final long inner_timestamp = System.currentTimeMillis();
			
			if (current_temperature > limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das erste innere Zeitfenster wurde bei der Suche der maximalen Y-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_y = current_y;
					previous_temperature = current_temperature;
					
					current_y += step_size;
					current_temperature = model.calculateTemperature(x, current_y, z);
				} while (current_temperature > limit_temperature);
			} else if (current_temperature < limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das zweite innere Zeitfenster wurde bei der Suche der maximalen Y-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_y = current_y;
					previous_temperature = current_temperature;
					
					current_y -= step_size;
					current_temperature = model.calculateTemperature(x, current_y, z);
				} while (current_temperature < limit_temperature);
			}
			
			step_size /= 10.0;
		} while (Math.abs(previous_temperature - limit_temperature) > temperature_threshold || Math.abs(current_temperature - limit_temperature) > temperature_threshold);
		
		final double inner_y = Math.min(previous_y, current_y);
		final double outer_y = Math.max(previous_y,  current_y);
		
		// System.out.println("[Search.findMaximumY(" + x + ", " + start_y + ", " + z + ")] Returning [" + inner_y + ", " + outer_y + "]");
		
		return new Range(inner_y, outer_y);
	}
	
	public Range findMinimumZ(final double x, final double y, final double start_z) throws SearchException {
		
		// System.out.println("[Search.findMinimumZ(" + x + ", " + y + ", " + start_z + ")] Starting ...");
		
		final long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		final double limit_temperature = configuration.getLimitTemperature();
		final double temperature_threshold = configuration.getTemperatureThershold();		
		double step_size = configuration.getInitialStepSize();
		
		double current_z = start_z;
		double previous_z = start_z;
		
		double current_temperature = model.calculateTemperature(x, y, current_z);
		double previous_temperature = model.calculateTemperature(x, y, previous_z);

		if (current_temperature < limit_temperature) {
			throw new SearchException("Die initiale Position bei der Suche der Z-Grenze liegt mit " + FORMAT.format(current_temperature) + "°C nicht innerhalb der Isotherm-Grenzen. Bitte passen sie die Parameter an.");
		}
		
		// Execute the search
		
		do {
			if (System.currentTimeMillis() - outer_timestamp > configuration.getOuterLimit()) {
				throw new SearchException("Das äußere Zeitfenster wurde bei der Suche der Z-Grenze überschritten. Bitte passen Sie die Parameter an.");
			}
			
			final long inner_timestamp = System.currentTimeMillis();
			
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
		} while (Math.abs(previous_temperature - limit_temperature) > temperature_threshold || Math.abs(current_temperature - limit_temperature) > temperature_threshold);
		
		final double outer_z = Math.min(previous_z, current_z);
		final double inner_z = Math.max(previous_z,  current_z);
		
		// System.out.println("[Search.findMinimumZ(" + x + ", " + y + ", " + start_z + ")] Returning [" + inner_z + ", " + outer_z + "]");
		
		return new Range(inner_z, outer_z);
	}
	
	public Range findMinimumX(final double start_x, final double y, final double z) throws SearchException {
		
		// System.out.println("[Search.findMinimumY(" + x + ", " + start_y + ", " + z + ")] Starting ...");
		
		final long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		final double limit_temperature = configuration.getLimitTemperature();
		final double temperature_threshold = configuration.getTemperatureThershold();		
		double step_size = configuration.getInitialStepSize();
		
		double current_x = start_x;
		double previous_x = start_x;
		
		double current_temperature = model.calculateTemperature(current_x, y, z);
		double previous_temperature = model.calculateTemperature(previous_x, y, z);
		
		if (current_temperature < limit_temperature) {
			throw new SearchException("Die initiale Position bei der Suche der minmalen X-Grenze liegt mit " + FORMAT.format(current_temperature) + "°C nicht innerhalb der Isotherm-Grenzen. Bitte passen sie die Parameter an.");
		}
		
		// Execute the search
		
		do {
			if (System.currentTimeMillis() - outer_timestamp > configuration.getOuterLimit()) {
				throw new SearchException("Das äußere Zeitfenster wurde bei der Suche der minimalen X-Grenze überschritten. Bitte passen Sie die Parameter an.");
			}
			
			final long inner_timestamp = System.currentTimeMillis();
			
			if (current_temperature > limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das erste innere Zeitfenster wurde bei der Suche der minimalen X-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_x = current_x;
					previous_temperature = current_temperature;
					
					current_x -= step_size;
					current_temperature = model.calculateTemperature(current_x, y, z);
				} while (current_temperature > limit_temperature);
			} else if (current_temperature < limit_temperature) {
				do {
					if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
						throw new SearchException("Das zweite innere Zeitfenster wurde bei der Suche der minimalen X-Grenze überschritten. Bitte passen Sie die Parameter an.");	
					}
					previous_x = current_x;
					previous_temperature = current_temperature;
					
					current_x += step_size;
					current_temperature = model.calculateTemperature(current_x, y, z);
				} while (current_temperature < limit_temperature);
			}
			
			step_size /= 10.0;
		} while (Math.abs(previous_temperature - limit_temperature) > temperature_threshold || Math.abs(current_temperature - limit_temperature) > temperature_threshold);
		
		final double inner_x = Math.max(previous_x, current_x);
		final double outer_x = Math.min(previous_x,  current_x);
		
		// System.out.println("[Search.findMinimumY(" + x + ", " + start_y + ", " + z + ")] Returning [" + inner_y + ", " + outer_y + "]");
		
		return new Range(inner_x, outer_x);
	}
	
	public Path findMinimumXPath(final double start_x, final double start_y, final double z) throws SearchException {
		
		// System.out.println("[Search.findMinimumX(" + start_x + ", " + start_y + ", " + z + ")] Starting ...");
		
		final long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		final double limit_temperature = configuration.getLimitTemperature();
		final double temperature_threshold = configuration.getTemperatureThershold();		
		double step_size = configuration.getInitialStepSize();
		
		double origin_x = start_x;
		double current_x = start_x;
		double previous_x = start_x;
		
		double current_y = start_y;
		
		double current_temperature = model.calculateTemperature(current_x, current_y, z);
		double previous_temperature = model.calculateTemperature(previous_x, current_y, z);

		if (current_temperature < limit_temperature) {
			throw new SearchException("Die initiale Position bei der Suche des unteren X-Pfades liegt mit " + FORMAT.format(current_temperature) + "°C nicht innerhalb der Isotherm-Grenzen. Bitte passen sie die Parameter an.");
		}
		
		Path path = new Path();
		
		// Execute the search
		
		while (true) {			
			do {
				if (System.currentTimeMillis() - outer_timestamp > configuration.getOuterLimit()) {
					throw new SearchException("Das äußere Zeitfenster wurde bei der Suche des unteren X-Pfades überschritten. Bitte passen Sie die Parameter an.");
				}
				
				final long inner_timestamp = System.currentTimeMillis();
				
				if (current_temperature > limit_temperature) {
					do {
						if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
							throw new SearchException("Das erste innere Zeitfenster wurde bei der Suche des unteren X-Pfades überschritten. Bitte passen Sie die Parameter an.");	
						}
						previous_x = current_x;
						previous_temperature = current_temperature;
						
						current_x -= step_size;
						current_temperature = model.calculateTemperature(current_x, current_y, z);
					} while (current_temperature > limit_temperature);
				} else if (current_temperature < limit_temperature) {
					do {
						if (System.currentTimeMillis() - inner_timestamp > configuration.getInnerLimit()) {
							throw new SearchException("Das zweite innere Zeitfenster wurde bei der Suche des unteren X-Pfades überschritten. Bitte passen Sie die Parameter an.");	
						}
						previous_x = current_x;
						previous_temperature = current_temperature;
						
						current_x += step_size;
						current_temperature = model.calculateTemperature(current_x, current_y, z);
					} while (current_temperature < limit_temperature);
				}
				
				step_size /= 10.0;
			} while (Math.abs(previous_temperature - limit_temperature) > temperature_threshold || Math.abs(current_temperature - limit_temperature) > temperature_threshold);

			Span span = new Span(origin_x, new Range(Math.max(previous_x,  current_x), Math.min(previous_x, current_x)), current_y, z);
			
			System.out.println("[Search.findMinimumX(" + start_x + ", " + start_y + ", " + z + ")] Adding span " + path.getSpans().size() + " = " + span);
			
			path.getSpans().add(span);
			
			final double maximum_y = findMaximumY(Math.max(previous_x,  current_x), current_y, z).getInnerValue();
			final double next_y = findDeepestY(Math.max(previous_x,  current_x), 0, maximum_y);
			
			// System.out.println("[Search.findMinimumX(" + start_x + ", " + start_y + ", " + z + ")] Previous x = " + previous_x);
			// System.out.println("[Search.findMinimumX(" + start_x + ", " + start_y + ", " + z + ")] Current x = " + current_x);
			
			// System.out.println("[Search.findMinimumX(" + start_x + ", " + start_y + ", " + z + ")] Current y = " + current_y);
			// System.out.println("[Search.findMinimumX(" + start_x + ", " + start_y + ", " + z + ")] Maximum y = " + maximum_y);
			// System.out.println("[Search.findMinimumX(" + start_x + ", " + start_y + ", " + z + ")] Next y = " + next_y);
			
			if (Math.abs(next_y - current_y) > 0.000000001) {
				origin_x = Math.max(previous_x,  current_x);
				
				current_y = next_y;
				
				current_temperature = model.calculateTemperature(current_x, current_y, z);
				previous_temperature = model.calculateTemperature(previous_x, current_y, z);
				
				// System.out.println("[Search.findMinimumX(" + start_x + ", " + start_y + ", " + z + ")] Current temperature = " + current_temperature);
				// System.out.println("[Search.findMinimumX(" + start_x + ", " + start_y + ", " + z + ")] Previous temperature = " + previous_temperature);
				
				step_size = configuration.getInitialStepSize();
			} else {
				break;
			}
		}
		
		// System.out.println("[Search.findMinimumX(" + start_x + ", " + start_y + ", " + z + ")] Returning " + path);
		
		return path;
	}
	
	public Range findMaximumX(final double start_x, final double y, final double z) throws SearchException {
		
		// System.out.println("[Search.findMaximumX(" + start_x + ", " + y + ", " + z + ")] Starting ...");
		
		final long outer_timestamp = System.currentTimeMillis();
		
		// Initialize the search variables
		
		final double limit_temperature = configuration.getLimitTemperature();
		final double temperature_threshold = configuration.getTemperatureThershold();		
		double step_size = configuration.getInitialStepSize();
		
		double current_x = start_x;
		double previous_x = start_x;
		
		double current_temperature = model.calculateTemperature(current_x, y, z);
		double previous_temperature = model.calculateTemperature(previous_x, y, z);

		if (current_temperature < limit_temperature) {
			throw new SearchException("Die initiale Position bei der Suche der oberen X-Grenze liegt mit " + FORMAT.format(current_temperature) + "°C nicht innerhalb der Isotherm-Grenzen. Bitte passen sie die Parameter an.");
		}
		
		// Execute the search
		
		do {
			if (System.currentTimeMillis() - outer_timestamp > configuration.getOuterLimit()) {
				throw new SearchException("Das äußere Zeitfenster wurde bei der Suche der oberen X-Grenze überschritten. Bitte passen Sie die Parameter an.");
			}
			
			final long inner_timestamp = System.currentTimeMillis();
			
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
		} while (Math.abs(previous_temperature - limit_temperature) > temperature_threshold || Math.abs(current_temperature - limit_temperature) > temperature_threshold);
		
		final double inner_x = Math.min(previous_x, current_x);
		final double outer_x = Math.max(previous_x,  current_x);
		
		// System.out.println("[Search.findMaximumX(" + start_x + ", " + y + ", " + z + ")] Returning [" + inner_x + ", " + outer_x + "]");
		
		return new Range(inner_x, outer_x);
	}
	
	public double findWidestX(final double min_x, final double max_x, final double y, final double z) throws SearchException {
		// System.out.println("[Search.findWidestX(" + min_x + ", " + max_x + ", " + z + ")]");
		if (max_x - min_x > 0.000000001) {
			final int steps = 10;
			
			final double delta_x = max_x - min_x;
			final double step_x = delta_x / steps;
			
			double opt_x = min_x;
			double opt_w = findMaximumY(opt_x, y, z).getInnerValue();
			
			for (int step = 1; step <= steps; step++) {
				final double next_x = min_x + step_x * step;
				final double next_t = model.calculateTemperature(next_x, y, z);
				
				if (next_t > configuration.getLimitTemperature() + configuration.getTemperatureThershold()) {
					final double next_w = findMaximumY(next_x, y, z).getInnerValue();
					
					if (next_w > opt_w) {
						opt_x = next_x;
						opt_w = next_w;
					}
				}
			}
			
			return findWidestX(Math.max(opt_x - step_x, min_x), Math.min(opt_x + step_x, max_x), y, z);
		} else {
			return (min_x + max_x) / 2;
		}
	}
	
	public double findDeepestX(final double min_x, final double max_x, final double y) throws SearchException {
		// System.out.println("[Search.findDeepestX(" + min_x + ", " + max_x + ", " + y + ")]");
		if (max_x - min_x > 0.000000001) {
			final int steps = 10;
			
			final double delta_x = max_x - min_x;
			final double step_x = delta_x / steps;
			
			double opt_x = min_x;
			double opt_d = findMinimumZ(opt_x, y, 0).getInnerValue();
			
			for (int step = 0; step <= steps; step++) {
				final double next_x = min_x + step_x * step;
				final double next_t = model.calculateTemperature(next_x, y, 0);
				
				if (next_t > configuration.getLimitTemperature() + configuration.getTemperatureThershold()) {
					final double next_d = findMinimumZ(next_x, y, 0).getInnerValue();
					
					if (next_d < opt_d) {
						opt_x = next_x;
						opt_d = next_d;
					}
				}
			}
			
			return findDeepestX(Math.max(opt_x - step_x, min_x), Math.min(opt_x + step_x, max_x), y);
		} else {
			return (min_x + max_x) / 2;
		}
	}
	
	public double findDeepestY(final double x, final double min_y, final double max_y) throws SearchException {
		// System.out.println("[Search.findDeepestY(" + x + ", " + min_y + ", " + max_y + ")]");
		if (max_y - min_y > 0.000000001) {
			final int steps = 10;
			
			final double delta_y = max_y - min_y;
			final double step_y = delta_y / steps;
			
			double opt_y = max_y;
			double opt_d = findMinimumZ(x, opt_y, 0).getInnerValue();
			
			for (int step = 0; step <= steps; step++) {
				final double next_y = min_y + step_y * step;
				final double next_t = model.calculateTemperature(x, next_y, 0);
				
				if (next_t > configuration.getLimitTemperature() + configuration.getTemperatureThershold()) {
					final double next_d = findMinimumZ(x, next_y, 0).getInnerValue();
					
					if (next_d < opt_d) {
						opt_y = next_y;
						opt_d = next_d;
					}
				}
			}
			
			return findDeepestY(x, Math.max(opt_y - step_y, min_y), Math.min(opt_y + step_y, max_y));
		} else {
			return (min_y + max_y) / 2;
		}
	}
	
}
