package com.hyperkit.welding.renderers;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Integrator;
import com.hyperkit.welding.Renderer;

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
	
	public void run(XYSeriesCollection dataset) {
		
		double upper_area = Integrator.calculateArea(dataset.getSeries(0));
		double lower_area = Integrator.calculateArea(dataset.getSeries(1));
		
		//System.out.println("Area 0: " + Integrator.calculateArea(dataset.getSeries(0)));
		//System.out.println("Area 1: " + Integrator.calculateArea(dataset.getSeries(1)));

		// Create chart

		JFreeChart chart = ChartFactory.createXYLineChart(name + " (Fläche = " + FORMAT.format(lower_area) + " bis " + FORMAT.format(upper_area) + " mm², " + getAnnotation() + ")",  x_axis + " (in mm)", y_axis + " (in mm)", dataset, PlotOrientation.VERTICAL, true, true, false);

		// Update panel

		chart_panel.setChart(chart);
		
	}

}
