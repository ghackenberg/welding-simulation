package com.hyperkit.welding.generators;

import java.util.stream.IntStream;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Search;
import com.hyperkit.welding.configurations.Render2DConfiguration;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.structures.Path;
import com.hyperkit.welding.structures.Range;

public class GeneratorXY extends Generator2D {
	
	private Search search;
	private Render2DConfiguration configuration;
	
	public GeneratorXY(Search search, Render2DConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}
	
	public void generateDataset(Path path_min_x, Range min_x, Range max_x, double z, XYSeriesCollection result, Progress progress) throws SearchException {		
		final XYSeries lower_series_positive = new XYSeries("Innen (+)");
		final XYSeries upper_series_positive = new XYSeries("Außen (+)");
		
		final XYSeries lower_series_negative = new XYSeries("Innen (-)");
		final XYSeries upper_series_negative = new XYSeries("Außen (-)");
		
		final int samples = configuration.getXYSamples();
		
		progress.initialize(samples);
		
		IntStream.range(0, samples).parallel().forEach(sample -> {
			try {
				double lower_x = (max_x.getInnerValue() - min_x.getInnerValue()) / (samples + 1) * (sample + 1) + min_x.getInnerValue();
				double upper_x = (max_x.getOuterValue() - min_x.getOuterValue()) / (samples + 1) * (sample + 1) + min_x.getOuterValue();
				
				// Find span
				
				Range lower_y_range = new Range(0, 0);
				
				double lower_y_start = path_min_x.calculateStartY(lower_x);
				
				if (search.getModel().calculateTemperature(lower_x, lower_y_start, z) >= search.getConfiguration().getLimitTemperature()) {
					lower_y_range = search.findMaximumY(lower_x, lower_y_start, z);
				}
				
				Range upper_y_range = new Range(0, 0);
				
				double upper_y_start = path_min_x.calculateStartY(upper_x);
				
				if (search.getModel().calculateTemperature(upper_x,  upper_y_start, z) >= search.getConfiguration().getLimitTemperature()) {
					upper_y_range = search.findMaximumY(upper_x, upper_y_start, z);
				}
				
				synchronized (lower_series_positive) {
					lower_series_positive.add(lower_x * 10, lower_y_range.getInnerValue() * 10);
					upper_series_positive.add(upper_x * 10, upper_y_range.getOuterValue() * 10);
					
					lower_series_negative.add(lower_x * 10, - lower_y_range.getInnerValue() * 10);
					upper_series_negative.add(upper_x * 10, - upper_y_range.getOuterValue() * 10);
				}
				
				progress.increment();
			} catch (SearchException e) {
				throw new IllegalStateException(e);
			}
		});
		
		synchronized (lower_series_positive) {
			lower_series_positive.add(min_x.getInnerValue() * 10, 0);
			upper_series_positive.add(min_x.getOuterValue() * 10, 0);
			
			lower_series_positive.add(max_x.getInnerValue() * 10, 0);
			upper_series_positive.add(max_x.getOuterValue() * 10, 0);

			lower_series_negative.add(min_x.getInnerValue() * 10, 0);
			upper_series_negative.add(min_x.getOuterValue() * 10, 0);
			
			lower_series_negative.add(max_x.getInnerValue() * 10, 0);
			upper_series_negative.add(max_x.getOuterValue() * 10, 0);
		}
		
		result.addSeries(lower_series_positive);
		result.addSeries(upper_series_positive);
		
		result.addSeries(lower_series_negative);
		result.addSeries(upper_series_negative);
	}
	
}
