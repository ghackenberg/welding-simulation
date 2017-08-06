package com.hyperkit.welding.renderers;

import org.jfree.chart.ChartPanel;

public class RendererXZ extends Renderer2D {
	
	private double y;

	public RendererXZ(ChartPanel chart_panel, double y) {
		super(chart_panel, "XZ-Schweiﬂprofil", "L‰nge [x]", "Tiefe [z]");
		
		this.y = y;
	}
	
	@Override
	protected String getAnnotation() {
		return "y = " + FORMAT.format(y * 10) + " mm";
	}

}
