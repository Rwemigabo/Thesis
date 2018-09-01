package com.mycompany.aatr2;

public class MediumVM extends VirtualMachine {

	public MediumVM() {
		this.price = 5.069;
		max_containers = 20;
	}

	void resetPrice(double price) {
		this.price = price;
	}
	
	void resetMaxContainers(int containers) {
		this.max_containers = containers;
	}
}
