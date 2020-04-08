package com.hyperkit.welding.renderers;

import org.jfree.chart.ChartPanel;

public class RendererYZ extends Renderer2D {
	
	private double widest_x;
	private double deepest_x;

	public RendererYZ(ChartPanel chart_panel, double widest_x, double deepest_x) {
		super(chart_panel, "YZ-Schweiﬂprofil", "Breite [y]", "Tiefe [z]");
		
		this.widest_x = widest_x;
		this.deepest_x = deepest_x;
	}
	
	@Override
	protected String getAnnotation() {
		return "x (b) = " + FORMAT.format(widest_x * 10) + " mm, x (t) = " + FORMAT.format(deepest_x * 10) + "mm";
	}

}
