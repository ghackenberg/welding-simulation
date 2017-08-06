package com.hyperkit.welding.loaders;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Progress;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.renderers.RendererYZ;

public class LoaderYZ extends Loader2D<RendererYZ> {
	
	private double x;

	public LoaderYZ(JFrame frame, JProgressBar progress_bar, RendererYZ render, ChartPanel chart_panel, double x) {
		super(frame, progress_bar, render, chart_panel, "XY-Schweiﬂprofil", "L‰nge", "Breite");
		
		this.x = x;
	}

	@Override
	protected XYSeriesCollection generateDataset(Progress progress) throws SearchException {
		return getRenderer().generateDataset(x, progress);
	}
	
	@Override
	protected String getAnnotation() {
		return "x = " + x;
	}

}
