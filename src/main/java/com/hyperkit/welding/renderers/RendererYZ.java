package com.hyperkit.welding.renderers;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Search;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.structures.Path;
import com.hyperkit.welding.structures.Range;
import com.hyperkit.welding.structures.extremes.DeepestX;
import com.hyperkit.welding.structures.extremes.WidestX;

public class RendererYZ extends Renderer2D {
	
	private double widest_x;
	private double deepest_x;

	public RendererYZ(ChartPanel chart_panel, double widest_x, double deepest_x) {
		super(chart_panel, "YZ-Schweiﬂprofil", "Breite [y]", "Tiefe [z]");
		
		this.widest_x = widest_x;
		this.deepest_x = deepest_x;
	}
	
	@Override
	protected String getAnnotation() {
		return "x (b) = " + FORMAT.format(widest_x * 10) + " mm, x (t) = " + FORMAT.format(deepest_x * 10) + "mm";
	}
	
	public void run(Search search, Range min_x, Range max_x, Path path_min_x, WidestX widest_x, DeepestX deepest_x, XYSeriesCollection dataset) throws SearchException {
		run(min_x, max_x, path_min_x, widest_x, deepest_x, dataset);
		
		// Add annotations
	}

}
