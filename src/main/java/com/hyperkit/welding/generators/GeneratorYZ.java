package com.hyperkit.welding.generators;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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
	
	public void generateDataset(double widest_x, double deepest_x, XYSeriesCollection result, Progress progress) throws SearchException {
		XYSeries lower_series = new XYSeries("Obere Grenze");
		XYSeries upper_series = new XYSeries("Untere Grenze");
		
		int samples = configuration.getYZSamples();
		
		progress.initialize(samples + 1);
		
		Range y_range = search.findMaximumY(widest_x, 0);
		
		for (int sample = 0; sample < samples; sample++) {
			double lower_y = Math.abs(y_range.getInnerValue()) * 2 / samples * sample - Math.abs(y_range.getInnerValue());
			double upper_y = Math.abs(y_range.getOuterValue()) * 2 / samples * sample - Math.abs(y_range.getOuterValue());
			
			Range lower_z_range_opt = new Range(0, 0);
			Range upper_z_range_opt = new Range(0, 0);
			
			for (int step = 0; step <= configuration.getYZXSamples(); step++) {
				double x = widest_x + (deepest_x - widest_x) / configuration.getYZXSamples() * step;
				
				Range y_range_current = search.findMaximumY(x, 0);
				
				if (Math.min(Math.abs(y_range_current.getInnerValue()), Math.abs(y_range_current.getOuterValue())) > Math.abs(lower_y)) {
					Range lower_z_range;
					
					try {
						lower_z_range = search.findMinimumZ(x, lower_y);
					} catch (SearchException exception) {
						lower_z_range = new Range(0, 0);
					}
					
					if (lower_z_range_opt.getInnerValue() > lower_z_range.getInnerValue()) {
						lower_z_range_opt = lower_z_range;
					}
				}
				if (Math.min(Math.abs(y_range_current.getInnerValue()), Math.abs(y_range_current.getOuterValue())) > Math.abs(upper_y)) {
					Range upper_z_range;
					
					try {
						upper_z_range = search.findMinimumZ(x, upper_y);
					} catch (SearchException exception) {
						upper_z_range = new Range(0, 0);
					}
					
					if (upper_z_range_opt.getInnerValue() > upper_z_range.getInnerValue()) {
						upper_z_range_opt = upper_z_range;
					}
				}
			}
			
			lower_series.add(lower_y * 10, -Math.abs(lower_z_range_opt.getInnerValue()) * 10);
			upper_series.add(upper_y * 10, -Math.abs(upper_z_range_opt.getOuterValue()) * 10);
			
			System.out.println("Sample: " + sample + " " + lower_y + " " + lower_z_range_opt.getInnerValue());
			System.out.println("Sample: " + sample + " " + upper_y + " " + upper_z_range_opt.getOuterValue());
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_series.add(Math.abs(y_range.getInnerValue()) * 10, 0);
		upper_series.add(Math.abs(y_range.getOuterValue()) * 10, 0);
		
		XYSeries zero_series = new XYSeries("Materialgrenze");
		
		zero_series.add(-Math.max(Math.abs(y_range.getInnerValue()), y_range.getOuterValue()) * 10, 0);
		zero_series.add(+Math.max(Math.abs(y_range.getInnerValue()), y_range.getOuterValue()) * 10, 0);
		
		result.addSeries(lower_series);
		result.addSeries(upper_series);
		result.addSeries(zero_series);
		
		progress.update(samples + 1, samples + 1);
	}
	
	public void generateDataset(double x, XYSeriesCollection result, Progress progress) throws SearchException {
		XYSeries lower_series = new XYSeries("Obere Grenze");
		XYSeries upper_series = new XYSeries("Untere Grenze");
		
		int samples = configuration.getYZSamples();
		
		progress.initialize(samples + 1);
		
		Range y_range = search.findMaximumY(x, 0);
		
		System.out.println("[RendererYZ.generateDataset(" + x + ", progress)] y_range = " + y_range);
		System.out.println("[RendererYZ.generateDataset(" + x + ", progress)]" + search.getModel().calculateTemperature(x, y_range.getInnerValue(), 0));
		System.out.println("[RendererYZ.generateDataset(" + x + ", progress)]" + search.getModel().calculateTemperature(x, y_range.getOuterValue(), 0));
		
		for (int sample = 0; sample < samples; sample++) {
			double lower_y = Math.abs(y_range.getInnerValue()) * 2 / samples * sample - Math.abs(y_range.getInnerValue());
			double upper_y = Math.abs(y_range.getOuterValue()) * 2 / samples * sample - Math.abs(y_range.getOuterValue());
			
			Range lower_z_range;
			
			try {
				lower_z_range = search.findMinimumZ(x, lower_y);
			} catch (SearchException exception) {
				lower_z_range = new Range(0, 0);
			}
			
			Range upper_z_range;
			
			try {
				upper_z_range = search.findMinimumZ(x, upper_y);
			} catch (SearchException exception) {
				upper_z_range = new Range(0, 0);
			}
			
			lower_series.add(lower_y * 10, -Math.abs(lower_z_range.getInnerValue()) * 10);
			upper_series.add(upper_y * 10, -Math.abs(upper_z_range.getOuterValue()) * 10);
			
			//System.out.println("Sample: " + sample + " " + lower_y + " " + lower_z_range.getLowerValue());
			//System.out.println("Sample: " + sample + " " + upper_y + " " + upper_z_range.getUpperValue());
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_series.add(Math.abs(y_range.getInnerValue()) * 10, 0);
		upper_series.add(Math.abs(y_range.getOuterValue()) * 10, 0);
		
		XYSeries zero_series = new XYSeries("Materialgrenze");
		
		zero_series.add(-Math.max(Math.abs(y_range.getInnerValue()), y_range.getOuterValue()) * 10, 0);
		zero_series.add(+Math.max(Math.abs(y_range.getInnerValue()), y_range.getOuterValue()) * 10, 0);
		
		result.addSeries(lower_series);
		result.addSeries(upper_series);
		result.addSeries(zero_series);
		
		progress.update(samples + 1, samples + 1);
	}
	
}
