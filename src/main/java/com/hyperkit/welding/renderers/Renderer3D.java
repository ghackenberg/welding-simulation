package com.hyperkit.welding.renderers;

import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Range;
import com.hyperkit.welding.Renderer;
import com.hyperkit.welding.Search;
import com.hyperkit.welding.configurations.Render3DConfiguration;
import com.hyperkit.welding.exceptions.SearchException;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;

public class Renderer3D extends Renderer {
	
	private Search search;
	private Render3DConfiguration configuration;
	
	public Renderer3D(Search search, Render3DConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}

	public void render(int width, int height, GLAutoDrawable drawable, GLU glu, Progress progress) {

		try {

			// Clear canvas

			GL2 gl = (GL2) drawable.getGL();
			gl.glClearColor(1f, 1f, 1f, 1f);
			gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);

			// Find ranges

			Range range_x = search.findMaximumX(0, 0);
			// Range range_y = search.findMaximumY(0, 0);
			Range range_z = search.findMaximumZ(0, 0);

			// Change to projection matrix

			gl.glMatrixMode(GL2.GL_PROJECTION);
			gl.glLoadIdentity();

			// Perspective

			double camera_x = configuration.getCameraX();
			double camera_y = configuration.getCameraY();
			double camera_z = configuration.getCameraZ();

			double eye_x = configuration.getEyeX();
			double eye_y = configuration.getEyeY();
			double eye_z = configuration.getEyeZ();

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
			gl.glPointSize(configuration.getPointSize());

			double points_step = configuration.getSamples();

			gl.glBegin(GL2.GL_POINTS);

			double min = search.getConfiguration().getLimitTemperature() - search.getConfiguration().getTemperatureThershold();
			double max = search.getConfiguration().getLimitTemperature() - search.getConfiguration().getTemperatureThershold();

			int total = 0;

			for (double x = -Math.abs(range_x.getLowerValue()); x <= Math.abs(range_x.getLowerValue()); x += points_step) {
				// for (double y = -Math.abs(range_y.getLowerValue()); y <= 0; y+= points_step)
				// {
				for (double z = -Math.abs(range_z.getLowerValue()); z <= Math.abs(range_z.getLowerValue()); z += points_step) {
					double temperature = search.getModel().calculateTemperature(x, 0, z);
					if (temperature >= search.getConfiguration().getLimitTemperature() - search.getConfiguration().getTemperatureThershold()) {
						max = Math.max(max, temperature);
						total++;
					}
				}
				// }
			}

			progress.initialize(total);
			progress.update(0, total);

			int count = 0;

			for (double x = -Math.abs(range_x.getLowerValue()) * 2; x <= Math.abs(range_x.getLowerValue()) * 2; x += points_step) {
				// for (double y = -Math.abs(range_y.getLowerValue()); y <= 0; y+= points_step)
				// {
				for (double z = -Math.abs(range_z.getLowerValue()) * 2; z <= Math.abs(range_z.getLowerValue()) * 2; z += points_step) {
					double temperature = search.getModel().calculateTemperature(x, 0, z);
					
					if (temperature >= search.getConfiguration().getLimitTemperature() - search.getConfiguration().getTemperatureThershold()) {
						gl.glColor3d(Math.sqrt((temperature - min) / (max - min)), 0, Math.sqrt(1 - (temperature - min) / (max - min)));
						gl.glVertex3d(x, 0, z);

						count++;

						progress.update(count, total);
					}
				}
				// }
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

}
