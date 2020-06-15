package com.hyperkit.welding.generators;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Path;
import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Range;
import com.hyperkit.welding.Search;
import com.hyperkit.welding.configurations.Render2DConfiguration;
import com.hyperkit.welding.exceptions.SearchException;

public class GeneratorXZ extends Generator2D {
	
	private Search search;
	private Render2DConfiguration configuration;
	
	public GeneratorXZ(Search search, Render2DConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}
	
	public void generateDataset(Path path_min_x, Range min_x, Range max_x, XYSeriesCollection result, Progress progress) throws SearchException {
		XYSeries lower_total_series = new XYSeries("Innen");
		XYSeries upper_total_series = new XYSeries("Au�en");
		
		int samples = configuration.getXZSamples();
		
		progress.initialize(samples + 1);

		lower_total_series.add(min_x.getInnerValue() * 10, 0);
		upper_total_series.add(min_x.getOuterValue() * 10, 0);
		
		progress.update(1, samples + 1);
		
		for (int sample = 1; sample < samples; sample++) {
			double lower_x = (max_x.getInnerValue() - min_x.getInnerValue()) / samples * sample + min_x.getInnerValue();
			double upper_x = (max_x.getOuterValue() - min_x.getOuterValue()) / samples * sample + min_x.getOuterValue();
			
			double lower_max_y = search.findMaximumY(lower_x, path_min_x.calculateStartY(lower_x), 0).getInnerValue();
			double upper_max_y = search.findMaximumY(upper_x, path_min_x.calculateStartY(upper_x), 0).getInnerValue();
			
			double lower_y = search.findDeepestY(lower_x, 0, lower_max_y);
			double upper_y = search.findDeepestY(upper_x, 0, upper_max_y);
			
			Range lower_z_range_opt = search.findMinimumZ(lower_x, lower_y, 0);
			Range upper_z_range_opt = search.findMinimumZ(upper_x, upper_y, 0);
			
			lower_total_series.add(lower_x * 10, lower_z_range_opt.getInnerValue() * 10);
			upper_total_series.add(upper_x * 10, upper_z_range_opt.getOuterValue() * 10);
			
			// System.out.println("Sample: " + sample + " " + lower_y + " " + lower_z_range_opt.getInnerValue());
			// System.out.println("Sample: " + sample + " " + upper_y + " " + upper_z_range_opt.getOuterValue());
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_total_series.add(max_x.getInnerValue() * 10, 0);
		upper_total_series.add(max_x.getOuterValue() * 10, 0);

		progress.update(samples + 1, samples + 1);

		result.addSeries(lower_total_series);
		result.addSeries(upper_total_series);
	}
	
	public void generateDataset(Range min_x, Range max_x, double y, XYSeriesCollection result, Progress progress) throws SearchException {
		XYSeries lower_series = new XYSeries("Innen");
		XYSeries upper_series = new XYSeries("Au�en");
		
		int samples = configuration.getXZSamples();
		
		progress.initialize(samples + 1);
		
		// System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)] " + search.getModel().calculateTemperature(min_x.getInnerValue(), y, 0));
		// System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)] " + search.getModel().calculateTemperature(min_x.getOuterValue(), y, 0));
		
		// System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)] " + search.getModel().calculateTemperature(max_x.getInnerValue(), y, 0));
		// System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)] " + search.getModel().calculateTemperature(max_x.getOuterValue(), y, 0));
		
		for (int sample = 0; sample < samples; sample++) {
			double lower_x = (max_x.getInnerValue() - min_x.getInnerValue()) / samples * sample + min_x.getInnerValue();
			double upper_x = (max_x.getOuterValue() - min_x.getOuterValue()) / samples * sample + min_x.getOuterValue();
			
			Range lower_z_range;
			
			try {
				lower_z_range = search.findMinimumZ(lower_x, y, 0);
			} catch (SearchException exception) {
				lower_z_range = new Range(0, 0);
			}
			
			Range upper_z_range;
			
			try {
				upper_z_range = search.findMinimumZ(upper_x, y, 0);
				/*
				double inner_temperature = search.getModel().calculateTemperature(upper_x, y, upper_z_range.getInnerValue());
				double outer_temperature = search.getModel().calculateTemperature(upper_x, y, upper_z_range.getOuterValue());
				
				System.out.println("Sample " + sample + " at (" + upper_x + ", " + y + ", 0) succeeded with " + upper_z_range + " and temperatures [" + inner_temperature + ", " + outer_temperature + "]");
				*/
			} catch (SearchException exception) {
				upper_z_range = new Range(0, 0);
			}
			
			lower_series.add(lower_x * 10, lower_z_range.getInnerValue() * 10);
			upper_series.add(upper_x * 10, upper_z_range.getOuterValue() * 10);
			
			// System.out.println("Sample: " + sample + " " + lower_x + " " + lower_z_range);
			// System.out.println("Sample: " + sample + " " + upper_x + " " + upper_z_range);
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_series.add(max_x.getInnerValue() * 10, 0);
		upper_series.add(max_x.getOuterValue() * 10, 0);
		
		progress.update(samples + 1, samples + 1);
		
		result.addSeries(lower_series);
		result.addSeries(upper_series);
	}
	
}
