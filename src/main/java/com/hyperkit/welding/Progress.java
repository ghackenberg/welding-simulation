package com.hyperkit.welding;

public interface Progress {

	public void initialize(int total);
	public void update(int current, int total);
	
}
