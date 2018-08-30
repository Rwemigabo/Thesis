package com.mycompany.aatr2;

import java.util.ArrayList;
import java.util.Random;

/*
 * The viable topologies to be set by the system user (Dummy  topologies set for testing use).
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
	
	
//	public void newTopology() {
//		Topology ntop = new Topology();
//		tops.add(ntop);
//	}
	
	/**
	 * creates test/ example representations of viable topologies
	 */
	public void defineTestTopologies() {
		Topology t1 = DockerManager.getInstance().getCurrentTopology();
		Topology vtop2 = new Topology("top1");
		Topology vtop3 = new Topology("top2");
		Topology vtop4 = new Topology("top3");
		Topology vtop5 = new Topology("top4");
		Topology vtop6 = new Topology("top5");
		
		String serv1 = "postgres:9.4";
		String serv2 = "example-voting-app_result";
		String serv3 = "example-voting-app_vote";
		String serv4 = "example-voting-app_worker";
		String serv5 = "redis:alpine";
		
		
		vtop2.addService(serv1, 1);
		vtop2.addService(serv2, 1);
		vtop2.addService(serv3, 2);
		vtop2.addService(serv4, 1);
		vtop2.addService(serv5, 1);
		

		vtop3.addService(serv1, 1);
		vtop3.addService(serv2, 1);
		vtop3.addService(serv3, 3);
		vtop3.addService(serv4, 1);
		vtop3.addService(serv5, 2);

		vtop4.addService(serv1, 1);
		vtop4.addService(serv2, 1);
		vtop4.addService(serv3, 4);
		vtop4.addService(serv4, 2);
		vtop4.addService(serv5, 3);

		vtop5.addService(serv1, 2);
		vtop5.addService(serv2, 1);
		vtop5.addService(serv3, 5);
		vtop5.addService(serv4, 3);
		vtop5.addService(serv5, 4);
		
		vtop6.addService(serv1, 3);
		vtop6.addService(serv2, 2);
		vtop6.addService(serv3, 6);
		vtop6.addService(serv4, 4);
		vtop6.addService(serv5, 5);
		
		addTopology(t1);
		addTopology(vtop2);
		addTopology(vtop3);
		addTopology(vtop4);
		addTopology(vtop5);
		addTopology(vtop6);
		
	}
	
	public void defineTopologies2() {
		ArrayList<String > myservs = createServices();
		Topology t1 = DockerManager.getInstance().getCurrentTopology();
		t1.setFilename("Current Topology");
		addTopology(t1);
		for(String str: myservs) {
			Topology t = new Topology("top"+myservs.indexOf(str));
			t.addService(str, randomNumber(1, 6));
			addTopology(t);
		}
	}
	
	private int randomNumber(int x, int y) {
		Random r = new Random();
		int Low = x;
		int High = y;
		int Result = r.nextInt(High-Low) + Low;
		return Result;
	}
	
	private ArrayList<String> createServices() {
		ArrayList<String > services = new ArrayList<>();
		for(Cluster serv: DockerManager.getInstance().getAppServices()) {
			if(!services.contains(serv.getServName())){
				services.add(serv.getServName());
			}
		}
		return services;
	}
}
