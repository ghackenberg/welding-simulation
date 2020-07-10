package com.hyperkit.welding.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Arc2D;

import org.jfree.chart.ChartPanel;
import org.jfree.chart.annotations.XYBoxAnnotation;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
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
		
		BasicStroke dashed = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);

		for (int index = 0; index < path_min_x.getSpans().size(); index++) {
			Span span = path_min_x.getSpans().get(index);
			
			Range widest_y = search.findMinimumY(span.getWidestX().getX(), span.getOriginY(), 0);
			
			float progress = (index + 1f) / path_min_x.getSpans().size();
			
			Color color = new Color(1 - progress / 2, 0, 0);
			
			plot.addAnnotation(new XYLineAnnotation(span.getOriginX() * 10, span.getOriginY() * 10, span.getExtremeX().getOuterValue() * 10, span.getOriginY() * 10, new BasicStroke(), color));
			plot.addAnnotation(new XYLineAnnotation(span.getWidestX().getX() * 10, widest_y.getOuterValue() * 10, span.getWidestX().getX() * 10, span.getWidestX().getMaxY().getOuterValue() * 10, dashed, color));
			
			addCross(span.getOriginX() * 10, span.getOriginY() * 10, color);
			addCircle(span.getWidestX().getX() * 10, span.getWidestX().getMaxY().getOuterValue() * 10, color);
			addCircle(span.getWidestX().getX() * 10, widest_y.getOuterValue() * 10, color);
			addBox(span.getExtremeX().getInnerValue() * 10, span.getOriginY() * 10, color);
		}
		
		plot.addAnnotation(new XYLineAnnotation(path_min_x.getFirstSpan().getOriginX() * 10, 0, max_x.getOuterValue() * 10, 0, dashed, Color.RED));
		
		addBox(max_x.getOuterValue() * 10, 0, Color.RED);
	}
	
	private void addCircle(double x, double y, Color color) {
		plot.addAnnotation(new XYShapeAnnotation(new Arc2D.Double(x - xyzInterval / 200, y - xyzInterval / 200, xyzInterval / 100, xyzInterval / 100, 0, 360, Arc2D.OPEN), new BasicStroke(), color));
	}
	
	private void addBox(double x, double y, Color color) {
		plot.addAnnotation(new XYBoxAnnotation(x - xyzInterval / 200, y - xyzInterval / 200, x + xyzInterval / 200, y + xyzInterval / 200, new BasicStroke(), color));
	}
	
	private void addCross(double x, double y, Color color) {
		plot.addAnnotation(new XYLineAnnotation(x - xyzInterval / 200, y - xyzInterval / 200, x + xyzInterval / 200, y + xyzInterval / 200, new BasicStroke(), color));
		plot.addAnnotation(new XYLineAnnotation(x - xyzInterval / 200, y + xyzInterval / 200, x + xyzInterval / 200, y - xyzInterval / 200, new BasicStroke(), color));
	}

}
