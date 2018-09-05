/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2;

import com.mycompany.aatr2.analyse.AnalyseManager;
import com.mycompany.aatr2.execute.ExecuteManager;
import com.mycompany.aatr2.monitor.MonitorManager;
import com.mycompany.aatr2.plan.PlanManager;
import com.spotify.docker.client.DefaultDockerClient;
import com.spotify.docker.client.DockerClient;
import com.spotify.docker.client.exceptions.DockerCertificateException;
import com.spotify.docker.client.exceptions.DockerException;
import com.spotify.docker.client.messages.Container;
import com.spotify.docker.client.messages.ContainerStats;
import com.spotify.docker.client.messages.Image;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
//import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * Singleton class Connects to docker and gathers image info about the
 * topologies(yml files) the manager then prompts the feedback control loops to
 * run on each of the containers of a service available in the running topology
 *
 * @author eric
 */
public class DockerManager {

	private final DockerClient cli;
	private List<Image> images;
	private final ArrayList<Cluster> appServices;
	private List<Container> containers;
	private final ArrayList<String> monitored;
	private List<Topology> topologies = new ArrayList<>();// list of topologies that have been run.
	private Topology currentTopology;
	private HashMap<Timestamp, Topology> executions;
	private Timestamp lastExecTime;
	private final static Logger LOGGER = Logger.getLogger(DockerManager.class.getName());

	private static DockerManager instance;

	public DockerManager() throws DockerCertificateException, DockerException, InterruptedException {
		this.cli = DefaultDockerClient.fromEnv().build();
		this.images = cli.listImages();
		this.monitored = new ArrayList<>();
		this.appServices = new ArrayList<>();
		this.executions = new HashMap<>();
		// LOGGER.setLevel(Level.INFO);
	}

	public static DockerManager getInstance() {
		return instance;
	}

	public void repopulateImagesList() throws DockerException, InterruptedException {
		this.images = cli.listImages();
	}

	public void repopulateContainersList() throws DockerException, InterruptedException {
		this.containers = cli.listContainers();
	}

	public void refreshContainersList() throws DockerException, InterruptedException {
		for (Container cont : cli.listContainers()) {
			if (!this.containers.contains(cont)) {
				containers.add(cont);
			}
		}
	}

	public Topology getCurrentTopology() {
		return currentTopology;
	}

	public void setCurrentTopology(Topology newTopology) {
		if (currentTopology == null || !currentTopology.compare(newTopology)) {
			this.currentTopology = newTopology;
		}
	}

	public Container getContainer(String id) {
		for (Container cont : containers) {
			if (cont.id().equals(id)) {
				return cont;
			}
		}
		return null;
	}

	private void newMapeLoop() throws DockerException, InterruptedException {
		MonitorManager mm = MonitorManager.getInstance();
		AnalyseManager am = AnalyseManager.getInstance();
		PlanManager pm = PlanManager.getInstance();
		ExecuteManager em = ExecuteManager.getInstance();
		pm.initiate();
		em.initiate();
		defineServices();
		newTopology();
		LOGGER.log(Level.INFO, "Service count: " + appServices.size());
		for (Cluster serv : appServices) {
			mm.newMonitor(serv);
			am.newAnalyser(serv);
		}

	}

	/**
	 * Populate the list of services
	 * 
	 * @throws DockerException
	 * @throws InterruptedException
	 */
	private void defineServices() throws DockerException, InterruptedException {
		Cluster s;
		for (Container cont : containers) {
			ArrayList<String> temp = new ArrayList<String>();
			appServices.forEach((service) -> {
				if(!cont.image().contains("visualizer")) {
					temp.add(service.getServName());// add already existing service names to the temp list.
				}
			});
			// if service doesn't exist then create it and add it to the list of services.
			if (!cont.image().contains("visualizer") && !temp.contains(cont.image())) {
				// System.out.println("\n Creating new service for " + cont.image());
				s = newService(cont.image());
				s.addContainer(cont);
				if (!this.appServices.contains(s)) {
					this.appServices.add(s);
				}
				this.monitored.add(s.getServName());

			} else {
				System.out.println("\n Service exists, adding container: " + cont.image());
				s = getCluster(cont.image());
				if(!cont.image().contains("visualizer")) {
					s.addContainer(cont);
				}
				
			}
		}
	}

