package com.hyperkit.welding.loaders;

import javax.swing.JFrame;
import javax.swing.JOptionPane;
import javax.swing.JProgressBar;

import com.hyperkit.welding.Progress;
import com.hyperkit.welding.exceptions.SearchException;
import com.hyperkit.welding.renderers.Renderer3D;

public class Loader3D extends Loader {

	private Renderer3D render;

	public Loader3D(JFrame frame, JProgressBar progress_bar, Renderer3D render) {
		super(frame, progress_bar);
		this.render = render;
	}

	@Override
	public void run() {
		try {

			render.generateDataset(new Progress() {
				@Override
				public void initialize(int total) {
					progress_bar.setMaximum(total);
					progress_bar.setValue(0);
				}
				@Override
				public void update(int current, int total) {
					progress_bar.setString(current + " / " + total + " Diagrammpunkte");
					progress_bar.setValue(current);
				}
			});

		} catch (SearchException exception) {

			JOptionPane.showMessageDialog(frame, "Das Schweiﬂvolumen konnte nicht berechnet werden. Passen sie die Parametereinstellungen an.", "Berechnungsfehler", JOptionPane.ERROR_MESSAGE);

			exception.printStackTrace();

		}
	}

}
