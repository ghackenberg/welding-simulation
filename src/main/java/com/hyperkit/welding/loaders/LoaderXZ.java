package com.hyperkit.welding.loaders;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

import org.jfree.chart.ChartPanel;
import org.jfree.data.xy.XYSeriesCollection;

import com.hyperkit.welding.Progress;
import com.hyperkit.welding.Range;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.renderers.RendererXZ;

public class LoaderXZ extends Loader2D<RendererXZ> {
	
	private Range min_x;
	private Range max_x;
	private double y;

	public LoaderXZ(JFrame frame, JProgressBar progress_bar, RendererXZ render, ChartPanel chart_panel, Range min_x, Range max_x, double y) {
		super(frame, progress_bar, render, chart_panel, "XY-Schweiﬂprofil", "L‰nge", "Breite");
		
		this.min_x = min_x;
		this.max_x = max_x;
		this.y = y;
	}

	@Override
	protected XYSeriesCollection generateDataset(Progress progress) throws SearchException {
		return getRenderer().generateDataset(min_x, max_x, y, progress);
	}
	
	@Override
	protected String getAnnotation() {
		return "y = " + y;
	}

}
