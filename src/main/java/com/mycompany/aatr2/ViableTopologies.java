package com.mycompany.aatr2;

import java.util.ArrayList;

/*
 * The viable topologies to be set by the system user.
 */
public class ViableTopologies {
	private static final ViableTopologies inst = new ViableTopologies();
	private ArrayList<Topology> tops = new ArrayList<>();
	
	public ViableTopologies() {
		
	}
	
	public static ViableTopologies getInstance() {
		return inst;
	}
	
	public ArrayList<Topology> getTops() {
		return tops;
	}
	
	public void addTopology(Topology ntop) {
		tops.add(ntop);
	}
	
	
	public void newTopology() {
		Topology ntop = new Topology();
		tops.add(ntop);
	}
	
	public void defineTestTopologies() {
		Topology vtop2 = new Topology("top1");
		Topology vtop3 = new Topology("top2");
		Topology vtop4 = new Topology("top3");
		Topology vtop5 = new Topology("top4");
		
		String serv1 = "node-docker-api_web";
		String serv2 = "node-docker-api_locations-service";
		String serv3 = "node-docker-api_users-service";
		String serv4 = "node-docker-api_locations-db";
		String serv5 = "node-docker-api_users-db ";
		
		
		vtop2.addService(serv1, 3);
		vtop2.addService(serv2, 2);
		vtop2.addService(serv3, 6);
		vtop2.addService(serv3, 4);
		vtop2.addService(serv3, 3);

		vtop3.addService(serv1, 6);
		vtop3.addService(serv2, 7);
		vtop3.addService(serv3, 5);
		vtop2.addService(serv4, 5);
		vtop2.addService(serv5, 7);

		vtop4.addService(serv1, 5);
		vtop4.addService(serv2, 5);
		vtop4.addService(serv3, 5);
		vtop2.addService(serv4, 5);
		vtop2.addService(serv5, 5);

		vtop5.addService(serv1, 4);
		vtop5.addService(serv2, 5);
		vtop5.addService(serv3, 6);
		vtop2.addService(serv4, 2);
		vtop2.addService(serv4, 1);
	}
}