	public Image getImage(String id) {
		for (Image img : images) {
			if (img.id().equals(id)) {
				return img;
			}
		}
		return null;
	}

	public ContainerStats getContainerStats(String id) throws DockerException, InterruptedException {
		ContainerStats stats = cli.stats(id);
		return stats;
	}

	public List<String> getMonitored() {
		return Collections.unmodifiableList(monitored);
	}

	public List<Image> getImages() {
		return Collections.unmodifiableList(images);
	}

	public List<Container> getContainers() {
		return Collections.unmodifiableList(containers);
	}

	public boolean isMonitored(Container cont) {
		if (this.monitored.contains(cont.id())) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * Create new service if the string passed to it has no service.
	 *
	 * @param img
	 *            image/ service name
	 * @return
	 */
	public Cluster newService(String img) {

		Cluster serv = new Cluster(img);
		List<String> temp = new ArrayList<String>();
		appServices.forEach((service) -> {
			temp.add(service.getServName());
		});
		if (temp.contains(img)) {
			System.out.print("\n Service already available for this container");
		} else {

			containers.stream().filter((cont) -> (cont.image().equals(img))).forEachOrdered((cont) -> {

				serv.addContainer(cont);
			});
			this.appServices.add(serv);
			return serv;
		}

		return null;
	}

//	private int randomNumber(int x, int y) {
//		Random r = new Random();
//		int Low = x;
//		int High = y;
//		int Result = r.nextInt(High - Low) + Low;
//		return Result;
//	}

	
//	/**
//	 * Ranks the services according to their total valuability in the application.
//	 * 
//	 * @param img
//	 * @return
//	 */
//	private Cluster newCluster(String img) {
//		int rank = randomNumber(1, images.size());
//		List<Integer> ranks = new ArrayList<>();
//		if (this.appServices.size() > 0) {
//			for (Cluster serv : this.appServices) {
//				ranks.add(serv.getRank());
//			}
//			while(ranks.contains(rank)) {
//				rank = randomNumber(1, images.size());
//			}
//		}
//		
//		return c;
//	}

	

	// public Topology createTopology() {
	// //if topology doesn't exist then create and store it's id in the list of
	// topologies
	// TBD: how to check if a topology exists (Structural specifications like
	// containers and number of VMS)
	//
	// }

	/**
	 * return the specified cluster
	 * 
	 * @param serv
	 * @return
	 */
	public Cluster getCluster(String serv) {
		Cluster clst = null;
		for (Cluster c : appServices) {
			if (c.getServName().equals(serv)) {
				clst = c;
				break;
			}

		}
		return clst;
	}

	public static void main(String[] args) {
		try {
			instance = new DockerManager();
			instance.repopulateContainersList();
			instance.newMapeLoop();
		} catch (DockerCertificateException | DockerException | InterruptedException ex) {
			Logger.getLogger(DockerManager.class.getName()).log(Level.SEVERE, null, ex);
		}

	}

	public ArrayList<Cluster> getAppServices() {
		return appServices;
	}

	public List<Topology> getTopologies() {
		return topologies;
	}

	public boolean exists(Topology topol) {
		if (topologies.size() > 0) {
			for (Topology top : topologies) {
				if (top.compare(topol)) {
					return true;
				}
			}
		}
		return false;
	}

	/*
	 * Creates a new topology if it doesn't already exist and adds it to list and
	 * returns it Else returns the topology that already exists
	 */
	public void newTopology() {
		Topology newtop = new Topology(appServices);
		if (!exists(newtop)) {
			topologies.add(newtop);
		} else {
			System.out.println("Topology already exists");
		}
		this.currentTopology = newtop;
	}

	public HashMap<Timestamp, Topology> getExecutions() {
		return executions;
	}

	public Topology getPendingExecution() {
		Topology top = null;
		for (Map.Entry<Timestamp, Topology> entry : executions.entrySet()) {
			if (entry.getKey().after(lastExecTime)) {
				top = entry.getValue();
			}
			break;

		}
		return top;

	}

	public Timestamp getLastExecTime() {
		return lastExecTime;
	}

	public void setLastExecTime(Timestamp lastExecTime) {
		this.lastExecTime = lastExecTime;
	}

	public void setExecutions(HashMap<Timestamp, Topology> executions) {
		this.executions = executions;
	}

	public void prepareForExecution(Topology selected) {
		executions.put(new Timestamp(System.currentTimeMillis()), selected);
	}

}
