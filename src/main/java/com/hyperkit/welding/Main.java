package com.hyperkit.welding;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JFrame;
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
import com.hyperkit.welding.loaders.Loader2D;
import com.hyperkit.welding.renderers.RendererXY;
import com.hyperkit.welding.renderers.RendererXZ;
import com.hyperkit.welding.renderers.RendererYZ;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
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

		Render2DConfiguration render_xy_configuration = new Render2DConfiguration("XY-Darstellungsparameter");
		Render2DConfiguration render_yz_configuration = new Render2DConfiguration("YZ-Darstellungsparameter");
		Render2DConfiguration render_xz_configuration = new Render2DConfiguration("XZ-Darstellungsparameter");
		Render3DConfiguration render_3d_configuration = new Render3DConfiguration();

		RendererXY render_xy = new RendererXY(search, render_xy_configuration);
		RendererYZ render_yz = new RendererYZ(search, render_yz_configuration);
		RendererXZ render_xz = new RendererXZ(search, render_xz_configuration);

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

		JButton button = new JButton("Schweißprofil berechnen");

		// Create panel

		JPanel parameter_panel = new JPanel();

		parameter_panel.setLayout(new GridLayout(6, 1));
		parameter_panel.add(model_configuration.createPanel());
		parameter_panel.add(search_configuration.createPanel());
		parameter_panel.add(render_xy_configuration.createPanel());
		parameter_panel.add(render_yz_configuration.createPanel());
		parameter_panel.add(render_xz_configuration.createPanel());
		parameter_panel.add(render_3d_configuration.createPanel());

		// Create panel

		JPanel configuration_panel = new JPanel();

		configuration_panel.setLayout(new BorderLayout());
		configuration_panel.add(parameter_panel, BorderLayout.CENTER);
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

		JFrame frame = new JFrame("Software für die Berechnung von Schweißprofilen");

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
				try {
					
					// Clear canvas
					
					GL2 gl = (GL2) drawable.getGL();
					gl.glClearColor(1f, 1f, 1f, 1f);
					gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
					
					// Find ranges
				
					Range range_x = search.findMaximumX(0, 0);
					//Range range_y = search.findMaximumY(0, 0);
					Range range_z = search.findMaximumZ(0, 0);
	
					// Change to projection matrix
					
					gl.glMatrixMode(GL2.GL_PROJECTION);
					gl.glLoadIdentity();
	
					// Perspective
					
					double camera_x = render_3d_configuration.getCameraX();
					double camera_y = render_3d_configuration.getCameraY();
					double camera_z = render_3d_configuration.getCameraZ();
					
					double eye_x = render_3d_configuration.getEyeX();
					double eye_y = render_3d_configuration.getEyeY();
					double eye_z = render_3d_configuration.getEyeZ();
					
					float widthHeightRatio = (float) width / (float) height;
					glu.gluPerspective(45, widthHeightRatio, 0.001, 1000);
					glu.gluLookAt(camera_x, camera_y, camera_z, eye_x, eye_y, eye_z, 0, 1, 0);
	
					// Change back to model view matrix
					
					gl.glMatrixMode(GL2.GL_MODELVIEW);
					gl.glLoadIdentity();
					
					// Render data
					
					gl.glEnable(GL2.GL_POINT_SMOOTH);
					gl.glEnable(GL2.GL_BLEND);
					gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
					gl.glPointSize(10);
					
					double points_step = render_3d_configuration.getSamples();
					
					gl.glBegin(GL2.GL_POINTS);
					
					double min = search_configuration.getLimitTemperature() - search_configuration.getTemperatureThershold();
					double max = search_configuration.getLimitTemperature() - search_configuration.getTemperatureThershold();
					
					int total = 0;
	
					for (double x = -Math.abs(range_x.getLowerValue()) * 2; x <= Math.abs(range_x.getLowerValue()) * 2; x += points_step) {
						//for (double y = -Math.abs(range_y.getLowerValue()); y <= 0; y+= points_step) {
							for (double z = -Math.abs(range_z.getLowerValue()) * 2; z <= Math.abs(range_z.getLowerValue()) * 2; z += points_step) {
								double temperature = model.calculateTemperature(x, 0, z);
								if (temperature >= search_configuration.getLimitTemperature() - search_configuration.getTemperatureThershold()) {
									max = Math.max(max, temperature);
									total++;
								}
							}
						//}
					}

					progress_bar.setMaximum(0);
					progress_bar.setMaximum(total);
					
					progress_bar.setValue(0);
					progress_bar.setString("0/" + total + " Datenpunkte");
					
					int count = 0;
	
					for (double x = -Math.abs(range_x.getLowerValue()) * 2; x <= Math.abs(range_x.getLowerValue()) * 2; x += points_step) {
						//for (double y = -Math.abs(range_y.getLowerValue()); y <= 0; y+= points_step) {
							for (double z = -Math.abs(range_z.getLowerValue()) * 2; z <= Math.abs(range_z.getLowerValue()) * 2; z += points_step) {
								double temperature = model.calculateTemperature(x, 0, z);
								if (temperature >= search_configuration.getLimitTemperature() - search_configuration.getTemperatureThershold()) {
									gl.glColor3d(Math.sqrt((temperature - min) / (max - min)), 0, Math.sqrt(1 - (temperature - min) / (max - min)));
									gl.glVertex3d(x, 0, z);
									
									count++;
									
									progress_bar.setValue(count);
									progress_bar.setString(count + "/" + total  + " Datenpunkte");
								}
							}
						//}
					}
					
					gl.glEnd();
					
					// Render grid
					
					float grid_length = 1f;
					float grid_step = 0.05f;
					
					float grid_r = 0.9f;
					float grid_g = 0.9f;
					float grid_b = 0.9f;
					
					for (float grid_x = -grid_length; grid_x <= grid_length; grid_x += grid_step) {
						gl.glBegin(GL2.GL_LINES);
						gl.glColor3f(grid_r, grid_g, grid_b);
						gl.glVertex3f(grid_x, 0, -grid_length);
						gl.glVertex3f(grid_x, 0, +grid_length);
						gl.glEnd();
					}
					
					for (float grid_z = -grid_length; grid_z <= grid_length; grid_z += grid_step) {
						gl.glBegin(GL2.GL_LINES);
						gl.glColor3f(grid_r, grid_g, grid_b);
						gl.glVertex3f(-grid_length, 0, grid_z);
						gl.glVertex3f(+grid_length, 0, grid_z);
						gl.glEnd();
					}
					
					// Render axes
					
					float axis_length = 110f;

					gl.glBegin(GL2.GL_LINES);
					gl.glColor3f(1, 0, 0);
					gl.glVertex3f(-axis_length, 0, 0);
					gl.glVertex3f(+axis_length, 0, 0);
					gl.glEnd();
					
					gl.glBegin(GL2.GL_LINES);
					gl.glColor3f(0, 0, 1);
					gl.glVertex3f(0, -axis_length, 0);
					gl.glVertex3f(0, +axis_length, 0);
					gl.glEnd();
					
					gl.glBegin(GL2.GL_LINES);
					gl.glColor3f(0, 1, 0);
					gl.glVertex3f(0, 0, -axis_length);
					gl.glVertex3f(0, 0, +axis_length);
					gl.glEnd();
					
				} catch (SearchException e) {
					e.printStackTrace();
				}
			}
		});

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						new Loader2D(frame, progress_bar, render_xy, chart_xy_panel, "XY-Schweißprofil", "Länge", "Breite").run();
						new Loader2D(frame, progress_bar, render_yz, chart_yz_panel, "YZ-Schweißprofil", "Breite", "Tiefe").run();
						new Loader2D(frame, progress_bar, render_xz, chart_xz_panel, "XZ-Schweißprofil", "Länge", "Tiefe").run();
						canvas.display();
					}
				}).start();
			}
		});
	}

}
