package com.mycompany.aatr2.plan;

import java.util.ArrayList;

public class YMLFileManager {
	private static YMLFileManager instance;
	private ArrayList<YMLFile> files = new ArrayList<>();
	
	public YMLFileManager() {
		
	}
	
	/*
	 * create yml file objects and store them into the database
	 */
	public void newYMLFile() {
		
	}
	
	
	public ArrayList<YMLFile> getFiles() {
		return files;
	}



	public void setFiles(ArrayList<YMLFile> files) {
		this.files = files;
	}



	public static YMLFileManager getInstance() {
		return instance;
	}
}
