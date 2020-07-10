package com.hyperkit.welding.renderers;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Search;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.structures.Path;
import com.hyperkit.welding.structures.Range;
import com.hyperkit.welding.structures.extremes.DeepestX;
import com.hyperkit.welding.structures.extremes.WidestX;

public class RendererXZ extends Renderer2D {
	
	private double y;

	public RendererXZ(ChartPanel chart_panel, double y) {
		super(chart_panel, "XZ-Schweiﬂprofil", "L‰nge [x]", "Tiefe [z]");
		
		this.y = y;
	}
	
	@Override
	protected String getAnnotation() {
		return "y = " + FORMAT.format(y * 10) + " mm";
	}
	
	public void run(Search search, Range min_x, Range max_x, Path path_min_x, WidestX widest_x, DeepestX deepest_x, XYSeriesCollection dataset) throws SearchException {
		run(min_x, max_x, path_min_x, widest_x, deepest_x, dataset);
		
		// Add annotations
		
		Range widest_z = search.findDeepestY(widest_x.getX(), 0, widest_x.getMaxY().getInnerValue()).getMinZ();

		plot.addAnnotation(new XYLineAnnotation(widest_x.getX() * 10, widest_z.getOuterValue() * 10, widest_x.getX() * 10, 0, new BasicStroke(), Color.BLUE));
		plot.addAnnotation(new XYLineAnnotation(deepest_x.getX() * 10, deepest_x.getMinZ().getOuterValue() * 10, deepest_x.getX() * 10, 0, new BasicStroke(), Color.BLUE));
		
		plot.addAnnotation(new XYTextAnnotation("Widest X", widest_x.getX() * 10, 0));
		plot.addAnnotation(new XYTextAnnotation("Deepest X", deepest_x.getX() * 10, 0));
	}

}
