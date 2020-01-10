package com.hyperkit.welding;

import javax.swing.JOptionPane;

import com.hyperkit.welding.programs.CircularProgram;
import com.hyperkit.welding.programs.LinearProgram;

public class Main {

	public static void main(String[] args) {
		
		String[] options = {"Linearpendel", "Kreispendel"};
		
		int choice = JOptionPane.showOptionDialog(null, "Bitte wählen Sie das gewünschte Modell aus", "Modellauswahl", JOptionPane.DEFAULT_OPTION, JOptionPane.INFORMATION_MESSAGE, null, options, options[0]);
		
		if (choice == 0) {
			LinearProgram.main(args);
		} else if (choice == 1) {
			CircularProgram.main(args);
		} else {
			JOptionPane.showMessageDialog(null, "Das ausgewählt Modell wird nicht unterstützt");
		}
		
	}
	
}
