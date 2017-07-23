package com.hyperkit.welding.loaders;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Integrator;
import com.hyperkit.welding.Loader;
import com.hyperkit.welding.Progress;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.renderers.Renderer2D;

public class Loader2D extends Loader {
	
	private Renderer2D render;
	private ChartPanel chart_panel;
	private String name;
	private String x_axis;
	private String y_axis;
	
	public Loader2D(JFrame frame, JProgressBar progress_bar, Renderer2D render, ChartPanel chart_panel, String name, String x_axis, String y_axis) {
		super(frame, progress_bar);
		this.render = render;
		this.chart_panel = chart_panel;
		this.name = name;
		this.x_axis = x_axis;
		this.y_axis = y_axis;
	}
	
	@Override
	public void run() {
		try {
			// Create dataset
	
			XYSeriesCollection dataset_yz = render.generateDataset(new Progress() {
				@Override
				public void initialize(int total) {
					progress_bar.setMaximum(total);
					progress_bar.setValue(0);
				}
				@Override
				public void update(int current, int total) {
					progress_bar.setString(current + " / " + total + " Diagrammpunkte");
					progress_bar.setValue(current);
				}
			});
			
			double upper_area = Integrator.calculateArea(dataset_yz.getSeries(0));
			double lower_area = Integrator.calculateArea(dataset_yz.getSeries(1));
			
			//System.out.println("Area 0: " + Integrator.calculateArea(dataset.getSeries(0)));
			//System.out.println("Area 1: " + Integrator.calculateArea(dataset.getSeries(1)));
	
			// Create chart
	
			JFreeChart chart_yz = ChartFactory.createXYLineChart(name + " (Fläche = " + FORMAT.format(lower_area) + " bis " + FORMAT.format(upper_area) + " mm²)",  x_axis + " (in mm)", y_axis + " (in mm)", dataset_yz, PlotOrientation.VERTICAL, true, true, false);
	
			// Update panel
	
			chart_panel.setChart(chart_yz);
			
		} catch (SearchException exception) {
			
			JOptionPane.showMessageDialog(frame, "Das " + name + " konnte nicht berechnet werden. Passen sie die Parametereinstellungen an.", "Berechnungsfehler", JOptionPane.ERROR_MESSAGE);
			
			exception.printStackTrace();
			
		}
	}

}
