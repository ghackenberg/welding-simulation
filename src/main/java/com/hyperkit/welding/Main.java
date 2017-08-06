package com.hyperkit.welding;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import org.jfree.chart.ChartPanel;

import com.hyperkit.welding.configurations.ModelConfiguration;
import com.hyperkit.welding.configurations.Render2DConfiguration;
import com.hyperkit.welding.configurations.Render3DConfiguration;
import com.hyperkit.welding.configurations.SearchConfiguration;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.loaders.LoaderXY;
import com.hyperkit.welding.loaders.LoaderXZ;
import com.hyperkit.welding.loaders.LoaderYZ;
import com.hyperkit.welding.renderers.Renderer3D;
import com.hyperkit.welding.renderers.RendererXY;
import com.hyperkit.welding.renderers.RendererXZ;
import com.hyperkit.welding.renderers.RendererYZ;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLCapabilities;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.GLProfile;
import com.jogamp.opengl.awt.GLCanvas;
import com.jogamp.opengl.glu.GLU;

public class Main {

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

		Render2DConfiguration render_2d_configuration = new Render2DConfiguration();
		Render3DConfiguration render_3d_configuration = new Render3DConfiguration();

		RendererXY render_xy = new RendererXY(search, render_2d_configuration);
		RendererYZ render_yz = new RendererYZ(search, render_2d_configuration);
		RendererXZ render_xz = new RendererXZ(search, render_2d_configuration);

		// Create panel

		ChartPanel chart_xy_panel = new ChartPanel(null);
		ChartPanel chart_yz_panel = new ChartPanel(null);
		ChartPanel chart_xz_panel = new ChartPanel(null);
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);

		// Create progressbar

		final JProgressBar progress_bar = new JProgressBar();

		progress_bar.setString("0 / 0 Diagrammpunkte");
		progress_bar.setStringPainted(true);
		progress_bar.setMinimum(0);
		progress_bar.setMaximum(100);

		// Create button

		JButton button = new JButton("Schweiﬂprofil berechnen");
		
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

		JFrame frame = new JFrame("Software f¸r die Berechnung von Schweiﬂprofilen");

		frame.setLayout(new BorderLayout());
		frame.add(configuration_panel, BorderLayout.WEST);
		frame.add(intermediate_panel, BorderLayout.CENTER);
		frame.pack();
		frame.setVisible(true);
		frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

		// Create button action listener
		
		canvas.addGLEventListener(new GLEventListener() {
			
			private GLU glu;
			private int width;
			private int height;

			@Override
			public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
				GL gl = drawable.getGL();
				gl.glViewport(0, 0, width, height);
				this.width = width;
				this.height = height;
			}
			@Override
			public void init(GLAutoDrawable drawable) {
				glu = new GLU();
			}
			@Override
			public void dispose(GLAutoDrawable drawable) {

			}
			@Override
			public void display(GLAutoDrawable drawable) {
				new Renderer3D(search, render_3d_configuration).render(width, height, drawable, glu, new Progress() {
					@Override
					public void update(int current, int total) {
						progress_bar.setValue(current);
						progress_bar.setString(current + " / " + total + " Datenpunkte");
					}
					@Override
					public void initialize(int total) {
						progress_bar.setMaximum(total);
					}
				});
			}
		});

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						try {
							Range min_x = search.findMinimumX(0, 0);
							Range max_x = search.findMaximumX(0, 0);
							
							// TODO find optimum x!
							
							new LoaderXY(frame, progress_bar, render_xy, chart_xy_panel, min_x, max_x, 0).run();
							new LoaderYZ(frame, progress_bar, render_yz, chart_yz_panel, 0).run();
							new LoaderXZ(frame, progress_bar, render_xz, chart_xz_panel, min_x, max_x, 0).run();
							
							canvas.display();
						} catch (SearchException e) {
							
							JOptionPane.showMessageDialog(frame, "Das Schweiﬂprofil konnte nicht berechnet werden. Passen sie die Parametereinstellungen an.", "Berechnungsfehler", JOptionPane.ERROR_MESSAGE);
							
							e.printStackTrace();
							
						}
					}
				}).start();
			}
		});
	}

}
