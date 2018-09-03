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

		String serv1 = "postgres:9.4";// 5
		String serv2 = "result";// 2
		String serv3 = "vote";// 1
		String serv4 = "worker";// 3
		String serv5 = "redis:alpine";// 4

		vtop2.addService(serv1, 1);
		vtop2.addService(serv2, 2);
		vtop2.addService(serv3, 2);
		vtop2.addService(serv4, 2);
		vtop2.addService(serv5, 1);
		MediumVM Mvm = new MediumVM();
		vtop2.addVM(Mvm);

		vtop3.addService(serv1, 1);
		vtop3.addService(serv2, 2);
		vtop3.addService(serv3, 3);
		vtop3.addService(serv4, 1);
		vtop3.addService(serv5, 2);
		SmallVM svm2 = new SmallVM();
		SmallVM svm3 = new SmallVM();
		vtop3.addVM(svm2);
		vtop3.addVM(svm3);

		vtop4.addService(serv1, 1);
		vtop4.addService(serv2, 3);
		vtop4.addService(serv3, 4);
		vtop4.addService(serv4, 2);
		vtop4.addService(serv5, 2);
		MediumVM Mvm1 = new MediumVM();
		MediumVM Mvm2 = new MediumVM();
		vtop4.addVM(Mvm1);
		vtop4.addVM(Mvm2);

		vtop5.addService(serv1, 1);
		vtop5.addService(serv2, 3);
		vtop5.addService(serv3, 4);
		vtop5.addService(serv4, 2);
		vtop5.addService(serv5, 2);
		LargeVM lvm = new LargeVM();
		vtop5.addVM(lvm);

		vtop6.addService(serv1, 1);
		vtop6.addService(serv2, 5);
		vtop6.addService(serv3, 5);
		vtop6.addService(serv4, 4);
		vtop6.addService(serv5, 3);
		LargeVM lvm1 = new LargeVM();
		vtop6.addVM(lvm1);

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
		for (String str : myservs) {
			Topology t = new Topology("top" + myservs.indexOf(str));
			int randomnum = randomNumber(1, 3);
			if (randomnum == 1) {
				t.addVM(makeVM());
			} else {
				t.addVM(makeVM());
				t.addVM(makeVM());
			}
			t.addService(str, randomNumber(1, 6));
			addTopology(t);
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
				"!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!!Numbber of services = " + services.size());
		return services;
	}

	public void clearTops() {
		this.tops.clear();

	}
}
