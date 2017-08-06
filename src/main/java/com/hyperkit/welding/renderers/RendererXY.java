package com.hyperkit.welding.renderers;

import org.jfree.chart.ChartPanel;

public class RendererXY extends Renderer2D {
	
	private double z;

	public RendererXY(ChartPanel chart_panel, double z) {
		super(chart_panel, "XY-Schweiﬂprofil", "L‰nge [x]", "Breite [y]");
		
		this.z = z;
	}
	
	@Override
	protected String getAnnotation() {
		return "z = " + FORMAT.format(z * 10) + " mm";
	}

}
