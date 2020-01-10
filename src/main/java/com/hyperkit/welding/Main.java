package com.hyperkit.welding;

import com.hyperkit.welding.programs.CircularProgram;
import com.hyperkit.welding.programs.LinearProgram;

public class Main {

	public static void main(String[] args) {
		
		if (args.length != 1) {
			System.err.println("Exactly one argument required");
		} else {
			if (args[0].equals("linear")) {
				LinearProgram.main(args);
			} else if (args[0].equals("circular")) {
				CircularProgram.main(args);
			} else {
				System.err.println("Unknown program mode");
			}
		}
	}
	
}
