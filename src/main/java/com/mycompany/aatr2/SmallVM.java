package com.mycompany.aatr2;

public class SmallVM extends VirtualMachine {

	public SmallVM() {
		this.price = 2.42;
		max_containers = 10;
	}
	
	void resetPrice(double price) {
		this.price = price;
	}
	
	void resetMaxContainers(int containers) {
		this.max_containers = containers;
	}
}
