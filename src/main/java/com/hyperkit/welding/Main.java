package com.hyperkit.welding;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.configurations.ModelConfiguration;
import com.hyperkit.welding.configurations.RenderConfiguration;
import com.hyperkit.welding.configurations.SearchConfiguration;
import com.hyperkit.welding.exceptions.SearchException;

public class Main {
	
	private static final DecimalFormat FORMAT = new DecimalFormat("0.00");

	public static void main(String[] args) {
		// Look and feel
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException e) {
			e.printStackTrace();
		} catch (InstantiationException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (UnsupportedLookAndFeelException e) {
			e.printStackTrace();
		}
		
		// Create model
		
		ModelConfiguration model_configuration = new ModelConfiguration();
		Model model = new Model(model_configuration);

		// Create search

		SearchConfiguration search_configuration = new SearchConfiguration();
		Search search = new Search(model, search_configuration);

		// Create render

		RenderConfiguration render_configuration = new RenderConfiguration();
		Render render = new Render(search, render_configuration);

		// Create panel

		ChartPanel chart_panel = new ChartPanel(null);
		
		// Create progressbar
		
		JProgressBar progress_bar = new JProgressBar();
		
		progress_bar.setString("0 / 0 Diagrammpunkte");
		progress_bar.setStringPainted(true);
		progress_bar.setMinimum(0);
		progress_bar.setMaximum(100);

		// Create button

		JButton button = new JButton("Schweiﬂprofil berechnen");
		
		// Create panel
		
		JPanel parameter_panel = new JPanel();
		
		parameter_panel.setLayout(new GridLayout(3, 1));
		parameter_panel.add(model_configuration.createPanel());
		parameter_panel.add(search_configuration.createPanel());
		parameter_panel.add(render_configuration.createPanel());
		
		// Create panel
		
		JPanel configuration_panel = new JPanel();
		
		configuration_panel.setLayout(new BorderLayout());
		configuration_panel.add(parameter_panel, BorderLayout.CENTER);
		configuration_panel.add(button, BorderLayout.PAGE_END);
		
		// Create panel
		
		JPanel display_panel = new JPanel();
		
		display_panel.setLayout(new BorderLayout());
		display_panel.add(chart_panel, BorderLayout.CENTER);
		display_panel.add(progress_bar, BorderLayout.PAGE_END);
		
		// Create frame

		JFrame frame = new JFrame("Software f¸r die Berechnung von Schweiﬂprofilen");

		frame.setLayout(new BorderLayout());
		frame.add(configuration_panel, BorderLayout.WEST);
		frame.add(display_panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		// Create button action listener
		
		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							// Create dataset

							XYSeriesCollection dataset = render.generateDataset(new Progress() {
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
							
							double upper_area = Integrator.calculateArea(dataset.getSeries(0));
							double lower_area = Integrator.calculateArea(dataset.getSeries(1));
							
							//System.out.println("Area 0: " + Integrator.calculateArea(dataset.getSeries(0)));
							//System.out.println("Area 1: " + Integrator.calculateArea(dataset.getSeries(1)));

							// Create chart

							JFreeChart chart = ChartFactory.createXYLineChart("Schweiﬂprofil (Fl‰che = " + FORMAT.format(lower_area) + " bis " + FORMAT.format(upper_area) + " mm≤)", "Breite (in mm)", "Tiefe (in mm)", dataset, PlotOrientation.VERTICAL, true, true, false);

							// Update panel

							chart_panel.setChart(chart);
							
						} catch (SearchException exception) {
							
							JOptionPane.showMessageDialog(frame, "Das Schweiﬂprofil konnte nicht berechnet werden. Passen sie die Parametereinstellungen an.", "Berechnungsfehler", JOptionPane.ERROR_MESSAGE);
							
							exception.printStackTrace();
							
						}
					}
				}).start();
			}
		});
	}

}
