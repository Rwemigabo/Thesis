package com.mycompany.aatr2.analyse;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.ArrayList;

import com.mycompany.aatr2.monitor.data.StatisticsLog;

public class AnalyseTest {
	private static ArrayList<StatisticsLog> stats = new ArrayList<>();

	public static void main(String args[]) {

		Analyser obj = new Analyser();

		ReadObjectFromFile(
				"../dataoutputs/0b6a51c886c94c2f0607799cbc9450d176ccc9e04ce9fa18d58bbd132e8bf7d6.ser");
		runLoganalysis(obj);


		
		//obj.filewindowCheck(address);

	}

	public static void runLoganalysis(Analyser ana) {
		for(StatisticsLog log: stats) {
			ana.filewindowCheck(log);
		}
		
	}

	public static void ReadObjectFromFile(String filepath) {
		StatisticsLog address = null;
		
		try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(filepath))) {
			if (ois.available() > 0) {
				address = (StatisticsLog) ois.readObject();
				stats.add(address);
			}else {	System.out.println("Nothing in file");}
			
		} catch (Exception ex) {
			ex.printStackTrace();
		}

	}
}