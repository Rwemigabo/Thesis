package com.mycompany.aatr2;

public class LargeVM extends VirtualMachine {

	public LargeVM() {
		this.price = 5.966;
		max_containers = 20;
	}

	void resetPrice(double price) {
		this.price = price;
	}
	
	void resetMaxContainers(int containers) {
		this.max_containers = containers;
	}
}
