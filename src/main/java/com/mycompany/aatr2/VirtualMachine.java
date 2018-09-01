package com.mycompany.aatr2;

public class VirtualMachine {
	private String serviceName = "AWS";
	double price;
	int max_containers = 20;

	public VirtualMachine() {

	}

	public double getPrice() {
		return price;
	}

	public void setPrice(double price) {
		this.price = price;
	}

	public int getMax_containers() {
		return max_containers;
	}

	public void setMax_containers(int max_containers) {
		this.max_containers = max_containers;
	}

	public String getServiceName() {
		return serviceName;
	}

	public void setServiceName(String serviceName) {
		this.serviceName = serviceName;
	}

}
