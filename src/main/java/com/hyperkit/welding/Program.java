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

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.configurations.ModelConfiguration;
import com.hyperkit.welding.configurations.Render2DConfiguration;
import com.hyperkit.welding.configurations.Render3DConfiguration;
import com.hyperkit.welding.configurations.SearchConfiguration;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.generators.GeneratorXY;
import com.hyperkit.welding.generators.GeneratorXZ;
import com.hyperkit.welding.generators.GeneratorYZ;
import com.hyperkit.welding.renderers.Renderer3D;
import com.hyperkit.welding.renderers.RendererXY;
import com.hyperkit.welding.renderers.RendererXZ;
import com.hyperkit.welding.renderers.RendererYZ;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;

public abstract class Program<S extends ModelConfiguration, T extends Model<S>> {
	
	protected static final DecimalFormat FORMAT = new DecimalFormat("0.00");

	public void run(String[] args) {
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
		
		// Create datasets
		
		XYSeriesCollection dataset_xy = new XYSeriesCollection();
		XYSeriesCollection dataset_xz = new XYSeriesCollection();
		XYSeriesCollection dataset_yz = new XYSeriesCollection();
		XYSeriesCollection dataset_yz_widest = new XYSeriesCollection();
		XYSeriesCollection dataset_yz_deepest = new XYSeriesCollection();

		// Create model

		S model_configuration = getModelConfiguration();
		
		T model = getModel(model_configuration);

		// Create search

		SearchConfiguration search_configuration = new SearchConfiguration();
		
		Search search = new Search(model, search_configuration);

		// Create generators

		Render2DConfiguration render_2d_configuration = new Render2DConfiguration();

		GeneratorXY generator_xy = new GeneratorXY(search, render_2d_configuration);
		GeneratorYZ generator_yz = new GeneratorYZ(search, render_2d_configuration);
		GeneratorXZ generator_xz = new GeneratorXZ(search, render_2d_configuration);
		
		// Create renderers
		
		Render3DConfiguration render_3d_configuration = new Render3DConfiguration();
		
		Renderer3D render_3d = new Renderer3D(search, render_3d_configuration);

		// Create panels

		ChartPanel chart_xy_panel = new ChartPanel(null);
		ChartPanel chart_yz_panel = new ChartPanel(null);
		ChartPanel chart_xz_panel = new ChartPanel(null);
		
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);

		Listener listener = new Listener(dataset_xy, dataset_xz, dataset_yz_widest, dataset_yz_deepest, render_3d);
		canvas.addGLEventListener(listener);

		// Create progress bar

		final JProgressBar progress_bar = new JProgressBar();

		progress_bar.setString("0 / 0 Diagrammpunkte");
		progress_bar.setStringPainted(true);
		progress_bar.setMinimum(0);
		progress_bar.setMaximum(100);

		// Create button

		JButton button = new JButton("Schwei�profil berechnen");
		
		// Create configurator
		
		Configurator configurator = new Configurator();
		
		configurator.addConfiguration(model_configuration);
		configurator.addConfiguration(search_configuration);
		configurator.addConfiguration(render_2d_configuration);
		configurator.addConfiguration(render_3d_configuration);

		// Create panel

		JPanel configuration_panel = new JPanel();

		configuration_panel.setLayout(new BorderLayout());
		configuration_panel.add(configurator.createPanel(), BorderLayout.CENTER);
		configuration_panel.add(button, BorderLayout.PAGE_END);

		// Create panel

		JPanel display_panel = new JPanel();

		display_panel.setLayout(new GridLayout(2, 2));
		display_panel.add(chart_xy_panel);
		display_panel.add(chart_yz_panel);
		display_panel.add(chart_xz_panel);
		display_panel.add(canvas);

		// Create panel

		JPanel intermediate_panel = new JPanel();

		intermediate_panel.setLayout(new BorderLayout());
		intermediate_panel.add(display_panel, BorderLayout.CENTER);
		intermediate_panel.add(progress_bar, BorderLayout.PAGE_END);

