package com.hyperkit.welding.renderers;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Range;
import com.hyperkit.welding.Search;
import com.hyperkit.welding.configurations.Render2DConfiguration;
import com.hyperkit.welding.exceptions.SearchException;

public class RendererXY extends Renderer2D {
	
	private Search search;
	private Render2DConfiguration configuration;
	
	public RendererXY(Search search, Render2DConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}
	
	public XYSeriesCollection generateDataset(Range min_x, Range max_x, double z, Progress progress) throws SearchException {
		XYSeries lower_series = new XYSeries("Obere Grenze");
		XYSeries upper_series = new XYSeries("Untere Grenze");
		
		int samples = configuration.getXYSamples();
		
		progress.initialize(samples + 1);
		
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)] min_x = " + min_x);
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(min_x.getLowerValue(), 0, z));
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(min_x.getUpperValue(), 0, z));
		
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)] max_x = " + max_x);
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(max_x.getLowerValue(), 0, z));
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(max_x.getUpperValue(), 0, z));
		
		for (int sample = 0; sample < samples; sample++) {
			double lower_x = (max_x.getLowerValue() - min_x.getLowerValue()) / samples * sample + min_x.getLowerValue();
			double upper_x = (max_x.getUpperValue() - min_x.getUpperValue()) / samples * sample + min_x.getUpperValue();
			
			Range lower_y_range;
			
			try {
				lower_y_range = search.findMaximumY(lower_x, z);
			} catch (SearchException exception) {
				lower_y_range = new Range(0, 0);
			}
			
			Range upper_y_range;
			
			try {
				upper_y_range = search.findMaximumY(upper_x, z);
			} catch (SearchException exception) {
				upper_y_range = new Range(0, 0);
			}
			
			lower_series.add(lower_x * 10, -Math.abs(lower_y_range.getLowerValue()) * 10);
			upper_series.add(upper_x * 10, -Math.abs(upper_y_range.getUpperValue()) * 10);
			
			//System.out.println("Sample: " + sample + " " + lower_y + " " + lower_z_range.getLowerValue());
			//System.out.println("Sample: " + sample + " " + upper_y + " " + upper_z_range.getUpperValue());
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_series.add(max_x.getLowerValue() * 10, 0);
		upper_series.add(max_x.getUpperValue() * 10, 0);
		
		XYSeriesCollection collection = new XYSeriesCollection();
		
		collection.addSeries(lower_series);
		collection.addSeries(upper_series);
		
		progress.update(samples + 1, samples + 1);
		
		return collection;
	}
	
}
