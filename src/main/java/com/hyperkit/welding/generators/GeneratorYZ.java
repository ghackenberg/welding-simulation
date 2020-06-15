package com.hyperkit.welding.generators;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Model;
import com.hyperkit.welding.Path;
import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Range;
import com.hyperkit.welding.Search;
import com.hyperkit.welding.Span;
import com.hyperkit.welding.configurations.Render2DConfiguration;
import com.hyperkit.welding.exceptions.SearchException;

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
			
			double deepest_x = search.findDeepestX(min_x, max_x, y);
			
			Range min_z = search.findMinimumZ(deepest_x, y, 0);
			
			if (min_z.getInnerValue() < previous_optimum.getInnerValue()) {
				return min_z;
			}
		}
		
		return previous_optimum;
	}
	
	public void generateDataset(Path path_min_x, Range min_x, Range max_x, Range max_y, XYSeriesCollection result, Progress progress) throws SearchException {
		XYSeries lower_total_series_left = new XYSeries("Innen (-)");
		XYSeries upper_total_series_left = new XYSeries("Außen (-)");
		
		XYSeries lower_total_series_right = new XYSeries("Innen (+)");
		XYSeries upper_total_series_right = new XYSeries("Außen (+)");
		
		int samples = configuration.getYZSamples();
		
		progress.initialize(samples + 1);
		
		lower_total_series_left.add(-max_y.getInnerValue() * 10, 0);
		upper_total_series_left.add(-max_y.getOuterValue() * 10, 0);
		
		for (int sample = 0; sample < samples; sample++) {			
			double lower_y = max_y.getInnerValue() / samples * sample;
			double upper_y = max_y.getOuterValue() / samples * sample;
			
			// System.out.println("[GeneratorYZ.generateDataset(pth_min_x, min_x, max_x, widest_x, max_y, result, progress)] Sample " + sample + " = [" + lower_y + ", " + upper_y + "]");
			
			Range lower_z_range_opt = new Range(0, 0);
			Range upper_z_range_opt = new Range(0, 0);
			
			double span_widest_x = search.findWidestX(path_min_x.getFirstSpan().getOriginX(), max_x.getInnerValue(), 0, 0);
			
			lower_z_range_opt = tryRange(max_x.getInnerValue(), lower_y, lower_z_range_opt);
			upper_z_range_opt = tryRange(max_x.getInnerValue(), upper_y, upper_z_range_opt);
			
			lower_z_range_opt = tryRange(span_widest_x, lower_y, lower_z_range_opt);
			upper_z_range_opt = tryRange(span_widest_x, upper_y, upper_z_range_opt);
			
			for (Span span : path_min_x.getSpans()) {
				span_widest_x = search.findWidestX(span.getExtremeX().getInnerValue(), span.getOriginX(), span.getY(), 0);
				
				lower_z_range_opt = tryRange(span.getOriginX(), lower_y, lower_z_range_opt);
				lower_z_range_opt = tryRange(span_widest_x, lower_y, lower_z_range_opt);
				
				upper_z_range_opt = tryRange(span.getOriginX(), upper_y, upper_z_range_opt);
				upper_z_range_opt = tryRange(span_widest_x, upper_y, upper_z_range_opt);
			}
			
			lower_total_series_left.add(-lower_y * 10, lower_z_range_opt.getInnerValue() * 10);
			upper_total_series_left.add(-upper_y * 10, upper_z_range_opt.getOuterValue() * 10);
			
			lower_total_series_right.add(lower_y * 10, lower_z_range_opt.getInnerValue() * 10);
			upper_total_series_right.add(upper_y * 10, upper_z_range_opt.getOuterValue() * 10);
			
			// System.out.println("Sample: " + sample + " " + lower_y + " " + lower_z_range_opt.getInnerValue());
			// System.out.println("Sample: " + sample + " " + upper_y + " " + upper_z_range_opt.getOuterValue());
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_total_series_right.add(max_y.getInnerValue() * 10, 0);
		upper_total_series_right.add(max_y.getOuterValue() * 10, 0);
		
		progress.update(samples + 1, samples + 1);

		result.addSeries(lower_total_series_left);
		result.addSeries(upper_total_series_left);
		result.addSeries(lower_total_series_right);
		result.addSeries(upper_total_series_right);
	}
	
	public void generateDataset(double x, double y, XYSeriesCollection result, Progress progress) throws SearchException {
		generateDataset(x, search.findMaximumY(x, y, 0), result, progress);
	}
	
	public void generateDataset(double x, Range max_y, XYSeriesCollection result, Progress progress) throws SearchException {
		XYSeries lower_series = new XYSeries("Innen");
		XYSeries upper_series = new XYSeries("Außen");
		
		int samples = configuration.getYZSamples();
		
		progress.initialize(samples + 1);
		
		// System.out.println("[RendererYZ.generateDataset(" + x + ", progress)] " + search.getModel().calculateTemperature(x, y_range.getInnerValue(), 0));
		// System.out.println("[RendererYZ.generateDataset(" + x + ", progress)] " + search.getModel().calculateTemperature(x, y_range.getOuterValue(), 0));
		
		for (int sample = 0; sample < samples; sample++) {
			double lower_y = Math.abs(max_y.getInnerValue()) * 2 / samples * sample - Math.abs(max_y.getInnerValue());
			double upper_y = Math.abs(max_y.getOuterValue()) * 2 / samples * sample - Math.abs(max_y.getOuterValue());
			
			Range lower_z_range;
			
			try {
				lower_z_range = search.findMinimumZ(x, lower_y, 0);
			} catch (SearchException exception) {
				lower_z_range = new Range(0, 0);
			}
			
			Range upper_z_range;
			
			try {
				upper_z_range = search.findMinimumZ(x, upper_y, 0);
			} catch (SearchException exception) {
				upper_z_range = new Range(0, 0);
			}
			
			lower_series.add(lower_y * 10, -Math.abs(lower_z_range.getInnerValue()) * 10);
			upper_series.add(upper_y * 10, -Math.abs(upper_z_range.getOuterValue()) * 10);
			
			//System.out.println("Sample: " + sample + " " + lower_y + " " + lower_z_range.getLowerValue());
			//System.out.println("Sample: " + sample + " " + upper_y + " " + upper_z_range.getUpperValue());
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_series.add(Math.abs(max_y.getInnerValue()) * 10, 0);
		upper_series.add(Math.abs(max_y.getOuterValue()) * 10, 0);
		
		XYSeries zero_series = new XYSeries("Material");
		
		zero_series.add(-Math.max(Math.abs(max_y.getInnerValue()), max_y.getOuterValue()) * 10, 0);
		zero_series.add(+Math.max(Math.abs(max_y.getInnerValue()), max_y.getOuterValue()) * 10, 0);
		
		result.addSeries(lower_series);
		result.addSeries(upper_series);
		result.addSeries(zero_series);
		
		progress.update(samples + 1, samples + 1);
	}
	
}
