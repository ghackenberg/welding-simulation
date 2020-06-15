package com.hyperkit.welding.generators;

import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Search;
import com.hyperkit.welding.configurations.Render2DConfiguration;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.structures.Path;
import com.hyperkit.welding.structures.Range;

public class GeneratorXY extends Generator2D {
	
	private Search search;
	private Render2DConfiguration configuration;
	
	public GeneratorXY(Search search, Render2DConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}
	
	public void generateDataset(Path path_min_x, Range max_x, double z, XYSeriesCollection result, Progress progress) throws SearchException {
		Range min_x = path_min_x.getLastSpan().getExtremeX();
		
		System.out.println("[RendererXY.generateDataset(" + path_min_x + ", " + max_x + ", progress)] " + min_x);
		
		XYSeries lower_series = new XYSeries("Innen (+)");
		XYSeries upper_series = new XYSeries("Auﬂen (+)");
		
		XYSeries lower_series_2 = new XYSeries("Innen (-)");
		XYSeries upper_series_2 = new XYSeries("Auﬂen (-)");
		
		int samples = configuration.getXYSamples();
		
		progress.initialize(samples + 1);
		
		// System.out.println("[RendererXY.generateDataset(" + path_min_x + ", " + max_x + ", progress)] " + search.getModel().calculateTemperature(min_x.getInnerValue(), 0, z));
		// System.out.println("[RendererXY.generateDataset(" + path_min_x + ", " + max_x + ", progress)] " + search.getModel().calculateTemperature(min_x.getOuterValue(), 0, z));
		
		// System.out.println("[RendererXY.generateDataset(" + path_min_x + ", " + max_x + ", progress)] " + search.getModel().calculateTemperature(max_x.getInnerValue(), 0, z));
		// System.out.println("[RendererXY.generateDataset(" + path_min_x + ", " + max_x + ", progress)] " + search.getModel().calculateTemperature(max_x.getOuterValue(), 0, z));
		
		for (int sample = 0; sample < samples; sample++) {
			double lower_x = (max_x.getInnerValue() - min_x.getInnerValue()) / samples * sample + min_x.getInnerValue();
			double upper_x = (max_x.getOuterValue() - min_x.getOuterValue()) / samples * sample + min_x.getOuterValue();
			
			// Find span
			
			Range lower_y_range = new Range(0, 0);
			
			double lower_y_start = path_min_x.calculateStartY(lower_x);
			
			if (search.getModel().calculateTemperature(lower_x, lower_y_start, z) >= search.getConfiguration().getLimitTemperature()) {
				lower_y_range = search.findMaximumY(lower_x, lower_y_start, z);
			}
			
			Range upper_y_range = new Range(0, 0);
			
			double upper_y_start = path_min_x.calculateStartY(upper_x);
			
			if (search.getModel().calculateTemperature(upper_x,  upper_y_start, z) >= search.getConfiguration().getLimitTemperature()) {
				upper_y_range = search.findMaximumY(upper_x, upper_y_start, z);
			}
			
			lower_series.add(lower_x * 10, lower_y_range.getInnerValue() * 10);
			upper_series.add(upper_x * 10, upper_y_range.getOuterValue() * 10);
			
			lower_series_2.add(lower_x * 10, - lower_y_range.getInnerValue() * 10);
			upper_series_2.add(upper_x * 10, - upper_y_range.getOuterValue() * 10);
			
			// System.out.println("Sample: " + sample + " " + lower_x + " " + lower_z_range);
			//System.out.println("Sample: " + sample + " " + upper_x + " " + upper_z_range);
			
			progress.update(sample + 1, samples + 1);
		}
		
		lower_series.add(max_x.getInnerValue() * 10, 0);
		upper_series.add(max_x.getOuterValue() * 10, 0);
		
		lower_series_2.add(max_x.getInnerValue() * 10, 0);
		upper_series_2.add(max_x.getOuterValue() * 10, 0);
		
		progress.update(samples + 1, samples + 1);
		
		result.addSeries(lower_series);
		result.addSeries(upper_series);
		
		result.addSeries(lower_series_2);
		result.addSeries(upper_series_2);
	}
	
}
