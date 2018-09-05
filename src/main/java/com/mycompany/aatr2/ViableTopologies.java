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

	// public void newTopology() {
	// Topology ntop = new Topology();
	// tops.add(ntop);
	// }

	/**
	 * creates test/ example representations of viable topologies
	 */
	public void defineTestTopologies() {
		Topology t1 = DockerManager.getInstance().getCurrentTopology();
		if (t1.getVMS().size() < 1) {
			t1.addVM(new SmallVM());
			t1.setFilename("Current Topology");
		}

		// Topology vtop1 = new Topology("Ntop");
		Topology vtop2 = new Topology("top1");
		Topology vtop3 = new Topology("top2");
		Topology vtop4 = new Topology("top3");
		Topology vtop5 = new Topology("top4");
		Topology vtop6 = new Topology("top5");
		Topology vtop7 = new Topology("top6");
		Topology vtop8 = new Topology("top7");

		String serv1 = "worker";// 5
		String serv2 = "vote";// 2
		String serv3 = "postgres:9.4";// 1
		String serv4 = "redis:alpine";// 3
		String serv5 = "result";// 4

		vtop2.addService(serv1, 2);
		vtop2.addService(serv2, 1);
		vtop2.addService(serv3, 1);
		vtop2.addService(serv4, 1);
		vtop2.addService(serv5, 1);
		vtop2.addVM(new SmallVM());
		vtop2.addVM(new SmallVM());

		vtop3.addService(serv1, 3);
		vtop3.addService(serv2, 2);
		vtop3.addService(serv3, 1);
		vtop3.addService(serv4, 1);
		vtop3.addService(serv5, 1);
		vtop3.addVM(new MediumVM());

		vtop4.addService(serv1, 4);
		vtop4.addService(serv2, 3);
		vtop4.addService(serv3, 2);
		vtop4.addService(serv4, 1);
		vtop4.addService(serv5, 1);
		MediumVM Mvm1 = new MediumVM();
		vtop4.addVM(Mvm1);

		vtop5.addService(serv1, 4);
		vtop5.addService(serv2, 4);
		vtop5.addService(serv3, 3);
		vtop5.addService(serv4, 2);
		vtop5.addService(serv5, 1);
		vtop5.addVM(new MediumVM());
		vtop5.addVM(new MediumVM());

		vtop6.addService(serv1, 4);
		vtop6.addService(serv2, 4);
		vtop6.addService(serv3, 4);
		vtop6.addService(serv4, 3);
		vtop6.addService(serv5, 2);
		MediumVM mvma = new MediumVM();
		MediumVM mvmb = new MediumVM();
		vtop6.addVM(mvma);
		vtop6.addVM(mvmb);

		vtop7.addService(serv1, 4);
		vtop7.addService(serv2, 4);
		vtop7.addService(serv3, 4);
		vtop7.addService(serv4, 4);
		vtop7.addService(serv5, 3);
		vtop7.addVM(new LargeVM());

		vtop8.addService(serv1, 4);
		vtop8.addService(serv2, 4);
		vtop8.addService(serv3, 4);
		vtop8.addService(serv4, 4);
		vtop8.addService(serv5, 4);

		vtop8.addVM(new LargeVM());
		vtop8.addVM(new LargeVM());

		// vtop1.addService(serv1, 1);
		// vtop1.addService(serv2, 1);
		// vtop1.addService(serv3, 1);
		// vtop1.addService(serv4, 1);
		// vtop1.addService(serv5, 1);
		// vtop1.addVM(new SmallVM());
		//
		// addTopology(vtop1);
		addTopology(t1);
		addTopology(vtop2);
		addTopology(vtop3);
		addTopology(vtop4);
		addTopology(vtop5);
		addTopology(vtop6);
		addTopology(vtop7);
		addTopology(vtop8);

	}

	public void defineDynamicTopologies2() {

		ArrayList<String> myservs = createServices();
		Topology t1 = DockerManager.getInstance().getCurrentTopology();
		t1.setFilename("Current Topology");
		if (t1.getVMS().size() < 1) {
			t1.addVM(new SmallVM());
			t1.addVM(new SmallVM());
			t1.setFilename("Current Topology");
		}
		addTopology(t1);
		Topology vtop2 = new Topology("top1");
		Topology vtop3 = new Topology("top2");
		Topology vtop4 = new Topology("top3");
		Topology vtop5 = new Topology("top4");
		Topology vtop6 = new Topology("top5");
		Topology vtop7 = new Topology("top6");
		

		addVMs(vtop2);
		addVMs(vtop3);
		addVMs(vtop4);
		addVMs(vtop5);
		addVMs(vtop6);
		addVMs(vtop7);
		addServices(myservs, vtop2);
		addServices(myservs, vtop3);
		addServices(myservs, vtop4);
		addServices(myservs, vtop5);
		addServices(myservs, vtop6);
		addServices(myservs, vtop7);
		addTopology(vtop2);
		addTopology(vtop3);
		addTopology(vtop4);
		addTopology(vtop5);
		addTopology(vtop6);
		addTopology(vtop7);

	}

	void addVMs(Topology t) {
		int randomnum = randomNumber(1, 3);
		if (randomnum == 1) {
			t.addVM(makeVM());
		} else if (randomnum == 2) {
			t.addVM(makeVM());
			t.addVM(makeVM());
		} else {
			t.addVM(makeVM());
			t.addVM(makeVM());
			t.addVM(makeVM());
		}
	}

	void addServices(ArrayList<String> s, Topology t) {
		int toprand = randomNumber(1, 3);
		for (String str : s) {
			if (toprand == 1) {
				t.addService(str, randomNumber(1, 3));
			} else if (toprand == 2) {
				t.addService(str, randomNumber(2, 4));
			} else {
				t.addService(str, randomNumber(3, 5));
			}

		}
	}

	VirtualMachine makeVM() {
		VirtualMachine vm = null;
		int numbr = randomNumber(1, 3);
		if (numbr == 1) {
			vm = new SmallVM();
		} else if (numbr == 2) {
			vm = new MediumVM();
		} else {
			vm = new LargeVM();
		}

		return vm;
	}

	private int randomNumber(int x, int y) {
		Random r = new Random();
		int Low = x;
		int High = y;
		int Result = r.nextInt(High - Low) + Low;
		return Result;
	}

	private ArrayList<String> createServices() {
		ArrayList<String> services = new ArrayList<>();
		for (Cluster serv : DockerManager.getInstance().getAppServices()) {
			if (!services.contains(serv.getServName())) {
				services.add(serv.getServName());
			}
		}

		System.out.println(
				"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Number of services = " + services.size());
		return services;
	}

	public void clearTops() {
		this.tops.clear();

	}
}
