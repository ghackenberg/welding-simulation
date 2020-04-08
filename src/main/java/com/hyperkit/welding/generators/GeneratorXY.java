package com.hyperkit.welding.generators;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Range;
import com.hyperkit.welding.Search;
import com.hyperkit.welding.configurations.Render2DConfiguration;
import com.hyperkit.welding.exceptions.SearchException;

public class GeneratorXY extends Generator2D {
	
	private Search search;
	private Render2DConfiguration configuration;
	
	public GeneratorXY(Search search, Render2DConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}
	
	public void generateDataset(Range min_x, Range max_x, double z, XYSeriesCollection result, Progress progress) throws SearchException {
		XYSeries lower_series = new XYSeries("Innen (+)");
		XYSeries upper_series = new XYSeries("Auﬂen (+)");
		
		XYSeries lower_series_2 = new XYSeries("Innen (-)");
		XYSeries upper_series_2 = new XYSeries("Auﬂen (-)");
		
		int samples = configuration.getXYSamples();
		
		progress.initialize(samples + 1);
		
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)] min_x = " + min_x);
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(min_x.getInnerValue(), 0, z));
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(min_x.getOuterValue(), 0, z));
		
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)] max_x = " + max_x);
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(max_x.getInnerValue(), 0, z));
		System.out.println("[RendererXY.generateDataset(" + min_x + ", " + max_x + ", progress)]" + search.getModel().calculateTemperature(max_x.getOuterValue(), 0, z));
		
		for (int sample = 0; sample < samples; sample++) {
			double lower_x = (max_x.getInnerValue() - min_x.getInnerValue()) / samples * sample + min_x.getInnerValue();
			double upper_x = (max_x.getOuterValue() - min_x.getOuterValue()) / samples * sample + min_x.getOuterValue();
			
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
			
			lower_series.add(lower_x * 10, -Math.abs(lower_y_range.getInnerValue()) * 10);
			upper_series.add(upper_x * 10, -Math.abs(upper_y_range.getOuterValue()) * 10);
			
			lower_series_2.add(lower_x * 10, Math.abs(lower_y_range.getInnerValue()) * 10);
			upper_series_2.add(upper_x * 10, Math.abs(upper_y_range.getOuterValue()) * 10);
			
			//System.out.println("Sample: " + sample + " " + lower_y + " " + lower_z_range.getLowerValue());
			//System.out.println("Sample: " + sample + " " + upper_y + " " + upper_z_range.getUpperValue());
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_series.add(max_x.getInnerValue() * 10, 0);
		upper_series.add(max_x.getOuterValue() * 10, 0);
		
		lower_series_2.add(max_x.getInnerValue() * 10, 0);
		upper_series_2.add(max_x.getOuterValue() * 10, 0);
		
		result.addSeries(lower_series);
		result.addSeries(upper_series);
		
		result.addSeries(lower_series_2);
		result.addSeries(upper_series_2);
		
		progress.update(samples + 1, samples + 1);
	}
	
}
