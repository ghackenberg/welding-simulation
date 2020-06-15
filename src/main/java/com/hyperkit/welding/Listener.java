package com.hyperkit.welding;

import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.renderers.Renderer3D;
import com.hyperkit.welding.structures.Path;
import com.hyperkit.welding.structures.Range;
import com.hyperkit.welding.structures.extremes.DeepestX;
import com.hyperkit.welding.structures.extremes.WidestX;
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
	private XYSeriesCollection dataset_yz_widest;
	private XYSeriesCollection dataset_yz_deepest;
	
	private Renderer3D renderer;
	
	private Path path_min_x;
	
	private Range min_x;
	private Range max_x;
	
	private WidestX widest_x;
	private DeepestX deepest_x;
	
	public Listener(XYSeriesCollection dataset_xy, XYSeriesCollection dataset_xz, XYSeriesCollection dataset_yz_widest, XYSeriesCollection dataset_yz_deepest, Renderer3D renderer) {
		this.dataset_xy = dataset_xy;
		this.dataset_xz = dataset_xz;
		this.dataset_yz_widest = dataset_yz_widest;
		this.dataset_yz_deepest = dataset_yz_deepest;
		
		this.renderer = renderer;
	}
	
	public void setPathMinX(Path path_min_x) {
		this.path_min_x = path_min_x;
	}
	
	public void setMinX(Range min_x) {
		this.min_x = min_x;
	}
	
	public void setMaxX(Range max_x) {
		this.max_x = max_x;
	}

	public void setWidestX(WidestX widest_x) {
		this.widest_x = widest_x;
	}
	
	public void setDeepestX(DeepestX deepest_x) {
		this.deepest_x = deepest_x;
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
		renderer.render(path_min_x, min_x, max_x, widest_x, deepest_x, dataset_xy, dataset_xz, dataset_yz_widest, dataset_yz_deepest, width, height, drawable, glu);
	}
	
	@Override
	public void dispose(GLAutoDrawable drawable) {

	}

}
