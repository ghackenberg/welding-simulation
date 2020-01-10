package com.hyperkit.welding.generators;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

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
	
	public void generateDataset(Range min_x, Range max_x, double y, XYSeriesCollection result, Progress progress) throws SearchException {
		XYSeries lower_series = new XYSeries("Obere Grenze");
		XYSeries upper_series = new XYSeries("Untere Grenze");
		
		int samples = configuration.getXZSamples();
		
		progress.initialize(samples + 1);
		
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)] min_x = " + min_x);
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(min_x.getLowerValue(), y, 0));
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(min_x.getUpperValue(), y, 0));
		
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)] max_x = " + max_x);
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(max_x.getLowerValue(), y, 0));
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(max_x.getUpperValue(), y, 0));
		
		for (int sample = 0; sample < samples; sample++) {
			double lower_x = (max_x.getLowerValue() - min_x.getLowerValue()) / samples * sample + min_x.getLowerValue();
			double upper_x = (max_x.getUpperValue() - min_x.getUpperValue()) / samples * sample + min_x.getUpperValue();
			
			Range lower_z_range;
			
			try {
				lower_z_range = search.findMaximumZ(lower_x, y);
			} catch (SearchException exception) {
				lower_z_range = new Range(0, 0);
			}
			
			Range upper_z_range;
			
			try {
				upper_z_range = search.findMaximumZ(upper_x, y);
			} catch (SearchException exception) {
				upper_z_range = new Range(0, 0);
			}
			
			lower_series.add(lower_x * 10, -Math.abs(lower_z_range.getLowerValue()) * 10);
			upper_series.add(upper_x * 10, -Math.abs(upper_z_range.getUpperValue()) * 10);
			
			//System.out.println("Sample: " + sample + " " + lower_y + " " + lower_z_range.getLowerValue());
			//System.out.println("Sample: " + sample + " " + upper_y + " " + upper_z_range.getUpperValue());
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_series.add(max_x.getLowerValue() * 10, 0);
		upper_series.add(max_x.getUpperValue() * 10, 0);
		
		XYSeries zero_series = new XYSeries("Materialgrenze"); 
		
		zero_series.add(min_x.getLowerValue() * 10, 0);
		zero_series.add(max_x.getUpperValue() * 10, 0);
		
		result.addSeries(lower_series);
		result.addSeries(upper_series);
		result.addSeries(zero_series);
		
		progress.update(samples + 1, samples + 1);
	}
	
}
