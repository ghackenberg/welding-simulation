package com.hyperkit.welding;

import java.awt.BorderLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.text.DecimalFormat;

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
import com.hyperkit.welding.loaders.Loader2D;
import com.hyperkit.welding.loaders.Loader3D;
import com.hyperkit.welding.renderers.Renderer3D;
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
import com.jogamp.opengl.glu.GLUquadric;

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
		Renderer3D render_3d = new Renderer3D(search, render_3d_configuration);

		// Create panel

		ChartPanel chart_xy_panel = new ChartPanel(null);
		ChartPanel chart_yz_panel = new ChartPanel(null);
		ChartPanel chart_xz_panel = new ChartPanel(null);
		GLProfile profile = GLProfile.getDefault();
		GLCapabilities capabilities = new GLCapabilities(profile);
		GLCanvas canvas = new GLCanvas(capabilities);
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
				GL2 gl = (GL2) drawable.getGL();
				gl.glClearColor(1f, 1f, 1f, 1f);
				gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

				// Change to projection matrix
				gl.glMatrixMode(GL2.GL_PROJECTION);
				gl.glLoadIdentity();

				// Perspective
				float widthHeightRatio = (float) width / (float) height;
				glu.gluPerspective(45, widthHeightRatio, 1, 1000);
				glu.gluLookAt(0, 100, -100, 0, 0, 0, 0, 1, 0);

				// Change back to model view matrix
				gl.glMatrixMode(GL2.GL_MODELVIEW);
				gl.glLoadIdentity();

				/*
				// Prepare light parameters
				float SHINE_ALL_DIRECTIONS = 1;
				float[] lightPos = { -200, 200, -200, SHINE_ALL_DIRECTIONS };
				float[] lightColorAmbient = { 0.2f, 0.2f, 0.2f, 1f };
				float[] lightColorSpecular = { 0.8f, 0.8f, 0.8f, 1f };

				// Set light parameters
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_POSITION, lightPos, 0);
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_AMBIENT, lightColorAmbient, 0);
				gl.glLightfv(GL2.GL_LIGHT1, GL2.GL_SPECULAR, lightColorSpecular, 0);

				// Enable lighting in GL
				gl.glEnable(GL2.GL_LIGHT1);
				gl.glEnable(GL2.GL_LIGHTING);
				gl.glEnable(GL2.GL_COLOR_MATERIAL);
				*/
				
				// Render sphere
				GLUquadric quadric = glu.gluNewQuadric();

				glu.gluQuadricDrawStyle(quadric, GLU.GLU_FILL);
				glu.gluQuadricNormals(quadric, GLU.GLU_FLAT);
				glu.gluQuadricOrientation(quadric, GLU.GLU_OUTSIDE);

				for (int x = 0; x < 10; x++) {
					for (int y = 0; y < 10; y++) {
						for (int z = 0; z < 10; z++) {
							gl.glPushMatrix();
							gl.glColor3f(x / 10f, y / 10f, z / 10f);
							gl.glTranslatef(x * 5, y * 5, z * 5);
							glu.gluSphere(quadric, 0.5f, 10, 10);
							gl.glPopMatrix();
						}
					}
				}

				glu.gluDeleteQuadric(quadric);
			}
		});

		// Create progressbar

		JProgressBar progress_bar = new JProgressBar();

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

		button.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent event) {
				new Thread(new Runnable() {
					@Override
					public void run() {
						new Loader2D(frame, progress_bar, render_xy, chart_xy_panel, "XY-Schweißprofil", "Länge", "Breite").run();
						new Loader2D(frame, progress_bar, render_yz, chart_yz_panel, "YZ-Schweißprofil", "Breite", "Tiefe").run();
						new Loader2D(frame, progress_bar, render_xz, chart_xz_panel, "XZ-Schweißprofil", "Länge", "Tiefe").run();
						new Loader3D(frame, progress_bar, render_3d).run();
					}
				}).start();
			}
		});
	}

}
