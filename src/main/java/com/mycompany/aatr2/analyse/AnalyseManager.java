/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.mycompany.aatr2.analyse;

import java.util.ArrayList;
//import java.util.concurrent.TimeUnit;
//import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.mycompany.aatr2.Cluster;
import com.mycompany.aatr2.DockerManager;
import com.mycompany.aatr2.Observable;
import com.mycompany.aatr2.Observer;

/**
 * 
 * @author eric
 *
 */
public class AnalyseManager implements Observable, Observer {

	private final ArrayList<Analyser> analysers = new ArrayList<>();
	private static final AnalyseManager inst = new AnalyseManager();
	private final ArrayList<Observer> obs;
	private final ArrayList<SystemState> systState;// to knowledge
	private final static Logger LOGGER = Logger.getLogger(AnalyseManager.class.getName());
	private long lastNotification = 0;
	private final long wait = 1 * 60 * 1000;
	private ArrayList<String> notificationCount = new ArrayList<>();

	private AnalyseManager() {
		this.obs = new ArrayList<>();
		this.systState = new ArrayList<SystemState>();
	}

	public void newAnalyser(Cluster c) {
		Analyser ana = new Analyser(c);
		setObservable(ana);
		ana.initiate();
		analysers.add(ana);

	}

	public static AnalyseManager getInstance() {
		return inst;
	}

	public ArrayList<Analyser> getAnalysers() {
		return analysers;
	}

	@Override
	public void addObserver(Observer o) {
		obs.add(o);
	}

	@Override
	public void removeObserver(Observer o) {
		obs.remove(o);
	}

	@Override
	public void notifyObservers() {

		obs.forEach((Observer ob) -> {
			ob.update();
		});
	}

	@Override
	public void notifyObservers(double metric) {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	public void notified(String name) {
		if (!this.notificationCount.contains(name)) {
			this.notificationCount.add(name);
		}

	}

	public boolean alreadyNotified(String name) {
		if (!this.notificationCount.contains(name)) {
			return false;
		}
		return true;
	}

	public ArrayList<SystemState> getArs() {
		return systState;
	}

	@Override
	public void update() {
		newSystemState();

	}

	@Override
	public void update(String context, double metric) {
		throw new UnsupportedOperationException("Not supported yet."); // To change body of generated methods, choose
																		// Tools | Templates.
	}

	@Override
	public void setObservable(Observable ob) {
		ob.addObserver(this);
	}

	/*
	 * Creates a new system state stores it in the list
	 */
	public void newSystemState() {
		System.out.println("Number of Notifications = " + notificationCount.size());
		if (this.notificationCount.size() == DockerManager.getInstance().getAppServices().size()) {
			SystemState state = new SystemState();
			LOGGER.log(Level.INFO, "Enough Notifications, Creating new System state");
			for (Analyser ana : analysers) {
				if (state.getcurrent_reqs().keySet().contains(ana.getCluster())) {
					System.out.println("State already has a result from this");
				} else {
					Cluster s = ana.getCluster();
					if (ana.getLatest() != null) {
						state.addSymptom(s, ana.getLatest());
					} else {
						ana.getLatest();
					}
				}

			}
			this.notificationCount.clear();
			systState.add(state);
			checkState(state);
		} else {
			System.out.println("Not enough notifications");
		}
	}

	public boolean statistics() {
		ArrayList<Symptom> symps = new ArrayList<>();
		for (Analyser ana : analysers) {
			if (ana.getLatest() != null) {
				symps.add(ana.getLatest());
			}
		}
		if (symps.size() == analysers.size()) {
			return true;
		}
		return false;
	}

	// private Symptom collectStat(Analyser a){
	// Symptom stat = null;
	// if(a.getLatest() != null) {
	// stat = a.getLatest();
	// }else {
	// try {
	//
	// System.out.println("Sleep 2 X " + x);
	// x++;
	// TimeUnit.SECONDS.sleep(2);
	// } catch (InterruptedException e) {
	// // TODO Auto-generated catch block
	// e.printStackTrace();
	// }
	// collectStat(a);
	// }
	// this.x = 0;
	// return stat;
	// }

	/*
	 * Checks the new state to make sure that it is within parameters and if not
	 * creates an adaptation request if get state returns true
	 */
	public void checkState(SystemState state) {
		if (state.getState()) {
			System.out.println("Change state? " + state.getState());
			AdaptationRequest ar = new AdaptationRequest();
			for (Analyser ana : analysers) {
				Cluster s = ana.getCluster();
				int prevcont = s.getContainers().size();
				int cond = (int) state.getSymptom(s).getCondition();
				if (cond < 0) {
					int cond1 = Math.abs(cond);
					int conts = prevcont - cond1;
					if (conts <= 0) {
						conts = 1;
						// System.out.println("Containers recommended for " + s.getServName() + " = " +
						// conts);
						ar.addItem(s.getServName(), conts);
					} else {
						// System.out.println("Containers recommended for " + s.getServName() + " = " +
						// conts);
						ar.addItem(s.getServName(), conts);
					}
				} else {
					int conts = prevcont + cond;
					// System.out.println("Containers recommended for " + s.getServName() + " = " +
					// conts);
					ar.addItem(s.getServName(), conts);
				}

			}
			DockerManager.getInstance().getCurrentTopology().addRequest(ar);
			if (this.lastNotification == 0) {
				LOGGER.log(Level.INFO,
						"||||||||||||||||||||||||||||||||Notifying Planner|||||||||||||||||||||||||||||");
				this.lastNotification = ar.getTime();
				notifyObservers();
			} else if (System.currentTimeMillis() - this.lastNotification > this.wait) {
				LOGGER.log(Level.INFO,
						"||||||||||||||||||||||||||||||||Notifying Planner|||||||||||||||||||||||||||||");
				this.lastNotification = ar.getTime();
				notifyObservers();
			}

		} else {
			System.out.println("Adaptation required (" + state.getState() + ")");
		}
	}

}
