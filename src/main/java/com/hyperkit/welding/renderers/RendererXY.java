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
import com.hyperkit.welding.structures.Span;
import com.hyperkit.welding.structures.extremes.DeepestX;
import com.hyperkit.welding.structures.extremes.WidestX;

public class RendererXY extends Renderer2D {
	
	protected final boolean SPAN = false;
	protected final boolean EXTREME = false;
	protected final boolean WIDEST = false;
	protected final boolean POSITIVE = true;
	
	private double z;

	public RendererXY(ChartPanel chart_panel, double z) {
		super(chart_panel, "XY-Schweiﬂprofil", "L‰nge [x]", "Breite [y]");
		
		this.z = z;
	}
	
	@Override
	protected String getAnnotation() {
		return "z = " + FORMAT.format(z * 10) + " mm";
	}
	
	public void run(Search search, Range min_x, Range max_x, Path path_min_x, WidestX widest_x, DeepestX deepest_x, XYSeriesCollection dataset) throws SearchException {
		run(min_x, max_x, path_min_x, widest_x, deepest_x, dataset);
		
		// Add annotations
		
		Range deepest_y = search.findMaximumY(deepest_x.getX(), path_min_x.calculateStartY(deepest_x.getX()), 0);
		
		plot.addAnnotation(new XYLineAnnotation(widest_x.getX() * 10, widest_x.getMaxY().getOuterValue() * 10, widest_x.getX() * 10, -widest_x.getMaxY().getOuterValue() * 10, new BasicStroke(), Color.BLUE));
		plot.addAnnotation(new XYLineAnnotation(deepest_x.getX() * 10, deepest_y.getOuterValue() * 10, deepest_x.getX() * 10, -deepest_y.getOuterValue() * 10, new BasicStroke(), Color.BLUE));
		
		plot.addAnnotation(new XYTextAnnotation("Widest X", widest_x.getX() * 10, 0));
		plot.addAnnotation(new XYTextAnnotation("Deepest X", deepest_x.getX() * 10, 0));

		for (int index = 0; index < path_min_x.getSpans().size(); index++) {
			Span span = path_min_x.getSpans().get(index);
			
			float progress = (index + 1f) / path_min_x.getSpans().size();
			
			Color red = new Color(1 - progress / 2, 0, 0);
			Color green = new Color(0, 0.75f - progress / 2, 0);

			if (SPAN) {
				plot.addAnnotation(new XYLineAnnotation(span.getOriginX() * 10, span.getOriginY() * 10, span.getExtremeX().getOuterValue() * 10, span.getOriginY() * 10, SOLID, red));
				
				addCross(span.getOriginX() * 10, span.getOriginY() * 10, red);
				addCross(span.getExtremeX().getInnerValue() * 10, span.getOriginY() * 10, red);
			}
			if (EXTREME) {
				Range extreme_y = search.findMinimumY(span.getExtremeX().getInnerValue(), span.getOriginY(), 0);
				
				plot.addAnnotation(new XYLineAnnotation(span.getExtremeX().getInnerValue() * 10, extreme_y.getOuterValue() * 10, span.getExtremeX().getInnerValue() * 10, span.getExtremeMaxY().getOuterValue() * 10, SOLID, green));
				
				addCircle(span.getExtremeX().getInnerValue() * 10, extreme_y.getOuterValue() * 10, green);
				addCircle(span.getExtremeX().getInnerValue() * 10, span.getExtremeMaxY().getOuterValue() * 10, green);
			}
			if (WIDEST) {
				Range widest_y = search.findMinimumY(span.getWidestX().getX(), span.getOriginY(), 0);
				
				plot.addAnnotation(new XYLineAnnotation(span.getWidestX().getX() * 10, widest_y.getOuterValue() * 10, span.getWidestX().getX() * 10, span.getWidestX().getMaxY().getOuterValue() * 10, SOLID, green));
				
				addCross(span.getWidestX().getX() * 10, span.getOriginY() * 10, green);
				
				addCircle(span.getWidestX().getX() * 10, span.getWidestX().getMaxY().getOuterValue() * 10, green);
				addCircle(span.getWidestX().getX() * 10, widest_y.getOuterValue() * 10, green);
			}
		}
		if (POSITIVE) {
			plot.addAnnotation(new XYLineAnnotation(path_min_x.getFirstSpan().getOriginX() * 10, 0, max_x.getOuterValue() * 10, 0, SOLID, Color.RED));
			
			addCross(path_min_x.getFirstSpan().getOriginX() * 10, path_min_x.getFirstSpan().getOriginY() * 10, Color.RED);
			addCross(max_x.getOuterValue() * 10, 0, Color.RED);
		}
	}

}
