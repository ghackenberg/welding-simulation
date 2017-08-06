package com.hyperkit.welding;

import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.renderers.Renderer3D;
import com.jogamp.opengl.GL;
import com.jogamp.opengl.GLAutoDrawable;
import com.jogamp.opengl.GLEventListener;
import com.jogamp.opengl.glu.GLU;

public class Listener implements GLEventListener {
	
	private GLU glu;
	
	private int width;
	private int height;
	
	private XYSeriesCollection dataset_xy;
	private XYSeriesCollection dataset_xz;
	private XYSeriesCollection dataset_yz;
	
	private Renderer3D renderer;
	
	private double opt_x;
	
	public Listener(XYSeriesCollection dataset_xy, XYSeriesCollection dataset_xz, XYSeriesCollection dataset_yz, Renderer3D renderer) {
		this.dataset_xy = dataset_xy;
		this.dataset_xz = dataset_xz;
		this.dataset_yz = dataset_yz;
		
		this.renderer = renderer;
	}

	public void setOptX(double opt_x) {
		this.opt_x = opt_x;
	}

	@Override
	public void init(GLAutoDrawable drawable) {
		glu = new GLU();
	}
	
	@Override
	public void reshape(GLAutoDrawable drawable, int x, int y, int width, int height) {
		GL gl = drawable.getGL();
		
		gl.glViewport(0, 0, width, height);
		
		this.width = width;
		this.height = height;
	}
	
	@Override
	public void display(GLAutoDrawable drawable) {
		renderer.render(opt_x, dataset_xy, dataset_xz, dataset_yz, width, height, drawable, glu);
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

}
