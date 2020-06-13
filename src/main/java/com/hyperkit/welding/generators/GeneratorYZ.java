package com.hyperkit.welding.generators;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Path;
import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Range;
import com.hyperkit.welding.Search;
import com.hyperkit.welding.configurations.Render2DConfiguration;
import com.hyperkit.welding.exceptions.SearchException;

public class GeneratorYZ extends Generator2D {
	
	private Search search;
	private Render2DConfiguration configuration;
	
	public GeneratorYZ(Search search, Render2DConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}
	
	public void generateDataset(Path path_min_x, Range min_x, Range max_x, double widest_x, Range max_y, XYSeriesCollection result, Progress progress) throws SearchException {
		XYSeries lower_total_series = new XYSeries("Innen");
		XYSeries upper_total_series = new XYSeries("Auﬂen");
		
		int samples = configuration.getYZSamples();
		
		progress.initialize(samples + 1);
		
		Range y_range_widest = max_y;

		lower_total_series.add(-y_range_widest.getInnerValue() * 10, 0);
		upper_total_series.add(-y_range_widest.getOuterValue() * 10, 0);
		
		progress.update(1, samples + 1);
		
		for (int sample = 1; sample < samples; sample++) {
			double lower_y = y_range_widest.getInnerValue() * 2 / samples * sample - y_range_widest.getInnerValue();
			double upper_y = y_range_widest.getOuterValue() * 2 / samples * sample - y_range_widest.getOuterValue();
			
			Range lower_z_range_opt = new Range(0, 0);
			Range upper_z_range_opt = new Range(0, 0);
			
			for (int step = 0; step <= configuration.getYZXSamples(); step++) {
				double x = min_x.getInnerValue() + (max_x.getInnerValue() - min_x.getInnerValue()) / configuration.getYZXSamples() * step;
				
				Range y_range_current = search.findMaximumY(x, path_min_x.calculateStartY(x), 0);
				
				if (y_range_current.getInnerValue() >= lower_y) {
					Range lower_z_range;
					
					try {
						lower_z_range = search.findMinimumZ(x, lower_y, 0);
					} catch (SearchException exception) {
						lower_z_range = new Range(0, 0);
					}
					
					if (lower_z_range_opt.getInnerValue() > lower_z_range.getInnerValue()) {
						lower_z_range_opt = lower_z_range;
					}
				}
				if (y_range_current.getInnerValue() >= upper_y) {
					Range upper_z_range;
					
					try {
						upper_z_range = search.findMinimumZ(x, upper_y, 0);
					} catch (SearchException exception) {
						upper_z_range = new Range(0, 0);
					}
					
					if (upper_z_range_opt.getInnerValue() > upper_z_range.getInnerValue()) {
						upper_z_range_opt = upper_z_range;
					}
				}
			}
			
			lower_total_series.add(lower_y * 10, lower_z_range_opt.getInnerValue() * 10);
			upper_total_series.add(upper_y * 10, upper_z_range_opt.getOuterValue() * 10);
			
			// System.out.println("Sample: " + sample + " " + lower_y + " " + lower_z_range_opt.getInnerValue());
			// System.out.println("Sample: " + sample + " " + upper_y + " " + upper_z_range_opt.getOuterValue());
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_total_series.add(y_range_widest.getInnerValue() * 10, 0);
		upper_total_series.add(y_range_widest.getOuterValue() * 10, 0);

		progress.update(samples + 1, samples + 1);

		result.addSeries(lower_total_series);
		result.addSeries(upper_total_series);
	}
	
	public void generateDataset(double x, double y, XYSeriesCollection result, Progress progress) throws SearchException {
		generateDataset(x, search.findMaximumY(x, y, 0), result, progress);
	}
	
	public void generateDataset(double x, Range max_y, XYSeriesCollection result, Progress progress) throws SearchException {
		XYSeries lower_series = new XYSeries("Innen");
		XYSeries upper_series = new XYSeries("Auﬂen");
		
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
