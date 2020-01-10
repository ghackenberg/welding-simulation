package com.hyperkit.welding;

import javax.swing.JOptionPane;

import com.hyperkit.welding.programs.CircularProgram;
import com.hyperkit.welding.programs.LinearProgram;

public class Main {

	public static void main(String[] args) {
		
		String[] options = {"Linearpendel", "Kreispendel"};
		
		int choice = JOptionPane.showOptionDialog(null, "Bitte w�hlen Sie das gew�nschte Modell aus", "Modellauswahl", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		
		if (choice == 0) {
			LinearProgram.main(args);
		} else if (choice == 1) {
			CircularProgram.main(args);
		} else {
			JOptionPane.showMessageDialog(null, "Das ausgew�hlt Modell wird nicht unterst�tzt");
		}
		
	}
	
}
