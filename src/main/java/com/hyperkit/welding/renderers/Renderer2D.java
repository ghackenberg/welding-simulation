package com.hyperkit.welding.renderers;

import java.awt.BasicStroke;
import java.awt.Color;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.ValueMarker;
import org.jfree.chart.plot.XYPlot;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Integrator;
import com.hyperkit.welding.Renderer;
import com.hyperkit.welding.structures.Range;

public abstract class Renderer2D extends Renderer {
	
	private ChartPanel chart_panel;
	private String name;
	private String x_axis;
	private String y_axis;
	
	public Renderer2D(ChartPanel chart_panel, String name, String x_axis, String y_axis) {
		this.chart_panel = chart_panel;
		this.name = name;
		this.x_axis = x_axis;
		this.y_axis = y_axis;
	}
	
	protected abstract String getAnnotation();
	
	public void run(Range min_x, Range max_x, Range max_y, Range min_z, XYSeriesCollection dataset) {
		
		double upper_area = Integrator.calculateArea(dataset.getSeries(0));
		double lower_area = Integrator.calculateArea(dataset.getSeries(1));
		
		//System.out.println("Area 0: " + Integrator.calculateArea(dataset.getSeries(0)));
		//System.out.println("Area 1: " + Integrator.calculateArea(dataset.getSeries(1)));

		// Create chart

		JFreeChart chart = ChartFactory.createXYLineChart(name + " (Fläche = " + FORMAT.format(lower_area) + " bis " + FORMAT.format(upper_area) + " mm², " + getAnnotation() + ")",  x_axis + " (in mm)", y_axis + " (in mm)", dataset, PlotOrientation.VERTICAL, true, true, false);

		// Get plot

		XYPlot plot = chart.getXYPlot();
		
		// Get axes
		
		ValueAxis domain = plot.getDomainAxis();
		ValueAxis range = plot.getRangeAxis();

		// Synchronize axes
		
		double xInterval = (max_x.getOuterValue() - min_x.getOuterValue()) * 10 * 1.1;
		double yInterval = max_y.getOuterValue() * 2 * 10 * 1.1;
		double zInterval = -min_z.getOuterValue() * 10 * 1.1;
		double xyzInterval = Math.max(Math.max(xInterval, yInterval), zInterval);
		
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

}
