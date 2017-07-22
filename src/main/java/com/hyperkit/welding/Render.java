package com.hyperkit.welding;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.configurations.RenderConfiguration;
import com.hyperkit.welding.exceptions.SearchException;

public class Render {
	
	private Search search;
	private RenderConfiguration configuration;
	
	public Render(Search search, RenderConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}
	
	public XYSeriesCollection generateDataset(Progress progress) throws SearchException {
		XYSeries lower_series = new XYSeries("Obere Grenze");
		XYSeries upper_series = new XYSeries("Untere Grenze");
		
		int samples = configuration.getSamples();
		
		progress.initialize(samples + 1);
		
		Range y_range = search.findMaximumY(0, 0);
		
		System.out.println("Y Range: " + y_range.getLowerValue() + " bis " + y_range.getUpperValue());
		System.out.println(search.getModel().calculateTemperature(0, y_range.getLowerValue(), 0));
		System.out.println(search.getModel().calculateTemperature(0, y_range.getUpperValue(), 0));
		
		for (int sample = 0; sample < samples; sample++) {
			double lower_y = y_range.getLowerValue() / samples * sample;
			double upper_y = y_range.getUpperValue() / samples * sample;
			
			Range lower_z_range;
			
			try {
				lower_z_range = search.findMaximumZ(0, lower_y);
			} catch (SearchException exception) {
				lower_z_range = new Range(0, 0);
			}
			
			Range upper_z_range;
			
			try {
				upper_z_range = search.findMaximumZ(0, upper_y);
			} catch (SearchException exception) {
				upper_z_range = new Range(0, 0);
			}
			
			lower_series.add(Math.abs(lower_y) * 10, -Math.abs(lower_z_range.getLowerValue()) * 10);
			upper_series.add(Math.abs(upper_y) * 10, -Math.abs(upper_z_range.getUpperValue()) * 10);
			
			//System.out.println("Sample: " + sample + " " + lower_y + " " + lower_z_range.getLowerValue());
			//System.out.println("Sample: " + sample + " " + upper_y + " " + upper_z_range.getUpperValue());
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_series.add(Math.abs(y_range.getLowerValue()) * 10, 0);
		upper_series.add(Math.abs(y_range.getUpperValue()) * 10, 0);
		
		XYSeriesCollection collection = new XYSeriesCollection();
		
		collection.addSeries(lower_series);
		collection.addSeries(upper_series);
		
		progress.update(samples + 1, samples + 1);
		
		return collection;
	}
	
}
