package com.hyperkit.welding;

import java.text.DecimalFormat;

import javax.swing.JFrame;
import javax.swing.JProgressBar;

public abstract class Loader {
	
	protected static final DecimalFormat FORMAT = new DecimalFormat("0.00");
	
	protected JFrame frame;
	protected JProgressBar progress_bar;
	
	public Loader(JFrame frame, JProgressBar progress_bar) {
		this.frame = frame;
		this.progress_bar = progress_bar;
	}
	
	public abstract void run();

}
