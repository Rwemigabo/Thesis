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
}
