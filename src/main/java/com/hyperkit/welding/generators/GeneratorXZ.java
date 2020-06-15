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
import com.hyperkit.welding.structures.extremes.DeepestY;

public class GeneratorXZ extends Generator2D {
	
	private Search search;
	private Render2DConfiguration configuration;
	
	public GeneratorXZ(Search search, Render2DConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}
	
	public void generateDataset(Path path_min_x, Range min_x, Range max_x, XYSeriesCollection result, Progress progress) throws SearchException {
		final XYSeries lower_total_series = new XYSeries("Innen");
		final XYSeries upper_total_series = new XYSeries("Außen");
		
		final int samples = configuration.getXZSamples();
		
		progress.initialize(samples);
		
		IntStream.range(0, samples).parallel().forEach(sample -> {
			try {
				double lower_x = (max_x.getInnerValue() - min_x.getInnerValue()) / (samples + 1) * (sample + 1) + min_x.getInnerValue();
				double upper_x = (max_x.getOuterValue() - min_x.getOuterValue()) / (samples + 1) * (sample + 1) + min_x.getOuterValue();
				
				double lower_max_y = search.findMaximumY(lower_x, path_min_x.calculateStartY(lower_x), 0).getInnerValue();
				double upper_max_y = search.findMaximumY(upper_x, path_min_x.calculateStartY(upper_x), 0).getInnerValue();
				
				DeepestY lower_y = search.findDeepestY(lower_x, 0, lower_max_y);
				DeepestY upper_y = search.findDeepestY(upper_x, 0, upper_max_y);
				
				synchronized (lower_total_series) {
					lower_total_series.add(lower_x * 10, lower_y.getMinZ().getInnerValue() * 10);
					upper_total_series.add(upper_x * 10, upper_y.getMinZ().getOuterValue() * 10);
				}
				
				progress.increment();
			} catch (SearchException e) {
				throw new IllegalStateException(e);
			}
		});
		
		synchronized (lower_total_series) {
			lower_total_series.add(min_x.getInnerValue() * 10, 0);
			upper_total_series.add(min_x.getOuterValue() * 10, 0);
			
			lower_total_series.add(max_x.getInnerValue() * 10, 0);
			upper_total_series.add(max_x.getOuterValue() * 10, 0);
		}

		result.addSeries(lower_total_series);
		result.addSeries(upper_total_series);
	}
	
	public void generateDataset(Range min_x, Range max_x, double y, XYSeriesCollection result, Progress progress) throws SearchException {
		final XYSeries lower_series = new XYSeries("Innen");
		final XYSeries upper_series = new XYSeries("Außen");
		
		final int samples = configuration.getXZSamples();
		
		progress.initialize(samples);
		
		IntStream.range(0, samples).parallel().forEach(sample -> {
			try {
				double lower_x = (max_x.getInnerValue() - min_x.getInnerValue()) / (samples + 1) * (sample + 1) + min_x.getInnerValue();
				double upper_x = (max_x.getOuterValue() - min_x.getOuterValue()) / (samples + 1) * (sample + 1) + min_x.getOuterValue();
				
				Range lower_z_range = new Range(0, 0);
				
				if (search.getModel().calculateTemperature(lower_x, y, 0) >= search.getConfiguration().getLimitTemperature()) {
					lower_z_range = search.findMinimumZ(lower_x, y, 0);
				}
				
				Range upper_z_range = new Range(0, 0);
				
				if (search.getModel().calculateTemperature(upper_x, y, 0) >= search.getConfiguration().getLimitTemperature()) {
					upper_z_range = search.findMinimumZ(upper_x, y, 0);
				}
				
				synchronized (lower_series) {
					lower_series.add(lower_x * 10, lower_z_range.getInnerValue() * 10);
					upper_series.add(upper_x * 10, upper_z_range.getOuterValue() * 10);
				}
				
				progress.increment();
			} catch (SearchException e) {
				throw new IllegalStateException(e);
			}
		});
		
		synchronized (lower_series) {
			lower_series.add(min_x.getInnerValue() * 10, 0);
			upper_series.add(min_x.getOuterValue() * 10, 0);
			
			lower_series.add(max_x.getInnerValue() * 10, 0);
			upper_series.add(max_x.getOuterValue() * 10, 0);
		}
		
		result.addSeries(lower_series);
		result.addSeries(upper_series);
	}
	
}
