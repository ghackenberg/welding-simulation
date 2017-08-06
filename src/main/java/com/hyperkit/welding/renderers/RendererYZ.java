package com.hyperkit.welding.renderers;

import org.jfree.chart.ChartPanel;

public class RendererYZ extends Renderer2D {
	
	private double x;

	public RendererYZ(ChartPanel chart_panel, double x) {
		super(chart_panel, "YZ-Schweiﬂprofil", "Breite [y]", "Tiefe [z]");
		
		this.x = x;
	}
	
	@Override
	protected String getAnnotation() {
		return "x = " + FORMAT.format(x * 10) + " mm";
	}

}
