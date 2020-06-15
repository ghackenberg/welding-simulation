package com.hyperkit.welding.generators;

import java.util.stream.IntStream;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Model;
import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Search;
import com.hyperkit.welding.configurations.Render2DConfiguration;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.structures.Path;
import com.hyperkit.welding.structures.Range;
import com.hyperkit.welding.structures.Span;
import com.hyperkit.welding.structures.extremes.DeepestX;
import com.hyperkit.welding.structures.extremes.WidestX;

public class GeneratorYZ extends Generator2D {
	
	private Search search;
	private Render2DConfiguration configuration;
	
	public GeneratorYZ(Search search, Render2DConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}
	
	private Range tryRange(double x, double y, Range previous_optimum) throws SearchException {
		final double limit_temperature = search.getConfiguration().getLimitTemperature();
		
		final Model<?> model = search.getModel();
		
		if (model.calculateTemperature(x, y, 0) >= limit_temperature) {
			double min_x = search.findMinimumX(x, y, 0).getInnerValue();
			double max_x = search.findMaximumX(x, y, 0).getInnerValue();
			
			DeepestX deepest_x = search.findDeepestX(min_x, max_x, y);
			
			if (deepest_x.getMinZ().getInnerValue() < previous_optimum.getInnerValue()) {
				return deepest_x.getMinZ();
			}
		}
		
		return previous_optimum;
	}
	
	public void generateDataset(Path path_min_x, Range min_x, Range max_x, Range max_y, XYSeriesCollection result, Progress progress) throws SearchException {
		final XYSeries lower_total_series = new XYSeries("Innen");
		final XYSeries upper_total_series = new XYSeries("Außen");
		
		final int samples = configuration.getYZSamples();
		
		progress.initialize(samples);
		
		IntStream.range(0, samples).parallel().forEach(sample -> {
			try {
				double lower_y = max_y.getInnerValue() / samples * sample;
				double upper_y = max_y.getOuterValue() / samples * sample;
				
				Range lower_z_range_opt = new Range(0, 0);
				Range upper_z_range_opt = new Range(0, 0);
				
				WidestX span_widest_x = search.findWidestX(path_min_x.getFirstSpan().getOriginX(), max_x.getInnerValue(), 0, 0);
				
				lower_z_range_opt = tryRange(max_x.getInnerValue(), lower_y, lower_z_range_opt);
				upper_z_range_opt = tryRange(max_x.getInnerValue(), upper_y, upper_z_range_opt);
				
				lower_z_range_opt = tryRange(span_widest_x.getX(), lower_y, lower_z_range_opt);
				upper_z_range_opt = tryRange(span_widest_x.getX(), upper_y, upper_z_range_opt);
				
				for (Span span : path_min_x.getSpans()) {
					span_widest_x = span.getWidestX();
					
					lower_z_range_opt = tryRange(span.getOriginX(), lower_y, lower_z_range_opt);
					lower_z_range_opt = tryRange(span_widest_x.getX(), lower_y, lower_z_range_opt);
					
					upper_z_range_opt = tryRange(span.getOriginX(), upper_y, upper_z_range_opt);
					upper_z_range_opt = tryRange(span_widest_x.getX(), upper_y, upper_z_range_opt);
				}
				
				synchronized (lower_total_series) {
					lower_total_series.add(-lower_y * 10, lower_z_range_opt.getInnerValue() * 10);
					upper_total_series.add(-upper_y * 10, upper_z_range_opt.getOuterValue() * 10);
					
					lower_total_series.add(+lower_y * 10, lower_z_range_opt.getInnerValue() * 10);
					upper_total_series.add(+upper_y * 10, upper_z_range_opt.getOuterValue() * 10);
				}
				
				progress.increment();
			} catch (SearchException e) {
				throw new IllegalStateException(e);
			}
		});
		
		synchronized (lower_total_series) {
			lower_total_series.add(-max_y.getInnerValue() * 10, 0);
			upper_total_series.add(-max_y.getOuterValue() * 10, 0);
			
			lower_total_series.add(+max_y.getInnerValue() * 10, 0);
			upper_total_series.add(+max_y.getOuterValue() * 10, 0);
		}

		result.addSeries(lower_total_series);
		result.addSeries(upper_total_series);
	}
	
	public void generateDataset(double x, double y, XYSeriesCollection result, Progress progress) throws SearchException {
		generateDataset(x, search.findMaximumY(x, y, 0), result, progress);
	}
	
	public void generateDataset(double x, Range max_y, XYSeriesCollection result, Progress progress) throws SearchException {
		final XYSeries lower_series = new XYSeries("Innen");
		final XYSeries upper_series = new XYSeries("Außen");
		
		final int samples = configuration.getYZSamples();
		
		progress.initialize(samples);
		
		IntStream.range(0, samples).parallel().forEach(sample -> {
			try {
				double lower_y = max_y.getInnerValue() / samples * sample;
				double upper_y = max_y.getOuterValue() / samples * sample;
				
				Range lower_z_range = new Range(0, 0);
				
				if (search.getModel().calculateTemperature(x, lower_y, 0) >= search.getConfiguration().getLimitTemperature()) {
					lower_z_range = search.findMinimumZ(x, lower_y, 0);
				}
				
				Range upper_z_range = new Range(0, 0);
				
				if (search.getModel().calculateTemperature(x, upper_y, 0) >= search.getConfiguration().getLimitTemperature()) {
					upper_z_range = search.findMinimumZ(x, upper_y, 0);
				}
				
				synchronized (lower_series) {
					lower_series.add(-lower_y * 10, lower_z_range.getInnerValue() * 10);
					upper_series.add(-upper_y * 10, upper_z_range.getOuterValue() * 10);
					
					lower_series.add(+lower_y * 10, lower_z_range.getInnerValue() * 10);
					upper_series.add(+upper_y * 10, upper_z_range.getOuterValue() * 10);
				}
				
				progress.increment();
			} catch (SearchException e) {
				throw new IllegalStateException(e);
			}
		});
		
		synchronized (lower_series) {
			lower_series.add(-max_y.getInnerValue() * 10, 0);
			upper_series.add(-max_y.getOuterValue() * 10, 0);
			
			lower_series.add(+max_y.getInnerValue() * 10, 0);
			upper_series.add(+max_y.getOuterValue() * 10, 0);
		}
		
		final XYSeries zero_series = new XYSeries("Material");
		
		zero_series.add(-max_y.getOuterValue() * 10, 0);
		zero_series.add(+max_y.getOuterValue() * 10, 0);
		
		result.addSeries(lower_series);
		result.addSeries(upper_series);
		result.addSeries(zero_series);
	}
	
}
