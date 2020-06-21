package com.hyperkit.welding.renderers;

import org.jfree.data.xy.XYDataItem;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Generator;
import com.hyperkit.welding.Search;
import com.hyperkit.welding.configurations.Render3DConfiguration;
import com.hyperkit.welding.structures.Path;
import com.hyperkit.welding.structures.Range;
import com.hyperkit.welding.structures.extremes.DeepestX;
import com.hyperkit.welding.structures.extremes.WidestX;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.glu.GLU;
import com.jogamp.opengl.glu.GLUquadric;

public class Renderer3D extends Generator {
	
	private Search search;
	private Render3DConfiguration configuration;
	
	public Renderer3D(Search search, Render3DConfiguration configuration) {
		this.search = search;
		this.configuration = configuration;
	}

	public void render(Path path_min_x, Range min_x, Range max_x, WidestX widest_x, DeepestX deepest_x, XYSeriesCollection dataset_xy, XYSeriesCollection dataset_xz, XYSeriesCollection dataset_yz_widest, XYSeriesCollection dataset_yz_deepest, int width, int height, GLAutoDrawable drawable, GLU glu) {

		// Clear canvas

		GL2 gl = (GL2) drawable.getGL();
		gl.glClearColor(1f, 1f, 1f, 1f);
		gl.glClear(GL.GL_COLOR_BUFFER_BIT | GL.GL_DEPTH_BUFFER_BIT);
		
		// Check datasets
		
		if (dataset_xy.getSeries().size() != 4 || dataset_xz.getSeries().size() != 2 || dataset_yz_widest.getSeries().size() != 3 || dataset_yz_deepest.getSeries().size() != 3) {
			return;
		}

		// Find ranges
		
		Range range_y = widest_x.getMaxY();
		Range range_z = deepest_x.getMinZ();

		// Change to projection matrix

		gl.glMatrixMode(GL2.GL_PROJECTION);
		gl.glLoadIdentity();

		// Perspective

		double camera_x = configuration.getCameraX();
		double camera_y = configuration.getCameraY();
		double camera_z = configuration.getCameraZ();

		/*
		double eye_x = configuration.getEyeX();
		double eye_y = configuration.getEyeY();
		double eye_z = configuration.getEyeZ();
		*/

		float widthHeightRatio = (float) width / (float) height;
		glu.gluPerspective(45, widthHeightRatio, 0.001, 1000);
		glu.gluLookAt(camera_x, camera_y, camera_z, (min_x.getInnerValue() + max_x.getInnerValue()) / 2, range_z.getOuterValue() / 2, 0, 0, 1, 0);

		// Change back to model view matrix

		gl.glMatrixMode(GL2.GL_MODELVIEW);
		gl.glLoadIdentity();

		// Render data

		gl.glEnable(GL2.GL_POINT_SMOOTH);
		gl.glEnable(GL2.GL_BLEND);
		gl.glBlendFunc(GL2.GL_SRC_ALPHA, GL2.GL_ONE_MINUS_SRC_ALPHA);
		gl.glPointSize(configuration.getPointSize());

		final double points_step = configuration.getSamples();

		gl.glBegin(GL2.GL_POINTS);

		final double limit = search.getConfiguration().getLimitTemperature();
		
		double min = limit;
		double max = limit;

		for (double x = min_x.getInnerValue() * 1.2; x <= max_x.getInnerValue() * 1.2; x += points_step) {
			for (double y = -Math.abs(range_y.getInnerValue()) * 1.2; y <= 0; y+= points_step) {
				double temperature = search.getModel().calculateTemperature(x, y, 0);
				
				min = Math.min(min, temperature);
				max = Math.max(max, temperature);
			}
		}

		for (double x = min_x.getInnerValue() * 1.2; x <= max_x.getInnerValue() * 1.2; x += points_step) {
			for (double y = -Math.abs(range_y.getInnerValue()) * 1.2; y <= Math.abs(range_y.getInnerValue()) * 1.2; y += points_step) {
				double temperature = search.getModel().calculateTemperature(x, y, 0);
				
				if (temperature >= limit) {
					gl.glColor3d((temperature - limit) / (max - limit), 1 - (temperature - limit) / (max - limit), 0);
				} else {
					gl.glColor3d(0.5 + 0.4 * (limit - temperature) / (limit - min), 0.5 + 0.4 * (limit - temperature) / (limit - min), 0.5 + 0.4 * (limit - temperature) / (limit - min));
				}
				
				gl.glVertex3d(x, 0, y);
			}
		}

		gl.glEnd();
		
		// Render datasets
		
		XYDataItem previous;
		
		GLUquadric quadirc = glu.gluNewQuadric();
		
		for (Object series_object : dataset_xy.getSeries()) {
			if (series_object instanceof XYSeries) {
				gl.glBegin(GL2.GL_LINES);
				gl.glLineWidth(3);
				gl.glColor3f(0,0,1);
				
				XYSeries series = (XYSeries) series_object;
				
				previous = null;
				
				for (Object item_object : series.getItems()) {
					if (item_object instanceof XYDataItem) {
						XYDataItem item = (XYDataItem) item_object;
						
						if (previous != null) {
							gl.glVertex3d(previous.getXValue() / 10, 0, previous.getYValue() / 10);
							gl.glVertex3d(item.getXValue() / 10, 0, item.getYValue() / 10);
						}
						
						previous = item;
					} else {
						throw new IllegalStateException();
					}
				}
				
				previous = null;
				
				for (Object item_object : series.getItems()) {
					if (item_object instanceof XYDataItem) {
						XYDataItem item = (XYDataItem) item_object;
						
						if (previous != null) {
							gl.glVertex3d(previous.getXValue() / 10, 0, -previous.getYValue()/ 10);
							gl.glVertex3d(item.getXValue() / 10, 0, -item.getYValue() / 10);
						}
						
						previous = item;
					} else {
						throw new IllegalStateException();
					}
				}
				
				gl.glEnd();
				
				/*
				XYDataItem first = (XYDataItem) series.getItems().get(0);
				
				gl.glPushMatrix();
				gl.glColor3d(0, 0, 0);
				gl.glTranslated(first.getXValue() / 10, 0, first.getYValue() / 10);
				glu.gluSphere(quadirc, 0.01, 10, 10);
				gl.glPopMatrix();
				
				XYDataItem last = (XYDataItem) series.getItems().get(series.getItemCount() - 1);
				
				gl.glPushMatrix();
				gl.glColor3d(0, 0, 0);
				gl.glTranslated(last.getXValue() / 10, 0, last.getYValue() / 10);
				glu.gluSphere(quadirc, 0.01, 10, 10);
				gl.glPopMatrix();
				*/
			} else {
				throw new IllegalStateException();
			}
		}
		
		for (Object series_object : dataset_xz.getSeries()) {
			if (series_object instanceof XYSeries) {
				gl.glBegin(GL2.GL_LINES);
				gl.glLineWidth(3);
				gl.glColor3f(0,0,1);
				
				XYSeries series = (XYSeries) series_object;
				
				previous = null;
				
				for (Object item_object : series.getItems()) {
					if (item_object instanceof XYDataItem) {
						XYDataItem item = (XYDataItem) item_object;
						
						if (previous != null) {
							gl.glVertex3d(previous.getXValue() / 10, previous.getYValue() / 10, 0);
							gl.glVertex3d(item.getXValue() / 10, item.getYValue() / 10, 0);
						}
						
						previous = item;
					} else {
						throw new IllegalStateException();
					}
				}
				
				gl.glEnd();
				
				/*
				XYDataItem first = (XYDataItem) series.getItems().get(0);
				
				gl.glPushMatrix();
				gl.glColor3d(0, 0, 0);
				gl.glTranslated(first.getXValue() / 10, first.getYValue() / 10, 0);
				glu.gluSphere(quadirc, 0.01, 10, 10);
				gl.glPopMatrix();
				
				XYDataItem last = (XYDataItem) series.getItems().get(series.getItemCount() - 1);
				
				gl.glPushMatrix();
				gl.glColor3d(0, 0, 0);
				gl.glTranslated(last.getXValue() / 10, last.getYValue() / 10, 0);
				glu.gluSphere(quadirc, 0.01, 10, 10);
				gl.glPopMatrix();
				*/
			} else {
				throw new IllegalStateException();
			}
		}
		
		for (Object series_object : dataset_yz_widest.getSeries()) {
			if (series_object instanceof XYSeries) {
				gl.glBegin(GL2.GL_LINES);
				gl.glLineWidth(3);
				gl.glColor3f(0,0,1);
				
				XYSeries series = (XYSeries) series_object;
				
				previous = null;
				
				for (Object item_object : series.getItems()) {
					if (item_object instanceof XYDataItem) {
						XYDataItem item = (XYDataItem) item_object;
						
						if (previous != null) {
							gl.glVertex3d(widest_x.getX(), previous.getYValue() / 10, previous.getXValue() / 10);
							gl.glVertex3d(widest_x.getX(), item.getYValue() / 10, item.getXValue() / 10);
						}
						
						previous = item;
					} else {
						throw new IllegalStateException();
					}
				}
				
				gl.glEnd();
				
				/*
				XYDataItem first = (XYDataItem) series.getItems().get(0);
				
				gl.glPushMatrix();
				gl.glColor3d(0, 0, 0);
				gl.glTranslated(opt_x, first.getYValue() / 10, first.getXValue() / 10);
				glu.gluSphere(quadirc, 0.01, 10, 10);
				gl.glPopMatrix();
				
				XYDataItem last = (XYDataItem) series.getItems().get(series.getItemCount() - 1);
				
				gl.glPushMatrix();
				gl.glColor3d(0, 0, 0);
				gl.glTranslated(opt_x, last.getYValue() / 10, last.getXValue() / 10);
				glu.gluSphere(quadirc, 0.01, 10, 10);
				gl.glPopMatrix();
				*/
			} else {
				throw new IllegalStateException();
			}
		}
		
		for (Object series_object : dataset_yz_deepest.getSeries()) {
			if (series_object instanceof XYSeries) {
				gl.glBegin(GL2.GL_LINES);
				gl.glLineWidth(3);
				gl.glColor3f(0,0,1);
				
				XYSeries series = (XYSeries) series_object;
				
				previous = null;
				
				for (Object item_object : series.getItems()) {
					if (item_object instanceof XYDataItem) {
						XYDataItem item = (XYDataItem) item_object;
						
						if (previous != null) {
							gl.glVertex3d(deepest_x.getX(), previous.getYValue() / 10, previous.getXValue() / 10);
							gl.glVertex3d(deepest_x.getX(), item.getYValue() / 10, item.getXValue() / 10);
						}
						
						previous = item;
					} else {
						throw new IllegalStateException();
					}
				}
				
				gl.glEnd();
				
				/*
				XYDataItem first = (XYDataItem) series.getItems().get(0);
				
				gl.glPushMatrix();
				gl.glColor3d(0, 0, 0);
				gl.glTranslated(opt_x, first.getYValue() / 10, first.getXValue() / 10);
				glu.gluSphere(quadirc, 0.01, 10, 10);
				gl.glPopMatrix();
				
				XYDataItem last = (XYDataItem) series.getItems().get(series.getItemCount() - 1);
				
				gl.glPushMatrix();
				gl.glColor3d(0, 0, 0);
				gl.glTranslated(opt_x, last.getYValue() / 10, last.getXValue() / 10);
				glu.gluSphere(quadirc, 0.01, 10, 10);
				gl.glPopMatrix();
				*/
			} else {
				throw new IllegalStateException();
			}
		}
		
		glu.gluDeleteQuadric(quadirc);

		// Render grid

		/*
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
		*/

		// Render axes

		float axis_length = 110f;

		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(0, 0, 0);
		gl.glVertex3f(-axis_length, 0, 0);
		gl.glVertex3f(+axis_length, 0, 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(0, 0, 0);
		gl.glVertex3f(0, -axis_length, 0);
		gl.glVertex3f(0, +axis_length, 0);
		gl.glEnd();

		gl.glBegin(GL2.GL_LINES);
		gl.glColor3f(0, 0, 0);
		gl.glVertex3f(0, 0, -axis_length);
		gl.glVertex3f(0, 0, +axis_length);
		gl.glEnd();

	}

}