		// Create frame

		JFrame frame = new JFrame("Software f�r die Berechnung von Schwei�profilen");

		frame.setLayout(new BorderLayout());
		frame.add(configuration_panel, BorderLayout.WEST);
		frame.add(intermediate_panel, BorderLayout.CENTER);
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
						// Disable button
						button.setEnabled(false);
						
						try {
							
							// Perform calculation
							
							// Clean datasets
							dataset_xy.removeAllSeries();
							dataset_xz.removeAllSeries();
							dataset_yz.removeAllSeries();
							dataset_yz_widest.removeAllSeries();
							dataset_yz_deepest.removeAllSeries();
							
							// Clean canvas
							canvas.display();
							
							// Calcuate min_x
							Range min_x = search.findMinimumX(search_configuration.getInitialPosition() / 10, 0, 0);
							JOptionPane.showMessageDialog(frame, "Untere X-Grenze gefunden bei X=" + FORMAT.format(min_x.getInnerValue() * 10) + "mm", "Zwischenmeldung", JOptionPane.INFORMATION_MESSAGE);
							
							// Calculate max_x
							Range max_x = search.findMaximumX(search_configuration.getInitialPosition() / 10, 0, 0);
							JOptionPane.showMessageDialog(frame, "Obere X-Grenze gefunden bei X=" + FORMAT.format(max_x.getOuterValue() * 10) + "mm", "Zwischenmeldung", JOptionPane.INFORMATION_MESSAGE);
							
							// Calculate widest_x
							double widest_x = search.findWidestX(min_x.getInnerValue(), max_x.getInnerValue());
							
							// Calculate max_y
							Range max_y = search.findMaximumY(widest_x, 0);
							JOptionPane.showMessageDialog(frame, "Breiteste Stelle gefunden bei X=" + FORMAT.format(widest_x * 10) + "mm", "Zwischenmeldung", JOptionPane.INFORMATION_MESSAGE);
							
							// Calculate deepest_x
							double deepest_x = search.findDeepestX(min_x.getInnerValue(), max_x.getInnerValue());
							
							// Calculate min_z
							Range min_z = search.findMinimumZ(deepest_x, 0);
							JOptionPane.showMessageDialog(frame, "Tiefste Stelle gefunden bei X=" + FORMAT.format(deepest_x * 10) + "mm", "Zwischenmeldung", JOptionPane.INFORMATION_MESSAGE);
							
							// Propagate values
							listener.setMinX(min_x);
							listener.setMaxX(max_x);
							listener.setWidestX(widest_x);
							listener.setDeepestX(deepest_x);
							
							// Create progress tracker
							Progress progress = new Progress() {
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
							};
							
							// Generate datasets
							generator_xy.generateDataset(min_x, max_x, 0, dataset_xy, progress);
							generator_xz.generateDataset(min_x, max_x, 0, dataset_xz, progress);
							generator_yz.generateDataset(widest_x, dataset_yz_widest, progress);
							generator_yz.generateDataset(deepest_x, dataset_yz_deepest, progress);
							generator_yz.generateDataset(widest_x, deepest_x, dataset_yz, progress);
							
							// Render datasets
							new RendererXY(chart_xy_panel, 0).run(min_x, max_x, max_y, min_z, dataset_xy);
							new RendererXZ(chart_xz_panel, 0).run(min_x, max_x, max_y, min_z, dataset_xz);
							new RendererYZ(chart_yz_panel, widest_x, deepest_x).run(min_x, max_x, max_y, min_z, dataset_yz);
							
							// Update canvas
							canvas.display();
							
						} catch (SearchException e) {
							
							// Handle search exception
							
							JOptionPane.showMessageDialog(frame, e.getMessage(), "Berechnungsfehler", JOptionPane.ERROR_MESSAGE);
							
							e.printStackTrace();
							
						}
						
						// Enable button
						button.setEnabled(true);
					}
				}).start();
			}
		});
	}
	
	protected abstract S getModelConfiguration();
	
	protected abstract T getModel(S configuration);

}
