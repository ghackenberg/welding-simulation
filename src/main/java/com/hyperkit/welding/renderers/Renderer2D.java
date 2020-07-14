package com.hyperkit.welding.renderers;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.geom.Arc2D;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYBoxAnnotation;
import org.jfree.chart.annotations.XYLineAnnotation;
import org.jfree.chart.annotations.XYShapeAnnotation;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Integrator;
import com.hyperkit.welding.Renderer;
import com.hyperkit.welding.structures.Path;
import com.hyperkit.welding.structures.Range;
import com.hyperkit.welding.structures.extremes.DeepestX;
import com.hyperkit.welding.structures.extremes.WidestX;

public abstract class Renderer2D extends Renderer {
	
	final protected BasicStroke SOLID = new BasicStroke();
	final protected BasicStroke DASHED = new BasicStroke(1, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL, 0, new float[]{3}, 0);
	
	private JFreeChart chart;
	protected XYPlot plot;
	private ValueAxis domain;
	private ValueAxis range;
	private ChartPanel chart_panel;
	private String name;
	private String x_axis;
	private String y_axis;
	protected double xyzInterval;
	
	public Renderer2D(ChartPanel chart_panel, String name, String x_axis, String y_axis) {
		this.chart_panel = chart_panel;
		this.name = name;
		this.x_axis = x_axis;
		this.y_axis = y_axis;
	}
	
	protected abstract String getAnnotation();
	
	protected void run(Range min_x, Range max_x, Path path_min_x, WidestX widest_x, DeepestX deepest_x, XYSeriesCollection dataset) {
		
		double upper_area = Integrator.calculateArea(dataset.getSeries(0));
		double lower_area = Integrator.calculateArea(dataset.getSeries(1));
		
		//System.out.println("Area 0: " + Integrator.calculateArea(dataset.getSeries(0)));
		//System.out.println("Area 1: " + Integrator.calculateArea(dataset.getSeries(1)));

		// Create chart

		chart = ChartFactory.createXYLineChart(name + " (Fl�che = " + FORMAT.format(lower_area) + " bis " + FORMAT.format(upper_area) + " mm�, " + getAnnotation() + ")",  x_axis + " (in mm)", y_axis + " (in mm)", dataset, PlotOrientation.VERTICAL, true, true, false);

		// Get plot

		plot = chart.getXYPlot();
		
		// Get axes
		
		domain = plot.getDomainAxis();
		range = plot.getRangeAxis();

		// Synchronize axes
		
		double xInterval = (max_x.getOuterValue() - min_x.getOuterValue()) * 10 * 1.1;
		double yInterval = widest_x.getMaxY().getOuterValue() * 2 * 10 * 1.1;
		double zInterval = -deepest_x.getMinZ().getOuterValue() * 10 * 1.1;
		
		xyzInterval = Math.max(Math.max(xInterval, yInterval), zInterval);
		
		double domainInterval = domain.getUpperBound() - domain.getLowerBound();
		double rangeInterval = range.getUpperBound() - range.getLowerBound();
		
		double domainDifference = xyzInterval - domainInterval;
		double rangeDifference = xyzInterval - rangeInterval;
			
		domain.setLowerBound(domain.getLowerBound() - domainDifference / 2);
		domain.setUpperBound(domain.getUpperBound() + domainDifference / 2);
			
		range.setLowerBound(range.getLowerBound() - rangeDifference / 2);
		range.setUpperBound(range.getUpperBound() + rangeDifference / 2);
		
		// Add markers
		
		plot.addDomainMarker(new ValueMarker(0, Color.BLACK, new BasicStroke()));
		plot.addRangeMarker(new ValueMarker(0, Color.BLACK, new BasicStroke()));
		
		// Set paint
		
		if (dataset.getSeriesCount() == 2) {
			plot.getRenderer().setSeriesPaint(0, new Color(0, 0, 255));
			plot.getRenderer().setSeriesPaint(1, new Color(0, 0, 128));
		}
		if (dataset.getSeriesCount() == 4) {
			plot.getRenderer().setSeriesPaint(0, new Color(0, 0, 255));
			plot.getRenderer().setSeriesPaint(1, new Color(0, 0, 128));
			plot.getRenderer().setSeriesPaint(2, new Color(0, 0, 255));
			plot.getRenderer().setSeriesPaint(3, new Color(0, 0, 128));
		}
		if (dataset.getSeriesCount() == 6) {
			plot.getRenderer().setSeriesPaint(0, new Color(0, 0, 255));
			plot.getRenderer().setSeriesPaint(1, new Color(0, 0, 128));
			plot.getRenderer().setSeriesPaint(2, new Color(255, 0, 0));
			plot.getRenderer().setSeriesPaint(3, new Color(128, 0, 0));
			plot.getRenderer().setSeriesPaint(4, new Color(0, 255, 0));
			plot.getRenderer().setSeriesPaint(5, new Color(0, 128, 0));
		}

		// Update panel

		chart_panel.setChart(chart);
		
	}
	
	protected void addCircle(double x, double y, Color color) {
		plot.addAnnotation(new XYShapeAnnotation(new Arc2D.Double(x - Math.sqrt(2) * xyzInterval / 200, y - Math.sqrt(2) * xyzInterval / 200, Math.sqrt(2) * xyzInterval / 100, Math.sqrt(2) * xyzInterval / 100, 0, 360, Arc2D.OPEN), new BasicStroke(), color));
	}
	
	protected void addBox(double x, double y, Color color) {
		plot.addAnnotation(new XYBoxAnnotation(x - xyzInterval / 200, y - xyzInterval / 200, x + xyzInterval / 200, y + xyzInterval / 200, new BasicStroke(), color));
	}
	
	protected void addCross(double x, double y, Color color) {
		plot.addAnnotation(new XYLineAnnotation(x - xyzInterval / 200, y - xyzInterval / 200, x + xyzInterval / 200, y + xyzInterval / 200, new BasicStroke(), color));
		plot.addAnnotation(new XYLineAnnotation(x - xyzInterval / 200, y + xyzInterval / 200, x + xyzInterval / 200, y - xyzInterval / 200, new BasicStroke(), color));
	}

}
